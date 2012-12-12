package com.aegamesi.mc.mcnsachat3.managers;

import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;

public class ChannelManager {
	public static ArrayList<ChatChannel> channels;
	
	public static void init() {
		channels = new ArrayList<ChatChannel>();
	}
	
	public static ChatChannel getChannel(String name) {
		for(ChatChannel chan : channels)
			if(chan.name.equalsIgnoreCase(name))
				return chan;
		return null;
	}
}