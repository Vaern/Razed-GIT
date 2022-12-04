package com.vaer.razed.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenRuins extends WorldGenerator {
	
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		
		int nodes = 0;
		final int maxNodes = rand.nextInt(7) + 1;
		
		final int borderX = (x - 8) / 16 * 16;
		final int borderZ = (z - 8) / 16 * 16;
		boolean movedX = rand.nextBoolean();
		
		int xN = x;
		int zN = z;
		
		while(nodes < maxNodes) {
			final int distance = rand.nextInt(8);
			
			if(movedX) {
				if(rand.nextBoolean() && zN + distance <= borderZ + 31) zN += distance;
				else if(zN - distance >= borderZ + 1) zN -= distance;
				else continue;
				
				movedX = false;
			} else {
				if(rand.nextBoolean() && xN + distance <= borderX + 31) xN += distance;
				else if(xN - distance >= borderX + 1) xN -= distance;
				else continue;
				
				movedX = true;
			}
			
			if(rand.nextInt(3) == 0) placePoint(world, rand, xN, y, zN);
			else placeLine(world, rand, Math.min(x, xN), Math.max(x, xN), Math.min(z, zN), Math.max(z, zN), y);
			
			x = xN;
			z = zN;
			nodes++;
		}
		
		return true;
	}
	
	private void placePoint(World world, Random rand, int posX, int posY, int posZ) {
		placeRubble(world, rand, posX, posY, posZ);
	}
	
	private void placeLine(World world, Random rand, int minX, int maxX, int minZ, int maxZ, int posY) {
		
		for(int x = minX; x <= maxX; x++) {
			for(int z = minZ; z <= maxZ; z++) {
				placeRubble(world, rand, x, posY, z);
			}
		}
	}
	
	private void placeRubble(World world, Random rand, int posX, int posY, int posZ) {
		Block block = selectBlock(rand, true);
		int meta = selectMeta(rand, block);
		if(world.getBlock(posX, posY - 1, posZ).isSideSolid(world, posX, posY - 1, posZ, ForgeDirection.UP) && world.isAirBlock(posX, posY + 1, posZ))
			world.setBlock(posX, posY, posZ, block, meta, 2);
		
		for(int i = 0; i < 2; i++) {
			final int x = posX + rand.nextInt(3) - 1;
			final int y = posY + rand.nextInt(2);
			final int z = posZ + rand.nextInt(3) - 1;
			if(world.getBlock(x, y - 1, z).isSideSolid(world, x, y - 1, z, ForgeDirection.UP) && world.isAirBlock(x, y + 1, z)) {
				block = selectBlock(rand, (x == posX || z == posZ) && y == posY && rand.nextInt(4) > 0);
				meta = selectMeta(rand, block);
				
				world.setBlock(x, y, z, block, meta, 2);
			}
		}
	}
	
	//probably want a nested class for these two
	private Block selectBlock(Random rand, boolean solidBlock) { //TODO: make this into a config too
		Block block = Blocks.cobblestone;
		
		if(solidBlock) {
			if(rand.nextBoolean()) block = Blocks.mossy_cobblestone;
		} else {
			block = Blocks.stone_slab;
		}
		
		return block;
	}
	
	private int selectMeta(Random rand, Block block) { // ditto
		int meta = 0;
		
		if(block == Blocks.stone_slab) meta = 3;
		
		return meta;
	}
}
