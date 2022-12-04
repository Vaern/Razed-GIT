package com.vaer.razed.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vaer.razed.References;
import com.vaer.razed.config.ConfigHandler;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class ConfigGUIFactory implements IModGuiFactory {
	
	@Override
	public void initialize(Minecraft minecraftInstance) { }
	
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() { return ConfigGUIScreen.class; }
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }
	
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) { return null; }
	
	public static class ConfigGUIScreen extends GuiConfig {
		
		public ConfigGUIScreen(GuiScreen parentScreen) {
			super(parentScreen, getConfigElements(), References.MODID, false, false, "Razed");
		}
		
		private static List<IConfigElement> getConfigElements() {
			
			List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.add(new DummyCategoryElement("razedCfg", "razedCfg", GeneralEntry.class));
			
			return list;
		}
		
		public static class GeneralEntry extends CategoryEntry {

			public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
				super(owningScreen, owningEntryList, configElement);
			}
			
			@Override
			protected GuiScreen buildChildScreen() {
				
				List<IConfigElement> list = new ArrayList<IConfigElement>();
				list.addAll((new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL))).getChildElements());
				
				return new GuiConfig(this.owningScreen, list, this.owningScreen.modID, Configuration.CATEGORY_GENERAL,
						false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
				
			}
		}
	}
	
}
