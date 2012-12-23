package com.aegamesi.mc.mcnsachat3.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatChannel.Mode;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.IPacket;

public class Server {
	public static ArrayList<ServerThread> threads;
	public static ServerSocket serverSock = null;
	public static ServerPersistence persist;
	
	public static BufferedWriter general_log;
	public static BufferedWriter chat_log;

	public static void main(String[] args) throws IOException {
		int port = 51325;
		threads = new ArrayList<ServerThread>();
		persist = new ServerPersistence();
		PlayerManager.init();
		ChannelManager.init();

		System.out.println("MCNSAChat3 Server v1");
		System.out.println("Loading data...");
		load();
		System.out.println("Server Started on port " + port);

		try {
			serverSock = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(-1);
		}

		System.out.println("Listening on port " + port);
		new ListenThread().start();
		OnlinePlayersThread onlinePlayersThread = new OnlinePlayersThread();
		onlinePlayersThread.start();

		// start the i/o loop now
		Scanner in = new Scanner(System.in);
		String line = null;
		while ((line = in.nextLine()) != null) {
			if (line.equalsIgnoreCase("stop")) {
				break;
			}
			System.out.println("Unknown command");
		}

		System.out.println("Server is shutting down NOW!");
		System.out.println("WARNING: Threads may take longer to close");
		serverSock.close();
		onlinePlayersThread.interrupt();
		for (ServerThread thread : threads) {
			thread.socket.close();
		}
		System.out.println("Saving data...");
		save();
	}

	public static void broadcast(IPacket packet) {
		for (ServerThread thread : threads) {
			thread.write(packet);
		}
	}

	@SuppressWarnings("unchecked")
	public static void load() throws IOException {
		// set up logging
		general_log = new BufferedWriter(new FileWriter(new File("server_general_log.txt"), true));
		boolean exists = new File("server_chat_log.csv").exists();
		chat_log = new BufferedWriter(new FileWriter(new File("server_chat_log.csv"), true));
		if (!exists) {
			chat_log.write("time,server,player,channel,message");
			chat_log.newLine();
			chat_log.flush();
		}
		
		
		// load channels
		List<Map<?, ?>> channelData = persist.get().getMapList("channels");
		for (Map<?, ?> channel : channelData) {
			ChatChannel c = new ChatChannel((String) channel.get("name"));
			c.read_permission = (String) (channel.containsKey("read_permission") ? channel.get("read_permission") : "");
			c.write_permission = (String) (channel.containsKey("write_permission") ? channel.get("write_permission") : "");
			c.alias = (String) (channel.containsKey("alias") ? channel.get("alias") : "");
			c.color = (String) (channel.containsKey("color") ? channel.get("color") : "");
			List<String> modes = (List<String>) channel.get("modes");
			for (String mode : modes)
				c.modes.add(Mode.valueOf(mode));
			ChannelManager.channels.add(c);
		}
	}

	public static void save() throws IOException {
		//unload logging
		general_log.close();
		chat_log.close();
		
		// save channels
		ArrayList<HashMap<String, Object>> chanMap = new ArrayList<HashMap<String, Object>>();
		for (ChatChannel c : ChannelManager.channels) {
			HashMap<String, Object> chan = new HashMap<String, Object>();
			chan.put("name", c.name);
			chan.put("read_permission", c.read_permission);
			chan.put("write_permission", c.write_permission);
			chan.put("alias", c.alias);
			chan.put("color", c.color);
			ArrayList<String> modes = new ArrayList<String>();
			for (ChatChannel.Mode mode : c.modes)
				modes.add(mode.name());
			chan.put("modes", modes);
			chanMap.add(chan);
		}
		persist.get().set("channels", chanMap);
		persist.save();
	}
}
