package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.things.PlayerThing;

public class ServerJoinedPacket implements IPacket {
	public static final short id = 1;

	public String shortName = null;
	public ArrayList<PlayerThing> players = null;

	public ServerJoinedPacket() {
	}
	
	public ServerJoinedPacket(String shortName, ArrayList<PlayerThing> players) {
		this.shortName = shortName;
		this.players = players;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		out.writeUTF(shortName);
		if (players == null) {
			out.writeInt(0);
		} else {
			out.writeInt(players.size());
			for (PlayerThing player : players) {
				player.write(out);
			}
		}
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		shortName = in.readUTF();
		players = new ArrayList<PlayerThing>();
		int numPlayers = in.readInt();
		for (int i = 0; i < numPlayers; i++) {
			players.add(PlayerThing.read(in));
		}
	}
}
