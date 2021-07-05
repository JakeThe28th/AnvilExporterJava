package mc2obj;

import mc2obj.WriteBlock;

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
	
	public Exporter(String region_folder_path, String filename_) { 
		
	BlockWriter = new WriteBlock(filename_);
		
	}
	
	public boolean exportChunk(Chunk chunk, int cullmode, int chunk_x, int chunk_z) throws ParseException, IOException {
		int x = 0; int y = 0; int z = 0; int i = 0; int i_sec = 0;
		//Set base values to 0
		
		JSONObject culling = (JSONObject) jsonParser.parse("{\"east\":1,\"west\":1,\"north\":1,\"south\":1}");
		
		while (i_sec < 16) {
			Section section = chunk.getSection(i_sec);	
			
			if (section != null) if (section.getBlockStates() !=null) {
			//Reset values
			x = 0; y = 0; z = 0;
			
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
				String BlockID_ = blockID.substring(blockID.indexOf(":")+1); //Remove namespace
				BlockWriter.WriteFromBlockstate("assets\\minecraft\\blockstates\\" + BlockID_.substring(0,BlockID_.length()-1) + ".json", states_, x+(chunk_x*16), z+(chunk_z*16), y+(i_sec*16), culling); //ADD NAMESPACE HERE, CHOPP CHOPP
			
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
		}
}
