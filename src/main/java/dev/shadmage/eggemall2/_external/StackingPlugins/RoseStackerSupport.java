package dev.shadmage.eggemall2._external.StackingPlugins;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
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
		if(!(entity instanceof LivingEntity livingEntity)) {
			return false;
		}

		StackedEntity stackedEntity = rsAPI.getStackedEntity(livingEntity);

		if(stackedEntity == null || stackedEntity.getStackSize() <= 1) {
			return false;
		}

		StackedEntity seperatedEntity = stackedEntity.decreaseStackSize();

		if(seperatedEntity == null) {
			return false;
		}

		seperatedEntity.getEntity().remove();
		return true;
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
