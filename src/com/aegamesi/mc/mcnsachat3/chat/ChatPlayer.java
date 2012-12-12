package com.aegamesi.mc.mcnsachat3.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ChatPlayer {
	public String name;
	public String server;
	public String channel;
	public ArrayList<String> listening;
	
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
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(server);
		out.writeUTF(channel);
		out.writeInt(listening.size());
		for(String listen : listening)
			out.writeUTF(listen);
	}
	
	public static ChatPlayer read(DataInputStream in) throws IOException {
		String name = in.readUTF();
		String server = in.readUTF();
		String channel = in.readUTF();
		ArrayList<String> listening = new ArrayList<String>();
		int size = in.readInt();
		for(int i = 0; i < size; i++)
			listening.add(in.readUTF());
		
		return new ChatPlayer(name, server, channel, listening);
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof ChatPlayer))
			return false;
		ChatPlayer p = (ChatPlayer) o;
		return p.name.equals(name) && p.server.equals(server);
	}
	
	public int hashCode() {
		return (name + "|" + server).hashCode();
	}
	
	public void changeChannels(String newChannel) {
		if(listening.contains(channel))
			listening.remove(channel);
		channel = newChannel;
		listening.add(channel);
	}
}
