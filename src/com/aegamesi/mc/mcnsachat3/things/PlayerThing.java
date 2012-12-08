package com.aegamesi.mc.mcnsachat3.things;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerThing {
	public String name;
	
	public PlayerThing(String name) {
		this.name = name;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(name);
	}
	
	public static PlayerThing read(DataInputStream in) throws IOException {
		String name = in.readUTF();
		return new PlayerThing(name);
	}
}
