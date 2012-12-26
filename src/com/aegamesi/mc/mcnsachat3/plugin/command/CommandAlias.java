package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.packets.ChannelUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "calias", permission = "alias", usage = "<channel> <alias>", description = "changes the alias of a channel")
public class CommandAlias implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandAlias(MCNSAChat3 plugin) {
		CommandAlias.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		String[] args = sArgs.split("\\s");
		if (sArgs.length() < 1)
			return false;
		ChatChannel chan = ChannelManager.getChannel(args[0]);

		if (chan == null) {
			PluginUtil.send(player.getName(), "&cChannel not found.");
			return true;
		}
		
		if(args[1].equals("null"))
			chan.alias = "";
		else
			chan.alias = args[1];

		PluginUtil.send(player.getName(), "Channel alias changed.");
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));

		return true;
	}
}
