package com.aegamesi.mc.mcnsachat3.chat;

import java.util.ArrayList;

public class ChatChannel {
	public String name;
	public ArrayList<ChannelMode> modes;
	
	public ChatChannel(String name) {
		this.name = name;
		this.modes = new ArrayList<ChannelMode>();
	}

	public enum ChannelMode {
		LOCKDOWN, LOCAL, HIDDEN;
	}
}