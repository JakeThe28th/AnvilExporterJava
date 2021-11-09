package io.github.jakethe28th.minecraft.obj;

import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.gui.Sprite;
import io.github.jakethe28th.minecraft.obj.WriteBlock;

import net.querz.nbt.tag.*;
import net.querz.nbt.io.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;

import net.querz.io.*;
import net.querz.mca.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Exporter {

	JSONParser jsonParser = new JSONParser();
	WriteBlock BlockWriter;
	
	public HashMap<Integer,ChunkExporter> chunks = new HashMap<Integer,ChunkExporter>();
	public Mesh myMesh;
	
	public Exporter(String region_folder_path, String filename_) { 	
		BlockWriter = new WriteBlock(filename_, new Sprite(256, 256));
		this.myMesh = BlockWriter.myMesh;
	}
	
	public static Boolean isTransparent(Section section, int x, int y, int z) throws IOException {
		
		//if (x > 16 || x < 0) return true;
		//if (y > 16 || y < 0) return true;
		//if (z > 16 || z < 0) return true;
		
		CompoundTag blockStateTemp = section.getBlockStateAt(x, y, z);
		String block_id = (String) SNBTUtil.toSNBT(blockStateTemp.get("Name"));
		
		block_id = block_id.replace("\"", "");
		if (block_id.equals("minecraft:air")) return true;
		if (block_id.equals("minecraft:cave_air")) return true;
		if (block_id.equals("minecraft:void_air")) return true;
		if (block_id.equals("minecraft:water")) return true;
		if (block_id.equals("minecraft:lava")) return true;
		if (block_id.equals("minecraft:grass")) return true;
		if (block_id.equals("minecraft:tall_grass")) return true;
		if (block_id.equals("minecraft:glass")) return true;
		if (block_id.equals("minecraft:oak_sign")) return true;
		if (block_id.equals("minecraft:end_gateway")) return true;
		if (block_id.equals("minecraft:glass_pane")) return true;
		if (block_id.equals("minecraft:chest")) return true;
		if (block_id.equals("minecraft:anvil")) return true;
		if (block_id.equals("minecraft:lantern")) return true;
		if (block_id.equals("minecraft:command_block")) return true;
		if (block_id.equals("minecraft:note_block")) return true;
		if (block_id.equals("minecraft:tripwire")) return true;
		if (block_id.equals("minecraft:string")) return true;
		if (block_id.equals("minecraft:cauldron")) return true;
		
		if (block_id.equals("minecraft:barrier")) return true;
		if (block_id.equals("minecraft:lilac")) return true;
		if (block_id.equals("minecraft:fern")) return true;
		if (block_id.equals("minecraft:rose_bush")) return true;
		if (block_id.equals("minecraft:vine")) return true;
		if (block_id.equals("minecraft:allium")) return true;
		
		if (block_id.contains("leaves")) return true;
		if (block_id.contains("tulip")) return true;
		if (block_id.contains("carpet")) return true;
		//System.out.println(block_id + " false");
		
		return false;
	}
	
	public boolean step() throws ParseException, IOException {
		// iterate list of chunk objects
		
		int done = 0;
		for (int key : chunks.keySet()) {
			done = chunks.get(key).step();
			//System.out.println(done);
			if (done == 1) chunks.remove(key);
		}
		
		
		return false; }
	
	public boolean addChunkToQueue(Chunk chunk, int cullmode, int chunk_x, int chunk_z) throws ParseException, IOException {
		chunks.put(chunks.size(), new ChunkExporter(chunk, cullmode, chunk_x, chunk_z, BlockWriter));
		return false; 
		}
	
	public void end() {
		BlockWriter.end();
		this.myMesh = BlockWriter.myMesh;
		}
}

class ChunkExporter {
	
	Chunk chunk;
	int cullmode;
	int chunk_x;
	int chunk_z;
	WriteBlock BlockWriter;
	
	int x = 0; int y = 0; int z = 0; int i = 0; int i_sec = 0;
	Section section = null;
	
	ChunkExporter(Chunk chunk, int cullmode, int chunk_x, int chunk_z, WriteBlock BlockWriter) {
		this.chunk = chunk;
		this.cullmode = cullmode;
		this.chunk_x = chunk_x;
		this.chunk_z = chunk_z;
		this.BlockWriter = BlockWriter;
	}
	
