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
import io.github.jakethe28th.engine.graphics.Texture;
import io.github.jakethe28th.engine.graphics.Vertex;
import io.github.jakethe28th.engine.graphics.gui.Sprite;
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
	
	public int v_count = 4;
	public int vt_count = 4;
	
	public String namespace = "minecraft";
	
	public Random ran = new Random();
	
	public Mesh myMesh;
	public void end() {
		try {
		objWriter.close();
		mtlWriter.close();
		if (texture_sheet != null) {
			texture_sheet.save(filename + ".png");
			texture_sheet.cleanup();
			try {
				this.myMesh.setTexture(new Texture(filename + ".png"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		System.out.println("Closed MC2OBJ inst");
		 	} catch (IOException e) {
		      System.out.println("An error occurred when closing filewriter..");
		      e.printStackTrace();
		    }
		    
	}
	
	public Sprite texture_sheet;
	public HashMap<String, Integer> texture_index;
	
	
	public WriteBlock(String filename_, Sprite texture_sheet) {
		filename = filename_;
		
		this.texture_sheet = texture_sheet;
		this.texture_index = new HashMap<String, Integer>();
		
		this.myMesh = new Mesh(new Vertex[] {}, new int[] {}, null);	
		
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

	
	
	public int WriteModel(String path, int x, int y, int z, Double rot_x, Double rot_y, Double rot_z, HashMap<String, Boolean> Culling, Boolean uvlock, String namespace) {
	this.namespace = namespace;
		
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
		String parent_path = FileHierarchy("assets\\" + namespace + "\\models\\" + parent.substring(parent.indexOf(":")+1) + ".json");
		if (parent_path == null) System.out.println("Parent file does not exist."); 
		
		//ADD NAMESPACE HERE. currently just chops off minecraft:
		
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
        		case 1: face_name = "south"; break;
        		case 2: face_name = "east"; break;
        		case 3: face_name = "west"; break;
        		case 4: face_name = "up"; break;
        		case 5: face_name = "down"; break;
        		}
        		 
        	JSONObject face = (JSONObject) faces.get(face_name);
        	Quad coords = null;
        	
        	if (face != null && Culling.get(face_name)) {
        		
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

        	if ( texture_sheet == null ) {	
        		if ( Utility.ArrayIndexOf(mtl_done, tex2) == -1) {
        			mtlWriter.write("newmtl " + tex2 + "\n");
        			mtlWriter.write("map_Kd  " + FileHierarchy("assets/" + namespace + "/textures/" + tex3 + ".png") + "\n");
        	
        			mtl_done.add(tex2);
        		}
        	} else {
        		//Add texture to sheet
        		
        		String texture_filename = FileHierarchy("assets/" + namespace + "/textures/" + tex3 + ".png");//.replace("\"", "/");
        		if (texture_filename !=null && texture_index.get(tex2) == null ) {
        			System.out.println(" Adding sprite " + texture_filename);
        			File f = new File(texture_filename + ".mcmeta");
        			if (!f.exists()) {
        				texture_index.put(tex2, texture_sheet.addSprite(texture_filename));
        				} else { texture_index.put(tex2, texture_sheet.addSprite("data/minecraft/assets/" + namespace + "/textures/block/stone.png")); }
        		}
        	}

        	if ( texture_sheet == null ) {
        		
        		objWriter.write("usemtl " + tex2 + "\n");
        		
        		}
        	
        	
        	//Write the actual face's vertices
        	coords = createFace(from, to, face_name);
        	
        	//Rotate / flip
        	coords.rotate(8, 8, 8, rot_x, rot_z, rot_y-90);
        	coords.scale(1, 1, -1);
        	
        	objWriter.write("v "+((coords.x1/16)+x) +" "+((coords.y1/16)+y)+" "+((coords.z1/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x2/16)+x) +" "+((coords.y2/16)+y)+" "+((coords.z2/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x3/16)+x) +" "+((coords.y3/16)+y)+" "+((coords.z3/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x4/16)+x) +" "+((coords.y4/16)+y)+" "+((coords.z4/16)+z)+ "\n");
  	     
    	     
    	    JSONArray uv = (JSONArray) face.get("uv");
    	    
    	     
    	    //NOTE: add support for different model texture resolutions
    	    int tex_w = 16;
    	    int tex_h = 16;
    	    
    	    if ( texture_sheet != null ) {
    	    	tex_w = texture_sheet.width;
    	    	tex_h = texture_sheet.height;
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
    	     	}
    	    if (face_name == "east" | face_name == "west" | face_name == "south" | face_name == "north") {
    	    	 uv_y2-= uv_y1;
    	         uv_y1-= uv_y1;
    	     	}
    	    
    	    if ( texture_sheet != null ) {
    	    	uv_x1 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
    	    	uv_y1 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y") - (tex_h-16);
    	    	uv_x2 += texture_sheet.sprites.get(texture_index.get(tex2)).get("x");
    	    	uv_y2 -= texture_sheet.sprites.get(texture_index.get(tex2)).get("y") - (tex_h-16);

    	    }
    	     

    	     uv_x1 /=tex_w;
    	     uv_y1 /=tex_h;
    	     uv_x2 /=tex_w;
    	     uv_y2 /=tex_h;
    	     
    	     
    	     //System.out.println("uv_x1 " + uv_x1.floatValue() + "uv_x2 " + uv_x2.floatValue());
    	     //System.out.println("uv_y1 " + uv_y1.floatValue() + "uv_y2 " + uv_y2.floatValue());
    	     //System.out.println("w " + tex_w + "h " + tex_h);
    	     
    	     int vc = myMesh.getVertices().length;
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
    	     
    	     this.myMesh.addVertices(new Vertex[] {
     	    		new Vertex(new 	Vector3f((coords.x1.floatValue()/16)+x,
     	    								 (coords.y1.floatValue()/16)+y,
     	    								 (coords.z1.floatValue()/16)+z), 
     	    								 new Vector3f(col, col, col), 
     	    								 new Vector2f(uv_x1.floatValue(), uv_y2.floatValue()*-1)),
     	    		
     	    		new Vertex(new 	Vector3f((coords.x2.floatValue()/16)+x,
     	    								 (coords.y2.floatValue()/16)+y,
     	    								 (coords.z2.floatValue()/16)+z), 
     	    								 new Vector3f(col, col, col), 
     	    								 new Vector2f(uv_x2.floatValue(), uv_y2.floatValue()*-1)),
     	    		
     	    		new Vertex(new 	Vector3f((coords.x3.floatValue()/16)+x,
     	    								 (coords.y3.floatValue()/16)+y,
     	    								 (coords.z3.floatValue()/16)+z), 
     	    								 new Vector3f(col, col, col), 
     	    								 new Vector2f(uv_x2.floatValue(), uv_y1.floatValue()*-1)),
     	    		
     	    		new Vertex(new 	Vector3f((coords.x4.floatValue()/16)+x,
     	    								 (coords.y4.floatValue()/16)+y,
     	    								 (coords.z4.floatValue()/16)+z), 
 							 				 new Vector3f(col, col, col), 
 							 				 new Vector2f(uv_x1.floatValue(), uv_y1.floatValue()*-1)),
     	    			
     	    		}, new int[] {vc+0, vc+1, vc+2, vc+0, vc+3, vc+2 });
    	     /*
    	     		System.out.println("X " + coords.x1 + " Y " + coords.y1 + " Z " + coords.z1);
    	     		System.out.println("X " + coords.x2 + " Y " + coords.y2 + " Z " + coords.z2);
    	     		System.out.println("X " + coords.x3 + " Y " + coords.y3 + " Z " + coords.z3);
    	     		System.out.println("X " + coords.x4 + " Y " + coords.y4 + " Z " + coords.z4);
    	     		System.out.println(face_name);
    	     		System.out.println(vc);
    	     */
    	     //x1-y2;x2-y2;x2-y1;x1-y1;
    	    
    	     if (!uvlock ) {
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
    	     				
    	     				if (lock_u1 != 0) {  lock_u1 = (lock_u1/tex_w); }
    	        			if (lock_u2 != 0) {  lock_u2 = (lock_u2/tex_w); }
    	        			if (lock_u3 != 0) {  lock_u3 = (lock_u3/tex_w); }
    	        			if (lock_u4 != 0) {  lock_u4 = (lock_u4/tex_w); }
    	        			
    	        			if (lock_v1 != 0) {  lock_v1 = (lock_v1/tex_h); }
    	        			if (lock_v2 != 0) {  lock_v2 = (lock_v2/tex_h); }
    	        			if (lock_v3 != 0) {  lock_v3 = (lock_v3/tex_h); }
    	        			if (lock_v4 != 0) {  lock_v4 = (lock_v4/tex_h); }
    	        			
    	        			 if ( texture_sheet != null ) {
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
    	        			
    	        			
    	        			objWriter.write("vt "+lock_u1+" "+lock_v1+" "+ "\n");
    	        			objWriter.write("vt "+lock_u2+" "+lock_v2+" "+ "\n");
    	        			objWriter.write("vt "+lock_u3+" "+lock_v3+" "+ "\n");
    	        			objWriter.write("vt "+lock_u4+" "+lock_v4+" "+ "\n");
    	     	}
    	     
    	     objWriter.write("f " 
    	    		 		+ (v_count-3) + "/" + (v_count-3) + " " 
    	    		 		+ (v_count-2) + "/" + (v_count-2) + " " 
    	    		 		+ (v_count-1) + "/" + (v_count-1) + " " 
    	    		 		+ v_count + "/" + v_count + " \n"); 
            
    	     v_count +=4;
    	     
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
	  
	 return 1;
	}

	
	public int WriteFromBlockstate(String path, String states, int x, int y, int z, HashMap<String, Boolean> Culling, String namespace) throws IOException, ParseException {
		this.namespace = namespace;
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
		WriteModel("assets\\" + namespace + "\\models\\" + model_name.substring(model_name.indexOf(":")+1).replace('/', '\\') + ".json", x,y,z, xr, yr, zr, Culling, uvlock, namespace); 
		} else System.out.println("Model had no Variants: " + path);
		}
		
		if (model.get("multipart") != null) { }
		
		
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