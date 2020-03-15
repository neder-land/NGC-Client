package com.github.neder_land.gamecenter.client.network;

import com.github.neder_land.gamecenter.client.init.Protocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Network {
    private static Bootstrap boot;
    private static Protocol protocol;

    public static void init() {
        try {
            connect("");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void connect(String url) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(url);
        String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
        final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        final int port;
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            //System.err.println("Only WS(S) is supported.");
            //Append error message on gui.
            return;
        }

        final boolean ssl = "wss".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory
                    .newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
            boot = new Bootstrap();
            boot.group(workerGroup);
            boot.channel(NioSocketChannel.class);
            boot.handler(new ChannelInitializer<SocketChannel>() {
                /**
                 * This method will be called once the {@link Channel} was registered. After the method returns this instance
                 * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
                 *
                 * @param ch the {@link Channel} which was registered.
                 * @throws Exception is thrown if an error occurs. In that case it will be handled by
                 *                   {@link #exceptionCaught(ChannelHandlerContext, Throwable)} which will by default close
                 *                   the {@link Channel}.
                 */
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("http-codec", new HttpServerCodec());
                    ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                    ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                    ch.pipeline().addLast("connection-handler", new ConnectionHandler(handshaker));
                    //ch.pipeline().addLast("wcnm-codec", new WCNMPacketCodec());
                }
            });
            boot.connect(host, port).sync();
        } finally {
            workerGroup.shutdownGracefully().syncUninterruptibly();
        }
    }
}
