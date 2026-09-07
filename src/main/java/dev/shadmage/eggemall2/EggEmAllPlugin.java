package dev.shadmage.eggemall2;

import dev.shadmage.eggemall2.Model.SpawnEggs;
import dev.shadmage.eggemall2.Settings.Settings;
import dev.shadmage.eggemall2.Utils.LegacyMigration;
import dev.shadmage.eggemall2._external.PlaceholderAPI.PlaceholderAPISupport;
import dev.shadmage.eggemall2._external.SpigotUpdateChecker;
import dev.shadmage.eggemall2._external.StackingPlugins.RoseStackerSupport;
import dev.shadmage.eggemall2._external.StackingPlugins.StackingPluginAPI;
import dev.shadmage.eggemall2._external.StackingPlugins.UltimateStackerSupport;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.*;

public class EggEmAllPlugin extends SimplePlugin {
	public static final Set<UUID> thrownEggs = new HashSet<>();
	public static SpawnEggs catchableMobs;

	private StackingPluginAPI stackingPlugin;

	public static EggEmAllPlugin getInstance() {
		return (EggEmAllPlugin) SimplePlugin.getInstance();
	}

	public StackingPluginAPI getStackingPlugin() {
		return stackingPlugin;
	}

	@Override
	protected void onPluginStart() {
		new Metrics(this, 33887);
		new SpigotUpdateChecker(this, 138577);

		//set logging & chat prefixes
		Common.setLogPrefix(Settings.LOG_PREFIX);
		Common.setTellPrefix(Settings.CHAT_PREFIX);

		if (LegacyMigration.hasLegacySettings(this) && !LegacyMigration.hasMigrationMarker(this)) {
			Common.log("&eExisting EggEmAll2 settings detected at plugins/EggEmAll2/settings.yml.");
			Common.log("&eRun &f/eggemall migrate &eto import them safely into EggEmAll Reloaded.");
		}

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			if (PlaceholderAPISupport.registerExpansion(this)) {
				Common.log("&6PlaceholderAPI detected! &fRegistered EggEmAll Reloaded placeholders.");
			}
		}

		// Check for a supported stacking plugin
		RoseStackerSupport rsSupport = new RoseStackerSupport();
		if (rsSupport.isStackingPluginLoaded()) {
			stackingPlugin = rsSupport;
			Common.log("&6RoseStacker detected! &fEggEmAll Reloaded will work with RoseStacker!");
			return;
		}

		UltimateStackerSupport usSupport = new UltimateStackerSupport();
		if (usSupport.isStackingPluginLoaded()) {
			stackingPlugin = usSupport;
			Common.log("&6UltimateStacker detected! &fEggEmAll Reloaded will work with UltimateStacker!");
			return;
		}

		stackingPlugin = null;
	}

	@Override
	protected void onReloadablesStart() {
		catchableMobs = new SpawnEggs();
		if (Settings.General.STARTUP_CONSOLE_STATS) {
			//print plugin loaded summary to console
			Common.log("&6=============== &bEGGEMALL RELOADED &6===============");
			Common.log("&aCatchable Entities: &f" + catchableMobs.countCatchableEntities());
			Common.log("&cBlacklisted Entities: &f" + Settings.Restrictions.BLACKLISTED_ENTITIES.size());
			Common.log((Settings.BlacklistWorlds.AS_WHITELIST ? "&aWhitelisted" : "&cBlacklisted") + " Worlds: &f" + Settings.BlacklistWorlds.WORLDS.size());
			Common.log("&6======================================================");
		}
	}

}
