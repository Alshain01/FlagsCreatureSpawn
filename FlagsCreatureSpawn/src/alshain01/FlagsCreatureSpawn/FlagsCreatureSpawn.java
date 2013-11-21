/* Copyright 2013 Kevin Seiden. All rights reserved.

 This works is licensed under the Creative Commons Attribution-NonCommercial 3.0

 You are Free to:
    to Share — to copy, distribute and transmit the work
    to Remix — to adapt the work

 Under the following conditions:
    Attribution — You must attribute the work in the manner specified by the author (but not in any way that suggests that they endorse you or your use of the work).
    Non-commercial — You may not use this work for commercial purposes.

 With the understanding that:
    Waiver — Any of the above conditions can be waived if you get permission from the copyright holder.
    Public Domain — Where the work or any of its elements is in the public domain under applicable law, that status is in no way affected by the license.
    Other Rights — In no way are any of the following rights affected by the license:
        Your fair dealing or fair use rights, or other applicable copyright exceptions and limitations;
        The author's moral rights;
        Rights other persons may have either in the work itself or in how the work is used, such as publicity or privacy rights.

 Notice — For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page.
 http://creativecommons.org/licenses/by-nc/3.0/
 */

package alshain01.FlagsCreatureSpawn;

import io.github.alshain01.Flags.Flag;
import io.github.alshain01.Flags.Flags;
import io.github.alshain01.Flags.ModuleYML;
import io.github.alshain01.Flags.Registrar;
import io.github.alshain01.Flags.area.Area;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Flags - Creature Spawn Module that adds creature spawn flags to the plugin Flags.
 * 
 * @author Alshain01
 */
public class FlagsCreatureSpawn extends JavaPlugin {
	/**
	 * Called when this module is enabled
	 */
	@Override
	public void onEnable() {
		final PluginManager pm = Bukkit.getServer().getPluginManager();

		if (!pm.isPluginEnabled("Flags")) {
			getLogger().severe("Flags was not found. Shutting down.");
			pm.disablePlugin(this);
		}

		// Connect to the data file and register the flags
		Flags.getRegistrar().register(new ModuleYML(this, "flags.yml"), "CreatureSpawn");

		// Load plug-in events and data
		Bukkit.getServer().getPluginManager()
				.registerEvents(new CreatureSpawnListener(), this);
	}
	
	/*
	 * The event handler for the flags we created earlier
	 */
	private class CreatureSpawnListener implements Listener {
		@EventHandler(ignoreCancelled = true)
		private void onCreatureSpawn(CreatureSpawnEvent e) {
			Flag flag = null;
			final Registrar flags = Flags.getRegistrar();

			switch (e.getSpawnReason()) {
			case NATURAL:
				flag = flags.getFlag("SpawnMob");
				break;
			case VILLAGE_INVASION:
				flag = flags.getFlag("SpawnInvasion");
				break;
			case EGG:
				flag = flags.getFlag("SpawnEgg");
				break;
			case JOCKEY:
				flag = flags.getFlag("SpawnJockey");
				break;
			case LIGHTNING:
				flag = flags.getFlag("SpawnLightning");
				break;
			case VILLAGE_DEFENSE:
				flag = flags.getFlag("SpawnGolem");
				break;
			case CUSTOM:
				flag = flags.getFlag("SpawnByPlugin");
				break;
			case CHUNK_GEN:
				flag = flags.getFlag("SpawnChunk");
				break;
			case SPAWNER:
				flag = flags.getFlag("Spawner");
				break;
			case SPAWNER_EGG:
				flag = flags.getFlag("SpawnerEgg");
				break;
			case BUILD_IRONGOLEM:
				flag = flags.getFlag("BuildGolem");
				break;
			case BUILD_SNOWMAN:
				flag = flags.getFlag("BuildSnowman");
				break;
			case BREEDING:
				if (e.getEntityType() == EntityType.VILLAGER) {
					flag = flags.getFlag("BreedVillager");
				}
				break;
			default:
				// Can't switch on API versions, will cause errors.
				if (Flags.checkAPI("1.2.5")
						&& e.getSpawnReason() == SpawnReason.SLIME_SPLIT) {
					flag = flags.getFlag("SlimeSplit");
				} else if (Flags.checkAPI("1.4.5")
						&& e.getSpawnReason() == SpawnReason.BUILD_WITHER) {
					flag = flags.getFlag("BuildWither");
				} else if (Flags.checkAPI("1.6.2")
						&& e.getSpawnReason() == SpawnReason.REINFORCEMENTS) {
					flag = flags.getFlag("SpawnReinforcements");
				} else {
					flag = flags.getFlag("SpawnOther");
				}
			}

			// Always guard this, even when it really can't happen.
			// (In this case, BREEDING can cause null)
			if (flag != null) {
				e.setCancelled(!Area.getAt(e.getLocation()).getValue(
						flag, false));
			}
		}
	}
}
