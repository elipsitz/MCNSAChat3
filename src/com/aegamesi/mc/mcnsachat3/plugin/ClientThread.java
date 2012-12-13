package com.aegamesi.mc.mcnsachat3.plugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.ChannelListingPacket;
import com.aegamesi.mc.mcnsachat3.packets.IPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerLeftPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerLeftPacket;

public class ClientThread extends Thread {
	public Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;
	public boolean run = true;
	public boolean connected = false;

	public MCNSAChat3 plugin;
	public Logger log;

	public ClientThread(MCNSAChat3 plugin, Logger log) {
		this.plugin = plugin;
		this.log = log;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		log.info("Attempting to connect to chat server...");
		try {
			socket = new Socket("127.0.0.1", 51325);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			log.warning("Chat server: Unknown host");
			plugin.thread = null;
			return;
		} catch (IOException e) {
			log.warning("Couldn't connect to chat server");
			plugin.thread = null;
			return;
		}

		connected = true;
		log.info("Connected to chat server.");

		try {
			new ServerJoinedPacket(plugin.name, PlayerManager.getPlayersByServer(plugin.name)).write(out);
			new ChannelListingPacket(ChannelManager.channels).write(out);
			
			while (loop(in, out))
				;
		} catch (Exception e) {
			log.warning("Chat server: connection lost?");
			String msg = "";
			ArrayList<ChatPlayer> players = (ArrayList<ChatPlayer>) PlayerManager.players.clone();
			for (ChatPlayer p : players) {
				if (!p.server.equals(plugin.name)) {
					msg += p.name + " ";
					PlayerManager.players.remove(p);
				}
			}
			log.warning("Players lost: " + msg);

			plugin.thread = null;
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
		plugin.thread = null;
	}

	public boolean loop(DataInputStream in, DataOutputStream out) throws IOException {
		short type = in.readShort();
		if (type == ServerJoinedPacket.id) {
			ServerJoinedPacket packet = new ServerJoinedPacket();
			packet.read(in);
			if (packet.shortName.equals(plugin.name))
				return true;

			// log + notify
			log.info("Server joined " + packet.shortName);
			String msg = "";
			for (ChatPlayer p : packet.players)
				msg += p.name + " ";
			log.info("Players joined: " + msg);

			PlayerManager.players.addAll(packet.players);
			return true;
		}
		if (type == ServerLeftPacket.id) {
			ServerLeftPacket packet = new ServerLeftPacket();
			packet.read(in);
			if (packet.shortName.equals(plugin.name))
				return true;

			// log + notify
			log.info("Server left " + packet.shortName);
			ArrayList<ChatPlayer> playersLost = PlayerManager.getPlayersByServer(packet.shortName);
			String msg = "";
			for (ChatPlayer p : playersLost) {
				msg += p.name + " ";
				PlayerManager.players.remove(p);
			}
			log.info("Players left: " + msg);
			return true;
		}
		if (type == ChannelListingPacket.id) {
			ChannelListingPacket packet = new ChannelListingPacket();
			packet.read(in);

			// log + notify
			log.info("Received updated channel list");
			ChannelManager.channels = packet.channels;
			return true;
		}
		if (type == PlayerJoinedPacket.id) {
			PlayerJoinedPacket packet = new PlayerJoinedPacket();
			packet.read(in);
			if (packet.player.server.equals(plugin.name))
				return true;

			// log + notify
			log.info("Player joined " + packet.player.name + " from " + packet.player.server);

			PlayerManager.players.add(packet.player);
			return true;
		}
		if (type == PlayerLeftPacket.id) {
			PlayerLeftPacket packet = new PlayerLeftPacket();
			packet.read(in);
			if (packet.player.server.equals(plugin.name))
				return true;

			// log + notify
			log.info("Player left" + packet.player.name + " from " + packet.player.server);

			PlayerManager.removePlayer(packet.player);
			return true;
		}
		return false;
	}

	public void write(IPacket packet) {
		if (!connected)
			return;
		try {
			packet.write(out);
		} catch (IOException e) {
			log.warning("Error writing packet " + packet.getClass());
		}
	}
}
