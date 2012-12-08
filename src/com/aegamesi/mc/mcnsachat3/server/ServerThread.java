package com.aegamesi.mc.mcnsachat3.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.aegamesi.mc.mcnsachat3.packets.IPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;

public class ServerThread extends Thread {
	private Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;
	public Client client = null;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}
	
	public void write(IPacket packet) {
		try {
			packet.write(out);
		} catch (IOException e) {
			System.out.println("Error writing packet " + packet.getClass());
		}
	}
	
	public void loop(DataInputStream in, DataOutputStream out) throws IOException {
		short type = in.readShort();
		if (type == ServerJoinedPacket.id) {
			ServerJoinedPacket packet = new ServerJoinedPacket();
			packet.read(in);
			Server.broadcast(new ServerJoinedPacket(packet.shortName, packet.players));
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
			System.out.println("Error reading...connection lost?");
			return;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				socket.close();
			} catch (IOException e) {
				System.out.println("Error closing socket");
			}
		}
	}
}
