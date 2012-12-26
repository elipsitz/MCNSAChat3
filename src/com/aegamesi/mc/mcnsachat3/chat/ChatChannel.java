package com.aegamesi.mc.mcnsachat3.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ChatChannel {
	public String name;
	public String read_permission;
	public String write_permission;
	public ArrayList<Mode> modes;
	public String alias;
	public String color;
	
	public ChatChannel(String name) {
		this.name = name;
		this.modes = new ArrayList<Mode>();
		this.read_permission = "";
		this.write_permission = "";
		this.alias = "";
		this.color = "";
	}

	public enum Mode {
		LOCAL, MUTE, RAVE, RANDOM, LOUD, BORING;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(read_permission);
		out.writeUTF(write_permission);
		out.writeUTF(alias);
		out.writeUTF(color);
		out.writeInt(modes.size());
		for(Mode mode : modes)
			out.writeUTF(mode.name());
	}
	
	public static ChatChannel read(DataInputStream in) throws IOException {
		String name = in.readUTF();
		String read_permission = in.readUTF();
		String write_permission = in.readUTF();
		String alias = in.readUTF();
		String color = in.readUTF();
		ArrayList<Mode> modes = new ArrayList<Mode>();
		int size = in.readInt();
		for(int i = 0; i < size; i++)
			modes.add(Mode.valueOf(in.readUTF()));
		
		ChatChannel chan = new ChatChannel(name);
		chan.modes = modes;
		chan.read_permission = read_permission;
		chan.write_permission = write_permission;
		chan.alias = alias;
		chan.color = color;
		return chan;
	}
}