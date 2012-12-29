package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.io.IOException;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "creconnect", permission = "reconnect", description = "breaks the connection to the chat server in hopes that it will be restored later")
public class CommandReconnect implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandReconnect(MCNSAChat3 plugin) {
		CommandReconnect.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		if (MCNSAChat3.thread != null) {
			try {
				MCNSAChat3.thread.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PluginUtil.send(player.getName(), "Broke connection");
		return true;
	}
}
