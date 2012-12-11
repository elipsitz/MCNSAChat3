package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;

public class PlayerJoinedPacket implements IPacket {
	public static final short id = 3;

	public ChatPlayer player = null;

	public PlayerJoinedPacket() {
	}

	public PlayerJoinedPacket(ChatPlayer player) {
		this.player = player;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		player.write(out);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		player = ChatPlayer.read(in);
	}
}
