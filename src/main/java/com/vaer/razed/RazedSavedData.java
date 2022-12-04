package com.vaer.razed;

import com.vaer.razed.config.ConfigHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class RazedSavedData extends WorldSavedData {
	
	public final static String key = "RazedModData";
	
	public int time;
	
	public RazedSavedData(String key) {
		super(key);
	}
	
	public static RazedSavedData loadOrCreateData(World world) {
		RazedSavedData data = (RazedSavedData) world.perWorldStorage.loadData(RazedSavedData.class, key);
		
		if(data == null) {
			world.perWorldStorage.setData(key, new RazedSavedData(key));
			data = (RazedSavedData) world.perWorldStorage.loadData(RazedSavedData.class, key);
		}
		
		return data;
	}
	
	/** Gets the current phase of the apocalypse, represented as an integer for convenience. 0 for burn, 1 for regrow, 2 for overgrow. */
	public int getPhase() {
		if(time <= ConfigHandler.burnPhase) return 0;
		else if(time <= ConfigHandler.regrowPhase) return 1;
		else return 2;
	}
	
	/** Returns how close the current phase is to completion as a percent */
	public float getPhaseCompletion() {
		if(time <= ConfigHandler.burnPhase) return (float)time / (ConfigHandler.burnPhase + 1);
		else if(time <= ConfigHandler.regrowPhase) return ((float)time - ConfigHandler.burnPhase) / (ConfigHandler.regrowPhase + 1);
		else return ((float)time - ConfigHandler.burnPhase - ConfigHandler.regrowPhase) / (ConfigHandler.overgrowPhase + 1);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.time = nbt.getInteger("time");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("time", time);
	}
}
