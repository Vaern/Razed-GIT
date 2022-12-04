package com.vaer.razed.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenRandomFire extends WorldGenerator {
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if(!world.isAirBlock(x, y, z)) return false;
		
		Block block = world.getBlock(x, y - 1, z);
		
		if(block.isSideSolid(world, x, y - 1, z, ForgeDirection.UP) || block.isFlammable(world, x, y - 1, z, ForgeDirection.UP)) {
			world.setBlock(x, y, z, Blocks.fire, 0, 2);
			
			for(int i = 0; i < 4; i++) {
				x += rand.nextInt(3) - rand.nextInt(3);
				y += rand.nextInt(2) - rand.nextInt(2);
				z += rand.nextInt(3) - rand.nextInt(3);
				if(world.isAirBlock(x, y, z) && world.getBlock(x, y - 1, z).isFlammable(world, x, y - 1, z, ForgeDirection.UP))
					world.setBlock(x, y, z, Blocks.fire, 0, 2);
			}
		}
		
		return true;
	}
}
