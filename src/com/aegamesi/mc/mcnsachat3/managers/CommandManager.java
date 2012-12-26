package com.aegamesi.mc.mcnsachat3.managers;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.packets.PlayerChatPacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;
import com.aegamesi.mc.mcnsachat3.plugin.command.Command;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandAlias;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandChannel;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandColor;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandHelp;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandList;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandListen;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandLock;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandMe;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandMode;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandMove;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandMsg;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandName;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandR;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandRanks;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandReload;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandSearch;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandSeeAll;
import com.aegamesi.mc.mcnsachat3.plugin.command.CommandSilence;
import com.aegamesi.mc.mcnsachat3.plugin.command.fun.CommandDicks;
import com.aegamesi.mc.mcnsachat3.plugin.command.fun.CommandPong;

public class CommandManager {
	public MCNSAChat3 plugin = null;
	public HashMap<String, InternalCommand> commands = new HashMap<String, InternalCommand>();
	public HashMap<String, String> aliases = new HashMap<String, String>();

	public CommandManager(MCNSAChat3 plugin) {
		this.plugin = plugin;
		registerCommand(new CommandList(plugin));
		registerCommand(new CommandRanks(plugin));
		registerCommand(new CommandMe(plugin));
		registerCommand(new CommandSeeAll(plugin));
		registerCommand(new CommandSilence(plugin));
		registerCommand(new CommandChannel(plugin));
		registerCommand(new CommandListen(plugin));
		registerCommand(new CommandHelp(plugin));
		registerCommand(new CommandMove(plugin));
		registerCommand(new CommandLock(plugin));
		registerCommand(new CommandMode(plugin));
		registerCommand(new CommandColor(plugin));
		registerCommand(new CommandSearch(plugin));
		registerCommand(new CommandMsg(plugin));
		registerCommand(new CommandR(plugin));
		registerCommand(new CommandReload(plugin));
		registerCommand(new CommandName(plugin));
		registerCommand(new CommandAlias(plugin));
		
		// "fun" commands
		registerCommand(new CommandDicks(plugin));
		registerCommand(new CommandPong(plugin));
	}

	public void registerCommand(Command command) {
		Class<? extends Command> cls = command.getClass();
		Annotation[] annotations = cls.getAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i] instanceof Command.CommandInfo) {
				Command.CommandInfo ci = (Command.CommandInfo) annotations[i];
				commands.put(ci.alias(), new InternalCommand(ci.alias(), ci.permission(), ci.usage(), ci.description(), ci.visible(), command));
				return;
			}
		}
	}

	public Boolean handleCommand(Player player, String command) {
		command = command.substring(1);
		String[] tokens = command.split("\\s");
		if (tokens.length < 1)
			return false;
		tokens[0] = tokens[0].toLowerCase();

		if (aliases.containsKey(tokens[0])) {
			String args = new String("");
			if (command.length() > 1 + tokens[0].length())
				args = command.substring(1 + tokens[0].length());
			handleAlias(player, tokens[0], args);
			return true;
		}
		if (!commands.containsKey(tokens[0]))
			return false;
		if (!commands.get(tokens[0]).permissions.equals("") && !MCNSAChat3.permissions.has(player, "mcnsachat3.command." + commands.get(tokens[0]).permissions)) {
			plugin.getLogger().info(player.getName() + " attempted to use command: " + tokens[0] + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return true;
		}

		String sArgs = new String("");
		if (command.length() > (1 + tokens[0].length()))
			sArgs = command.substring(1 + tokens[0].length());
		if (commands.get(tokens[0]).command.handle(player, sArgs))
			return true;

		PluginUtil.send(player.getName(), "&cInvalid usage! &aCorrect usage: &6/" + commands.get(tokens[0]).alias + " &e" + commands.get(tokens[0]).usage + " &7(" + commands.get(tokens[0]).description + ")");
		return true;
	}

	private void handleAlias(Player player, String alias, String message) {
		ChatPlayer cp = PlayerManager.getPlayer(player.getName(), plugin.name);
		if (cp.modes.contains(ChatPlayer.Mode.LOCKED)) {
			PluginUtil.send(cp.name, "You have been locked in your channel and may not change channels.");
			return;
		}

		String channel = aliases.get(alias);
		String read_perm = ChannelManager.getChannel(channel).read_permission;
		if (!read_perm.equals("") && !MCNSAChat3.permissions.has(player, "mcnsachat3.read." + read_perm)) {
			plugin.getLogger().info(player.getName() + " attempted to read channel " + channel + " without permission!");
			PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
			return;
		}

		if (!message.trim().equals("")) {
			// send a message rather than changing
			String write_perm = ChannelManager.getChannel(channel).write_permission;
			if (!write_perm.equals("") && !MCNSAChat3.permissions.has(player, "mcnsachat3.write." + write_perm)) {
				plugin.getLogger().info(player.getName() + " attempted to write to channel " + channel + " without permission!");
				PluginUtil.send(player.getName(), "&cYou don't have permission to do that!");
				return;
			}
			if (cp.modes.contains(ChatPlayer.Mode.MUTE) || ChannelManager.getChannel(channel).modes.contains(ChatChannel.Mode.MUTE)) {
				PluginUtil.send(cp.name, "You are not allowed to speak right now.");
				return;
			}
			plugin.chat.chat(cp, message, channel);
			if (MCNSAChat3.thread != null)
				MCNSAChat3.thread.write(new PlayerChatPacket(cp, message, channel, PlayerChatPacket.Type.CHAT));
			return;
		}

		cp.changeChannels(channel.toLowerCase());
	}

	public InternalCommand[] listCommands() {
		int numInvisible = 0;
		for (String cmd : commands.keySet())
			if (!commands.get(cmd).visible)
				numInvisible++;
		InternalCommand[] list = new InternalCommand[commands.size() - numInvisible];
		int i = 0;
		for (String cmd : commands.keySet()) {
			if (commands.get(cmd).visible) {
				list[i] = commands.get(cmd);
				i += 1;
			}
		}
		Arrays.sort(list, new CommandComp());
		return list;
	}

	public class InternalCommand {
		public String alias = new String("");
		public String permissions = new String("");
		public String usage = new String("");
		public String description = new String("");
		public Boolean visible = new Boolean(true);
		public Command command = null;

		public InternalCommand(String _alias, String _perms, String _usage, String _desc, boolean _visible, Command _command) {
			alias = _alias;
			permissions = _perms;
			usage = _usage;
			description = _desc;
			visible = _visible;
			command = _command;
		}
	}

	class CommandComp implements Comparator<InternalCommand> {
		public int compare(InternalCommand a, InternalCommand b) {
			return a.alias.compareTo(b.alias);
		}
	}
}
