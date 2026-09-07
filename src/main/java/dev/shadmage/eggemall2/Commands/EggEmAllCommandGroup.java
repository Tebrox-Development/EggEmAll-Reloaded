package dev.shadmage.eggemall2.Commands;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;

@AutoRegister
public final class EggEmAllCommandGroup extends SimpleCommandGroup {
	@Override
	protected void registerSubcommands() {
		registerSubcommand(new GUICommand(this));
		registerSubcommand(new ReloadCommand(this));
		registerSubcommand(new MigrateCommand(this));
	}

	@Override
	protected String getCredits() {
		return "&bEggEmAll Reloaded &7- &dhttps://github.com/Tebrox-Development/EggEmAll-Reloaded";
	}
}
