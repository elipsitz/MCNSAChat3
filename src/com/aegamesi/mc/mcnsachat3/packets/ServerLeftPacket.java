package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerLeftPacket implements IPacket {
	public static final short id = 2;

	public String shortName = null;
	
	public ServerLeftPacket() {
	}
	
	public ServerLeftPacket(String shortName) {
		this.shortName = shortName;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		out.writeUTF(shortName);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		shortName = in.readUTF();
	}
}
