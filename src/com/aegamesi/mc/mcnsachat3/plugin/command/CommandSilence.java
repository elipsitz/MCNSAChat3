package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "silence", permission = "silence", usage = "<player>", description = "toggles a player's silenced status")
public class CommandSilence implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandSilence(MCNSAChat3 plugin) {
		CommandSilence.plugin = plugin;
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
		if(p.modes.contains(ChatPlayer.Mode.MUTE)) {
			PluginUtil.send(p.name, "You are no longer silenced.");
			p.modes.remove(ChatPlayer.Mode.MUTE);
		} else {
			PluginUtil.send(p.name, "You have been silenced.");
			p.modes.add(ChatPlayer.Mode.MUTE);
		}
		
		PluginUtil.send(player.getName(), "Toggled silenced " + PluginUtil.formatUser(p.name));
		
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerUpdatePacket(p));
		
		return true;
	}
}
