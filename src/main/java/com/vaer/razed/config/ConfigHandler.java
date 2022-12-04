package com.vaer.razed.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vaer.razed.References;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration config;
	
	public static int burnPhase; //TODO: make these able to be disabled, even if it's hacky
	public static int regrowPhase;
	public static int overgrowPhase;
	
	public static int maxTime;
	
	public static int timeStart; //TODO make this work
	public static boolean enableTimeIncrease;
	
	public static int firesPerChunk;
	public static int ruinsRarity;
	public static boolean enableBlockSubstitutions;
	
	public static void init(File directory) {
		
		if(config == null) {
			File newConfig = new File(directory, References.MODID + ".cfg");
			
			config = new Configuration(newConfig);
			load();
		}
	}
	
	private static void load() {
		
		List<String> propOrder = new ArrayList<String>();
		
		//TODO: make these use proper properties, like how Forge does it
		//TODO: add config for loot tables, block substitutions, and rubble blocks. might need to use something funky like JSON or figure out a different approach
		burnPhase = config.getInt("Burn Phase", Configuration.CATEGORY_GENERAL, 20 * 60 * 20, 0, Integer.MAX_VALUE, "Length of time the burn phase should last, in ticks. This phase is immediately post-apocalypse.");
		regrowPhase = config.getInt("Regrowth Phase", Configuration.CATEGORY_GENERAL, 20 * 60 * 20 * 10, 0, Integer.MAX_VALUE, "Length of time the regrowth phase should last, in ticks. The period of time where plants and life come back.");
		overgrowPhase = config.getInt("Overgrowth Phase", Configuration.CATEGORY_GENERAL, 20 * 60 * 20 * 30, 0, Integer.MAX_VALUE, "Length of time the overgrowth phase should last, in ticks. The phase where nature takes over the ruins of old.");
		propOrder.add("Burn Phase");
		propOrder.add("Regrowth Phase");
		propOrder.add("Overgrowth Phase");
		
		maxTime = burnPhase + regrowPhase + overgrowPhase; // used for convenience
		
		timeStart = config.getInt("Beginning Tick", Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE, "The world's progression in the apocalypse will start at this tick. Only applies for new worlds.");
		enableTimeIncrease = config.getBoolean("Enable Progression", Configuration.CATEGORY_GENERAL, true, "Enable the progression of the world through the apocalypse. Disabling will 'freeze' the world in its current phase.");
		propOrder.add("Beginning Tick");
		propOrder.add("Enable Progression");
		
		firesPerChunk = config.getInt("Fires Per Chunk", Configuration.CATEGORY_GENERAL, 10, 0, 256, "Number of randomly-generated fires during the burn phase. 0 to disable.");
		ruinsRarity = config.getInt("Ruins Rarity", Configuration.CATEGORY_GENERAL, 20, 0, Integer.MAX_VALUE, "Generated ruins every given number of chunks. 0 to disable, but 15 or higher recommended!");
		enableBlockSubstitutions = config.getBoolean("Enable Block Substitutions", Configuration.CATEGORY_GENERAL, true, "Enable the replacement of blocks such as leaves and grass. May have an effect on performance.");
		propOrder.add("Fires Per Chunk");
		propOrder.add("Ruins Rarity");
		propOrder.add("Enable Block Substitutions");
		
		config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrder);
		
		if(config.hasChanged())
			config.save();
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.modID.equalsIgnoreCase(References.MODID))
			load();
	}
}
