package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;

public class ServerLeftPacket implements IPacket {
	public static final short id = 2;

	public String shortName = null;
	public ArrayList<ChatPlayer> players = null;

	public ServerLeftPacket() {
	}
	
	public ServerLeftPacket(String shortName, ArrayList<ChatPlayer> players) {
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
			for (ChatPlayer player : players) {
				player.write(out);
			}
		}
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		shortName = in.readUTF();
		players = new ArrayList<ChatPlayer>();
		int numPlayers = in.readInt();
		for (int i = 0; i < numPlayers; i++) {
			players.add(ChatPlayer.read(in));
		}
	}
}
