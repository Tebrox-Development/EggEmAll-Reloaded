package dev.shadmage.eggemall2.Model;

import dev.shadmage.eggemall2.Settings.PermissionData;
import dev.shadmage.eggemall2.Settings.Settings;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.EntityUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompEntityType;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class SpawnEggs {
	private final List<EntityType> CatchableEntities;

	public SpawnEggs() {
		CatchableEntities = Arrays.stream(EntityType.values()).filter(
				entityType ->
						entityType.isSpawnable()
							&& entityType.isAlive()
							&& CompEntityType.getSpawnEgg(entityType) != null
							&& !Settings.Restrictions.BLACKLISTED_ENTITIES.contains(entityType)
		).collect(Collectors.toList());
	}

	public boolean isCatchable(Entity entity) {
		return isCatchable(entity.getType());
	}

	public boolean isCatchable(EntityType entityType) {
		return CatchableEntities.contains(entityType);
	}

	public ItemStack getSpawnEgg(Entity entity) {
		EntityType entityType = entity.getType();

		if(isCatchable(entityType)) {
			return ItemCreator.ofEgg(entityType).make();
		}

		return ItemCreator.of(CompMaterial.EGG).make();
	}

	public String getCatchPermission(Entity entity) {
		if (entity instanceof AbstractVillager) {
			return PermissionData.CATCH_VILLAGERS;
		} else if (EntityUtil.isAggressive(entity)) {
			return PermissionData.CATCH_AGGRESSIVE;
		} else if (EntityUtil.isCreature(entity)) {
			return PermissionData.CATCH_PASSIVE;
		} else {
			return PermissionData.CATCH_UNKNOWN;
		}
	}

	public int countCatchableEntities() {
		return CatchableEntities.size();
	}

	public List<EntityType> getCatchableEntitiesList() {
		return CatchableEntities.stream()
				.sorted(Comparator.comparing(EntityType::name))
				.collect(Collectors.toList());
	}

	public List<EntityType> getBlacklistedEntitiesList() {
		return Settings.Restrictions.BLACKLISTED_ENTITIES.stream()
				.sorted(Comparator.comparing(EntityType::name))
				.collect(Collectors.toList());
	}
}