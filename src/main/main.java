package main;

import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class

//

import java.io.FileNotFoundException;
import java.io.FileReader;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import mc2obj.WriteBlock;

public class main {
	//My first serious java project, better put some to-do's here.
	//In order of importance
	//[?] Learn java
	//[x] write a basic .obj file 					//5/16/2021
	//[-..] read minecraft json files
	//[-..] convert minecraft json files to obj files
		//[	] Read UV coordinates
		//[	] Fix coordinates to 16 pixels per unit
		//[	] Add rotation
		//[	] Parent handling
		//[	] Culling
		//[	] Proper UVS (adapt to non 16 texture sizes aswell.)
	//[ ] create nbt modules
	//[ ] read minecraft level data
	//[ ] export minecraft level data into .obj files
	//[ ] configuration
	
	//STRETCH GOALS
	//[ ] Texture sheet packing
	//[ ] Biome colors
	//[ ] Resource packs as seperate rather than conbined
	//[ ] a GUI.
	//[ ] minecraft style smooth and flat lighting (flat will be easy, smooth not so much...?)
	
	//OPTIONAL
	//[ ] Read schematic files and structure block NBT data
	//[ ] Add entities export (armorstands and itemframes)
	//[ ] Tris or quads export
	//Check pack, if model doesn't exist/ has no elements, go to default. if doesn't exist / has no, go to blockentity
	
	//5/10/2021 (c) Jake 28
	
	//
	public static int v_count = 0;
	public static int vt_count = 0;
	
	
	  public static void main(String[] args) {
		  
		  mc2obj.WriteBlock mod = new  mc2obj.WriteBlock("brewing_stand");
		  mod.WriteModel("assets\\\\minecraft\\\\models\\\\block\\\\brewing_stand.json", 182, 2, 4);

		  mod.end();
		  
		  System.out.println(v_count);
	       
	        	
		  
		  
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
	    
	  }
}
