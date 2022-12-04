package com.vaer.razed;

import com.vaer.razed.config.ConfigHandler;
import com.vaer.razed.world.RazedWorldGenerator;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = References.MODID, version = References.VERSION, guiFactory = References.GUI_FACTORY)
public class Razed {
	
	@Instance(References.MODID)
	public static Razed instance;
	
	public static RazedEventHandler commonHandler;
	
	@Metadata
	public static ModMetadata meta;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		commonHandler = new RazedEventHandler();
		FMLCommonHandler.instance().bus().register(commonHandler);
		MinecraftForge.EVENT_BUS.register(commonHandler);
		MinecraftForge.TERRAIN_GEN_BUS.register(commonHandler);
		
		ConfigHandler.init(event.getModConfigurationDirectory());
		FMLCommonHandler.instance().bus().register(new ConfigHandler());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
		GameRegistry.registerWorldGenerator(new RazedWorldGenerator(), Integer.MAX_VALUE);
	}
}
