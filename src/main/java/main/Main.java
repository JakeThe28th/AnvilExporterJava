package main;

import java.io.File;  // Import the File class
import java.io.FileReader;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Iterator;
import java.util.Random;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;   // Import the FileWriter class

import net.querz.nbt.tag.*;
import net.querz.nbt.io.*;
import net.querz.io.*;
import net.querz.mca.*;
//

import mc2obj.WriteBlock;

public class Main {
	//My first serious java project, better put some to-do's here.
	//In order of importance
	//[?] Learn java
	//[x] write a basic .obj file 					//5/16/2021
	//[-..] read minecraft json files
	//[-..] convert minecraft json files to obj files
		//[	] Read UV coordinates
		//[	] Fix coordinates to 16 pixels per unit
		//[	] Add rotation
		//[x] Parent handling
		//[-..] blockstate handling
			//[x] variant handling (basic)
			//[ ] rotation and more variant properties
			//[ ] multipart
			//[ ] random variant selection
			//[ ] apparently some blocks have blockstate ingame that arent part of the model file 
				//(EG powered for doors, or persistent and distance for leaves)
				//So, make a one layer deep "JsonMatchesNull" or such, 
				//and ignore values that dont exist in one or the other.
		//[	] Culling
		//[	] Proper UVS (adapt to non 16 texture sizes aswell.)
		//[ ] rotation
	
	//[no] create nbt modules -- Just use Querz/NBT
	
	//MISC
	//[ ] One MTL Entry Per material.
		//RELATED QUERIES:
		//https://stackoverflow.com/questions/1522108/how-to-find-the-index-of-an-element-in-an-array-in-java
		//https://stackoverflow.com/questions/13543457/how-do-you-create-a-dictionary-in-java
	//[ ] https://alvinalexander.com/java/java-file-exists-directory-exists/
	//[ ] https://minecraft.fandom.com/wiki/Model
	
	//[ ] read minecraft level data
	//[ ] export minecraft level data into .obj files
	//[ ] configuration
	
	//STRETCH GOALS
	//[ ] Texture sheet packing
	//[ ] Biome colors
	//[ ] Resource packs as seperate rather than conbined
	//[ ] a GUI.
	//[ ] minecraft style smooth and flat lighting (flat will be easy, smooth not so much...?)
	//[ ] proper namespace support
	//[ ] clean up
	//[ ] 2d isometric preview
	//[ ] cached ^
	
	//OPTIONAL
	//[ ] Read schematic files and structure block NBT data
	//[ ] Add entities export (armorstands and itemframes)
	//[ ] Tris or quads export
	//Check pack, if model doesn't exist/ has no elements, go to default. if doesn't exist / has no, go to blockentity
	
	//5/10/2021 (c) Jake 28
	
	//
	public static int v_count = 0;
	public static int vt_count = 0;
	
	public static Random ran = new Random();
	
	
	  public static void main(String[] args) throws IOException, ParseException {
		  
		  //JSONObject temp;
		  mc2obj.WriteBlock mod = new  mc2obj.WriteBlock("brewing_stand");
		  //mod.WriteModel("assets\\\\minecraft\\\\models\\\\block\\\\acacia_fence_gate_wall_open.json", 182, 2, 4, 0, 0, 0, null);

		  JSONParser jsonParser = new JSONParser();
		  String states = "facing=east,half=bottom,open=false";
		  JSONObject culling = (JSONObject) jsonParser.parse("{\"east\":1,\"west\":1,\"north\":1,\"south\":1}");
		  
		  //System.out.println(states);
		  //System.out.println(culling);
		  
		  mod.WriteFromBlockstate("assets\\minecraft\\blockstates\\birch_trapdoor.json", states, 0, 0, 0, culling);
		  
		  //mod.end();
		  
		  System.out.println(v_count);
	    
		  
		//  /*
		  
		MCAFile mcaFile = null;
		mcaFile = MCAUtil.read("r.0.0.mca");
		
		Chunk chunk = mcaFile.getChunk(0, 0);
		
		Section section = chunk.getSection(1);
		
		//only get blocks empty secoction no
		
		
		
		int i_sec = 0;
		System.out.println(i_sec);
		do {
		section = chunk.getSection(i_sec);	
		
		if (section != null) if (section.getBlockStates() !=null) {
		
		long i = 0;
		int x = 0;
		int y = 0;
		int z = 0;
		do {
		CompoundTag blockState = section.getBlockStateAt(y, z, x);
		 
		//iterate over properties
		String states_ = "";
		StringTag property;
		if (blockState.get("Properties") != null) {
			CompoundTag properties = (CompoundTag) blockState.get("Properties");
			Iterator<String> itr = properties.keySet().iterator();
			String prefix = "";
			while (itr.hasNext())
			{
				String key = itr.next();
				StringTag value = (StringTag) properties.get(key);
		 
				//System.out.println(key + "=" + value);
				//property = (CompoundTag) properties.get(key);
				states_ = states_+prefix+ key + "=" + SNBTUtil.toSNBT(properties.get(key));
				prefix = ",";
			}
			//System.out.println(states_);
			}
		
		String blockID = (String) SNBTUtil.toSNBT(blockState.get("Name"));
		if (!blockID.equals("\"minecraft:air\"")) {
			
			
			String BlockID_ = blockID.substring(blockID.indexOf(":")+1);
			//String blockID = SNBTUtil.toSNBT(abc);
			mod.WriteFromBlockstate("assets\\minecraft\\blockstates\\" + BlockID_.substring(0,BlockID_.length()-1) + ".json", states_, x, z, y, culling); //ADD NAMESPACE HERE, CHOPP CHOPP
			
			//System.out.println(states_);
			//System.out.println(SNBTUtil.toSNBT(blockState)); }

			
		}
		
		x+=1;
		if (x == 16) {
			x = 0;
			y+= 1;
			}
		
		if (y == 16) {
			y = 0;
			z+= 1;
			}
			
		i+= 1;
		} while (i <= (16*16*16)); 
		}
		
		i_sec += 1;
		System.out.println(i_sec);
	  } while (i_sec < 16);
		 
		  ////////////
	    try {
	      File myObj = new File("filename.obj");
	      if (myObj.createNewFile()) {
	        System.out.println("File created: " + myObj.getName());
	      } else {
	        System.out.println("File already exists.");
	      }
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	    
	    
	    try {
	        FileWriter myWriter = new FileWriter("filename.obj");
	        myWriter.write("v 0 0 0 \n");
	        myWriter.write("v 0 1 0 \n");
	        myWriter.write("v 1 1 0 \n");
	        myWriter.write("v 1 0 0 \n");
	        myWriter.write("f 1 2 3 4 \n");
	        myWriter.close();
	        System.out.println("Successfully wrote to the file.");
	      } catch (IOException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }
	    //*/
	    
	    mod.end();
	  }
}