package com.aegamesi.mc.mcnsachat3.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.aegamesi.mc.mcnsachat3.packets.IPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerLeftPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerLeftPacket;

public class ServerThread extends Thread {
	private Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;
	public Client client = null;
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
	
	public void loop(DataInputStream in, DataOutputStream out) throws IOException {
		short type = in.readShort();
		if (type == ServerJoinedPacket.id) {
			ServerJoinedPacket packet = new ServerJoinedPacket();
			packet.read(in);
			Server.broadcast(packet);
			log("Server joined: " + packet.shortName);
			client = new Client(packet.shortName);
			client.players = packet.players;
			return;
		}
		if (type == PlayerJoinedPacket.id) {
			PlayerJoinedPacket packet = new PlayerJoinedPacket();
			packet.read(in);
			Server.broadcast(packet);
			log("Player joined: " + packet.player.name);
			return;
		}
		if (type == PlayerLeftPacket.id) {
			PlayerLeftPacket packet = new PlayerLeftPacket();
			packet.read(in);
			Server.broadcast(packet);
			log("Player left: " + packet.player.name);
			return;
		}
	}
	
	public void run() {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			while (true)
				loop(in, out);
		} catch (Exception e) {
			log("Connection lost.");
			Server.threads.remove(this);
			return;
		} finally {
			try {
				Server.broadcast(new ServerLeftPacket(client.shortName));
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
		System.out.println("#" + getId() + "[" + (client == null ? host : client.shortName) + "] " + o.toString());
	}
}
