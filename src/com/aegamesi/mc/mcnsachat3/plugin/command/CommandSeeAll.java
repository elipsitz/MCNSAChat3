package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "seeall", permission = "seeall", description = "toggles your seeall status")
public class CommandSeeAll implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandSeeAll(MCNSAChat3 plugin) {
		CommandSeeAll.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		ChatPlayer p = PlayerManager.getPlayer(player.getName(), plugin.name);
		if(p.modes.contains(ChatPlayer.Mode.SEEALL)) {
			PluginUtil.send(p.name, "You are no longer seeing all messages.");
			p.modes.remove(ChatPlayer.Mode.SEEALL);
		} else {
			PluginUtil.send(p.name, "You are now seeing all messages.");
			p.modes.add(ChatPlayer.Mode.SEEALL);
		}
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(p));
		
		return true;
	}
}
