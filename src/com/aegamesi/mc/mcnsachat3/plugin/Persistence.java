package com.aegamesi.mc.mcnsachat3.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Persistence {
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	private MCNSAChat3 plugin = null;

	public Persistence(MCNSAChat3 plugin) {
		this.plugin = plugin;
	}

	public void reload() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "persistence.yml");
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource("persistence.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			customConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration get() {
		if (customConfig == null) {
			this.reload();
		}
		return customConfig;
	}

	public void save() {
		if (customConfig == null || customConfigFile == null) {
			return;
		}
		try {
			get().save(customConfigFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
		}
	}

	public void saveDefault() {
		if (customConfigFile == null)
			reload();
		if (!customConfigFile.exists()) {
			this.plugin.saveResource("persistence.yml", false);
		}
	}
}
