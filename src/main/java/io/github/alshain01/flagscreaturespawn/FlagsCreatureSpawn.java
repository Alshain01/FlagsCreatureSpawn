/* Copyright 2013 Kevin Seiden. All rights reserved.

 This works is licensed under the Creative Commons Attribution-NonCommercial 3.0

 You are Free to:
    to Share: to copy, distribute and transmit the work
    to Remix: to adapt the work

 Under the following conditions:
    Attribution: You must attribute the work in the manner specified by the author (but not in any way that suggests that they endorse you or your use of the work).
    Non-commercial: You may not use this work for commercial purposes.

 With the understanding that:
    Waiver: Any of the above conditions can be waived if you get permission from the copyright holder.
    Public Domain: Where the work or any of its elements is in the public domain under applicable law, that status is in no way affected by the license.
    Other Rights: In no way are any of the following rights affected by the license:
        Your fair dealing or fair use rights, or other applicable copyright exceptions and limitations;
        The author's moral rights;
        Rights other persons may have either in the work itself or in how the work is used, such as publicity or privacy rights.

 Notice: For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page.
 http://creativecommons.org/licenses/by-nc/3.0/
 */
package io.github.alshain01.flagscreaturespawn;

import io.github.alshain01.flags.Flags;
import io.github.alshain01.flags.Flag;
import io.github.alshain01.flags.ModuleYML;
import io.github.alshain01.flags.CuboidType;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Flags Creature Spawn - Module that adds creature spawn flags to the plugin Flags.
 */
@SuppressWarnings("unused")
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
		Set<Flag> flags = Flags.getRegistrar().register(new ModuleYML(this, "flags.yml"), "CreatureSpawn");
        Map<String, Flag> flagMap = new HashMap<String, Flag>();
        for(Flag f : flags) {
            flagMap.put(f.getName(), f);
        }

		// Load plug-in events and data
		this.getServer().getPluginManager().registerEvents(new CreatureSpawnListener(flagMap), this);
	}
	
	/*
	 * The event handler for the flags we created earlier
	 */
	private class CreatureSpawnListener implements Listener {
        final Map<String, Flag> flags;
        final CuboidType system = CuboidType.getActive();

        private CreatureSpawnListener(Map<String, Flag> flags) {
            this.flags = flags;
        }

        @EventHandler(ignoreCancelled = true)
		private void onCreatureSpawn(CreatureSpawnEvent e) {
			Flag flag = null;

			switch (e.getSpawnReason()) {
			case NATURAL:
				flag = flags.get("SpawnMob");
				break;
			case VILLAGE_INVASION:
				flag = flags.get("SpawnInvasion");
				break;
			case EGG:
				flag = flags.get("SpawnEgg");
				break;
			case JOCKEY:
				flag = flags.get("SpawnJockey");
				break;
			case LIGHTNING:
				flag = flags.get("SpawnLightning");
				break;
			case VILLAGE_DEFENSE:
				flag = flags.get("SpawnGolem");
				break;
			case CUSTOM:
				flag = flags.get("SpawnByPlugin");
				break;
			case CHUNK_GEN:
				flag = flags.get("SpawnChunk");
				break;
            case SLIME_SPLIT:
                flag = flags.get("SlimeSplit");
                break;
			case SPAWNER:
				flag = flags.get("Spawner");
				break;
			case SPAWNER_EGG:
				flag = flags.get("SpawnerEgg");
				break;
			case BUILD_IRONGOLEM:
				flag = flags.get("BuildGolem");
				break;
			case BUILD_SNOWMAN:
				flag = flags.get("BuildSnowman");
				break;
            case BUILD_WITHER:
                flag = flags.get("BuildWither");
                break;
			case BREEDING:
				if (e.getEntityType() == EntityType.VILLAGER) {
					flag = flags.get("BreedVillager");
				}
				break;
			default:
				// Can't switch on API versions, will cause errors.
                SpawnReason reason = e.getSpawnReason();
				if (Flags.checkAPI("1.6.2") && reason == SpawnReason.REINFORCEMENTS) {
					flag = flags.get("SpawnReinforcements");
				} else {
					flag = flags.get("SpawnOther");
				}
			}

			// Always guard this, even when it really can't happen.
			// (In this case, BREEDING can cause null)
			if (flag != null) {
				e.setCancelled(!system.getAreaAt(e.getLocation()).getValue(flag, false));
			}
		}
	}
}
