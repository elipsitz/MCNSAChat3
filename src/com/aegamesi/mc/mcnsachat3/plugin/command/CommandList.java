package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "list", permission = "", description = "lists everyone who is online")
public class CommandList implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandList(MCNSAChat3 plugin) {
		CommandList.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		PluginUtil.send(player.getName(), PluginUtil.getPlayerList());
		return true;
	}
}
