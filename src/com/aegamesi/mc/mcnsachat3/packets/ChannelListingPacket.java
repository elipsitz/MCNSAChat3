package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;

public class ChannelListingPacket implements IPacket {
	public static final short id = 5;

	public ArrayList<ChatChannel> channels;

	public ChannelListingPacket() {
	}

	public ChannelListingPacket(ArrayList<ChatChannel> channels) {
		this.channels = channels;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		out.writeInt(channels.size());
		for(ChatChannel channel : channels)
			channel.write(out);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		channels = new ArrayList<ChatChannel>();
		int num = in.readInt();
		for(int i = 0; i < num; i++)
			channels.add(ChatChannel.read(in));
	}
}
