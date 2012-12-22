package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.packets.ChannelUpdatePacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "cmode", permission = "mode", usage = "<channel> <mode changes>", description = "changes the modes of a channel. LOCAL, MUTE, RAVE")
public class CommandMode implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandMode(MCNSAChat3 plugin) {
		CommandMode.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		String[] args = sArgs.split("\\s");
		if (sArgs.length() < 1)
			return false;
		ChatChannel chan = ChannelManager.getChannel(args[0]);

		if (chan == null) {
			PluginUtil.send(player.getName(), "&cChannel not found.");
			return true;
		}

		if (args.length == 1) {
			String modeString = "Modes for channel " + chan.color + chan.name + "&7: ";
			for (ChatChannel.Mode mode : chan.modes)
				modeString += mode.name() + " ";
			PluginUtil.send(player.getName(), modeString);
			return true;
		}

		for (int i = 1; i < args.length; i++) {
			String arg = args[i];
			ChatChannel.Mode mode = null;
			try {
				mode = ChatChannel.Mode.valueOf(arg.substring(1).toUpperCase());
			} catch (IllegalArgumentException e) {
				continue;
			}
			if (arg.startsWith("+")) {
				if (!chan.modes.contains(mode))
					chan.modes.add(mode);
			}
			if (arg.startsWith("-")) {
				chan.modes.remove(mode);
			}
		}

		PluginUtil.send("Modes changed.");
		if (MCNSAChat3.thread != null)
			MCNSAChat3.thread.write(new ChannelUpdatePacket(chan));

		return true;
	}
}
