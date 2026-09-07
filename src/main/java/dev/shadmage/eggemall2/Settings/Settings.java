package dev.shadmage.eggemall2.Settings;

import org.bukkit.entity.EntityType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.settings.SimpleSettings;

import java.util.List;

public final class Settings extends SimpleSettings {

	@Override
	protected boolean saveComments() {
		return true;
	}

	public static String LOG_PREFIX;
	public static String CHAT_PREFIX;

	private static void init() {
		setPathPrefix(null);
		LOG_PREFIX = getString("LogPrefix");
		CHAT_PREFIX = getString("ChatPrefix");
	}

	@Override
	protected int getConfigVersion() {
		return 1;
	}

	public static class BlacklistWorlds {
		public static Boolean AS_WHITELIST;
		public static List<String> WORLDS;

		private static void init() {
			setPathPrefix("BlacklistWorlds");
			AS_WHITELIST = getBoolean("AsWhitelist");
			WORLDS = getList("Worlds", String.class);
		}
	}

	public static class Particles {
		public static Boolean EGG_TRAILS;
		public static Boolean PLAYER_THROW_ONLY;
		public static Boolean EXPLOSION_ON_SUCCESS;
		public static Boolean SMOKE_ON_ESCAPE;

		private static void init() {
			setPathPrefix("Particles");
			EGG_TRAILS = getBoolean("EggTrails");
			PLAYER_THROW_ONLY = getBoolean("PlayerThrowOnly");
			EXPLOSION_ON_SUCCESS = getBoolean("ExplosionOnSuccess");
			SMOKE_ON_ESCAPE = getBoolean("SmokeOnEscape");
		}
	}

	public static class CatchChance {
		public static Integer CHANCE_PERCENTAGE;
		public static Boolean SPAWN_CHICKEN_ON_FAIL;
		public static Boolean REMOVE_ENTITY_ON_FAIL_CHANCE;
		public static Boolean ADD_LORE_TO_EGG;
		public static List<String> LORE_LINES;

		private static void init() {
			setPathPrefix("CatchChance");
			CHANCE_PERCENTAGE = Common.getOrDefault(getInteger("ChancePercentage"), 100);
			SPAWN_CHICKEN_ON_FAIL = getBoolean("SpawnChickenOnFail");
			REMOVE_ENTITY_ON_FAIL_CHANCE = getBoolean("RemoveEntityOnFail");
			ADD_LORE_TO_EGG = getBoolean("AddLoreToSpawnEgg");
			LORE_LINES = getList("Lore_Lines", String.class);
		}
	}

	public static class NBT {
		public static Boolean MAINTAIN_ENTITY_DATA;

		private static void init() {
			setPathPrefix("NBT");
			MAINTAIN_ENTITY_DATA = getBoolean("MaintainEntityDataValues");
		}
	}

	public static class Restrictions {
		public static Boolean ONLY_ALLOW_PLAYER_THROWN_EGGS;
		public static Boolean PREVENT_CATCHING_BABIES;
		public static Boolean PREVENT_CATCHING_TAMED;
		public static Boolean PREVENT_CATCHING_SHEARED_SHEEP;
		public static Boolean PREVENT_CATCHING_NAMED_ENTITIES;
		public static Boolean REQUIRE_PERMISSIONS;
		public static List<EntityType> BLACKLISTED_ENTITIES;

		private static void init() {
			setPathPrefix("Restrictions");
			ONLY_ALLOW_PLAYER_THROWN_EGGS = getBoolean("OnlyAllowPlayerThrownEggs");
			PREVENT_CATCHING_BABIES = getBoolean("PreventCatchingBabyEntities");
			PREVENT_CATCHING_TAMED = getBoolean("PreventCatchingTamedEntities");
			PREVENT_CATCHING_SHEARED_SHEEP = getBoolean("PreventCatchingShearedSheep");
			PREVENT_CATCHING_NAMED_ENTITIES = getBoolean("PreventCatchingNamedEntities");
			REQUIRE_PERMISSIONS = getBoolean("RequirePermissions");
			BLACKLISTED_ENTITIES = getList("EntityBlacklist", EntityType.class);
		}
	}

	public static class EntityInventories {
		public static Boolean ERASE_ENTITY_INVENTORY;

		private static void init() {
			setPathPrefix("EntityInventories");
			ERASE_ENTITY_INVENTORY = getBoolean("DeleteInventoryOnCatch");
		}
	}

	public static class General {
		public static Boolean STARTUP_CONSOLE_STATS;
		;

		private static void init() {
			setPathPrefix("General");
			STARTUP_CONSOLE_STATS = getBoolean("StartupConsoleStats");
		}
	}

	public static class GUI {
		public static String MAIN_TITLE;
		public static String CATCHABLE_ENTITIES_TITLE;
		public static String BLACKLISTED_ENTITIES_TITLE;

		private static void init() {
			setPathPrefix("GUI");
			MAIN_TITLE = getString("Title");
			CATCHABLE_ENTITIES_TITLE = getString("CatchableEntitiesTitle");
			BLACKLISTED_ENTITIES_TITLE = getString("BlacklistedEntitiesTitle");
		}
	}

	public static class Messages {
		public static String NO_PERMISSION;
		public static String CATCH_SUCCESS;
		public static String CATCH_FAILED_CHANCE;
		public static String NO_BABIES;
		public static String NO_TAMED;
		public static String NO_SHEARED_SHEEP;
		public static String NO_NAMED_ENTITIES;
		public static String BLACKLISTED_WORLD;
		public static String NOT_CATCHABLE;

		private static void init() {
			setPathPrefix("Messages");
			NO_PERMISSION = getString("NoPermissions");
			CATCH_SUCCESS = getString("CatchSuccess");
			CATCH_FAILED_CHANCE = getString("CatchFailedChance");
			NO_BABIES = getString("NoBabies");
			NO_TAMED = getString("NoTamed");
			NO_SHEARED_SHEEP = getString("NoShearedSheep");
			NO_NAMED_ENTITIES = getString("NoNamedEntities");
			BLACKLISTED_WORLD = getString("BlacklistedWorld");
			NOT_CATCHABLE = getString("NotCatchable");
		}
	}
}
