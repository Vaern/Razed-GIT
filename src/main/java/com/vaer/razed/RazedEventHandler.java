package com.vaer.razed;

import java.util.Random;

import com.vaer.razed.config.ConfigHandler;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class RazedEventHandler {
	
	public RazedSavedData data;
	//public ArrayList<Long> times = new ArrayList<Long>();
	public Random rand = new Random();
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		
		if(event.world != null && !event.world.isRemote && event.phase == event.phase.END) {
			data = data.loadOrCreateData(event.world);
			
			if(ConfigHandler.enableTimeIncrease && data.time < ConfigHandler.maxTime) {
				data.time++;
				data.markDirty();
			}
			
			/*if(event.world.getTotalWorldTime() % 5 > 0) return;
			int x = rand.nextInt(1000) + 100;
			int z = rand.nextInt(1000) + 100;
			if(!event.world.getChunkProvider().chunkExists(x, z)) {
				long time = System.currentTimeMillis();
				event.world.getChunkProvider().provideChunk(x, z);
				time = System.currentTimeMillis() - time;
				System.out.println("Time @ " + x + ", " + z + ": " + time + " ms");
				times.add(time);
			}
			
			if(event.world.getTotalWorldTime() % 300 == 0) {
				
				int total = 0;
				System.out.println("Chunks generated: " + times.size());
				for(long time : times) {
					total += time;
				}
				
				System.out.println("Average ms per chunk: " + ((double)total / times.size()));
				
				times.clear();
			}*/
		}
	}
	
	@SubscribeEvent
	public void onFeatureDecorate(DecorateBiomeEvent.Decorate event) {
		
		data = data.loadOrCreateData(event.world);
		final int phase = data.getPhase();
		EventType type = event.type;
		
		if(phase < 2) {
			final float percent = data.getPhaseCompletion();
			// hmmm, today i will make trees sparser
			// the roofed forest in question:
			if(type == EventType.TREE || type == EventType.BIG_SHROOM || type == EventType.CACTUS) {
				float chance;
				if(phase < 1) chance = percent * percent;
				else chance = 1 - percent * percent;
				
				if(rand.nextFloat() <= chance) event.setResult(Result.DENY);
			}
			
			else if(type == EventType.FLOWERS || type == EventType.GRASS || type == EventType.LILYPAD || type == EventType.PUMPKIN || type == EventType.REED) {
				if(phase < 1) {
					event.setResult(Result.DENY);
				} else if(percent < 0.5F) {
					final float chance = 4 * percent * percent; 
					
					if(rand.nextFloat() >= chance) event.setResult(Result.DENY);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntitySpawnCheck(LivingSpawnEvent.CheckSpawn event) {
		
		if(!event.world.isRemote && event.world.provider.dimensionId == 0) {
			data = data.loadOrCreateData(event.world);
			
			if(data.getPhase() < 2) {
				if(event.entityLiving instanceof IAnimals && !(event.entityLiving instanceof IMob)) {
					final float percent = data.getPhaseCompletion();
					
					float chance;
					if(data.getPhase() < 1) chance = percent * percent;
					else chance = 1 - percent * percent;
					
					if(rand.nextFloat() <= chance) event.setResult(Result.DENY);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onChunkPopulate(PopulateChunkEvent.Populate event) {
		
		if(!event.world.isRemote && event.world.provider.dimensionId == 0) {
			data = data.loadOrCreateData(event.world);
			
			if(data.getPhase() < 2 && event.type == PopulateChunkEvent.Populate.EventType.ANIMALS) {
				final float percent = data.getPhaseCompletion();
				
				float chance;
				if(data.getPhase() < 1) chance = percent * percent;
				else chance = 1 - percent * percent;
					
				if(rand.nextFloat() <= chance) event.setResult(Result.DENY);
			}
		}
	}
}
