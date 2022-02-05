package io.github.jakethe28th.minecraft.obj;

import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Random;
import java.io.FileWriter;   // Import the FileWriter class

import java.io.FileNotFoundException;
import java.io.FileReader;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.math.*;

import io.github.jakethe28th.anvilexporter.Utility;
import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.Sprite;
import io.github.jakethe28th.engine.graphics.Texture;
import io.github.jakethe28th.engine.graphics.Vertex;
import io.github.jakethe28th.engine.math.Vector2f;
import io.github.jakethe28th.engine.math.Vector3f;

//delete quad somehow?

//Input: block model, xyz positon, optional rotation, mtl file, obj file
public class WriteBlock {
	
	public float color = 1.0f;
	
	public String filename;
	public FileWriter objWriter;
	public FileWriter mtlWriter;
	
	public ArrayList<String> mtl_done = new ArrayList<String>();
	
	public int v_count = 0;
	public int vt_count = 4;
	
	public String namespace = "minecraft";
	
	public Random ran = new Random();
	
	public boolean preview = true;
	
	public void end() {
		System.out.println("Ended WriteBlock " + filename + " at " + System.currentTimeMillis());
		try {
		objWriter.close();
		mtlWriter.close();
		if (texture_sheet != null) {
			texture_sheet.save(filename + ".png");
			texture_sheet.cleanup();
			}
		System.out.println("Closed MC2OBJ inst");
		 	} catch (IOException e) {
		      System.out.println("An error occurred when closing filewriter..");
		      e.printStackTrace();
		    }
		    
	}
	
	public Sprite texture_sheet;
	public HashMap<String, Integer> texture_index;
	
	public HashMap<String, String[]> cache;

	private String cachepath;
	
