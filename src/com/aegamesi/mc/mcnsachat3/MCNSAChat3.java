package com.aegamesi.mc.mcnsachat3;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCNSAChat3 extends JavaPlugin implements Listener {
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		//getCommand("command").setExecutor(new CommandExecutor(this));
	}

	public void onDisable() {
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
	}
}