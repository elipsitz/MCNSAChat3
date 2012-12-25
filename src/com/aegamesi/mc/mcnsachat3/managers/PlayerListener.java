package com.aegamesi.mc.mcnsachat3.managers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.packets.PlayerChatPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerJoinedPacket;
import com.aegamesi.mc.mcnsachat3.packets.PlayerLeftPacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

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
		p.formatted = PluginUtil.formatUser(p.name);
		boolean welcomeThem = false;
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
			welcomeThem = true;
		}

		PlayerManager.players.add(p);
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerJoinedPacket(p, plugin.longname));
		// tell *everybody!*
		String joinString = plugin.getConfig().getString("strings.player-join");
		joinString = joinString.replaceAll("%prefix%", MCNSAChat3.permissions.getUser(evt.getPlayer()).getPrefix());
		joinString = joinString.replaceAll("%player%", evt.getPlayer().getName());
		joinString = joinString.replaceAll("%server%", plugin.longname);
		PluginUtil.send(joinString);
		if (welcomeThem) {
			String welcomeString = plugin.getConfig().getString("strings.player-welcome");
			welcomeString = welcomeString.replaceAll("%player%", evt.getPlayer().getName());
			for (Player player : Bukkit.getOnlinePlayers())
				if (!player.getName().equals(evt.getPlayer().getName()))
					PluginUtil.send(player.getName(), welcomeString);

		}

		// welcome them, send list of players, set colored name
		String result = PluginUtil.color(PluginUtil.formatUser(evt.getPlayer().getName()));
		if (result.length() > 16)
			result = result.substring(0, 16);
		evt.getPlayer().setPlayerListName(result);
		PluginUtil.send(evt.getPlayer().getName(), PluginUtil.getPlayerList());
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
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerLeftPacket(p, plugin.longname));
		// tell *everybody!*
		String quitString = plugin.getConfig().getString("strings.player-quit");
		quitString = quitString.replaceAll("%prefix%", MCNSAChat3.permissions.getUser(evt.getPlayer()).getPrefix());
		quitString = quitString.replaceAll("%player%", evt.getPlayer().getName());
		quitString = quitString.replaceAll("%server%", plugin.longname);
		PluginUtil.send(quitString);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void chatHandler(AsyncPlayerChatEvent evt) {
		if (evt.isCancelled())
			return;
		evt.setCancelled(true);
		ChatPlayer player = PlayerManager.getPlayer(evt.getPlayer().getName(), plugin.name);
		// XXX blah blah check some stuff, like timeout maybe? are they allowed
		// to chat?
		String write_perm = ChannelManager.getChannel(player.channel).write_permission;
		if (!write_perm.equals("") && !MCNSAChat3.permissions.has(evt.getPlayer(), "mcnsachat3.write." + write_perm)) {
			plugin.getLogger().info(player.name + " attempted to write to channel " + player.channel + " without permission!");
			PluginUtil.send(player.name, "&cYou don't have permission to do that!");
			return;
		}
		if(player.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(player.channel).modes.contains(ChatChannel.Mode.MUTE)) {
			PluginUtil.send(player.name, "You are not allowed to speak right now.");
			return;
		}
		plugin.chat.chat(player, evt.getMessage(), null);
		// tell *everybody!*
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new PlayerChatPacket(player, evt.getMessage(), null, PlayerChatPacket.Type.CHAT));
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

		if (plugin.command.handleCommand(evt.getPlayer(), evt.getMessage())) {
			evt.setCancelled(true);
			System.out.println("Intercepted command from " + evt.getPlayer().getName() + ": " + evt.getMessage());
		}
	}
}
