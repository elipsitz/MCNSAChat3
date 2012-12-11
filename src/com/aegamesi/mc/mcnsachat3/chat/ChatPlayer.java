package com.aegamesi.mc.mcnsachat3.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatPlayer {
	public String name;
	public String server;
	
	public ChatPlayer(String name, String server) {
		this.name = name;
		this.server = server;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(server);
	}
	
	public static ChatPlayer read(DataInputStream in) throws IOException {
		String name = in.readUTF();
		String server = in.readUTF();
		return new ChatPlayer(name, server);
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
}
