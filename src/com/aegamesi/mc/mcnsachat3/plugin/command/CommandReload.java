package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "creload", permission = "reload", description = "reloads config and persist")
public class CommandReload implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandReload(MCNSAChat3 plugin) {
		CommandReload.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		plugin.reloadConfig();
		PluginUtil.send(player.getName(), "Reloaded config.");
		
		return true;
	}
}
