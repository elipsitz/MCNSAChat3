package com.aegamesi.mc.mcnsachat3.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.packets.IPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerLeftPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerLeftPacket;

public class ServerThread extends Thread {
	private Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;
	public String name = "";
	public String host;

	public ServerThread(Socket socket) {
		this.socket = socket;

		host = socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort();
	}

	public void write(IPacket packet) {
		try {
			packet.write(out);
		} catch (IOException e) {
			log("Error writing packet " + packet.getClass());
		}
	}

	public boolean loop(DataInputStream in, DataOutputStream out) throws IOException {
		short type = in.readShort();
		if (type == ServerJoinedPacket.id) {
			ServerJoinedPacket packet = new ServerJoinedPacket();
			packet.read(in);
			Server.broadcast(packet);
			name = packet.shortName;
			log("Server joined: " + name);
			Server.players.addAll(packet.players);
			String msg = "";
			for(ChatPlayer player : packet.players)
				msg += player.name + " ";
			log("Players added: " + msg);
			// TODO add players and channels from server
			// TODO send players and channels from other servers
			return true;
		}
		// on server left
		// TODO remove players from server
		// TODO send removed players to other servers
		if (type == PlayerJoinedPacket.id) {
			PlayerJoinedPacket packet = new PlayerJoinedPacket();
			packet.read(in);
			Server.broadcast(packet);
			Server.players.add(packet.player);
			log(packet.player.name + " joined " + packet.player.server);
			return true;
		}
		if (type == PlayerLeftPacket.id) {
			PlayerLeftPacket packet = new PlayerLeftPacket();
			packet.read(in);
			Server.broadcast(packet);
			Server.players.remove(packet.player);
			log(packet.player.name + " left " + packet.player.server);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			while (loop(in, out));
		} catch (Exception e) {
			log("Connection lost.");
			Server.threads.remove(this);
			return;
		} finally {
			try {
				ArrayList<ChatPlayer> playersLost = new ArrayList<ChatPlayer>();
				ArrayList<ChatPlayer> players = (ArrayList<ChatPlayer>) Server.players.clone();
				String msg = "";
				for (ChatPlayer p : players) {
					if (!p.server.equals(name)) {
						msg += p.name + " ";
						Server.players.remove(p);
					}
				}
				log("Players lost: " + msg);
				Server.broadcast(new ServerLeftPacket(name, playersLost));
				
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				socket.close();
				Server.threads.remove(this);
			} catch (IOException e) {
				log("Error closing socket");
			}
		}
	}

	public void log(Object o) {
		System.out.println("#" + getId() + "[" + (name.length() == 0 ? host : name) + "] " + o.toString());
	}
}
