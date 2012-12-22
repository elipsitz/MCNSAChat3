package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "c", permission = "channel", usage = "<channel>", description = "switches to a channel.")
public class CommandChannel implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandChannel(MCNSAChat3 plugin) {
		CommandChannel.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1)
			return false;
	
		ChatPlayer cp = PlayerManager.getPlayer(player.getName(), plugin.name);
		if(cp.modes.contains(ChatPlayer.Mode.LOCKED)) {
			PluginUtil.send(cp.name, "You have been locked in your channel and may not change channels.");
			return true;
		}

		String read_perm = ChannelManager.getChannel(sArgs) == null ? "" : ChannelManager.getChannel(sArgs).read_permission;
		if (!read_perm.equals("") && !player.hasPermission("mcnsachat3.read." + read_perm)) {
			plugin.getLogger().info(player.getName() + " attempted to read channel " + sArgs + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		
		cp.changeChannels(sArgs);
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(cp));
		
		return true;
	}
}
