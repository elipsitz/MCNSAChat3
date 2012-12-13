package com.aegamesi.mc.mcnsachat3.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ChatChannel {
	public String name;
	public ArrayList<Mode> modes;
	
	public ChatChannel(String name) {
		this.name = name;
		this.modes = new ArrayList<Mode>();
	}

	public enum Mode {
		LOCKDOWN, LOCAL, HIDDEN;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(name);
		out.writeInt(modes.size());
		for(Mode mode : modes)
			out.writeUTF(mode.name());
	}
	
	public static ChatChannel read(DataInputStream in) throws IOException {
		String name = in.readUTF();
		ArrayList<Mode> modes = new ArrayList<Mode>();
		int size = in.readInt();
		for(int i = 0; i < size; i++)
			modes.add(Mode.valueOf(in.readUTF()));
		
		ChatChannel chan = new ChatChannel(name);
		chan.modes = modes;
		return chan;
	}
}