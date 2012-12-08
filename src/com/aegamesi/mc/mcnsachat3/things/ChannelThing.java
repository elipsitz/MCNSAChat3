package com.aegamesi.mc.mcnsachat3.things;

import java.util.ArrayList;

public class ChannelThing {
	public String name;
	public ArrayList<ChannelMode> modes;
	
	public ChannelThing(String name) {
		this.name = name;
		this.modes = new ArrayList<ChannelMode>();
	}

	public enum ChannelMode {
		LOCKDOWN, LOCAL, HIDDEN;
	}
}