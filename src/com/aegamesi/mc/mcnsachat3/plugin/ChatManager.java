package com.aegamesi.mc.mcnsachat3.plugin;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;

public class ChatManager {
	public MCNSAChat3 plugin;

	public ChatManager(MCNSAChat3 plugin) {
		this.plugin = plugin;
	}

	public void chat(ChatPlayer player, String line, String channel) {
		if (channel == null || channel.length() <= 0)
			channel = PlayerManager.getPlayer(player).channel;
		if (MCNSAChat3.permissions.getUser(player.name).has("mcnsachat3.user.cancolor"))
			line = PluginUtil.color(line);
		else
			line = PluginUtil.stripColor(line);
		String message = "{&7" + player.server + "&f} <" + channel + "&f> [" + PluginUtil.formatRank(player.name) + "&f] " + player.name + ": &7" + line;

		ChatChannel chan = ChannelManager.getChannel(channel);
		if (chan == null)
			return;
		boolean net = !(player.server.equals(plugin.name));
		ArrayList<ChatPlayer> players = PlayerManager.getPlayersListeningToChannel(chan.name);
		for (ChatPlayer p : players) {
			boolean send = false;
			if(net)
				send = Bukkit.getPlayerExact(p.name) != null && !p.server.equals(player.server);
			else
				send = Bukkit.getPlayerExact(p.name) != null && p.server.equals(player.server);
			if (send) {
				PluginUtil.sendLater(p.name, message);
			}
		}
		Bukkit.getConsoleSender().sendMessage(PluginUtil.color(message));
	}
}
