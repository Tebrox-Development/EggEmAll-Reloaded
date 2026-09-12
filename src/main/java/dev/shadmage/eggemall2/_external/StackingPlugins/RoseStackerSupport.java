package dev.shadmage.eggemall2._external.StackingPlugins;

import dev.rosewood.rosestacker.RoseStacker;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.manager.StackManager;
import dev.rosewood.rosestacker.stack.StackedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.mineacademy.fo.Common;

public class RoseStackerSupport implements StackingPluginAPI {
	private RoseStackerAPI rsAPI;


	@Override
	public boolean isStackingPluginLoaded() {
		if (Common.doesPluginExist("RoseStacker")) {
			rsAPI = RoseStackerAPI.getInstance();
			return true;
		} else
			return false;
	}

	@Override
	public boolean unstackEntity(Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			StackedEntity stackedEntity = rsAPI.getStackedEntity(livingEntity);
			if (stackedEntity != null && stackedEntity.getStackSize() > 1) {
				StackManager stackManager = RoseStacker.getInstance().getManager(StackManager.class);
				stackedEntity.getDataStorage().pop();
				stackManager.updateStackedEntityKey(livingEntity, stackedEntity);
				stackedEntity.updateDisplay();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isStackedEntity(Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			StackedEntity stackedEntity = rsAPI.getStackedEntity(livingEntity);
			return stackedEntity != null;
		}
		return false;
	}
}
