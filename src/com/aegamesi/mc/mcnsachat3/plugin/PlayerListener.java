package com.aegamesi.mc.mcnsachat3.plugin;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerLeftPacket;

public class PlayerListener implements Listener {
	public MCNSAChat3 plugin;

	public PlayerListener(MCNSAChat3 plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void loginHandler(PlayerLoginEvent evt) {
		// see if they are allowed to login
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR)
	public void joinHandler(PlayerJoinEvent evt) {
		evt.setJoinMessage("");

		ChatPlayer p = new ChatPlayer(evt.getPlayer().getName(), plugin.name);
		// load data for the player, if it exists
		ConfigurationSection playerData = MCNSAChat3.persist.get().getConfigurationSection("players");
		if (playerData.contains(p.name)) {
			ConfigurationSection section = playerData.getConfigurationSection(p.name);
			p.channel = section.getString("channel");
			p.listening.addAll((List<String>) section.get("listening"));
		} else {
			// use default info
			p.channel = plugin.getConfig().getString("default-channel");
			p.listening.addAll((List<String>) plugin.getConfig().getList("default-listen"));
		}
		PlayerManager.players.add(p);
		if (plugin.thread != null)
			plugin.thread.write(new PlayerJoinedPacket(p));
		// tell *everybody!*
		PluginUtil.send(PluginUtil.formatUser(evt.getPlayer().getName()) + " &ehas joined the game!");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void quitHandler(PlayerQuitEvent evt) {
		evt.setQuitMessage("");

		ChatPlayer p = PlayerManager.getPlayer(evt.getPlayer().getName(), plugin.name);
		PlayerManager.removePlayer(p);
		// persist
		String pre = "players." + p.name + ".";
		MCNSAChat3.persist.get().set(pre + "channel", p.channel);
		MCNSAChat3.persist.get().set(pre + "listening", p.listening);
		// network
		if (plugin.thread != null)
			plugin.thread.write(new PlayerLeftPacket(p));
		// tell *everybody!*
		PluginUtil.send(PluginUtil.formatUser(evt.getPlayer().getName()) + " &ehas left the game!");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void chatHandler(AsyncPlayerChatEvent evt) {
		if (evt.isCancelled())
			return;
		// evt.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void tabCompleteHandler(PlayerChatTabCompleteEvent evt) {
		if (evt.getChatMessage().startsWith("/") && evt.getChatMessage().indexOf(" ") < 0) {
			// it's a command
			return;
		} else {
			evt.getTabCompletions().clear();
			String token = evt.getLastToken().toLowerCase();
			for (ChatPlayer player : PlayerManager.players) {
				if (player.name.toLowerCase().startsWith(token))
					evt.getTabCompletions().add(player.name);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void preprocessHandler(PlayerCommandPreprocessEvent evt) {
		if (evt.isCancelled())
			return;

		// TODO handle commands
		/*
		 * if (false) plugin.commandManager.handleCommand(event.getPlayer(),
		 * event.getMessage())) evt.setCancelled(true);
		 */
	}
}
