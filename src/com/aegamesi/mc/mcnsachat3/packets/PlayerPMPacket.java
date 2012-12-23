package com.aegamesi.mc.mcnsachat3.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;

public class PlayerPMPacket implements IPacket {
	public static final short id = 10;

	public ChatPlayer from = null;
	public String to = null;
	public String message;
	
	public PlayerPMPacket() {
	}

	public PlayerPMPacket(ChatPlayer from, String to, String message) {
		this.from = from;
		this.to = to;
		this.message = message;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(id);
		from.write(out);
		out.writeUTF(to);
		out.writeUTF(message);
		out.flush();
	}

	public void read(DataInputStream in) throws IOException {
		from = ChatPlayer.read(in);
		to = in.readUTF();
		message = in.readUTF();
	}
}
