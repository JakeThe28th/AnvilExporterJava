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
import mc2obj.Exporter;

public class Main {
	//My first serious java project, better put some to-do's here.
	//In order of importance
	//[?] Learn java
	//[x] write a basic .obj file 													//5/16/2021
	//[-..] read minecraft json files
	//[-..] convert minecraft json files to obj files
		//[x] Read UV coordinates													//i dont remember
		//[x] Fix coordinates to 16 pixels per unit									//i dont remember
		//[-..] Add rotation
		//[x] Parent handling														//i dont remember
		//[-..] blockstate handling
			//[x] variant handling (basic)											//i dont remember
			//[x] rotation and more variant properties  							//6/22/2021
			//[ ] multipart
			//[x] random variant selection											//i dont remember
			//[x] apparently some blocks have 'unused' states						//06/20/2021
				//(EG powered for doors, or persistent and distance for leaves)

		//[ ] east and west are wrong way around 
		//[	] Culling
		//[	] Proper UVS (adapt to non 16 texture sizes aswell.)
		
	
	//[no] create nbt modules -- Just use Querz/NBT
		//[ ] Modify Querz/NBT to use any world height l
	
	//MISC
	//[x] One MTL Entry Per material.
		//RELATED QUERIES:
		//https://stackoverflow.com/questions/1522108/how-to-find-the-index-of-an-element-in-an-array-in-java
		//https://stackoverflow.com/questions/13543457/how-do-you-create-a-dictionary-in-java
	//[x] https://alvinalexander.com/java/java-file-exists-directory-exists/
	//[.. ] https://minecraft.fandom.com/wiki/Model
	
	//[x] read minecraft level data
	//[x] export minecraft level data into .obj files
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
	//[ ] Optimization
	//[ ] make coordinate system more consistent
	//[ ] vertex normals
	
	//OPTIONAL
	//[ ] Read schematic files and structure block NBT data
	//[ ] Add entities export (armorstands and itemframes)
	//[ ] Tris or quads export
	//Check pack, if model doesn't exist/ has no elements, go to default. if doesn't exist / has no, go to blockentity

	//error e
	
	//Instead of a 2d pixel preview, use a top down 3d preview from the start. Add a fast mode with pixels later?
	//Or render pixels and bake to textures, and use one plane instead of multiple geom 
	
	//read notes
	//add culling
	//fix scaling /  mirroring
	
	//Focus on GUI for now.
	//Render 64*64 preview of every needed block, as needed. cache this.
	//compile previews into single image of chunk, and cache it, but occasionally refresh.
	//selection is based off the first block which isn't air past a certain y??
	//selection preview is a vertical column, and a preview is an iso selection square.
	
	//MORE TO DO
	//Make a special thanks and credits file
	//ADD:
	//Robotpantaloons, and by extention the team working on Origin Realms
	//GamesWithGabe, I used their tutorials to learn how to make GUI in Java
		//(They're a pretty good youtuber, you should check them out of you're reading this)
	
	
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
		mcaFile = MCAUtil.read("region_testing\\r.0.0.mca");
		
		//Chunk chunk = mcaFile.getChunk(0, 0);

		Exporter exporter = new Exporter("", "export");
		

		
		//exporter.exportChunk(mcaFile.getChunk(0, 0), 0, 0, 0);
		//exporter.exportChunk(mcaFile.getChunk(0, 1), 0, 0, 1);
		//exporter.exportChunk(mcaFile.getChunk(1, 1), 0, 1, 1);
		//exporter.exportChunk(mcaFile.getChunk(1, 0), 0, 1, 0);
		
		
		int expx = -2;
		int expz = -2;
		int i = 0;
		while (i < (4*4)) {
			
			int rx = (int) Math.floor((double) expx/32);
			int rz = (int) Math.floor((double) expz/32);
			//if (expx <0) rx = (int) Math.ceil(expx/32);
			//if (expz <0) rz = (int) Math.ceil(expx/32);
			String mcafilename = "r."+rx+"."+rz+".mca";
			
			mcaFile = MCAUtil.read("region_testing\\" + mcafilename);
			System.out.println(mcafilename);
			System.out.println(expx + " . " + expz);
			exporter.exportChunk(mcaFile.getChunk(expx, expz), 0, expx, expz);
			
			expx += 1;
			if (expx > 2) { expx = 0; expz+=1; }
			i+=1;
		}
		

		
		exporter.end();
	    mod.end();
	  }
}