package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.aegamesi.mc.mcnsachat3.things.PlayerThing;

public class PlayerJoinedPacket implements IPacket {
	public static final short id = 3;

	public PlayerThing player = null;
	public String server = null;

	public PlayerJoinedPacket() {
	}

	public PlayerJoinedPacket(PlayerThing player, String server) {
		this.player = player;
		this.server = server;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		player.write(out);
		out.writeUTF(server);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		player = PlayerThing.read(in);
		server = in.readUTF();
	}
}
