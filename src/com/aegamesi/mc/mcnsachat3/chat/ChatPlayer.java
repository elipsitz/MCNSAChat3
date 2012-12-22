package com.aegamesi.mc.mcnsachat3.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.ChannelUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

public class ChatPlayer {
	public String name;
	public String server;
	public String channel;
	public String formatted;
	public ArrayList<String> listening;
	public ArrayList<Mode> modes;

	public ChatPlayer(String name, String server) {
		this(name, server, "", new ArrayList<String>());
		// XXX change this to defaults
	}

	@SuppressWarnings("unchecked")
	public ChatPlayer(String name, String server, String channel, ArrayList<String> listening) {
		this.name = name;
		this.server = server;
		this.channel = channel;
		this.listening = (ArrayList<String>) listening.clone();
		this.formatted = name;
		this.modes = new ArrayList<Mode>();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(formatted);
		out.writeUTF(server);
		out.writeUTF(channel);
		out.writeInt(listening.size());
		for (String listen : listening)
			out.writeUTF(listen);
		out.writeInt(modes.size());
		for (Mode mode : modes)
			out.writeUTF(mode.name());
	}

	public static ChatPlayer read(DataInputStream in) throws IOException {
		String name = in.readUTF();
		String formatted = in.readUTF();
		String server = in.readUTF();
		String channel = in.readUTF();
		ArrayList<String> listening = new ArrayList<String>();
		int size = in.readInt();
		for (int i = 0; i < size; i++)
			listening.add(in.readUTF());
		ArrayList<Mode> modes = new ArrayList<Mode>();
		size = in.readInt();
		for (int i = 0; i < size; i++)
			modes.add(Mode.valueOf(in.readUTF()));

		ChatPlayer p = new ChatPlayer(name, server, channel, listening);
		p.formatted = formatted;
		p.modes = modes;
		return p;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof ChatPlayer))
			return false;
		ChatPlayer p = (ChatPlayer) o;
		return p.name.equals(name) && p.server.equals(server);
	}

	public int hashCode() {
		return (name + "|" + server).hashCode();
	}

	public void changeChannels(String newChannel) {
		if (listening.contains(channel))
			listening.remove(channel);
		channel = newChannel;
		listening.add(channel);

		// create it if it doesn't exist
		ChatChannel chan = ChannelManager.getChannel(channel);
		if (chan == null) {
			chan = new ChatChannel(channel);
			ChannelManager.channels.add(chan);
			if (MCNSAChat3.thread != null)
				MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));
		}

		// welcome them
		PluginUtil.sendLater(name, "Welcome to channel " + chan.color + chan.name + "!");
		ArrayList<String> names = new ArrayList<String>();
		for (ChatPlayer p : PlayerManager.getPlayersInChannel(chan.name))
			names.add(p.name);
		PluginUtil.sendLater(name, "Players here: " + PluginUtil.formatPlayerList((String[]) names.toArray()));
	}

	public enum Mode {
		SEEALL, MUTE, LOCKED, POOFED;
	}
}
