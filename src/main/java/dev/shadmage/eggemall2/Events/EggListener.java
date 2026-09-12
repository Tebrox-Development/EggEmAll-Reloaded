package dev.shadmage.eggemall2.Events;

import dev.shadmage.eggemall2.CustomEvents.EntityCaptureEvent;
import dev.shadmage.eggemall2.CustomEvents.EntityEscapeCaptureEvent;
import dev.shadmage.eggemall2.EggEmAllPlugin;
import dev.shadmage.eggemall2.Settings.Settings;
import dev.shadmage.eggemall2.Utils.ProcessPlaceholderMessages;
import dev.shadmage.eggemall2._external.StackingPlugins.StackingPluginAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompParticle;

import java.util.*;

@AutoRegister
public final class EggListener implements Listener {
	private static final NamespacedKey EGGEMALL_ENTITY_DATA = new NamespacedKey(EggEmAllPlugin.getInstance(), "eggemall_entity_data");
	private final Set<UUID> handledEggInteractions = new HashSet<>();

	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		if(EggEmAllPlugin.thrownEggs.remove(event.getEgg().getUniqueId())) {
			event.setHatching(false);
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if(!(event.getEntity() instanceof Egg egg)) {
			return;
		}

		if(Settings.Particles.PLAYER_THROW_ONLY && !(egg.getShooter() instanceof Player)) {
			return;
		}

		if(!isCaptureAllowedInWorld(egg.getWorld().getName())) {
			return;
		}

		if(!Settings.Particles.EGG_TRAILS) {
			return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				if(!egg.isValid() || egg.isOnGround() || egg.isInWater()) {
					cancel();
					return;
				}

				CompParticle.SPELL_WITCH.spawn(egg.getLocation());
			}
		}.runTaskTimer(EggEmAllPlugin.getInstance(), 0, 1);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityHitByEgg(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent damageEvent))
			return;

		if (!(damageEvent.getDamager() instanceof Egg egg))
			return;

		Common.setTellPrefix(Settings.CHAT_PREFIX);
		Entity targetEntity = event.getEntity();

		EntityCaptureEvent entityCaptureEvent = new EntityCaptureEvent(targetEntity, egg);
		EntityEscapeCaptureEvent entityEscapeEvent = new EntityEscapeCaptureEvent(targetEntity, egg);

		if (!isCaptureAllowedInWorld(egg.getWorld().getName())) {
			if (!Settings.Messages.BLACKLISTED_WORLD.isEmpty() && egg.getShooter() instanceof Player player)
				Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.BLACKLISTED_WORLD, targetEntity, player));
			return;
		}

		if (!EggEmAllPlugin.catchableMobs.isCatchable(targetEntity)) {
			if (!Settings.Messages.NOT_CATCHABLE.isEmpty() && egg.getShooter() instanceof Player player)
				Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.NOT_CATCHABLE, targetEntity, player));
			return;
		}

		if (!Settings.CatchChance.SPAWN_CHICKEN_ON_FAIL)
			trackThrownEgg(egg);

		if (Settings.Restrictions.PREVENT_CATCHING_BABIES && targetEntity instanceof Ageable ageable && !ageable.isAdult()) {
			if (!Settings.Messages.NO_BABIES.isEmpty() && egg.getShooter() instanceof Player player)
				Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.NO_BABIES, targetEntity, player));
			return;
		}

		if (Settings.Restrictions.PREVENT_CATCHING_TAMED && targetEntity instanceof Tameable tameable && tameable.isTamed()) {
			if (!Settings.Messages.NO_TAMED.isEmpty() && egg.getShooter() instanceof Player player)
				Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.NO_TAMED, targetEntity, player));
			return;
		}

		if (Settings.Restrictions.PREVENT_CATCHING_SHEARED_SHEEP && targetEntity instanceof Sheep sheep && sheep.isSheared()) {
			if (!Settings.Messages.NO_SHEARED_SHEEP.isEmpty() && egg.getShooter() instanceof Player player)
				Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.NO_SHEARED_SHEEP, targetEntity, player));
			return;
		}

		if (Settings.Restrictions.PREVENT_CATCHING_NAMED_ENTITIES && targetEntity.customName() != null) {
			if (!Settings.Messages.NO_NAMED_ENTITIES.isEmpty() && egg.getShooter() instanceof Player player)
				Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.NO_NAMED_ENTITIES, targetEntity, player));
			return;
		}

		EggEmAllPlugin.getInstance().getServer().getPluginManager().callEvent(entityCaptureEvent);
		if (entityCaptureEvent.isCancelled())
			return;

		if (!(egg.getShooter() instanceof Player player)) {
			if (Settings.Restrictions.ONLY_ALLOW_PLAYER_THROWN_EGGS)
				return;
			if (!RandomUtil.chance(Settings.CatchChance.CHANCE_PERCENTAGE)) {
				EggEmAllPlugin.getInstance().getServer().getPluginManager().callEvent(entityEscapeEvent);
				return;
			}
		} else {
			if(Settings.Restrictions.REQUIRE_PERMISSIONS) {
				String groupPermission = EggEmAllPlugin.catchableMobs.getCatchPermission(targetEntity);
				String mobSpecificPermission = "eggemall.catchmob." + targetEntity.getType().name().toLowerCase(Locale.ROOT);
				String legacyMobSpecificPermission = "eggemall.catchmob." + targetEntity.getName();

				if(!(player.hasPermission(groupPermission)
					|| player.hasPermission(mobSpecificPermission)
					|| player.hasPermission(legacyMobSpecificPermission))) {

					if(!Settings.Messages.NO_PERMISSION.isEmpty()) {
						Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.NO_PERMISSION, targetEntity, player));
					}
					return;
				}
			}

			if (RandomUtil.chance(Settings.CatchChance.CHANCE_PERCENTAGE)) {
				if (!Settings.Messages.CATCH_SUCCESS.isEmpty())
					Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.CATCH_SUCCESS, targetEntity, player));
			} else {
				EggEmAllPlugin.getInstance().getServer().getPluginManager().callEvent(entityEscapeEvent);
				if (!Settings.Messages.CATCH_FAILED_CHANCE.isEmpty())
					Common.tell(player, ProcessPlaceholderMessages.ReplacePlaceholders(Settings.Messages.CATCH_FAILED_CHANCE, targetEntity, player));
				return;
			}
		}

		if (Settings.Particles.EXPLOSION_ON_SUCCESS) {
			CompParticle.EXPLOSION_LARGE.spawn(targetEntity.getLocation());
		}

		ItemStack eggStack = EggEmAllPlugin.catchableMobs.getSpawnEgg(targetEntity);

		if(targetEntity instanceof InventoryHolder inventoryHolder) {
			ItemStack[] items = inventoryHolder.getInventory().getContents();

			if(!Settings.EntityInventories.ERASE_ENTITY_INVENTORY) {
				for(ItemStack itemStack : items) {
					if(itemStack != null && !itemStack.getType().isAir()) {
						targetEntity.getWorld().dropItemNaturally(targetEntity.getLocation(), itemStack.clone());
					}
				}
			}

			inventoryHolder.getInventory().clear();
		}

		ItemMeta meta = eggStack.getItemMeta();
		if (meta != null) {
			if (egg.getShooter() instanceof Player player && Settings.CatchChance.ADD_LORE_TO_EGG) {
				List<Component> newLore = replacePlaceholders(Settings.CatchChance.LORE_LINES, targetEntity, player);
				meta.lore(newLore);
			}

			if (Settings.NBT.MAINTAIN_ENTITY_DATA) {
				EntitySnapshot entitySnapshot = targetEntity.createSnapshot();
				if (entitySnapshot != null) {
					if (meta instanceof SpawnEggMeta spawnEggMeta) {
						spawnEggMeta.setSpawnedEntity(entitySnapshot);
					} else {
						// Fallback for unusual egg implementations. New normal Paper spawn eggs use SpawnEggMeta.
						meta.getPersistentDataContainer().set(
								EGGEMALL_ENTITY_DATA,
								PersistentDataType.STRING,
								entitySnapshot.getAsString());
					}
				}
			}

			eggStack.setItemMeta(meta);
		}

		// Check if we have an active stacker plugin that we support
		StackingPluginAPI stackerPluginAPI = EggEmAllPlugin.getInstance().getStackingPlugin();
		if (!(stackerPluginAPI != null && stackerPluginAPI.isStackedEntity(targetEntity) && stackerPluginAPI.unstackEntity(targetEntity))) {
			targetEntity.remove();
		}

		targetEntity.getWorld().dropItem(targetEntity.getLocation(), eggStack);

		trackThrownEgg(egg);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onVillagerInteractWithEgg(PlayerInteractEntityEvent event) {
		handleEntityEggInteraction(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onVillagerInteractAtWithEgg(PlayerInteractAtEntityEvent event) {
		handleEntityEggInteraction(event);
	}

	private void handleEntityEggInteraction(PlayerInteractEntityEvent event) {
		if(event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		UUID playerId = player.getUniqueId();
		ItemStack item = player.getInventory().getItemInMainHand();

		if(handledEggInteractions.contains(playerId)) {
			event.setCancelled(true);
			return;
		}

		EntitySnapshot snapshot = getStoredEntitySnapshot(item);

		if(snapshot != null) {
			event.setCancelled(true);

			if(!handledEggInteractions.add(playerId)) {
				return;
			}

			Location spawnLocation = event.getRightClicked().getLocation().clone().add(0, 0.1, 0);
			snapshot.createEntity(spawnLocation);

			if(player.getGameMode() != GameMode.CREATIVE) {
				item.setAmount(item.getAmount() - 1);
			}

			clearHandledEggInteraction(playerId);
			return;
		}

		if(!(event.getRightClicked() instanceof AbstractVillager)) return;

		if(item.getType() != Material.EGG) return;

		event.setCancelled(true);

		if(!handledEggInteractions.add(playerId)) {
			return;
		}

		player.launchProjectile(Egg.class);

		if(player.getGameMode() != GameMode.CREATIVE) {
			item.setAmount(item.getAmount() - 1);
		}

		clearHandledEggInteraction(playerId);
	}

	private boolean isCaptureAllowedInWorld(String worldName) {
		boolean worldIsListed = Settings.BlacklistWorlds.WORLDS.contains(worldName);
		return Settings.BlacklistWorlds.AS_WHITELIST == worldIsListed;
	}

	private List<Component> replacePlaceholders(List<String> loreLines, Entity entity, Player player) {
		List<Component> newLore = new ArrayList<>();

		for (String line : loreLines) {
			line = line.replace("{entity_name}", entity.getName());
			line = line.replace("{entity}", ItemUtil.bountifyCapitalized(entity.getType().toString()));
			line = line.replace("{player}", player.getName());

			if (entity instanceof Villager villager && villager.getProfession() != Villager.Profession.NONE) {
				line = line.replace("{profession}", ItemUtil.bountifyCapitalized(villager.getProfession().getKey().getKey()));
			} else
				line = line.replace("{profession}", "");

			line = ProcessPlaceholderMessages.ReplacePlaceholders(line, entity, player);
			newLore.add(LegacyComponentSerializer.legacySection().deserialize(Common.colorize(line)));
		}

		return newLore;
	}

	private EntitySnapshot getStoredEntitySnapshot(ItemStack item) {
		ItemMeta meta = item.getItemMeta();

		if(meta == null) {
			return null;
		}

		if(meta instanceof SpawnEggMeta spawnEggMeta) {
			EntitySnapshot snapshot = spawnEggMeta.getSpawnedEntity();

			if(snapshot != null) {
				return snapshot;
			}
		}

		String snapshotString = meta.getPersistentDataContainer().get(EGGEMALL_ENTITY_DATA, PersistentDataType.STRING);

		if(snapshotString != null) {
			return Bukkit.getEntityFactory().createEntitySnapshot(snapshotString);
		}

		return null;
	}

	private void clearHandledEggInteraction(UUID playerId) {
		Bukkit.getScheduler().runTask(EggEmAllPlugin.getInstance(), () -> handledEggInteractions.remove(playerId));
	}

	private void trackThrownEgg(Egg egg) {
		if(!(egg.getShooter() instanceof Player)) {
			return;
		}

		UUID eggId = egg.getUniqueId();
		if(!EggEmAllPlugin.thrownEggs.add(eggId)) {
			return;
		}

		Bukkit.getScheduler().runTaskLater(
				EggEmAllPlugin.getInstance(),
				() -> EggEmAllPlugin.thrownEggs.remove(eggId),
				400L
		);
	}

	@EventHandler
	public void onEntityEscapeCapture(EntityEscapeCaptureEvent event) {
		if(!Settings.CatchChance.REMOVE_ENTITY_ON_FAIL_CHANCE) {
			return;
		}

		Location location = event.getEntity().getLocation();
		event.getEntity().remove();

		if(Settings.Particles.SMOKE_ON_ESCAPE) {
			CompParticle.SMOKE_LARGE.spawn(location);
		}
	}

	@EventHandler
	public void itemuse(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null) {
			ItemStack item = e.getItem();
			if (CompMaterial.isMonsterEgg(item.getType()) && Settings.NBT.MAINTAIN_ENTITY_DATA) {
				ItemMeta meta = item.getItemMeta();
				if (meta != null && meta.getPersistentDataContainer().has(EGGEMALL_ENTITY_DATA, PersistentDataType.STRING)) {
					String snapshotString = meta.getPersistentDataContainer().get(EGGEMALL_ENTITY_DATA, PersistentDataType.STRING);
					EntitySnapshot snapshot = Bukkit.getEntityFactory().createEntitySnapshot(snapshotString);
					Location loc = e.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5);
					while (!CompMaterial.isAir(loc.getBlock()) && !CompMaterial.isAir(loc.getBlock().getRelative(BlockFace.UP)))
						loc = loc.add(0, 1, 0);
					snapshot.createEntity(loc);
					PlayerUtil.takeOnePiece(e.getPlayer(), item);
					e.setCancelled(true);
				}
			}
		}
	}
}
