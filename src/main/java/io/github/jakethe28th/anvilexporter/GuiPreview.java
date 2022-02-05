package io.github.jakethe28th.anvilexporter;

import java.awt.Color;
import java.awt.image.BufferedImage;

import io.github.jakethe28th.engine.graphics.Sprite;
import net.querz.nbt.*;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;

public class GuiPreview {
	
	public static final int CHUNK_SIZE = 16;
	public static final int HEIGHTMAP_INDEX_SIZE = 9;
	public static final int INDEXES_PER_LONG = 7;
	
	
	public static int genChunkPreview(LongArrayTag heightmaps, Sprite sheet) {
		
		int block_x = 0, block_z = 0;
		int i = 0;
		
		BufferedImage img_out = new BufferedImage(CHUNK_SIZE, CHUNK_SIZE, BufferedImage.TYPE_INT_RGB);
		
		if (heightmaps != null) {
		
		while (i < (16*16)) {
			
			int index = block_z * CHUNK_SIZE + block_x;
					
					int INDEXES_PER_LONG = (int) Math.floor(64/HEIGHTMAP_INDEX_SIZE); //The amount of indexes in a long value (64 bits).
					int long_index = (int) Math.floor(index / (INDEXES_PER_LONG)); //The index of the long that contains the block.
					
					int bits = HEIGHTMAP_INDEX_SIZE;
					int clean = (int) (Math.pow(2, bits) - 1);								
					int startbit = (int) Math.ceil((index % INDEXES_PER_LONG) * bits);	
					long y_level = heightmaps.getValue()[long_index] >> startbit & clean; //Get the block's Y level.
					
			//System.out.println(y_level);
			
			img_out.setRGB(block_x, block_z, Color.HSBtoRGB(.0f, .0f, (float) y_level/256));
			
			block_x+=1;
			 if (block_x == 16) {
				  block_x = 0;
				  block_z+=1;
				 }
			i+=1;
		}
		
		return sheet.addSprite(img_out);
		}
		
	return sheet.addSprite(img_out);
		
	}
	
	public static int genChunkPreviewNoMCA(Sprite sheet) {
		
		int block_x = 0, block_z = 0;
		int i = 0;
		
		BufferedImage img_out = new BufferedImage(CHUNK_SIZE, CHUNK_SIZE, BufferedImage.TYPE_INT_RGB);
		

		img_out.setRGB(block_x, block_z, Color.HSBtoRGB(.7f, 1f, (float) .5f));

		return sheet.addSprite(img_out);
		
	}
	
	public static int genChunkPreviewNoChunk(Sprite sheet) {
		
		int block_x = 0, block_z = 0;
		int i = 0;
		
		BufferedImage img_out = new BufferedImage(CHUNK_SIZE, CHUNK_SIZE, BufferedImage.TYPE_INT_RGB);
		

		img_out.setRGB(block_x, block_z, Color.HSBtoRGB(.5f, 1f, (float) .5f));

		return sheet.addSprite(img_out);
		
	}
	
	/*
	 * 	var buff = buffer_load(chunk_filename)
	var heightmaps = nbt_get_buffer(buff, ";", ";Level;Heightmaps;WORLD_SURFACE",0)
	if heightmaps >0 heightmaps = heightmaps[? "payload"] else return -100
	
	var block_x = 0
	var block_z = 0
	repeat CHUNK_SIZE*CHUNK_SIZE {
	
	var index = block_z * CHUNK_SIZE + block_x
	
	var INDEXES_PER_LONG = floor(64/HEIGHTMAP_INDEX_SIZE) //The amount of indexes in a long value (64 bits).
	var long_index = floor(index / (INDEXES_PER_LONG)) //The index of the long that contains the block.
	
	var bits = HEIGHTMAP_INDEX_SIZE
	var clean = (power(2, bits) - 1)								
	var startbit = ceil((index mod INDEXES_PER_LONG) * bits)	
	var y_level = (heightmaps[| long_index]) >> startbit & clean //Get the block's Y level.
	
	//draw_point_color(block_x, block_z, make_color_hsv(0, 0, y_level))
	draw_sprite_ext(spr_pixel, 0, block_x, block_z, 1, 1, 0, make_color_hsv(0, 0, y_level), 1)
	
	 block_x++
	 if block_x = CHUNK_SIZE {
		  block_x = 0
		  block_z++ 
		 }
	}
	 */
	
}
