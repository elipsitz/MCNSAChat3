package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.ChannelUpdatePacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "clisten", permission = "listen", usage = "<channel>", description = "toggles listening to a channel.")
public class CommandListen implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandListen(MCNSAChat3 plugin) {
		CommandListen.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1)
			return false;

		ChatPlayer cp = PlayerManager.getPlayer(player.getName(), plugin.name);
		ChatChannel chan = ChannelManager.getChannel(sArgs);
		String read_perm = chan == null ? "" : chan.read_permission;
		if (!read_perm.equals("") && !player.hasPermission("mcnsachat3.read." + read_perm)) {
			plugin.getLogger().info(player.getName() + " attempted to read channel " + sArgs + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}
		
		if (cp.listening.contains(sArgs.toLowerCase())) {
			cp.listening.remove(sArgs.toLowerCase());
			PluginUtil.send(player.getName(), "You are now no longer listening to channel " + chan.color + chan.name);
			return true;
		}
		cp.listening.add(sArgs.toLowerCase());

		if (chan == null) {
			chan = new ChatChannel(sArgs);
			ChannelManager.channels.add(chan);
			if (MCNSAChat3.thread != null)
				MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));
		}

		// welcome them
		PluginUtil.sendLater(cp.name, "You are now listening to channel " + chan.color + chan.name + "&f!");
		ArrayList<String> names = new ArrayList<String>();
		for (ChatPlayer p : PlayerManager.getPlayersInChannel(chan.name))
			names.add(p.name);
		PluginUtil.sendLater(cp.name, "Players here: " + PluginUtil.formatPlayerList(names.toArray(new String[0])));
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(cp));
		
		return true;
	}
}
