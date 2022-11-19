package com.vaer.razed;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = Razed.MODID, version = Razed.VERSION)
public class Razed {
	
	public static final String MODID = "razed";
	public static final String VERSION = "0"; //TODO: figure out version scheme
	
	@Instance(Razed.MODID)
	public static Razed instance;
	
	public static RazedEventHandler commonHandler = new RazedEventHandler();
	
	@Metadata
	public static ModMetadata meta;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(commonHandler);
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
