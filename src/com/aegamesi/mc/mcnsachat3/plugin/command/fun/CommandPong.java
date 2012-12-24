package com.aegamesi.mc.mcnsachat3.plugin.command.fun;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;
import com.aegamesi.mc.mcnsachat3.plugin.command.Command;

@Command.CommandInfo(alias = "pong", permission = "", usage = "", description = "")
public class CommandPong implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandPong(MCNSAChat3 plugin) {
		CommandPong.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		PluginUtil.send(player.getName(), "I hear " + player.getName() + " likes cute asian boys.");
		return true;
	}
}
