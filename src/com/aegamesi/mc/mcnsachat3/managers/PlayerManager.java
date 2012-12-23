package com.aegamesi.mc.mcnsachat3.managers;

import java.util.ArrayList;

import com.aegamesi.mc.mcnsachat3.chat.ChatPlayer;

public class PlayerManager {
	public static ArrayList<ChatPlayer> players;

	public static void init() {
		players = new ArrayList<ChatPlayer>();
	}

	public static ChatPlayer getPlayer(String name, String server) {
		for (ChatPlayer play : players)
			if (play.name.equalsIgnoreCase(name) && play.server.equalsIgnoreCase(server))
				return play;
		return null;
	}

	public static ChatPlayer getPlayer(ChatPlayer play) {
		return getPlayer(play.name, play.server);
	}

	public static void removePlayer(String name, String server) {
		for (int i = 0; i < players.size(); i++) {
			ChatPlayer play = players.get(i);
			if (play.name.equalsIgnoreCase(name) && play.server.equalsIgnoreCase(server)) {
				players.remove(play);
				break;
			}
		}
	}

	public static void removePlayer(ChatPlayer play) {
		removePlayer(play.name, play.server);
	}

	public static ArrayList<ChatPlayer> getPlayersByName(String name) {
		ArrayList<ChatPlayer> plays = new ArrayList<ChatPlayer>();
		for (ChatPlayer play : players)
			if (play.name.equalsIgnoreCase(name))
				plays.add(play);
		return plays;
	}
	
	public static ArrayList<ChatPlayer> getPlayersByFuzzyName(String name) {
		ArrayList<ChatPlayer> plays = new ArrayList<ChatPlayer>();
		for (ChatPlayer play : players) {
			if (play.name.toLowerCase().startsWith(name.toLowerCase()))
				plays.add(play);
		}
		return plays;
	}
	
	public static ArrayList<ChatPlayer> getPlayersListeningToChannel(String channel) {
		ArrayList<ChatPlayer> plays = new ArrayList<ChatPlayer>();
		for (ChatPlayer play : players)
			if (play.listening.contains(channel.toLowerCase()) || play.modes.contains(ChatPlayer.Mode.SEEALL))
				plays.add(play);
		return plays;
	}
	
	public static ArrayList<ChatPlayer> getPlayersInChannel(String channel) {
		ArrayList<ChatPlayer> plays = new ArrayList<ChatPlayer>();
		for (ChatPlayer play : players)
			if (play.channel.equals(channel.toLowerCase()))
				plays.add(play);
		return plays;
	}

	public static ArrayList<ChatPlayer> getPlayersByServer(String server) {
		ArrayList<ChatPlayer> plays = new ArrayList<ChatPlayer>();
		for (ChatPlayer play : players)
			if (play.server.equalsIgnoreCase(server))
				plays.add(play);
		return plays;
	}
}
