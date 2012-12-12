package com.aegamesi.mc.mcnsachat3.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;

public final class MCNSAChat3 extends JavaPlugin implements Listener {
	public ClientThread thread = null;
	public String name;

	public PlayerListener pHandler;

	public void onEnable() {
		saveDefaultConfig();

		name = getConfig().getString("name");
		pHandler = new PlayerListener(this);
		PlayerManager.init();
		ChannelManager.init();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			ChatPlayer p = new ChatPlayer(player.getName(), name);
			PlayerManager.players.add(p);
			// XXX figure out a way to save channel state/positions
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