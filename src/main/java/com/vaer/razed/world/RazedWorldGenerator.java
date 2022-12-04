package com.vaer.razed.world;

import java.util.Random;

import com.vaer.razed.RazedSavedData;
import com.vaer.razed.config.ConfigHandler;
import com.vaer.razed.world.gen.WorldGenRandomFire;
import com.vaer.razed.world.gen.WorldGenRuins;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockVine;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.IPlantable;

public class RazedWorldGenerator implements IWorldGenerator {
	
	public RazedSavedData data;
	
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		switch (world.provider.dimensionId) {
		case -1: break;
		case 0:
			generateSurface(world, rand, chunkGenerator, chunkProvider, chunkX, chunkZ); break;
		case 1: break;
		}
	}
	
	public WorldGenerator randomFire = new WorldGenRandomFire();
	public WorldGenerator randomRuins = new WorldGenRuins();
	
	private void generateSurface(World world, Random rand, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, int chunkX, int chunkZ) {
		data = data.loadOrCreateData(world);
		
		int baseX = chunkX << 4;
		int baseZ = chunkZ << 4;
		
		//TODO: add more-intact structure ruins with chests, as well as potentially more ambitiously-minded structures
		boolean doGen = rand.nextInt(15) == 0;
		if(doGen) {
			int x = baseX + rand.nextInt(16) + 8;
			int z = baseZ + rand.nextInt(16) + 8;
			if(!BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x, z), BiomeDictionary.Type.WATER))
				this.randomRuins.generate(world, rand, x, world.getTopSolidOrLiquidBlock(x, z), z);
		}
		
		// Annoyingly, the leaves of trees often pour over into other already-populated chunks. This normally isn't an issue, but here it means
		// that often leaves will be left floating. My solution was introducing offsets to make the replace checks occur over a 2x2
		// chunk, 16x16 area like how it's supposed to be, but that just moves the problem one step down into finding out which chunks have
		// been populated (overflow could occur). The below is significantly simpler and more readable, without affecting performance, even
		if(ConfigHandler.enableBlockSubstitutions) {
			for(int x = 0; x <= 1; x++) { // Testing found that most chunks take 4 ms or less, with the occasional 6 and sometimes 10 for mountains.
				for(int z = 0; z <= 1; z++) {
					if(chunkProvider.chunkExists(chunkX + x, chunkZ + z)) { // should exist, but it's better to be safe than sorry
						Chunk chunk = chunkProvider.provideChunk(chunkX + x, chunkZ + z);
						if(chunk.isTerrainPopulated) replaceBlocks(world, chunk, rand);
					}
				}
			}
		}
		
		if(ConfigHandler.firesPerChunk > 0 && data.getPhase() == 0) {
			int numFires = rand.nextInt(rand.nextInt(ConfigHandler.firesPerChunk) + 1) + 1;
			
			for(int i = 0; i < numFires; i++) { // config: FiresPerChunk or something
				int x = baseX + rand.nextInt(16) + 8;
				int z = baseZ + rand.nextInt(16) + 8;
				this.randomFire.generate(world, rand, x, world.getTopSolidOrLiquidBlock(x, z), z);
			}
		}
		
		/*for(int x = -1; x <= 1; x++) {
			for(int z = -1; z <= 1; z++) {
				if(x == 0 && z == 0) continue;
				
				if(chunkProvider.chunkExists(chunkX + x, chunkZ + z)) {
					Chunk chunk = chunkProvider.provideChunk(chunkX + x, chunkZ + z);
					if(!chunk.isTerrainPopulated) continue;
				
					switch(x + z) {
					case 2:
						replaceBlocks(world, chunkProvider.provideChunk(chunkX + 1, chunkZ + 1), 0, 0);
						break;
					case 1:
						replaceBlocks(world, chunkProvider.provideChunk(chunkX + 1, chunkZ + 1), z, x);
						replaceBlocks(world, chunkProvider.provideChunk(chunkX + x, chunkZ + z), 0, 0);
						break;
					case 0:
						final int offsetX = x < 0 ? 1 : 0; final int offsetZ = z < 0 ? 1 : 0;
						replaceBlocks(world, chunkProvider.provideChunk(chunkX + offsetZ, chunkZ + offsetX), offsetX, offsetZ);
						break;
					case -1:
						replaceBlocks(world, chunkProvider.provideChunk(chunkX, chunkZ), Math.abs(x), Math.abs(z));
						replaceBlocks(world, chunkProvider.provideChunk(chunkX - z, chunkZ - x), 1, 1);
						break;
					case -2:
						replaceBlocks(world, chunkProvider.provideChunk(chunkX, chunkZ), 1, 1);
						break;
					}
				}
			}
		}*/
	}
	
	public void replaceBlocks(World world, Chunk chunk, Random rand) {
		ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
		if(storage == null) return;
		
		final float percent = data.getPhaseCompletion();
		
		for(int i = 3; i < storage.length; i++) {
			ExtendedBlockStorage ext = storage[i];
			if(ext == null) continue;
			
			byte[] lsbIDs = ext.getBlockLSBArray();
			NibbleArray msbIDs = ext.getBlockMSBArray();
			final boolean flagMSB = msbIDs == null;
			
			for(int x = 0; x < 16; x++) {
				for(int z = 0; z < 16; z++) {
					BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(x, z, world.getWorldChunkManager());
					
					for(int y = 0; y < 16; y++) {
						int fullID = lsbIDs[y << 8 | z << 4 | x] & 255 | (flagMSB ? 0 : msbIDs.get(x, y, z) << 8);
						Block block = Block.getBlockById(fullID);
						// TODO: create a block substitution class that allows for more flexibility/cleaner code
						// probably use predicates for the type and chance, so instanceof checks work
						// BoP thankfully inherits this and others
						switch(data.getPhase()) {
						case 0:	burnPhaseSubstitute(ext, biome, block, x, y, z); break;
						case 1: regrowPhaseSubstitute(ext, biome, block, x, y, z, rand, percent); break;
						default: overgrowPhaseSubstitute(ext, biome, block, x, y, z, rand, percent);
						}
					}
				}
			}
		}
	}
	
	private void burnPhaseSubstitute(ExtendedBlockStorage ext, BiomeGenBase biome, Block block, int x, int y, int z) {
		
		if(block instanceof BlockGrass || block instanceof BlockFarmland) { 
			ext.func_150818_a(x, y, z, biome.fillerBlock instanceof BlockDirt ? biome.fillerBlock : Blocks.dirt); // even if it's not dirt it's close enough
			ext.setExtBlockMetadata(x, y, z, biome.field_150604_aj); // this seems like the metadata?
		} else
		
		if(block instanceof BlockLeavesBase || block instanceof IPlantable || block instanceof BlockVine) {
			ext.func_150818_a(x, y, z, Blocks.air);
			ext.setExtBlockMetadata(x, y, z, 0);
		} else
		
		if(block instanceof BlockSnow) {
			ext.func_150818_a(x, y, z, Blocks.air);
			ext.setExtBlockMetadata(x, y, z, 0);
		}
	}
	
	private void regrowPhaseSubstitute(ExtendedBlockStorage ext, BiomeGenBase biome, Block block, int x, int y, int z, Random rand, float percent) {
		
		if(block instanceof BlockGrass || block instanceof BlockFarmland) { 
			if(1 - (percent - 1) * (percent - 1) <= rand.nextFloat()) {
				ext.func_150818_a(x, y, z, biome.fillerBlock instanceof BlockDirt ? biome.fillerBlock : Blocks.dirt); // even if it's not dirt it's close enough
				ext.setExtBlockMetadata(x, y, z, biome.field_150604_aj); // this seems like the metadata?
			}
		}
		
	}
	
	private void overgrowPhaseSubstitute(ExtendedBlockStorage ext, BiomeGenBase biome, Block block, int x, int y, int z, Random rand, float percent) {
		/* placeholder */
		if(block == Blocks.cobblestone) {
			if(1 - (percent - 1) * (percent - 1) >= rand.nextFloat()) {
				ext.func_150818_a(x, y, z, Blocks.mossy_cobblestone);
			}
		} else
		
		if(block == Blocks.stonebrick && ext.getExtBlockMetadata(x, y, z) == 0) {
			if(1 - (percent - 1) * (percent - 1) >= rand.nextFloat()) {
				ext.setExtBlockMetadata(z, x, y, 1);
			}
		}
		
		/* Will definitely need a config for replacement blocks for this one */
	}
	
	/*public void replaceBlocks(World world, Chunk chunk, int offsetX, int offsetZ) {
		ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
		if(storage == null) return;
		
		for(int i = 3; i < storage.length; i++) {
			ExtendedBlockStorage ext = storage[i];
			if(ext == null) continue;
			
			byte[] lsbIDs = ext.getBlockLSBArray();
			NibbleArray msbIDs = ext.getBlockMSBArray();
			final boolean flagMSB = msbIDs == null;
			
			for(int x = 8 - 8 * offsetX; x < 16 - 8 * offsetX; x++) {
				for(int z = 8 - 8 * offsetZ; z < 16 - 8 * offsetZ; z++) {
					BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(x, z, world.getWorldChunkManager());
					final boolean flagDirt = biome.fillerBlock instanceof BlockDirt;
					
					for(int y = 0; y < 16; y++) {
						int fullID = lsbIDs[y << 8 | z << 4 | x] & 255 | (flagMSB ? 0 : msbIDs.get(x, y, z) << 8);
						Block block = Block.getBlockById(fullID);
						// TODO: create a block substiution class that allows for more flexibility/cleaner code
						// probably use predicates for the type and chance, so instanceof checks work
						// BoP thankfully inherits this and others
						if(block instanceof BlockGrass || block instanceof BlockFarmland) { 
							ext.func_150818_a(x, y, z, flagDirt ? biome.fillerBlock : Blocks.dirt); // even if it's not dirt it's close enough
							ext.setExtBlockMetadata(x, y, z, biome.field_150604_aj); // this seems like the metadata?
							continue;
						}
						
						if(block instanceof BlockLeavesBase || block instanceof IPlantable) {
							ext.func_150818_a(x, y, z, Blocks.air);
							ext.setExtBlockMetadata(x, y, z, 0);
							continue;
						}
					}
				}
			}
		}
	}*/
}
