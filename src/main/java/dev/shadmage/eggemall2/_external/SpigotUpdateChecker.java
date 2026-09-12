package dev.shadmage.eggemall2._external;

import lombok.NonNull;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Consumer;

public class SpigotUpdateChecker {
	private static final String SPIGOT_DOWNLOAD_LINK = "https://www.spigotmc.org/resources/";

	private final SimplePlugin plugin;
	private final int resourceId;

	public SpigotUpdateChecker(@NonNull SimplePlugin plugin, int resourceId) {
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
			int comparison = compareVersion(version, currentVersion);

			if (comparison > 0) {
				Common.logFramed("&6New update available for " + this.plugin.getName(),
						"&7 - You are running version: &c" + currentVersion,
						"&7 - Latest release: &a" + version,
						"",
						"&6Please update your plugin",
						"&7 - &e" + SPIGOT_DOWNLOAD_LINK + this.resourceId);

			} else if(comparison == 0) {
				Common.log("&aYou are running the latest release.");
			} else {
				Common.logFramed("&aYou are running a newer version than the latest release reported by SpigotMC.",
						"&7 - You are running version: &a" + currentVersion,
						"&7 - Latest release: &a" + version);
			}
		});
	}

	private int compareVersion(String first, String second) {
		Version firstVersion = parseVersion(first);
		Version secondVersion = parseVersion(second);

		int maxLength = Math.max(firstVersion.parts.length, secondVersion.parts.length);

		for(int i = 0; i < maxLength; i++) {
			int firstPart = i < firstVersion.parts.length ? firstVersion.parts[i] : 0;
			int secondPart = i < secondVersion.parts.length ? secondVersion.parts[i] : 0;

			if(firstPart != secondPart) {
				return Integer.compare(firstPart, secondPart);
			}
		}

		if(firstVersion.snapshot != secondVersion.snapshot) {
			return firstVersion.snapshot ? -1 : 1;
		}

		return 0;
	}

	private Version parseVersion(String version) {
		String normalized = version.trim();

		if(normalized.startsWith("v") || normalized.startsWith("V")) {
			normalized = normalized.substring(1);
		}

		boolean snapshot = normalized.toUpperCase(Locale.ROOT).endsWith("-SNAPSHOT");

		if(snapshot) {
			normalized = normalized.substring(0, normalized.length() - "-SNAPSHOT".length());
		}

		String[] split = normalized.split("\\.");
		int[] parts = new int[split.length];

		for(int i = 0; i < split.length; i++) {
			try {
				parts[i] = Integer.parseInt(split[i]);
			}catch(NumberFormatException ignored) {
				parts[i] = 0;
			}
		}

		return new Version(parts, snapshot);
	}

	private record Version(int[] parts, boolean snapshot) {
	}
}
