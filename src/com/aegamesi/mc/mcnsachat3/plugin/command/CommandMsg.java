package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerPMPacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "msg", permission = "msg", usage = "<player> <message>", description = "sends a private message to a player")
public class CommandMsg implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandMsg(MCNSAChat3 plugin) {
		CommandMsg.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1) {
			return false;
		}
		
		String[] args = sArgs.split("\\s");
		if (args.length < 2) {
			return false;
		}
		String to = args[0];
		String message = "";
		for(int i = 1; i < args.length; i++)
			message += args[i] + " ";
		
		ChatPlayer from = PlayerManager.getPlayer(player.getName(), plugin.name);
		ArrayList<ChatPlayer> tos = PlayerManager.getPlayersByFuzzyName(to);
		if(tos.size() == 0) {
			PluginUtil.send(from.name, "&cPlayer not found");
			return true;
		}
		ArrayList<String> uniques = new ArrayList<String>();
		for(ChatPlayer tooo : tos) {
			if(!uniques.contains(tooo.name))
				uniques.add(tooo.name);
		}
		if(uniques.size() > 1) {
			String matches = "";
			for(String match : uniques)
				matches += match + " ";
			PluginUtil.send(from.name, matches);
			return true;
		}
		to = uniques.get(0);
		
		plugin.chat.pm_send(from, to, message);
		if(Bukkit.getPlayerExact(to) != null)
			plugin.chat.pm_receive(from, to, message);
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerPMPacket(from, to, message));
		
		return true;
	}
}
