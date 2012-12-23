package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VersionPacket implements IPacket {
	public static final short id = 9;
	public static final int CURRENT_VERSION = 1;

	public int version;
	
	public VersionPacket() {
	}
	
	public VersionPacket(int version) {
		this.version = version;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(version);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		version = in.readShort();
	}
}
