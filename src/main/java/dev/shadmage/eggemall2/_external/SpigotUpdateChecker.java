package dev.shadmage.eggemall2._external;

import lombok.NonNull;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class SpigotUpdateChecker {
	private static final String SPIGOT_DOWNLOAD_LINK = "https://www.spigotmc.org/resources/";

	private final SimplePlugin plugin;
	private final int resourceId;

	public SpigotUpdateChecker(@NonNull SimplePlugin plugin, @NonNull int resourceId) {
		this.plugin = plugin;
		this.resourceId = resourceId;
		this.CheckForUpdates();
	}

	private void getVersion(final Consumer<String> consumer) {
		Common.runAsync(() -> {
			try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scann = new Scanner(is)) {
				if (scann.hasNext()) {
					consumer.accept(scann.next());
				}
			} catch (IOException e) {
				plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
			}
		});
	}

	public void CheckForUpdates() {
		this.getVersion(version -> {
			String currentVersion = this.plugin.getDescription().getVersion();
			if (!currentVersion.equals(version)) {
				Common.logFramed("&6New update available for " + this.plugin.getName(),
						"&7 - You are running version: &c" + currentVersion,
						"&7 - Latest release: &a" + version,
						"",
						"&6Please update your plugin",
						"&7 - &e" + SPIGOT_DOWNLOAD_LINK + this.resourceId);
			} else {
				Common.log("&aYou are running the latest release.");
			}
		});
	}
}
