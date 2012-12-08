package com.aegamesi.mc.mcnsachat3;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.aegamesi.mc.mcnsachat3.packets.PlayerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerLeftPacket;
import com.aegamesi.mc.mcnsachat3.things.PlayerThing;

public final class MCNSAChat3 extends JavaPlugin implements Listener {
	public static ClientThread thread = null;
	public static String name;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		// getCommand("command").setExecutor(new CommandExecutor(this));
		
		name = getConfig().getString("name");

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
		thread.run = false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		thread.write(new PlayerJoinedPacket(new PlayerThing(evt.getPlayer().getName()), name));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent evt) {
		thread.write(new PlayerLeftPacket(new PlayerThing(evt.getPlayer().getName()), name));
	}
}