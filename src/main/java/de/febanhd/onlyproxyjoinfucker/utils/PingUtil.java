package de.febanhd.onlyproxyjoinfucker.utils;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PingUtil {

    public static List<String> getPlayerNames(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(handshake_bytes);
        handshake.writeByte(MinecraftPingUtil.PACKET_HANDSHAKE);
        MinecraftPingUtil.writeVarInt(handshake, MinecraftPingUtil.PROTOCOL_VERSION);
        MinecraftPingUtil.writeVarInt(handshake, host.length());
        handshake.writeBytes(host);
        handshake.writeShort(port);
        MinecraftPingUtil.writeVarInt(handshake, MinecraftPingUtil.STATUS_HANDSHAKE);
        MinecraftPingUtil.writeVarInt(out, handshake_bytes.size());
        out.write(handshake_bytes.toByteArray());
        out.writeByte(1);
        out.writeByte(MinecraftPingUtil.PACKET_STATUSREQUEST);
        MinecraftPingUtil.readVarInt(in);
        int id = MinecraftPingUtil.readVarInt(in);
        MinecraftPingUtil.io(id == -1, "Server prematurely ended stream.");
        MinecraftPingUtil.io(id != MinecraftPingUtil.PACKET_STATUSREQUEST, "Server returned invalid packet.");
        int length = MinecraftPingUtil.readVarInt(in);
        MinecraftPingUtil.io(length == -1, "Server prematurely ended stream.");
        MinecraftPingUtil.io(length == 0, "Server returned unexpected value.");
        byte[] data = new byte[length];
        in.readFully(data);
        String jsonString = new String(data, "UTF-8");
        out.writeByte(9);
        out.writeByte(MinecraftPingUtil.PACKET_PING);
        out.writeLong(System.currentTimeMillis());
        MinecraftPingUtil.readVarInt(in);
        id = MinecraftPingUtil.readVarInt(in);
        MinecraftPingUtil.io(id == -1, "Server prematurely ended stream.");
        MinecraftPingUtil.io(id != MinecraftPingUtil.PACKET_PING, "Server returned invalid packet.");
        handshake.close();
        handshake_bytes.close();
        out.close();
        in.close();
        socket.close();

        ArrayList<String> names = Lists.newArrayList();
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonArray array = json.getAsJsonObject("players").getAsJsonArray("sample");
        if(array != null) {
            for (JsonElement jsonElement : array) {
                JsonObject playerJson = jsonElement.getAsJsonObject();
                String name = playerJson.get("name").getAsString();
                names.add(name);
            }
        }
        return names;
    }
}
