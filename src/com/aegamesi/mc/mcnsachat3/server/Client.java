package com.aegamesi.mc.mcnsachat3.server;

import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.things.PlayerThing;

public class Client {
	public String shortName;
	public ArrayList<PlayerThing> players;
	
	public Client(String shortName) {
		this.shortName = shortName;
	}
}