	int step() throws IOException, ParseException {
		
		Section section = chunk.getSection(i_sec);	
		exportSection(section);
				
		i_sec += 1;
		//System.out.println(i_sec);
		
		if (i_sec >=16) return 1; //done iterating
		return 0; //still iterating
	}
	
	
	int exportSection(Section section) throws IOException, ParseException {
			int x = 0; int y = 0; int z = 0; int i = 0;
			//Set base values to 0
				
				if (section == null || section.getBlockStates() == null)  return -1; //done
				
				//Reset values
				x = 0; y = 0; z = 0; i = 0;
				
				while (i < (16*16*16)) {
				CompoundTag blockState = section.getBlockStateAt(y, z, x);
				 
				//iterate over properties
				String states_ = ""; String prefix = "";
				CompoundTag properties = (CompoundTag) blockState.get("Properties");
				
				if (properties != null) {
					Iterator<String> itr = properties.keySet().iterator();
					while (itr.hasNext()) {
						String key = itr.next();
						states_ = states_ + prefix + key + "=" + SNBTUtil.toSNBT(properties.get(key));
						prefix = ",";
					} }
				
					String blockID = (String) SNBTUtil.toSNBT(blockState.get("Name"));
					String BlockID_ = blockID.split(":")[1];			 	//ID
						   BlockID_ = BlockID_.substring(0,BlockID_.length()-1);
					String namespace = blockID.split(":")[0].substring(1);	//Namespace

					
					if (!BlockID_.equals("air")) { //Need to add a list of empty blocks later.
					
					HashMap<String,Boolean> culling = new HashMap<String,Boolean>();
					String BiD_temp;
					
					
					//fullblock culling
					int sides = 6;
					
					
					culling.put("north", Exporter.isTransparent(section, 	y, z, x-1));
									if (!Exporter.isTransparent(section, 	y, z, x-1)) sides-=1;
					culling.put("east", Exporter.isTransparent(section, 	y+1, z, x));
									if (!Exporter.isTransparent(section,	y+1, z, x)) sides-=1;
					culling.put("south", Exporter.isTransparent(section, 	y, z, x+1));
									if (!Exporter.isTransparent(section, 	y, z, x+1)) sides-=1;
					culling.put("west", Exporter.isTransparent(section, 	y-1, z, x));
									if (!Exporter.isTransparent(section, 	y-1, z, x)) sides-=1;
					culling.put("up", Exporter.isTransparent(section, 		y, z+1, x));
									if (!Exporter.isTransparent(section, 	y, z+1, x)) sides-=1;
					culling.put("down", Exporter.isTransparent(section, 	y, z-1, x));
									if (!Exporter.isTransparent(section, 	y, z-1, x)) sides-=1;
					
									culling.put("north", true);
									culling.put("east", true);
									culling.put("south", true);
									culling.put("west", true);
									culling.put("up", true);
									culling.put("down", true);
									
					//if (section.getBlockLight() != null) {
					//System.out.println("l");
					//byte lightLevel = section.getBlockLight()[(int) Math.floor((y*16*16 + z*16 + x)/2)];
					//byte lightLevel = Nibble4(section.getBlockLight(), y*16*16 + z*16 + x);
					//System.out.println(lightLevel);
					//lightLevel = (byte) (lightLevel << 4);
					/*if ((y*16*16 + z*16 + x) % 2 == 0) { //even
						lightLevel &= 0x0F;
						} else {
						lightLevel &= 0xF0;
						lightLevel >>= 4;
						}*/
					//System.out.println(lightLevel);
					
					//float lightLevelFloat = (float) lightLevel;
					//BlockWriter.color = lightLevelFloat / 15;
					//}
					if (sides > 0 ) BlockWriter.WriteFromBlockstate("assets\\minecraft\\blockstates\\" + BlockID_ + ".json", states_, x+(chunk_z*16), z+(i_sec*16), y+(chunk_x*16), culling, namespace); //ADD NAMESPACE HERE, CHOPP CHOPP
				
					}
					
				x+=1;
				if (x == 16) { x = 0; y+= 1; }
				if (y == 16) { y = 0; z+= 1; }
					
				i+= 1;
				}
				return 1;
		
	}
	
}
