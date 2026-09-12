package dev.shadmage.eggemall2._external.PlaceholderAPI;

import dev.shadmage.eggemall2.EggEmAllPlugin;
import dev.shadmage.eggemall2.Settings.Settings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class EggEmAllPlaceholderExpansion extends PlaceholderExpansion {

	private final EggEmAllPlugin plugin;

	public EggEmAllPlaceholderExpansion(EggEmAllPlugin plugin) {
		this.plugin = plugin;
	}

	@NotNull
	@Override
	public String getIdentifier() {
		return "eggemall";
	}

	@NotNull
	@Override
	public String getAuthor() {
		return "Tebrox-Development";
	}

	@NotNull
	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String onRequest(OfflinePlayer player, String params) {
		String identifier = params.toLowerCase(Locale.ROOT);

		return switch (identifier) {
			case "version" -> getVersion();
			case "catch_chance" -> String.valueOf(Settings.CatchChance.CHANCE_PERCENTAGE);
			case "world_mode" -> Boolean.TRUE.equals(Settings.BlacklistWorlds.AS_WHITELIST) ? "whitelist" : "blacklist";
			case "require_permissions" -> String.valueOf(Settings.Restrictions.REQUIRE_PERMISSIONS);
			case "catchable_entities" -> EggEmAllPlugin.catchableMobs == null
					? "0"
					: String.valueOf(EggEmAllPlugin.catchableMobs.countCatchableEntities());
			case "blacklisted_entities" -> Settings.Restrictions.BLACKLISTED_ENTITIES == null
					? "0"
					: String.valueOf(Settings.Restrictions.BLACKLISTED_ENTITIES.size());
			case "world_allowed" -> player instanceof Player onlinePlayer
					? String.valueOf(isCaptureAllowedInWorld(onlinePlayer.getWorld().getName()))
					: "";
			default -> null;
		};
	}

	private boolean isCaptureAllowedInWorld(String worldName) {
		boolean worldIsListed = Settings.BlacklistWorlds.WORLDS != null
				&& Settings.BlacklistWorlds.WORLDS.contains(worldName);
		return Boolean.TRUE.equals(Settings.BlacklistWorlds.AS_WHITELIST) == worldIsListed;
	}
}
