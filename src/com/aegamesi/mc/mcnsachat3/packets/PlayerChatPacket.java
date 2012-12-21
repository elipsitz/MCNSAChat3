package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;

public class PlayerChatPacket implements IPacket {
	public static final short id = 8;

	public ChatPlayer player = null;
	public String message;
	public String channel;
	public Type type;

	public PlayerChatPacket() {
	}

	public PlayerChatPacket(ChatPlayer player, String message, String channel, Type type) {
		this.player = player;
		if (channel == null || channel.length() <= 0)
			channel = player.channel;
		this.channel = channel;
		this.message = message;
		this.type = type;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		out.writeUTF(type.name());
		if (type != Type.MISC)
			player.write(out);
		out.writeUTF(message);
		out.writeUTF(channel);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		type = Type.valueOf(in.readUTF());
		if (type != Type.MISC)
			player = ChatPlayer.read(in);
		message = in.readUTF();
		channel = in.readUTF();
	}

	public enum Type {
		CHAT, ACTION, MISC;
	}
}
