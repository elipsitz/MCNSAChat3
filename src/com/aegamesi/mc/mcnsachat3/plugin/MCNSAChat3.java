package com.aegamesi.mc.mcnsachat3.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.plugin.managers.PlayerHandler;

public final class MCNSAChat3 extends JavaPlugin implements Listener {
	public ClientThread thread = null;
	public String name;

	public PlayerHandler pHandler;

	public static HashMap<String, ChatChannel> channels;
	public static ArrayList<ChatPlayer> players;

	public void onEnable() {
		saveDefaultConfig();

		name = getConfig().getString("name");
		pHandler = new PlayerHandler(this);

		channels = new HashMap<String, ChatChannel>();
		players = new ArrayList<ChatPlayer>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			ChatPlayer p = new ChatPlayer(player.getName(), name);
			MCNSAChat3.players.add(p);
		}

		// start connecting to server
		final MCNSAChat3 finalThis = this;
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (thread == null) {
					thread = new ClientThread(finalThis, finalThis.getLogger());
					thread.start();
				}
			}
		}, 0L, 200L);
	}

	public void onDisable() {
		getLogger().info("Disconnecting from chat server.");
		if (thread != null)
			thread.run = false;
	}
}