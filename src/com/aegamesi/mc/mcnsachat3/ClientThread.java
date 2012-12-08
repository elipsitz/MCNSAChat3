package com.aegamesi.mc.mcnsachat3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.aegamesi.mc.mcnsachat3.packets.IPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;

public class ClientThread extends Thread {
	public Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;
	public boolean run = true;
	
	public MCNSAChat3 plugin;
	public Logger log;
	
	public ClientThread(MCNSAChat3 plugin, Logger log) {
		this.plugin = plugin;
		this.log = log;
	}

	public void run() {
		log.info("Attempting to connect to chat server...");
		try {
			socket = new Socket("127.0.0.1", 51325);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			log.warning("Chat server: Unknown host");
			MCNSAChat3.thread = null;
			return;
		} catch (IOException e) {
			log.warning("Couldn't connect to chat server");
			MCNSAChat3.thread = null;
			return;
		}
		
		log.info("Connected to chat server.");

		try {
			ServerJoinedPacket joinPacket = new ServerJoinedPacket("sdot", null);
			joinPacket.write(out);

			while (run)
				loop(in, out);
		} catch (Exception e) {
			log.warning("Chat server: connection lost?");
			MCNSAChat3.thread = null;
			return;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				socket.close();
				log.info("Disconnected from chat server.");
			} catch (IOException e) {
				log.warning("Error closing socket");
			}
		}
		MCNSAChat3.thread = null;
	}

	public void loop(DataInputStream in, DataOutputStream out) throws IOException {
		short type = in.readShort();
		if (type == ServerJoinedPacket.id) {
			ServerJoinedPacket packet = new ServerJoinedPacket();
			packet.read(in);
			return;
		}
	}
	
	public void write(IPacket packet) {
		try {
			packet.write(out);
		} catch (IOException e) {
			log.warning("Error writing packet " + packet.getClass());
		}
	}
}
