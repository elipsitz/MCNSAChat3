package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

public interface Command {
	public Boolean handle(Player player, String sArgs);
}
