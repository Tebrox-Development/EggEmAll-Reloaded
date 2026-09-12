package dev.shadmage.eggemall2.GUI;

import dev.shadmage.eggemall2.EggEmAllPlugin;
import dev.shadmage.eggemall2.Settings.Settings;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompEntityType;
import org.mineacademy.fo.remain.CompMaterial;

public class EggEmAllGUI extends Menu {

	private final Button catchableEntitiesButton;
	private final Button blacklistedEntitiesButton;

	public EggEmAllGUI() {
		setTitle(Settings.GUI.MAIN_TITLE);
		setSize(9 * 5);

		this.catchableEntitiesButton = new ButtonMenu(
				new CatchableEntitiesMenu(),
				CompMaterial.ENDERMAN_SPAWN_EGG,
				"Catchable Entities List",
				"",
				"Click to see all",
				"catchable entities");

		this.blacklistedEntitiesButton = new ButtonMenu(
				new BlacklistedEntitiesMenu(),
				CompMaterial.BARRIER,
				"Blacklisted Entities List",
				"",
				"Click to see all",
				"blacklisted entities");
	}

	@Override
	public ItemStack getItemAt(int slot) {
		if (slot == ((9 * 2) + 3))
			return catchableEntitiesButton.getItem();
		if (slot == ((9 * 2) + 5))
			return blacklistedEntitiesButton.getItem();
		return null;
	}

	@Override
	protected String[] getInfo() {
		return new String[]{
				"EggEmAll allows you to 'capture'",
				"animals/mobs by throwing chicken",
				"eggs at them"
		};
	}

	private final class CatchableEntitiesMenu extends MenuPagged<EntityType> {
		private CatchableEntitiesMenu() {
			super(9 * 5, EggEmAllGUI.this, EggEmAllPlugin.catchableMobs.getCatchableEntitiesList());
			setTitle(Settings.GUI.CATCHABLE_ENTITIES_TITLE);
		}

		@Override
		protected ItemStack convertToItemStack(EntityType entityType) {
			return ItemCreator.ofEgg(entityType, ItemUtil.bountifyCapitalized(entityType)).make();
		}

		@Override
		protected void onPageClick(Player player, EntityType entityType, ClickType clickType) {
			//do nothing
		}

		@Override
		protected String[] getInfo() {
			return new String[]{
					"List of all catchable entities"
			};
		}
	}

	private final class BlacklistedEntitiesMenu extends MenuPagged<EntityType> {
		private BlacklistedEntitiesMenu() {
			super(9 * 5, EggEmAllGUI.this, EggEmAllPlugin.catchableMobs.getBlacklistedEntitiesList());
			setTitle(Settings.GUI.BLACKLISTED_ENTITIES_TITLE);
		}

		@Override
		protected ItemStack convertToItemStack(EntityType entityType) {
			String name = ItemUtil.bountifyCapitalized(entityType);

			if(CompEntityType.getSpawnEgg(entityType) != null) {
				return ItemCreator.ofEgg(entityType, name).make();
			}

			return ItemCreator.of(CompMaterial.BARRIER).name(name).make();
		}

		@Override
		protected void onPageClick(Player player, EntityType entityType, ClickType clickType) {
			//do nothing
		}

		@Override
		protected String[] getInfo() {
			return new String[]{
					"List of all blacklisted entities"
			};
		}
	}
}
