package com.aegamesi.mc.mcnsachat3.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

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
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.ServerLeftPacket;

public class ServerThread extends Thread {
	public Socket socket = null;
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
			log("Server joined: " + packet.shortName);
			name = packet.shortName;
			PlayerManager.players.addAll(packet.players);
			String msg = "";
			for (ChatPlayer player : packet.players)
				msg += player.name + " ";
			log("Players added: " + msg);
			// send the servers
			for (ServerThread thread : Server.threads)
				if (thread != this)
					write(new ServerJoinedPacket(thread.name, PlayerManager.getPlayersByServer(thread.name)));
			return true;
		}
		if (type == ChannelListingPacket.id) {
			ChannelListingPacket packet = new ChannelListingPacket();
			packet.read(in);
			log("Received channel listing. Size: " + packet.channels.size());
			// merge with current list
			for (ChatChannel channel : packet.channels) {
				ChatChannel old = ChannelManager.getChannel(channel.name);
				if (old == null) {
					ChannelManager.channels.add(channel);
				} else {
					HashSet<ChatChannel.Mode> duplicateRemover = new HashSet<ChatChannel.Mode>();
					duplicateRemover.addAll(old.modes);
					duplicateRemover.addAll(channel.modes);
					old.modes.clear();
					old.modes.addAll(duplicateRemover);
				}
			}
			// okay, now send the updated list to everybody
			Server.broadcast(new ChannelListingPacket(ChannelManager.channels));
			return true;
		}
		if (type == PlayerJoinedPacket.id) {
			PlayerJoinedPacket packet = new PlayerJoinedPacket();
			packet.read(in);
			Server.broadcast(packet);
			PlayerManager.players.add(packet.player);
			log(packet.player.name + " joined " + packet.player.server);
			return true;
		}
		if (type == PlayerLeftPacket.id) {
			PlayerLeftPacket packet = new PlayerLeftPacket();
			packet.read(in);
			Server.broadcast(packet);
			PlayerManager.removePlayer(packet.player);
			log(packet.player.name + " left " + packet.player.server);
			return true;
		}
		if (type == PlayerUpdatePacket.id) {
			PlayerUpdatePacket packet = new PlayerUpdatePacket();
			packet.read(in);
			Server.broadcast(packet);
			PlayerManager.removePlayer(packet.player);
			PlayerManager.players.add(packet.player);
			log(packet.player.name + " updated on " + packet.player.server);
			return true;
		}
		if (type == ChannelUpdatePacket.id) {
			ChannelUpdatePacket packet = new ChannelUpdatePacket();
			packet.read(in);
			Server.broadcast(packet);
			ChannelManager.removeChannel(packet.channel);
			ChannelManager.channels.add(packet.channel);
			log(packet.channel.name + " channel updated");
			return true;
		}
		if (type == PlayerChatPacket.id) {
			PlayerChatPacket packet = new PlayerChatPacket();
			packet.read(in);
			Server.broadcast(packet);
			String channel = packet.channel == null ? packet.player.channel : packet.channel;
			log("[" + channel + "] <" + packet.player.name + "> " + packet.message);

			// now log, specifically in chat_log too
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", time, name, packet.player.name, channel, packet.message);
			try {
				Server.chat_log.write(line);
				Server.chat_log.newLine();
				Server.chat_log.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}

	public void run() {
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			while (loop(in, out))
				;
		} catch (IOException e) {
			log("Connection lost.");

			Server.threads.remove(this);
			return;
		} finally {
			try {
				ArrayList<ChatPlayer> playersLost = PlayerManager.getPlayersByServer(name);
				String msg = "";
				for (ChatPlayer p : playersLost) {
					msg += p.name + " ";
					PlayerManager.players.remove(p);
				}
				log("Players lost: " + msg);
				Server.broadcast(new ServerLeftPacket(name));

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
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String line = "#" + getId() + "[" + (name.length() == 0 ? host : name) + "] " + time + " |" + o.toString();
		System.out.println(line);
		try {
			Server.general_log.write(line);
			Server.general_log.newLine();
			Server.general_log.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
