package com.github.neder_land.gamecenter.client.network;

import com.github.neder_land.gamecenter.client.ClientHandler;
import com.github.neder_land.gamecenter.client.api.network.GameContent;
import com.github.neder_land.gamecenter.client.api.network.GamePacket;
import com.github.neder_land.gamecenter.client.api.network.IPacketDispatcher;
import com.github.neder_land.jww.packet.ClientDecoder;
import com.github.neder_land.jww.packet.PacketContent;
import com.github.neder_land.jww.packet.content.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class PacketDispatcher extends SimpleChannelInboundHandler<TextWebSocketFrame> implements ClientDecoder, IPacketDispatcher, JsonSerializer<GamePacket>, JsonDeserializer<GamePacket> {

    private Channel channelIn;
    private final Map<String, Class<? extends GamePacket>> packets = Maps.newHashMap();
    private final Map<Class<? extends GamePacket>, Class<? extends GameContent>> contentMapper = Maps.newHashMap();
    private final Multimap<Class<? extends GamePacket>, Consumer<? extends GamePacket>> listeners = MultimapBuilder.hashKeys().hashSetValues().build();
    private static final Map<Class<? extends GamePacket>, String> ACTION_CACHE = new ConcurrentHashMap<>();
    private static final Field ACTION_CONTENT;

    static {
        try {
            ACTION_CONTENT = PacketContent.class.getDeclaredField("action");
            ACTION_CONTENT.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class PlaceHolder<T extends GamePacket<E>, E extends GameContent> implements Consumer<T> {
        /**
         * Performs this operation on the given argument.
         *
         * @param t the input argument
         */
        @Override
        public void accept(T t) {
        }
    }

    public PacketDispatcher() {
        super(TextWebSocketFrame.class);
    }

    @Override
    public void extraDecode(String action, JsonElement json) {
        AtomicBoolean nil = new AtomicBoolean(true);
        if (!packets.containsKey(action))
            throw new UnsupportedProtocolException.UnsupportedPacketException("Don't know how to deserialize action " + action + " into packet...");
        Runnable r = () -> {
            Class<? extends GamePacket> packetClz = packets.get(action);
            GamePacket packet = ClientHandler.GSON.fromJson(json, packetClz);
            if (!listeners.containsKey(packet.getClass())) {
                System.err.println("A packet seemed to have no listener!Ignoring...");
                return;
            }
            nil.set(false);
            listeners.get(packetClz).forEach(consumer -> {
                //noinspection unchecked
                ((Consumer<GamePacket>) consumer).accept(packet);
            });
        };
        if (channelIn.eventLoop().inEventLoop()) r.run();
        else channelIn.eventLoop().execute(r);
        if (nil.get())
            throw new UnsupportedProtocolException.UnsupportedPacketException("Cannot deserialize packet with action " + action + " :No impl found");
    }

    @Override
    public void handleBreakFail(ReasonContent content) {

    }

    @Override
    public void handleCreateFail(ReasonContent content) {

    }

    @Override
    public void handleCreateSuccess(RoomCreationContent content) {

    }

    @Override
    public void handleJoinFail(ReasonContent content) {

    }

    @Override
    public void handleJoinSuccess(DetailedRoomContent content) {

    }

    @Override
    public void handleLeaveFail(ReasonContent content) {

    }

    @Override
    public void handleListRoomResponse(RoomListContent content) {

    }

    @Override
    public void handleListTypeResponse(GameTypeListContent content) {

    }

    @Override
    public void handleLoginCancel(ReasonContent content) {

    }

    @Override
    public void handleRoomStatusChange(RoomStatusContent content) {

    }

    @Override
    public void handleUserStatusChange(UserStatusContent content) {

    }

    /**
     * Is called for each message of type {@link TextWebSocketFrame}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        if (channelIn.eventLoop().inEventLoop()) decode(msg.text());
        else channelIn.eventLoop().execute(() -> decode(msg.text()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channelIn = ctx.channel();
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        channelIn = null;
        ctx.fireChannelInactive();
    }

    @Override
    public <T extends GamePacket<E>, E extends GameContent> void registerPacket(Class<T> packet, Class<E> content, String game, String subtype) {
        String action = "game." + game + "." + subtype;
        if (!ACTION_CACHE.containsKey(packet)) ACTION_CACHE.put(packet, action);
        if (!(packets.putIfAbsent(action, packet) == null))
            throw new UnsupportedOperationException("A packet with action " + action + " has already been registered!");
        if (!(contentMapper.putIfAbsent(packet, content) == null))
            throw new UnsupportedOperationException("A packet with action " + action + " has already been registered!");
        listeners.put(packet, new PlaceHolder<>());
    }

    public <T extends GamePacket<E>, E extends GameContent> void unregisterPacket(Class<T> packet) {
        String action;
        if (ACTION_CACHE.containsKey(packet)) {
            action = ACTION_CACHE.get(packet);
        } else {
            action = getKey(packet, packets).orElseThrow(UnknownPacketException::new);
            ACTION_CACHE.put(packet, action);
        }
        packets.remove(action);
        contentMapper.remove(packet);
        listeners.removeAll(packet);
    }

    private static final <K, V> Optional<K> getKey(V value, Map<K, V> map) {
        return map.entrySet().parallelStream().filter(e -> e.getValue().equals(value)).findAny().map(Map.Entry::getKey);
    }

    @Override
    public <T extends GamePacket<E>, E extends GameContent> void registerListener(Class<T> clz, Consumer<T> listener) {
        Objects.requireNonNull(clz, "class must not be null");
        Objects.requireNonNull(listener, "listener must not be null");
        listeners.put(clz, listener);
    }

    @Override
    public <T extends GamePacket<E>, E extends GameContent> void unregisterListener(Class<T> clz, Consumer<T> listener) {
        Objects.requireNonNull(clz, "class must not be null");
        Objects.requireNonNull(listener, "listener must not be null");
        listeners.remove(clz, listener);
    }

    @Override
    public <T extends GamePacket<E>, E extends GameContent> void sendCustomPacket(T packet, @Nullable Runnable callback) {
        if (channelIn.eventLoop().inEventLoop())
            channelIn.writeAndFlush(ClientHandler.GSON.toJson(packet, GamePacket.class)).addListener(future -> Optional.ofNullable(callback).ifPresent(Runnable::run));
        else
            channelIn.eventLoop().execute(() -> channelIn.writeAndFlush(ClientHandler.GSON.toJson(packet, GamePacket.class)).addListener(future -> Optional.ofNullable(callback).ifPresent(Runnable::run)));
    }

    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeofT}
     */
    @Override
    public GamePacket deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject $ = json.getAsJsonObject();
            if ($.has("action") && $.has("content")) {
                JsonElement action = $.get("action");
                JsonElement content = $.get("content");
                if (action.isJsonPrimitive() && content.isJsonObject()) {
                    JsonPrimitive primitive = action.getAsJsonPrimitive();
                    if (primitive.isString()) {
                        String actStr = primitive.getAsString();
                        Class<? extends GamePacket> packetClz = packets.get(actStr);
                        try {
                            GameContent pc = ClientHandler.GSON.fromJson(content.getAsJsonObject(), contentMapper.get(packetClz));
                            ACTION_CONTENT.set(pc, actStr);
                            Constructor<? extends GamePacket> constructor = packetClz.getConstructor(contentMapper.get(packetClz));
                            constructor.setAccessible(true);
                            return constructor.newInstance(pc);
                        } catch (NoSuchMethodException e) {
                            throw new WrongPacketDefinitionException("Packet " + packetClz + " should define a constructor required a content!", e);
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        throw new JsonParseException("Wrong format of packet!");
    }

    /**
     * Gson invokes this call-back method during serialization when it encounters a field of the
     * specified type.
     *
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonSerializationContext#serialize(Object, Type)} method to create JsonElements for any
     * non-trivial field of the {@code src} object. However, you should never invoke it on the
     * {@code src} object itself since that will cause an infinite loop (Gson will call your
     * call-back method again).</p>
     *
     * @param src       the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @param context
     * @return a JsonElement corresponding to the specified object.
     */
    @Override
    public JsonElement serialize(GamePacket src, Type typeOfSrc, JsonSerializationContext context) {
        return JsonParser.parseString(src.serialize());
    }
}
