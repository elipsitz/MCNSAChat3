package com.aegamesi.mc.mcnsachat3.plugin.command;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.chat.ChatChannel;
import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.ChannelManager;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;
import com.aegamesi.mc.mcnsachat3.packets.PlayerChatPacket;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;

@Command.CommandInfo(alias = "me", permission = "", usage = "<action>", description = "emotes your message")
public class CommandMe implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandMe(MCNSAChat3 plugin) {
		CommandMe.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		if(sArgs.length() < 1) {
			return false;
		}
		
		ChatPlayer p = PlayerManager.getPlayer(player.getName(), plugin.name);
		// XXX blah blah check some stuff, like timeout maybe? are they allowed
		// to chat?
		plugin.chat.action(p, sArgs, null);
		// tell *everybody!*
		if (plugin.thread != null && !ChannelManager.getChannel(p.channel).modes.contains(ChatChannel.Mode.LOCAL))
			plugin.thread.write(new PlayerChatPacket(p, sArgs, null, PlayerChatPacket.Type.ACTION));
		
		return true;
	}
}
