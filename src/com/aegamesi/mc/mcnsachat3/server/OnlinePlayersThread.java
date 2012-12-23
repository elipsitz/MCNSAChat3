package com.aegamesi.mc.mcnsachat3.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;
import com.aegamesi.mc.mcnsachat3.managers.PlayerManager;

public class OnlinePlayersThread extends Thread {
	public void run() {
		try {
			while (true) {
				File file = new File("online_players.json");
				file.delete();
				BufferedWriter w = new BufferedWriter(new FileWriter(file, true));

				String json = "{";
				for (ServerThread t : Server.threads) {
					String name = t.name;
					ArrayList<ChatPlayer> players = PlayerManager.getPlayersByServer(name);
					json += "\"" + name + "\": [";
					for (ChatPlayer player : players) {
						json += "\"" + player.formatted + "\",";
					}
					if (players.size() > 0)
						json = json.substring(0, json.length() - 1);
					json += "],";
				}
				if (Server.threads.size() > 0)
					json = json.substring(0, json.length() - 1);
				json += "}";

				w.write(json);
				w.flush();
				w.close();
				Thread.sleep(30 * 1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
