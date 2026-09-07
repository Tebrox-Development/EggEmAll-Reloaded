package dev.shadmage.eggemall2.Utils;

import dev.shadmage.eggemall2._external.PlaceholderAPI.PlaceholderAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class ProcessPlaceholderMessages {

	private ProcessPlaceholderMessages() {
	}

	public static String ReplacePlaceholders(String msg, Entity entity) {
		return ReplacePlaceholders(msg, entity, null);
	}

	public static String ReplacePlaceholders(String msg, Entity entity, Player player) {
		if (msg == null) {
			return null;
		}

		if (entity != null) {
			msg = msg.replace("{entity}", entity.getName());

			String worldName = Objects.requireNonNull(entity.getLocation().getWorld().getName());

			if(worldName.startsWith("world_")) {
				worldName = worldName.substring("world_".length());
			}
			msg = msg.replace("{world}", worldName);
		}

		if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			msg = PlaceholderAPISupport.setPlaceholders(player, msg);
		}

		return msg;
	}
}
