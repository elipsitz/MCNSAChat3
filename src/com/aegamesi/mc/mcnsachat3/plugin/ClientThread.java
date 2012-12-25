package com.aegamesi.mc.mcnsachat3.plugin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.ChannelListingPacket;
import com.aegamesi.mc.mcnsachat3.packets.ChannelUpdatePacket;
import com.aegamesi.mc.mcnsachat3.packets.IPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerChatPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerLeftPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerPMPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerLeftPacket;

public class ClientThread extends Thread {
	public Socket socket = null;
	public DataOutputStream out = null;
	public DataInputStream in = null;
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
			socket = new Socket(plugin.getConfig().getString("chat-server"), 51325);
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

		connected = true;
		log.info("Connected to chat server.");

		try {
			new ServerJoinedPacket(plugin.name, PlayerManager.getPlayersByServer(plugin.name)).write(out);
			new ChannelListingPacket(ChannelManager.channels).write(out);

			while (loop(in, out))
				;
		} catch (IOException e) {
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
			// TODO server join/left message (broadcast)
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
			if (!ChannelManager.getChannel(packet.player.channel).modes.contains(ChatChannel.Mode.LOCAL)) {
				String joinString = plugin.getConfig().getString("strings.player-join");
				joinString = joinString.replaceAll("%prefix%", MCNSAChat3.permissions.getUser(packet.player.name).getPrefix());
				joinString = joinString.replaceAll("%player%", packet.player.name);
				joinString = joinString.replace("%server%", packet.player.server);
				ArrayList<ChatPlayer> toNotify = PlayerManager.getPlayersListeningToChannel(packet.player.channel);
				for (ChatPlayer p : toNotify)
					if (p.server.equals(plugin.name))
						PluginUtil.sendLater(p.name, joinString);
			}

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
			if (!ChannelManager.getChannel(packet.player.channel).modes.contains(ChatChannel.Mode.LOCAL)) {
				String quitString = plugin.getConfig().getString("strings.player-quit");
				quitString = quitString.replaceAll("%prefix%", MCNSAChat3.permissions.getUser(packet.player.name).getPrefix());
				quitString = quitString.replaceAll("%player%", packet.player.name);
				quitString = quitString.replace("%server%", packet.player.server);
				ArrayList<ChatPlayer> toNotify = PlayerManager.getPlayersListeningToChannel(packet.player.channel);
				for (ChatPlayer p : toNotify)
					if (p.server.equals(plugin.name))
						PluginUtil.sendLater(p.name, quitString);
			}

			PlayerManager.removePlayer(packet.player);
			return true;
		}
		if (type == PlayerUpdatePacket.id) {
			PlayerUpdatePacket packet = new PlayerUpdatePacket();
			packet.read(in);
			if (packet.player.server.equals(plugin.name))
				return true;

			// log + notify
			log.info("Updated player" + packet.player.name + " on " + packet.player.server);
			// this usually signifies a mode change or channel change. We don't
			// really care, however, as it is on another server
			PlayerManager.removePlayer(packet.player);
			PlayerManager.players.add(packet.player);
			return true;
		}
		if (type == ChannelUpdatePacket.id) {
			ChannelUpdatePacket packet = new ChannelUpdatePacket();
			packet.read(in);

			// log + notify
			log.info("Updated channel" + packet.channel.name);
			// this usually signifies a mode change or something
			ChannelManager.removeChannel(packet.channel);
			ChannelManager.channels.add(packet.channel);
			return true;
		}
		if (type == PlayerChatPacket.id) {
			PlayerChatPacket packet = new PlayerChatPacket();
			packet.read(in);
			if (packet.player.server.equals(plugin.name))
				return true;
			if (ChannelManager.getChannel(packet.channel == null ? packet.player.channel : packet.channel).modes.contains(ChatChannel.Mode.LOCAL))
				return true;

			if (packet.type == PlayerChatPacket.Type.CHAT)
				plugin.chat.chat(packet.player, packet.message, packet.channel);
			if (packet.type == PlayerChatPacket.Type.ACTION)
				plugin.chat.action(packet.player, packet.message, packet.channel);
			if (packet.type == PlayerChatPacket.Type.MISC)
				plugin.chat.info(null, packet.message, packet.channel, true);
			return true;
		}
		if (type == PlayerPMPacket.id) {
			PlayerPMPacket packet = new PlayerPMPacket();
			packet.read(in);
			if (packet.from.server.equals(plugin.name))
				return true;

			plugin.getLogger().info("[" + packet.from.name + " -> " + packet.to + "] " + packet.message);
			if (Bukkit.getPlayerExact(packet.to) != null)
				plugin.chat.pm_receive(packet.from, packet.to, packet.message);
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
			e.printStackTrace();
		}
	}
}
