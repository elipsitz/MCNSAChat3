package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "clist", permission = "list", description = "lists available occupied channels")
public class CommandClist implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandClist(MCNSAChat3 plugin) {
		CommandClist.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		String chans = "";
		for(ChatChannel chan : ChannelManager.channels) {
			String perm = chan.read_permission;
			boolean hasPerm = perm.equals("") || MCNSAChat3.permissions.has(player, "mcnsachat3.read." + perm);
			boolean chanOccupied = PlayerManager.getPlayersInChannel(chan.name).size() > 0 || chan.modes.contains(ChatChannel.Mode.PERSIST);
			if(hasPerm && chanOccupied) 
				chans += chan.color + chan.name + " ";
		}
		PluginUtil.send(player.getName(), "Channels: " + chans);
		return true;
	}
}
