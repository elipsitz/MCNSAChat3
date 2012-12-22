package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "csearch", permission = "search", usage = "<player>", description = "views the channel a player is currently in")
public class CommandSearch implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandSearch(MCNSAChat3 plugin) {
		CommandSearch.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1)
			return false;
	
		Player bukkitPlayer = Bukkit.getPlayer(sArgs);
		if(bukkitPlayer == null) {
			PluginUtil.send(player.getName(), "&cPlayer not found.");
			return true;
		}
		ChatPlayer p = PlayerManager.getPlayer(bukkitPlayer.getName(), plugin.name);
		ChatChannel chan = ChannelManager.getChannel(p.channel);
		
		PluginUtil.send(player.getName(), PluginUtil.formatUser(p.name) + "&f is in channel " + chan.color + chan.name);
		
		return true;
	}
}
