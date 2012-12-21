package com.aegamesi.mc.mcnsachat3.plugin;

import java.util.Arrays;
import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PluginUtil {
	public static MCNSAChat3 plugin = null;

	public static String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static String stripColor(String str) {
		return ChatColor.stripColor(color(str));
	}

	public static String formatUser(String user) {
		return MCNSAChat3.permissions.getUser(user).getPrefix() + user;
	}
	
	public static String formatRank(String user) {
		return color(MCNSAChat3.permissions.getUser(user).getPrefix() + MCNSAChat3.permissions.getUser(user).getSuffix());
	}

	public static void send(String who, String message) {
		if (message.length() <= 0)
			return;
		Player player = Bukkit.getPlayerExact(who);
		if (player != null)
			player.sendMessage(color(message));
	}

	public static void send(String message) {
		if (message.length() <= 0)
			return;
		Bukkit.broadcastMessage(color(message));
	}

	public static void sendLater(final String who, final String message) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				send(who, message);
			}
		}, 0L);
	}

	public static String getPlayerList() {
		Player[] online = Bukkit.getServer().getOnlinePlayers();
		Arrays.sort(online, new Comparator<Player>() {
			public int compare(Player a, Player b) {
				int ra = MCNSAChat3.permissions.getUser(a).getOptionInteger("rank", "", 9999);
				int rb = MCNSAChat3.permissions.getUser(b).getOptionInteger("rank", "", 9999);
				return (ra < rb ? 1 : (ra > rb ? -1 : 0));
			}
		});
		String out = "&7Online (" + online.length + "/" + Bukkit.getServer().getMaxPlayers() + "): ";
		for (int i = 0; i < online.length; i++)
			out += MCNSAChat3.permissions.getUser(online[i]).getPrefix() + online[i].getDisplayName() + (i < online.length - 1 ? "&7, " : "");
		return out;
	}

	public static void sendLater(final String message) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				send(message);
			}
		}, 0L);
	}
}
