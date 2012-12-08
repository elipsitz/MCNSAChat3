package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IPacket {
	public void read(DataInputStream in) throws IOException;
	public void write(DataOutputStream out) throws IOException;
}