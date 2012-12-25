package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;

public class PlayerJoinedPacket implements IPacket {
	public static final short id = 3;

	public ChatPlayer player = null;
	public String longname = "";

	public PlayerJoinedPacket() {
	}

	public PlayerJoinedPacket(ChatPlayer player, String longname) {
		this.player = player;
		this.longname = longname;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		out.writeUTF(longname);
		player.write(out);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		longname = in.readUTF();
		player = ChatPlayer.read(in);
	}
}