	public WriteBlock(String filename_, Sprite texture_sheet) {
		System.out.println("Created WriteBlock " + filename_ + " at " + System.currentTimeMillis());
		filename = filename_;
		
		this.texture_sheet = texture_sheet;
		this.texture_index = new HashMap<String, Integer>();
		this.cache = new HashMap<String, String[]>();

		if (this.texture_sheet != null) {
			
			}
		
		try {
		
		//Create OBJ file.
			  File myObj = new File(filename + ".obj");
			  if (myObj.createNewFile())
				//Success
		    	System.out.println("File created: " + myObj.getName());
			  	//Error
		      	else System.out.println("File already exists.");

		 //Test writing to it.
		    	FileWriter myWriter_ = new FileWriter(filename + ".obj");
		    	objWriter = myWriter_;
		        
		    	objWriter.write("# obj made with AnvilExporter \n");
		    	
		
		  //Create MTL file.
			     myObj = new File(filename + ".mtl");
			     if (myObj.createNewFile()) 
			     //Success
			     System.out.println("File created: " + myObj.getName());
			     //Error
			     else  System.out.println("File already exists.");
			
		  //Test writing to it.
			     myWriter_ = new FileWriter(filename + ".mtl");
			     mtlWriter = myWriter_;
			        
			     mtlWriter.write("# obj made with AnvilExporter \n");
			     
			     
			    
		  } catch (IOException err) {
		      System.out.println("Failed to create WriteBlock()");
		      err.printStackTrace();
		    }
		
		try {
			if (texture_sheet != null) {
				mtlWriter.write("newmtl " + filename + "\n");
				mtlWriter.write("map_Kd " + filename + ".png" + "\n");
				
				objWriter.write("usemtl " + filename + "\n");
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
	}
	
	
	
	public String FileHierarchy(String path) {

		int attempts = 0;
		String path_ = "";
		while(attempts<=2) {
			if (attempts == 0) path_ = "data\\anvilexporter\\" + path; //Check AExporter. This way, if a model is blank in MC, i can overwrite it.
			if (attempts == 1) path_ = "data\\resourcepack\\" + path; //Check the resource pack. If AE doesn't have a model, then just use the regular hierarchy.
			if (attempts == 2) path_ = "data\\minecraft\\" + path;	 //Check MC. 
			
			boolean exists = new File(path_).exists();
			if (!exists && attempts == 2 ) { 
				System.out.println("file does not exist. " + path); return null; 
				}
			if (exists) break;
			
			attempts+=1;
			}
			path = path_;
		
		//System.out.println("Filename Hierarchy: " + (String) path);
		return path;
		}

	public int addModel(String path, int x, int y, int z, Double rot_x, Double rot_y, Double rot_z, HashMap<String, Boolean> Culling, Boolean uvlock) throws IOException {
		this.cachepath = path + Culling.get("north") + 
				 Culling.get("east") + 
				 Culling.get("south") + 
				 Culling.get("west") + 
				 Culling.get("up") + 
				 Culling.get("down") +
				 rot_x + 
				 rot_y + 
				 rot_z;
		
		if (cache.get(cachepath) == null) {
			WriteModel(path, 0, 0, 0, rot_x, rot_y, rot_z, Culling, uvlock);
			}
		
		String[] cachedmodel = cache.get(cachepath);
		//if (cachedmodel == null) System.out.println("Cached model for " + path + " was null.");
		if (cachedmodel != null) {
		int i = 0;
		while (i < cachedmodel.length) {
			String[] t = cachedmodel[i].split(" ");
			String write = " ";
			if (t[0].equals("v")) {
				write = t[0] + " " + (Double.parseDouble(t[1])+x)
							 + " " + (Double.parseDouble(t[2])+y)
							 + " " + (Double.parseDouble(t[3])+z)
							 + "\n";
				this.v_count  += 1;
				}
			
			if (t[0].equals("f")) {
				write = "f " 
	    		 		+ (v_count-3) + "/" + t[1].split("/")[1] + " " 
	    		 		+ (v_count-2) + "/" + t[2].split("/")[1] + " " 
	    		 		+ (v_count-1) + "/" + t[3].split("/")[1] + " " 
	    		 		+ v_count + "/" + t[4].split("/")[1] + " \n";
				}
			objWriter.write(write);
			//System.out.println(write);
			i++;
		} }
		
		
		return 0;
	}
	
	
	public int WriteModel(String path, int x, int y, int z, Double rot_x, Double rot_y, Double rot_z, HashMap<String, Boolean> Culling, Boolean uvlock) {
		ArrayList<String> array_cached = new ArrayList<String>();
		
		//System.out.println(path);
		
	path = FileHierarchy(path);
	if (path == null) System.out.println("Model file does not exist."); 
	
	try {
	
	//JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
	  
	FileReader reader_ = new FileReader(path);
	JSONObject model = (JSONObject) jsonParser.parse(reader_);
	
	if (model.get("empty") != null) return -1;
	
	//Loop through parents and merge them into model until parent value is null.
	String parent = (String) model.get("parent");
	do {
		if (parent != null) {
		//System.out.println(parent);
		String tempspace = namespace;
		if (parent.indexOf(":") != -1) tempspace = parent.substring(0, parent.indexOf(":"));
		String parent_path = FileHierarchy("assets\\" + tempspace + "\\models\\" + parent.substring(parent.indexOf(":")+1) + ".json");
		if (parent_path == null) System.out.println("Parent file does not exist.");
		
		if (parent.contains("generated")) {
			System.out.println("Parent for " + path + " uses builtin/generated, and can't be loaded.");
			cache.put(cachepath, new String[] {});
			return -1;
			}
		
		
		FileReader parentReader = new FileReader(parent_path); 
		Object parent_model = jsonParser.parse(parentReader);
		
		model.remove("parent"); //Remove previous parent value before merging, otherwise this code would loop forever.

        if (parent_model != null) model = Utility.deepMerge((JSONObject) model, (JSONObject) parent_model);
		
		parent = (String) model.get("parent");
		}
		} while (parent != null);
		
		
        JSONArray elements = (JSONArray) model.get("elements");
        JSONObject textures = (JSONObject) model.get("textures");
        
        if (elements == null) System.out.println(path);

         int i = 0;
         String face_name = "";
         
         //Loop through elements.
         do {			
        	JSONObject element = (JSONObject) elements.get(i);
        	JSONArray from_js = (JSONArray) element.get("from");
        	JSONArray to_js = (JSONArray) element.get("to");
        	
        	Object  from_x_ = (Object)from_js.get(0);
        	Object  from_y_ = (Object)from_js.get(1);
        	Object  from_z_ = (Object)from_js.get(2);
        	
        	Object  to_x_ = (Object)to_js.get(0);
        	Object  to_y_ = (Object)to_js.get(1);
        	Object  to_z_ = (Object)to_js.get(2);
        	
        	Double from_x = (Double) Double.parseDouble(from_x_.toString());
        	Double from_y = (Double) Double.parseDouble(from_y_.toString());
        	Double from_z = (Double) Double.parseDouble(from_z_.toString());
        	
        	Double to_x = (Double) Double.parseDouble(to_x_.toString());
        	Double to_y = (Double) Double.parseDouble(to_y_.toString());
        	Double to_z = (Double) Double.parseDouble(to_z_.toString());
        	
        	Point3D from = new Point3D(from_x, from_y, from_z);
        	Point3D to = new Point3D(to_x, to_y, to_z);
        	
        	
        	int i_faces = 0;
        	JSONObject faces = (JSONObject) element.get("faces");
        	
        	
        	do {
        	switch (i_faces) {
        		case 0: face_name = "north"; break;
        		case 1: face_name = "east"; break;
        		case 2: face_name = "south"; break;
        		case 3: face_name = "west"; break;
        		case 4: face_name = "up"; break;
        		case 5: face_name = "down"; break;
        		}
        		 
        	JSONObject face = (JSONObject) faces.get(face_name);
        	Quad coords = null;
        	
        	String face_cull_s = face_name;
        	if (i_faces < 4) {
        	//Fix face culling for rotation
        	int face_cull = i_faces;
        	face_cull += Math.floor(rot_y/90);
        	face_cull %= 4;
        	
        	switch (face_cull) {
    			case 0: face_cull_s = "north"; break;
    			case 1: face_cull_s = "east"; break;
    			case 2: face_cull_s = "south"; break;
    			case 3: face_cull_s = "west"; break;
    			}
        	}
        	
        	if (i_faces == 4) { if (rot_z >=180) face_cull_s = "down"; }
        	if (i_faces == 5) { if (rot_z >=180) face_cull_s = "up"; }
            
        	
        	if (face != null && Culling.get(face_cull_s)) {
        		
        	//Get texture ID In model
        	String tex = ((String) face.get("texture")).substring(1);
        	
        	//Get filename from that.
        	String tex2 = (String) textures.get(tex);
        	
        	while(tex2.indexOf("#") != -1) {
        		//Get texture ID In model
            	tex = (String) tex2.substring(1);
            	
            	//Get filename from that.
            	tex2 = (String) textures.get(tex);
            	
        		}
        	
        	//Fix filename to remove namespace
        	String tex3 = tex2;
        	if (tex2.indexOf(":") != -1) tex3 = tex2.split(":")[1];
        	String tempspace = namespace;
    		if (tex2.indexOf(":") != -1) tempspace = tex2.substring(0, tex2.indexOf(":"));

        	if ( texture_sheet == null ) {	
        		if ( Utility.ArrayIndexOf(mtl_done, tex2) == -1) {
        			mtlWriter.write("newmtl " + tex2 + "\n");
        			mtlWriter.write("map_Kd  " + FileHierarchy("assets/" + tempspace + "/textures/" + tex3 + ".png") + "\n");
        	
        			mtl_done.add(tex2);
        		}
        	} else {
        		//Add texture to sheet
        		
        		String texture_filename = FileHierarchy("assets/" + tempspace + "/textures/" + tex3 + ".png");//.replace("\"", "/");
        		if (texture_filename !=null && texture_index.get(tex2) == null ) {
        			System.out.println(" Adding sprite " + texture_filename);
        			File f = new File(texture_filename + ".mcmeta");
        			if (!f.exists()) {
        				texture_index.put(tex2, texture_sheet.addSprite(texture_filename));
        				} else { texture_index.put(tex2, texture_sheet.addSprite("data/minecraft/assets/" + tempspace + "/textures/block/stone.png")); }
        		}
        	}

        	if ( texture_sheet == null ) {
        		
        		objWriter.write("usemtl " + tex2 + "\n");
        		}
        	
        	if ( face.get("tintindex") != null ) {
        		if ( (long) face.get("tintindex") == 0 ) { 
        			HashMap<String, Integer> sprite = texture_sheet.sprites.get(texture_index.get(tex2));
        				
        			if ( sprite.get("AE_hue_shifted") == null) {
        				texture_sheet.editHSV(texture_index.get(tex2), ((float) 96f/360f), .6f, -1);
        				System.out.println("Tinting " + tex2);
        				sprite.put("AE_hue_shifted", 1);
        			}
        		}
        	}
        	
        	
        	//Write the actual face's vertices
        	coords = createFace(from, to, face_name);
        	
        	//Rotate / flip
        	coords.rotate(8, 8, 8, rot_x, rot_z, rot_y-90);
        	coords.scale(1, 1, -1);
        	
        	//objWriter.write("v "+((coords.x1/16)+x) +" "+((coords.y1/16)+y)+" "+((coords.z1/16)+z)+ "\n");
        	//objWriter.write("v "+((coords.x2/16)+x) +" "+((coords.y2/16)+y)+" "+((coords.z2/16)+z)+ "\n");
        	//objWriter.write("v "+((coords.x3/16)+x) +" "+((coords.y3/16)+y)+" "+((coords.z3/16)+z)+ "\n");
        	//objWriter.write("v "+((coords.x4/16)+x) +" "+((coords.y4/16)+y)+" "+((coords.z4/16)+z)+ "\n");
        	array_cached.add("v "+((coords.x1/16)+x) +" "+((coords.y1/16)+y)+" "+((coords.z1/16)+z)+ "\n");
        	array_cached.add("v "+((coords.x2/16)+x) +" "+((coords.y2/16)+y)+" "+((coords.z2/16)+z)+ "\n");
        	array_cached.add("v "+((coords.x3/16)+x) +" "+((coords.y3/16)+y)+" "+((coords.z3/16)+z)+ "\n");
        	array_cached.add("v "+((coords.x4/16)+x) +" "+((coords.y4/16)+y)+" "+((coords.z4/16)+z)+ "\n");
        	
    	    JSONArray uv = (JSONArray) face.get("uv");
    	    
    	     
    	    //NOTE: add support for different model texture resolutions
    	    int tex_w = 16;
    	    int tex_h = 16;
    	    int true_texture_width = 16; // TODO: replace with image width / height
	    	int true_texture_height = 16;
    	    
    	    if ( texture_sheet != null ) {
    	    	tex_w = texture_sheet.width;
    	    	tex_h = texture_sheet.height;
    	    	
    	    	true_texture_width = texture_sheet.sprites.get(texture_index.get(tex2)).get("w");
    	    	true_texture_height = texture_sheet.sprites.get(texture_index.get(tex2)).get("h");
    	    } 
    	     
    	    //Base UV values
    	    Double uv_x1 = (double) 0;
    	    Double uv_y1 = (double) 0;
    	    Double uv_x2 = (double) 16;
    	    Double uv_y2 = (double) 16;
	    	 
	    	//If UV is set in JSON:
    	    if (uv != null) {
    	    	Object uv_x1_ = uv.get(0);
    	    	Object uv_y1_ = uv.get(1);
    	    	Object uv_x2_ = uv.get(2);
    	    	Object uv_y2_ = uv.get(3);
    	    	 
    	    	uv_x1 = (Double) Double.parseDouble(uv_x1_.toString());
    	       	uv_y1 = (Double) Double.parseDouble(uv_y1_.toString());
    	       	uv_x2 = (Double) Double.parseDouble(uv_x2_.toString());
    	       	uv_y2 = (Double) Double.parseDouble(uv_y2_.toString());
    	       	
    	       	// UV coords need to be scaled as if they are 16*16. 
        	    //If a texture is 8*8 for example, 8 would need to become 16.
    	       	//System.out.println(true_texture_width);
    	       	//System.out.println(uv_x1);
    	       	if (true_texture_width < 16) { 
    	       		uv_x1 *=(16/true_texture_width);
        	    	uv_y1 *=(16/true_texture_height);
        	    	uv_x2 *=(16/true_texture_width);
        	    	uv_y2 *=(16/true_texture_height);
    	       		} else {
    	       		uv_x1 *=(true_texture_width/16);
            	   	uv_y1 *=(true_texture_height/16);
            	   	uv_x2 *=(true_texture_width/16);
            	   	uv_y2 *=(true_texture_height/16);
    	       		}
    	      
        	    //System.out.println(uv_x1);
    	     	}
    	    if (face_name == "east" | face_name == "west" | face_name == "south" | face_name == "north") {
    	    	 uv_y2-= uv_y1;
    	         uv_y1-= uv_y1;
    	     	}
    	    
    	    if ( texture_sheet != null ) {
    	    	uv_x1 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
    	    	uv_y1 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y");// - (tex_h);
    	    	uv_x2 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
    	    	uv_y2 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y");// - (tex_h);
    	    	
    	    	//uv_y1 *=-1;
    	    	//uv_y2 *=-1;

    	    }
    	    
    	     uv_x1 /=tex_w;
    	     uv_y1 /=tex_h;
    	     uv_x2 /=tex_w;
    	     uv_y2 /=tex_h;
    	     
    	     
    	     //System.out.println("uv_x1 " + uv_x1.floatValue() + "uv_x2 " + uv_x2.floatValue());
    	     //System.out.println("uv_y1 " + uv_y1.floatValue() + "uv_y2 " + uv_y2.floatValue());
    	     //System.out.println("w " + tex_w + "h " + tex_h);
    	     
    	     
    	     if (preview) {
    	    	 
    	     float col = this.color;
    	     switch (face_name) {
     			case "north": col -= .2	; break;
     			case "south": col -= .2	; break;
     			case "east":  col -= .3	; break;
     			case "west":  col -= .3	; break;
     			case "up":	  col -= .0	; break;
     			case "down":  col -= .4	; break;
     			}
    	     
    	 
    	     if (col < 0 ) col = 0;
    	     /*
    	     		System.out.println("X " + coords.x1 + " Y " + coords.y1 + " Z " + coords.z1);
    	     		System.out.println("X " + coords.x2 + " Y " + coords.y2 + " Z " + coords.z2);
    	     		System.out.println("X " + coords.x3 + " Y " + coords.y3 + " Z " + coords.z3);
    	     		System.out.println("X " + coords.x4 + " Y " + coords.y4 + " Z " + coords.z4);
    	     		System.out.println(face_name);
    	     		System.out.println(vc);
    	     */
    	     //x1-y2;x2-y2;x2-y1;x1-y1;
    	     
    	     }
    	    
    	     //Don't need to cache UV coordinates because you only write them once
    	     if (!uvlock) {
    	    	 /*
    	     	switch (face_name) {
    	     		case "north":
    	     			objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
        	     		objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
        	     		objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
        	     		objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
            			break;
            		case "west":
            			objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
            			break;
            		case "south":
            			objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
            	     	objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
            			break;
            		case "east":
            			objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
            	     	objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
            			break;
            		case "up":
            			objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
            	     	objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
            			break;
            		case "down":
            			objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
            	     	objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
            	     	objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
            			break;
                 
            		}
            		*/
    	     	objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
	     		objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
	     		objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
	     		objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
    	     	} else {
    	     		//Faces for UVlock
    	     		//TODO: may need to rewrite this all.
    	     		
    	     				double lock_u1=0, lock_u2=0, lock_u3=0, lock_u4=0;
    	     				double lock_v1=0, lock_v2=0, lock_v3=0, lock_v4=0;
    	     				
    	     				//if z same, north/south
    	     				//if y same, up/down
    	     				//if x same, east/west
    	     				
    	     				if (Utility.match4(coords.z1, coords.z2, coords.z3, coords.z4)) {
    	     					lock_u1 = coords.x1; lock_v1 = coords.y1;
    	     					lock_u2 = coords.x2; lock_v2 = coords.y2;
    	     					lock_u3 = coords.x3; lock_v3 = coords.y3;
    	     					lock_u4 = coords.x4; lock_v4 = coords.y4;
    	     					//System.out.println("ns");
    	     					}
    	     				
    	     				//U/D
    	     				if (Utility.match4(coords.y1, coords.y2, coords.y3, coords.y4)) {
    	     					lock_u1 = coords.z1; lock_v1 = coords.x1;
    	     					lock_u2 = coords.z2; lock_v2 = coords.x2;
    	     					lock_u3 = coords.z3; lock_v3 = coords.x3;
    	     					lock_u4 = coords.z4; lock_v4 = coords.x4;
    	     					//System.out.println("ud");
    	     					}
    	     				
    	     				//E/W
    	     				if (Utility.match4(coords.x1, coords.x2, coords.x3, coords.x4)) {
    	     					lock_u1 = coords.z1; lock_v1 = coords.y1;
    	     					lock_u2 = coords.z2; lock_v2 = coords.y2;
    	     					lock_u3 = coords.z3; lock_v3 = coords.y3;
    	     					lock_u4 = coords.z4; lock_v4 = coords.y4;
    	     					//System.out.println("ew");
    	     					}
    	     				
    	     				
    	     				if ( texture_sheet != null ) {
    	     					int w = texture_sheet.sprites.get(texture_index.get(tex2)).get("w");
    	     					int h = texture_sheet.sprites.get(texture_index.get(tex2)).get("h");
    	     					
    	     					double anchor = Utility.leastOf(new double[] {lock_u1, lock_u2, lock_u3, lock_u4});
    	    	     			
    	     					double diff;
    	     					diff = lock_u1 - anchor;
    	     					lock_u1 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					diff = lock_u2 - anchor;
    	     					lock_u2 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					diff = lock_u3 - anchor;
    	     					lock_u3 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					diff = lock_u4 - anchor;
    	     					lock_u4 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					anchor = Utility.leastOf(new double[] {lock_v1, lock_v2, lock_v3, lock_v4});
    	     					
    	     					diff = lock_v1 - anchor;
    	     					lock_v1 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					diff = lock_v2 - anchor;
    	     					lock_v2 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					diff = lock_v3 - anchor;
    	     					lock_v3 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					diff = lock_v4 - anchor;
    	     					lock_v4 = Math.floorMod((int) anchor, w) + diff;
    	     					
    	     					
    	     					
    	     					
    	     					//System.out.println("lock_u1: " + lock_u1 + " modulo: " + Math.floorMod((int) lock_u1, w) + " w: " + w);
    	     					/*lock_u1 = Math.floorMod((int) lock_u1, w); //(((lock_u1 % w) + w) % w);
    	     					lock_u2 = Math.floorMod((int) lock_u2, w); //(((lock_u2 % w) + w) % w);
    	     					lock_u3 = Math.floorMod((int) lock_u3, w); //(((lock_u3 % w) + w) % w);
    	     					lock_u4 = Math.floorMod((int) lock_u4, w); //(((lock_u4 % w) + w) % w);
    	     					
    	     					lock_v1 = Math.floorMod((int) lock_v1, w); //(((lock_v1 % w) + w) % h);
    	     					lock_v2 = Math.floorMod((int) lock_v2, w); //(((lock_v2 % w) + w) % h);
    	     					lock_v3 = Math.floorMod((int) lock_v3, w); //(((lock_v3 % w) + w) % h);
    	     					lock_v4 = Math.floorMod((int) lock_v4, w); //(((lock_v4 % w) + w) % h);
    	     					 */
    	     					
	        				    lock_u1 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
	        				    lock_u2 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
	        				    lock_u3 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
	        				    lock_u4 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
	        				    
	        				    lock_v1 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y") - (tex_h-16);
	        				    lock_v2 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y") - (tex_h-16);
	        				    lock_v3 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y") - (tex_h-16);
	        				    lock_v4 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y") - (tex_h-16);
								
								//broken

	        	    	    }
	        			
    	     				
    	     				if (lock_u1 != 0) {  lock_u1 = (lock_u1/tex_w); }
    	        			if (lock_u2 != 0) {  lock_u2 = (lock_u2/tex_w); }
    	        			if (lock_u3 != 0) {  lock_u3 = (lock_u3/tex_w); }
    	        			if (lock_u4 != 0) {  lock_u4 = (lock_u4/tex_w); }
    	        			
    	        			if (lock_v1 != 0) {  lock_v1 = (lock_v1/tex_h); }
    	        			if (lock_v2 != 0) {  lock_v2 = (lock_v2/tex_h); }
    	        			if (lock_v3 != 0) {  lock_v3 = (lock_v3/tex_h); }
    	        			if (lock_v4 != 0) {  lock_v4 = (lock_v4/tex_h); }
    	        			
    	        			 
    	        			objWriter.write("# see \n");
    	        			objWriter.write("vt "+lock_u1+" "+lock_v1+" "+ "\n");
    	        			objWriter.write("vt "+lock_u2+" "+lock_v2+" "+ "\n");
    	        			objWriter.write("vt "+lock_u3+" "+lock_v3+" "+ "\n");
    	        			objWriter.write("vt "+lock_u4+" "+lock_v4+" "+ "\n");
    	     	}
    	     
    	     /*objWriter.write("f " 
    	    		 		+ (v_count-3) + "/" + (v_count-3) + " " 
    	    		 		+ (v_count-2) + "/" + (v_count-2) + " " 
    	    		 		+ (v_count-1) + "/" + (v_count-1) + " " 
    	    		 		+ v_count + "/" + v_count + " \n"); 
    	    */
    	     array_cached.add("f " 
    	    		 		+ (v_count-3) + "/" + (vt_count-3) + " " 
    	    		 		+ (v_count-2) + "/" + (vt_count-2) + " " 
    	    		 		+ (v_count-1) + "/" + (vt_count-1) + " " 
    	    		 		+ v_count + "/" + vt_count + " \n");
            
    	     //v_count +=4;
    	     vt_count+=4;
    	     
        	}
    	     
    	     i_faces+=1;
        	 } while (i_faces < 6);
    	     
        	
        	i+=1	;	
         	} while (i < elements.size());

         
     } catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     } catch (ParseException e) {
         e.printStackTrace();
     }
	
	String[] arr = new String[array_cached.size()];
    Object[] array = array_cached.toArray();
    
    int iarr = 0;
    while(iarr < array.length) {
    	arr[iarr] = (String) array[iarr];
        iarr++;
        //System.out.println(iarr);
    }
	cache.put(cachepath, arr);
	  
	 return 1;
	}

	
	public int WriteFromBlockstate(String path, String states, int x, int y, int z, HashMap<String, Boolean> Culling) throws IOException, ParseException {
		//System.out.println(" path= " + path + " state= " + states);
		//path = "data\\resourcepack\\" + path;
		
		//File tmpDir = new File(path);
		//boolean exists;
		//if (!exists) { System.out.println("Blockstate file does not exist."); return -1; }
		
		path = FileHierarchy(path);
		if (path == null) System.out.println("Blockstate file does not exist."); 
		//System.out.println("Blockstate: " + path);
		
		//JSON parser object to parse read file
	    JSONParser jsonParser = new JSONParser();
		FileReader reader_ = new FileReader(path);
		JSONObject model = (JSONObject) jsonParser.parse(reader_);
		
		//If variants exist
		if (model.get("variants") != null) {
		JSONObject variants = (JSONObject) model.get("variants");
		
		String model_name = "";
		Object state_obj = null;
		JSONObject state = null;
		
		if (!states.isEmpty()) {
		
		//Iter variants
		for (Object keyO : variants.keySet()) {
	    	String key = (String)keyO;
            
            if (!key.isEmpty()) {
            JSONObject states_this = Utility.StatesToObject(key); 
            	
	    	//JSONObject statestoobj = null;
            //If the states match
            JSONObject statestoobj = Utility.StatesToObject((String) states);
            if (Utility.JSObjectMatches(statestoobj,states_this)) { 
            	state_obj = variants.get(key); 
            	break;
            	}
            states_this.clear(); //Reset states jobject, to loop again.
			} else state_obj = variants.get(""); 
            }
		
		} else { state_obj = variants.get(""); } //If state is "", use "".
		
		if (state_obj instanceof JSONArray) {
			JSONArray state_arr = (JSONArray) state_obj;
			int random = ran.nextInt(state_arr.size());

            state = (JSONObject) state_arr.get(random);
		} else state = (JSONObject) state_obj;
		
	
		if (state != null) {
		model_name = (String) state.get("model");
		Object xr_ = state.get("x");
		Object yr_ = state.get("y");
		Object zr_ = state.get("z");
		
		if (xr_ == null) { xr_ = 0; }
		if (yr_ == null) { yr_ = 0; }
		if (zr_ == null) { zr_ = 0; }
		Double xr = (Double) Double.parseDouble(xr_.toString());
		Double yr = (Double) Double.parseDouble(yr_.toString());
		Double zr = (Double) Double.parseDouble(zr_.toString());
		
		Object uvlock_ = state.get("uvlock");
		if (uvlock_ == null) uvlock_ = false;
		Boolean uvlock = (Boolean) uvlock_;
		
		//System.out.println(states);
		//System.out.println(model_name);
		
		//System.out.println(state);
		String tempspace = namespace;
		if (model_name.indexOf(":") != -1) model_name.substring(0, model_name.indexOf(":"));
		addModel("assets\\" + tempspace + "\\models\\" + model_name.substring(model_name.indexOf(":")+1).replace('/', '\\') + ".json", x,y,z, xr, yr, zr, Culling, uvlock); 
		} else System.out.println("Model had no Variants: " + path);
		}
		
		if (model.get("multipart") != null) { 
			JSONArray parts = (JSONArray) model.get("multipart");
			
			//Iter parts
			for (int i = 0; i < parts.size(); i++) {
				JSONObject current = (JSONObject) parts.get(i);
				boolean true_ = false;
				
				//If there is no when condition, override.
				if (current.get("when") == null) true_ = true;
				if (current.get("when") != null) {
					//Check if when condition matches blockstate
					JSONObject when = (JSONObject) current.get("when");
					JSONObject statestoobj = Utility.StatesToObject((String) states);
					if (Utility.JSObjectMatches(statestoobj,when)) { 
			            true_ = true;
			            } else true_ = false;
				}
				
				//If true, write that model.
				if (true_) {
					JSONObject state;
					Object state_obj = current.get("apply");
					
					if (state_obj instanceof JSONArray) {
						JSONArray state_arr = (JSONArray) state_obj;
						int random = ran.nextInt(state_arr.size());

			            state = (JSONObject) state_arr.get(random);
					} else state = (JSONObject) state_obj;
					
					if (state != null) {
						String model_name = "";
						model_name = (String) state.get("model");
						Object xr_ = state.get("x");
						Object yr_ = state.get("y");
						Object zr_ = state.get("z");
						
						if (xr_ == null) { xr_ = 0; }
						if (yr_ == null) { yr_ = 0; }
						if (zr_ == null) { zr_ = 0; }
						Double xr = (Double) Double.parseDouble(xr_.toString());
						Double yr = (Double) Double.parseDouble(yr_.toString());
						Double zr = (Double) Double.parseDouble(zr_.toString());
						
						Object uvlock_ = state.get("uvlock");
						if (uvlock_ == null) uvlock_ = false;
						Boolean uvlock = (Boolean) uvlock_;
						
						//System.out.println(states);
						//System.out.println(model_name);
						
						//System.out.println(state);
						String tempspace = namespace;
						if (model_name.indexOf(":") != -1) model_name.substring(0, model_name.indexOf(":"));
						addModel("assets\\" + tempspace + "\\models\\" + model_name.substring(model_name.indexOf(":")+1).replace('/', '\\') + ".json", x,y,z, xr, yr, zr, Culling, uvlock); 
						} else System.out.println("Multipart failed to load: " + path);
				}
				
				}
	            
			
		}
		
		
	return 1;
	}
		
	/**
	Create the vertices for a specific face on a cube.
	@param from A 3D point representing the start XYZ position
	@param to A 3D point representing the end XYZ position
	@param face_name north, south, east, west, up, or down.
	@return Quad
	*/
	public Quad createFace(Point3D from, Point3D to, String face_name) {
		
		//Create a quad with the vertices of a face on a cube.

		Quad coords = null;
		
    	switch (face_name) {
    	case "north":
			coords = new Quad(	  from.x, to.y, from.z,
			 							  to.x, to.y, from.z, 
			 							  to.x, from.y, from.z, 
			 							  from.x, from.y, from.z);
    			break;
    		case "east":
    			coords = new Quad(		to.x, to.y, to.z,
    											to.x, to.y, from.z, 
    											to.x, from.y, from.z, 
    											to.x, from.y, to.z);
    			break;
    		case "south":
    			coords = new Quad(		from.x, to.y, to.z,
    											to.x, to.y, to.z, 
    											to.x, from.y, to.z, 
    											from.x, from.y, to.z);
    			break;
    		case "west":
    			coords = new Quad(	  from.x, to.y, to.z,
    										  from.x, to.y, from.z, 
    			 							  from.x, from.y, from.z, 
    			 							  from.x, from.y, to.z);
    			break;
    		case "up":
    			coords = new Quad(	from.x, to.y, to.z,
						  					to.x, to.y, to.z, 
						  					to.x, to.y, from.z, 
						  					from.x, to.y, from.z);
    			break;
    		case "down":
    			coords = new Quad(	from.x, from.y, to.z,
    			 							to.x, from.y, to.z, 
    			 							to.x, from.y, from.z, 
    			 							from.x, from.y, from.z);
    		
    			break;
         
    		}
    	
    	return coords;	
	}
	

	
}