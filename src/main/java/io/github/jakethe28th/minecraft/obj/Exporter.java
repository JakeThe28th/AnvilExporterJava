package io.github.jakethe28th.minecraft.obj;

import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.gui.Sprite;
import io.github.jakethe28th.minecraft.obj.WriteBlock;

import net.querz.nbt.tag.*;
import net.querz.nbt.io.*;

import java.io.IOException;
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
	
	public Mesh myMesh;
	
	public Exporter(String region_folder_path, String filename_) { 
		
	BlockWriter = new WriteBlock(filename_, new Sprite(512, 512));
		
	}
	
	public boolean isTransparent(String block_id) {
		block_id = block_id.replace("\"", "");
		if (block_id.equals("minecraft:air")) return true;
		if (block_id.equals("minecraft:cave_air")) return true;
		if (block_id.equals("minecraft:void_air")) return true;
		if (block_id.equals("minecraft:water")) return true;
		if (block_id.equals("minecraft:lava")) return true;
		if (block_id.equals("minecraft:grass")) return true;
		if (block_id.equals("minecraft:tall_grass")) return true;
		if (block_id.equals("minecraft:glass")) return true;
		//System.out.println(block_id + " false");
		
		return false;
		}
	
	public boolean exportChunk(Chunk chunk, int cullmode, int chunk_x, int chunk_z) throws ParseException, IOException {
		int x = 0; int y = 0; int z = 0; int i = 0; int i_sec = 0;
		//Set base values to 0
		
		while (i_sec < 16) {
			Section section = chunk.getSection(i_sec);	
			
			if (section != null) if (section.getBlockStates() !=null) {
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

				
				if (BlockID_ != "air") { //Need to add a list of empty blocks later.
				
				JSONObject culling = (JSONObject) jsonParser.parse("{\"east\":1,\"west\":1,\"north\":1,\"south\":1}");
				String BiD_temp;
				
				
				//fullblock culling
				int sides = 6;
				
					CompoundTag blockStateTemp = section.getBlockStateAt(y, z, x+1);
					BiD_temp = (String) SNBTUtil.toSNBT(blockStateTemp.get("Name"));
					if (!isTransparent(BiD_temp)) sides -= 1;
				culling.put("north", isTransparent(BiD_temp));
					blockStateTemp = section.getBlockStateAt(y+1, z, x);
					BiD_temp = (String) SNBTUtil.toSNBT(blockStateTemp.get("Name"));
					if (!isTransparent(BiD_temp)) sides -= 1;
				culling.put("east", isTransparent(BiD_temp));
					blockStateTemp = section.getBlockStateAt(y, z, x-1);
					BiD_temp = (String) SNBTUtil.toSNBT(blockStateTemp.get("Name"));
					if (!isTransparent(BiD_temp)) sides -= 1;
				culling.put("south", isTransparent(BiD_temp));
					blockStateTemp = section.getBlockStateAt(y-1, z, x);
					BiD_temp = (String) SNBTUtil.toSNBT(blockStateTemp.get("Name"));
					if (!isTransparent(BiD_temp)) sides -= 1;
				culling.put("west", isTransparent(BiD_temp));
					blockStateTemp = section.getBlockStateAt(y, z+1, x);
					BiD_temp = (String) SNBTUtil.toSNBT(blockStateTemp.get("Name"));
					if (!isTransparent(BiD_temp)) sides -= 1;
				culling.put("up", isTransparent(BiD_temp));
					blockStateTemp = section.getBlockStateAt(y, z-1, x);
					BiD_temp = (String) SNBTUtil.toSNBT(blockStateTemp.get("Name"));
					if (!isTransparent(BiD_temp)) sides -= 1;
				culling.put("down", isTransparent(BiD_temp));

				//System.out.println(sides);
				
				if (sides > 0 ) BlockWriter.WriteFromBlockstate("assets\\minecraft\\blockstates\\" + BlockID_ + ".json", states_, x+(chunk_z*16), z+(i_sec*16), y+(chunk_x*16), culling, namespace); //ADD NAMESPACE HERE, CHOPP CHOPP
			
				}
				
			x+=1;
			if (x == 16) { x = 0; y+= 1; }
			if (y == 16) { y = 0; z+= 1; }
				
			i+= 1;
			} }
			
			i_sec += 1;
			System.out.println(i_sec);
		  }

		return true;
	}
	
	
	public void end() {
		BlockWriter.end();
		this.myMesh = BlockWriter.myMesh;
		}
}
