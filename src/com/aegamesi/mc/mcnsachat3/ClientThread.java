package com.aegamesi.mc.mcnsachat3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;

public class ClientThread extends Thread {
	public Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;

	public void run() {
		System.out.println("Attempting to connect to host...");
		try {
			socket = new Socket("127.0.0.1", 6768);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Unknown host");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't connect to host");
			System.exit(1);
		}
		
		System.out.println("Connected to host.");

		try {
			ServerJoinedPacket joinPacket = new ServerJoinedPacket("sdot", null);
			joinPacket.write(out);

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

	public void loop(DataInputStream in, DataOutputStream out) throws IOException {
		short type = in.readShort();
		if (type == ServerJoinedPacket.id) {
			ServerJoinedPacket packet = new ServerJoinedPacket();
			packet.read(in);
			return;
		}
	}
}
