package com.github.neder_land.gamecenter.client.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ConnectionHandler extends SimpleChannelInboundHandler {

    private ChannelPromise handshakeFuture;
    private boolean isAdded;
    private WebSocketClientHandshaker handshaker;
    private EventExecutor ee = new DefaultEventExecutor(GlobalEventExecutor.INSTANCE);
    private boolean handshaked;

    public ConnectionHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }


    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("handlerAdded");
        if (isAdded) ctx.channel().pipeline().remove(this);
        isAdded = true;
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        isAdded = false;
        handshaker = null;
        handshakeFuture = null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive");
        handshaker.handshake(ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.fireChannelInactive();
    }

    /**
     * Is called for each message of type {@link Object}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead0");
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                System.out.println("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                System.out.println("WebSocket Client failed to connect");
                handshakeFuture.setFailure(e);
            }
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            System.err.println("Unexpected FullHttpResponse (status=" + response.status()
                    + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }


        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            if (!handshaked) {
                JsonElement je = JsonParser.parseString(((TextWebSocketFrame) msg).text());
                if (!je.isJsonObject()) ctx.writeAndFlush(new CloseWebSocketFrame());
                JsonObject jo = new JsonObject();
                if (!jo.has("version")) ctx.writeAndFlush(new CloseWebSocketFrame());
                JsonElement ver = jo.get("version");
                if (!ver.isJsonPrimitive()) ctx.writeAndFlush(new CloseWebSocketFrame());
                JsonPrimitive primitive = ver.getAsJsonPrimitive();
                if (!primitive.isNumber()) ctx.writeAndFlush(new CloseWebSocketFrame());
                int version = primitive.getAsInt();

            }
        } else if (frame instanceof PongWebSocketFrame) {
            System.out.println("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client received closing");
            ch.close();
        } else if (frame instanceof BinaryWebSocketFrame) {
            System.err.println("Unexpected binary frame found:" + frame.content().toString());
        }
    }
}
