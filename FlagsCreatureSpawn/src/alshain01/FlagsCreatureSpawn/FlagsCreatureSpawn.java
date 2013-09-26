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
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import alshain01.Flags.Flags;
import alshain01.Flags.ModuleYML;
import alshain01.Flags.Flag;
import alshain01.Flags.Registrar;
import alshain01.Flags.Director;

/**
 * Flags - Damage
 * Module that adds creature spawn flags to the plugin Flags.
 * 
 * @author Alshain01
 */
public class FlagsCreatureSpawn extends JavaPlugin {
	/**
	 * Called when this module is enabled
	 */
	@Override
	public void onEnable(){
		PluginManager pm =  Bukkit.getServer().getPluginManager();

		if(!pm.isPluginEnabled("Flags")) {
		    this.getLogger().severe("Flags was not found. Shutting down.");
		    pm.disablePlugin(this);
		}
		
		// Connect to the data file
		ModuleYML dataFile = new ModuleYML(this, "flags.yml");
		
		// Register with Flags
		Registrar flags = Flags.instance.getRegistrar();
		for(String f : dataFile.getModuleData().getConfigurationSection("Flag").getKeys(false)) {
			ConfigurationSection data = dataFile.getModuleData().getConfigurationSection("Flag." + f);
			
			// We don't want to register flags that aren't supported.
			// It would just muck up the help menu.
			// Null value is assumed to support all versions.
			String api = data.getString("MinimumAPI");  
			if(api != null && !Flags.instance.checkAPI(api)) { continue; }
			
			// The description that appears when using help commands.
			String desc = data.getString("Description");
			
			// Register it!  (All flags are defaulting to true in this module)
			// Be sure to send a plug-in name for the help command!
			// It can be this.getName() or another string.
			flags.register(f, desc, true, "CreatureSpawn");
		}
		
		// Load plug-in events and data
		Bukkit.getServer().getPluginManager().registerEvents(new CreatureSpawnListener(), this);
	}
	
	/*
	 * The event handler for the flags we created earlier
	 */
	private class CreatureSpawnListener implements Listener{
		@EventHandler(ignoreCancelled = true)
		private void onCreatureSpawn(CreatureSpawnEvent e){
			Flag flag = null;
			Registrar flags = Flags.instance.getRegistrar();
			
			if (e.getSpawnReason() == SpawnReason.NATURAL) {
				flag = flags.getFlag("SpawnMob");
			} else if (e.getSpawnReason() == SpawnReason.VILLAGE_INVASION) {
				flag = flags.getFlag("SpawnInvasion");
			} else if (e.getSpawnReason() == SpawnReason.EGG) {
				flag = flags.getFlag("SpawnEgg");
			} else if (e.getSpawnReason() == SpawnReason.JOCKEY) {
				flag = flags.getFlag("SpawnJockey");
			} else if (e.getSpawnReason() == SpawnReason.LIGHTNING) {
				flag = flags.getFlag("SpawnLightning");
			} else if (e.getSpawnReason() == SpawnReason.VILLAGE_DEFENSE) {
				flag = flags.getFlag("SpawnGolem");
			} else if (e.getSpawnReason() == SpawnReason.CUSTOM) {
				flag = flags.getFlag("SpawnByPlugin");
			} else if (e.getSpawnReason() == SpawnReason.CHUNK_GEN) {
				flag = flags.getFlag("SpawnChunk");
			} else if (e.getSpawnReason() == SpawnReason.SPAWNER) {
				flag = flags.getFlag("Spawner");
			} else if (e.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
				flag = flags.getFlag("SpawnerEgg");
			} else if (e.getSpawnReason() == SpawnReason.BUILD_IRONGOLEM) {
				flag = flags.getFlag("BuildGolem");
			} else if (e.getSpawnReason() == SpawnReason.BUILD_SNOWMAN) {
				flag = flags.getFlag("BuildSnowman");
			} else if (e.getSpawnReason() == SpawnReason.BREEDING) {
				if (e.getEntityType() == EntityType.VILLAGER) {
					flag = flags.getFlag("BreedVillager");
				}
			} else if (Flags.instance.checkAPI("1.2.5")
					&& e.getSpawnReason() == SpawnReason.SLIME_SPLIT) {
				flag = flags.getFlag("SlimeSplit");
			} else if(Flags.instance.checkAPI("1.4.5")
					&& e.getSpawnReason() == SpawnReason.BUILD_WITHER ) {
				flag = flags.getFlag("BuildWither");
			} else {
				flag = flags.getFlag("SpawnOther");
			}
			
			if (flag != null) { // Always guard this, even when it really can't happen. (In this case, BREEDING can cause null)
				e.setCancelled(!Director.getAreaAt(e.getLocation()).getValue(flag, false));
			}
		}
	}
}
