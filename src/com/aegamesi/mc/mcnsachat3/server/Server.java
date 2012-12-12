package com.aegamesi.mc.mcnsachat3.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.IPacket;

public class Server {
	public static ArrayList<ServerThread> threads;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		boolean listening = true;
		int port = 51325;
		
		threads = new ArrayList<ServerThread>();
		PlayerManager.init();
		ChannelManager.init();
		
		System.out.println("MCNSAChat3 Server");
		System.out.println("Server Started on port " + port);

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(-1);
		}
		
		System.out.println("Listening on port " + port);
		while (listening) {
			Socket sock = serverSocket.accept();
			ServerThread t = new ServerThread(sock);
			t.start();
			threads.add(t);
			t.log("New connection");
		}

		serverSocket.close();
	}
	
	public static void broadcast(IPacket packet) {
		for(ServerThread thread : threads) {
			thread.write(packet);
		}
	}
}
