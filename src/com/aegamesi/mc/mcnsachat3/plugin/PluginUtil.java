package com.aegamesi.mc.mcnsachat3.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PluginUtil {
	public static MCNSAChat3 plugin = null;

	public static String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String formatUser(String user) {
		return color(MCNSAChat3.permissions.getUser(user).getPrefix() + user);
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

	public static void sendLater(final String message) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				send(message);
			}
		}, 0L);
	}
}
