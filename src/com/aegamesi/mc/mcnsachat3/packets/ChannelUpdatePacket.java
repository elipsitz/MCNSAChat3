package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;

public class ChannelUpdatePacket implements IPacket {
	public static final short id = 7;

	public ChatChannel channel = null;

	public ChannelUpdatePacket() {
	}

	public ChannelUpdatePacket(ChatChannel channel) {
		this.channel = channel;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		channel.write(out);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		channel = ChatChannel.read(in);
	}
}
