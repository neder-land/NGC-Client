package com.github.neder_land.gamecenter.client.network;

import com.github.neder_land.jww.packet.Packet;
import com.github.neder_land.jww.packet.PacketContent;
import com.github.neder_land.jww.packet.Packets;
import com.github.neder_land.jww.packet.center.client.*;
import com.github.neder_land.jww.packet.center.server.*;
import com.github.neder_land.jww.packet.sys.client.Login;
import com.github.neder_land.jww.packet.sys.client.Logout;
import com.github.neder_land.jww.packet.sys.server.Disconnect;
import com.github.neder_land.jww.packet.sys.server.LoginCancel;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;
import java.util.function.Function;

/**
 * @deprecated
 */
@Deprecated
public class WCNMPacketCodec extends MessageToMessageCodec<TextWebSocketFrame, Packet<? extends PacketContent>> {

    Function<String, Class<? extends Packet<? extends PacketContent>>> FACTORY = s -> {
        switch (s) {
            case Packets.BREAK_FAIL:
                return BreakFail.class;
            case Packets.CREATE:
                return Create.class;
            case Packets.CREATE_FAIL:
                return CreateFail.class;
            case Packets.CREATE_SUCCESS:
                return CreateSuccess.class;
            case Packets.DISCONNECT:
                return Disconnect.class;
            case Packets.JOIN:
                return Join.class;
            case Packets.JOIN_FAIL:
                return JoinFail.class;
            case Packets.JOIN_SUCCESS:
                return JoinSuccess.class;
            case Packets.LEAVE_FAIL:
                return LeaveFail.class;
            case Packets.LIST_ROOM_RESPONSE:
                return ListRoomResponse.class;
            case Packets.LIST_ROOMS:
                return ListRooms.class;
            case Packets.LIST_TYPE_RESPONSE:
                return ListTypeResponse.class;
            case Packets.LIST_TYPES:
                return ListTypes.class;
            case Packets.LOGIN:
                return Login.class;
            case Packets.LOGIN_CANCEL:
                return LoginCancel.class;
            case Packets.LOGOUT:
                return Logout.class;
            case Packets.ROOM_OPERATION:
                return RoomOperationPacket.class;
            case Packets.ROOM_STATUS_CHANGE:
                return RoomStatusChange.class;
            case Packets.USER_ACTION:
                return UserActionPacket.class;
            case Packets.USER_STATUS_CHANGE:
                return UserStatusChange.class;
            default:
                throw new UnsupportedProtocolException();
        }
    };

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<? extends PacketContent> msg, List<Object> out) throws Exception {
        out.add(new TextWebSocketFrame(Packet.GSON.toJsonTree(msg).getAsString()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
        String json = msg.text();
        String action = JsonParser.parseString(json).getAsJsonObject().get("action").getAsString();
        Class<? extends Packet<? extends PacketContent>> impl = FACTORY.apply(action);
        Packet<? extends PacketContent> packet = Packet.GSON.fromJson(json, impl);
        out.add(packet);
    }
}
