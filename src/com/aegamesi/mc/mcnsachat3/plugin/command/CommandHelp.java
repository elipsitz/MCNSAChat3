package com.aegamesi.mc.mcnsachat3.plugin.command;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.aegamesi.mc.mcnsachat3.managers.CommandManager.InternalCommand;
import com.aegamesi.mc.mcnsachat3.plugin.MCNSAChat3;
import com.aegamesi.mc.mcnsachat3.plugin.PluginUtil;

@Command.CommandInfo(alias = "chelp", usage = "<page #>", permission = "", description = "views the MCNSAChat3 help menus")
public class CommandHelp implements Command {
	public static MCNSAChat3 plugin = null;

	public CommandHelp(MCNSAChat3 plugin) {
		CommandHelp.plugin = plugin;
	}

	public Boolean handle(Player player, String sArgs) {
		sArgs = sArgs.trim();
		int page = 0;
		try {
			page = Integer.parseInt(sArgs) - 1;
		} catch (NumberFormatException e) {
		}
		InternalCommand[] commands = plugin.command.listCommands();
		ArrayList<InternalCommand> permCommands = new ArrayList<InternalCommand>();
		for (int i = 0; i < commands.length; i++)
			if (commands[i].permissions.equals("") || MCNSAChat3.permissions.has(player, commands[i].permissions))
				permCommands.add(commands[i]);

		int totalPages = permCommands.size() / 4;
		if (permCommands.size() % 4 != 0)
			totalPages++;
		page = Math.min(totalPages - 1, Math.max(0, page));

		int start = page * 4;
		int end = Math.min(start + 4, permCommands.size());
		PluginUtil.send(player.getName(), "&7--- &fMCNSAChat3 Help &7- &fPage &e" + (page + 1) + "&7/&e" + totalPages + " &f---");
		for (int i = start; i < end; i++) {
			PluginUtil.send(player.getName(), "&f/" + permCommands.get(i).alias + " &e" + permCommands.get(i).usage);
			PluginUtil.send(player.getName(), "    &7" + permCommands.get(i).description);
		}
		return true;
	}
}
