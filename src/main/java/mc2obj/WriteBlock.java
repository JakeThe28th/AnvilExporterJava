package mc2obj;

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
import java.util.List;

import java.math.*;

//delete quad somehow?

//Input: block model, xyz positon, optional rotation, mtl file, obj file
public class WriteBlock {
	
	public String filename;
	public FileWriter objWriter;
	public FileWriter mtlWriter;
	
	public ArrayList<String> mtl_done = new ArrayList<String>();
	
	public int v_count = 4;
	public int vt_count = 4;
	
	public Random ran = new Random();
	
	public void end() {
		try {
		objWriter.close();
		mtlWriter.close();
		System.out.println("Closed MC2OBJ inst");
		 	} catch (IOException e) {
		      System.out.println("An error occurred when closing filewriter..");
		      e.printStackTrace();
		    }
		    
	}
	
	public boolean match4(double a, double b, double c, double d) {
		
		if (a != b) return false;
		if (b != c) return false;
		if (c != d) return false;
		//System.out.println("winner");
		return true;
	}
	
	public WriteBlock(String filename_) {
		filename = filename_;
		
		//Create file. obj.
		  try {
		      File myObj = new File(filename + ".obj");
		      if (myObj.createNewFile()) {
		        System.out.println("File created: " + myObj.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred starting a writeblock.");
		      e.printStackTrace();
		    }
		    
		    
		    try {
		    	FileWriter myWriter_ = new FileWriter(filename + ".obj");
		    	objWriter = myWriter_;
		        
		    	objWriter.write("# obj made with AnvilExporterJava \n");
		        //myWriter.close();
		        //System.out.println("Successfully wrote to the file.");
		      } catch (IOException e) {
		        System.out.println("An error occurred writing.");
		        e.printStackTrace();
		      }
		    
		    //
		    
		  //Create file.
			  try {
			      File myObj = new File(filename + ".mtl");
			      if (myObj.createNewFile()) {
			        System.out.println("File created: " + myObj.getName());
			      } else {
			        System.out.println("File already exists.");
			      }
			    } catch (IOException e) {
			      System.out.println("An error occurred starting a writeblock.");
			      e.printStackTrace();
			    }
			    
			    
			    try {
			    	FileWriter myWriter_ = new FileWriter(filename + ".mtl");
			    	mtlWriter = myWriter_;
			        
			    	mtlWriter.write("# obj made with AnvilExporterJava \n");
			        //myWriter.close();
			        //System.out.println("Successfully wrote to the file.");
			      } catch (IOException e) {
			        System.out.println("An error occurred writing.");
			        e.printStackTrace();
			      }
			    
			    
	}
	
	
	public JSONObject deepMerge(JSONObject source, JSONObject target) {
	    for (Object keyO : source.keySet()) {
	    	String key = (String)keyO;
            Object value = source.get(key);
            
            //System.out.println(key);
            
            if (target.get(key) != null) {
                // new value for "key":
            	if (value instanceof JSONObject) {
                    deepMerge((JSONObject) value, (JSONObject) target.get(key));
            		} else target.put(key, value);
            	
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JSONObject && target.get(key) != null) {
                    JSONObject valueJson = (JSONObject)value;
                   deepMerge(valueJson, (JSONObject) target.get(key));
                  } else {
                  target.put(key, value);
                }
            }
	    }
    return target;
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
				System.out.println("file does not exist."); return null; 
				}
			if (exists) break;
			
			attempts+=1;
			}
			path = path_;
		
		//System.out.println("Filename Hierarchy: " + (String) path);
		return path;
		}

	public static int ArrayIndexOf(List<String> arr, String str) {
	    for (int i = 0; i < arr.size(); i++) {
	        if (str.equals(arr.get(i))) return i;
	    }
	    return -1;
	}
	
	public int WriteModel(String path, int x, int y, int z, Double rot_x, Double rot_y, Double rot_z, JSONObject Culling, Boolean uvlock) {
		
	path = FileHierarchy(path);
	if (path == null) System.out.println("Model file does not exist."); 
	
	
	//System.out.println("Filename: " + (String) path);
	

	//if (rot_y == 90) {
	//	rot_z = rot_x;
	//	rot_x = (double) 0;
	//	}
	
	//System.out.println(rot_x);
	//System.out.println(rot_y);
	//System.out.println(rot_z);
	
	//Code body
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
		String parent_path = FileHierarchy("assets\\minecraft\\models\\" + parent.substring(parent.indexOf(":")+1) + ".json");
		if (parent_path == null) System.out.println("Parent file does not exist."); 
		
		//ADD NAMESPACE HERE. currently just chops off minecraft:
		
		FileReader parentReader = new FileReader(parent_path); 
		Object parent_model = jsonParser.parse(parentReader);
		
		model.remove("parent"); //Remove previous parent value before merging, otherwise this code would loop forever.

        if (parent_model != null) model = deepMerge((JSONObject) model, (JSONObject) parent_model);
		
		parent = (String) model.get("parent");
		}
		} while (parent != null);
		
		
        JSONArray elements = (JSONArray) model.get("elements");
        JSONObject textures = (JSONObject) model.get("textures");
        

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
        	Double from_z = (Double) Double.parseDouble(from_z_.toString())/-1;
        	
        	Double to_x = (Double) Double.parseDouble(to_x_.toString());
        	Double to_y = (Double) Double.parseDouble(to_y_.toString());
        	Double to_z = (Double) Double.parseDouble(to_z_.toString())/-1;
        	
        	Point3D from = new Point3D(from_x, from_y, from_z);
        	Point3D to = new Point3D(to_x, to_y, to_z);
        	
        	//from.rotate(8, 8, 8, rot_x, rot_x, (rot_y-90));
        	//to.rotate(8, 8, 8, rot_x, rot_x, (rot_y-90));
        
        	
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
        	mc2obj.Quad coords = null;
        	
        	if (face != null) {
        		
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
        	
        	//System.out.println("data/minecraft/textures/" + tex2 + ".png");
        	//System.out.println("data/minecraft/textures/" + tex + ".png");
        	
        	if ( ArrayIndexOf(mtl_done, tex2) == -1) {
        	mtlWriter.write("newmtl " + tex2 + "\n");
        	mtlWriter.write("map_Kd  " + "data/minecraft/textures/" + tex2 + ".png" + "\n");
        	
        	mtl_done.add(tex2);
        	}

        	objWriter.write("usemtl " + tex2 + "\n");
        	
        	//Write the actual faces vertices
        	switch (face_name) {
        	case "north":
    			coords = new mc2obj.Quad(	  from.x, to.y, from.z,
    			 							  to.x, to.y, from.z, 
    			 							  to.x, from.y, from.z, 
    			 							  from.x, from.y, from.z);
        			break;
        		case "east":
        			coords = new mc2obj.Quad(		to.x, to.y, to.z,
        											to.x, to.y, from.z, 
        											to.x, from.y, from.z, 
        											to.x, from.y, to.z);
        			break;
        		case "south":
        			coords = new mc2obj.Quad(		from.x, to.y, to.z,
        											to.x, to.y, to.z, 
        											to.x, from.y, to.z, 
        											from.x, from.y, to.z);
        			break;
        		case "west":
        			coords = new mc2obj.Quad(	  from.x, to.y, to.z,
        										  from.x, to.y, from.z, 
        			 							  from.x, from.y, from.z, 
        			 							  from.x, from.y, to.z);
        			break;
        		case "up":
        			coords = new mc2obj.Quad(	from.x, to.y, to.z,
							  					to.x, to.y, to.z, 
							  					to.x, to.y, from.z, 
							  					from.x, to.y, from.z);
        			break;
        		case "down":
        			coords = new mc2obj.Quad(	from.x, from.y, to.z,
        			 							to.x, from.y, to.z, 
        			 							to.x, from.y, from.z, 
        			 							from.x, from.y, from.z);
        		
        			break;
             
        		}
        	 
        	//Write the actual faces vertices to file
          	//objWriter.write("v "+coords.x1+" "+coords.y1+" "+coords.z1+ "\n");
        	//objWriter.write("v "+coords.x2+" "+coords.y2+" "+coords.z2+ "\n");
       	 	//objWriter.write("v "+coords.x3+" "+coords.y3+" "+coords.z3+ "\n");
       	 	//objWriter.write("v "+coords.x4+" "+coords.y4+" "+coords.z4+ "\n");
       	 
        	
        	
        	coords.rotate(8, 8, 8, rot_x, rot_z, rot_y-90);
        	//coords.scale(1, 1, -1);
        	
        	objWriter.write("v "+((coords.x1/16)+x) +" "+((coords.y1/16)+y)+" "+((coords.z1/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x2/16)+x) +" "+((coords.y2/16)+y)+" "+((coords.z2/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x3/16)+x) +" "+((coords.y3/16)+y)+" "+((coords.z3/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x4/16)+x) +" "+((coords.y4/16)+y)+" "+((coords.z4/16)+z)+ "\n");
  	     
    	     
    	     JSONArray uv = (JSONArray) face.get("uv");
    	    
    	     
    	     //NOTE: add support for different model texture resolutions
    	     int tex_w = 16;
    	     int tex_h = 16;
    	     
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
    	     
    	     uv_x1 /=tex_w;
    	     uv_y1 /=tex_h;
    	     uv_x2 /=tex_w;
    	     uv_y2 /=tex_h;
    	     
    	     //x1-y2;x2-y2;x2-y1;x1-y1;
    	     if (!uvlock ) {
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
    	     	} else {
    	     		//Faces for UVlock
    	     		/*
    	     		// east/west, use y and z as teycoords
	        		if (face_name=="east" || face_name=="west") {
	        			//use y/z as u/v
	        			double div_y1, div_y2, div_y3, div_y4;
	        			double div_z1, div_z2, div_z3, div_z4;
	        			if (coords.y1 == 0) {  div_y1 = 0; } else {  div_y1 = (coords.y1/tex_w); }
	        			if (coords.y2 == 0) {  div_y2 = 0; } else {  div_y2 = (coords.y2/tex_w); }
	        			if (coords.y3 == 0) {  div_y3 = 0; } else {  div_y3 = (coords.y3/tex_w); }
	        			if (coords.y4 == 0) {  div_y4 = 0; } else {  div_y4 = (coords.y4/tex_w); }
	        			
	        			if (coords.z1 == 0) {  div_z1 = 0; } else {  div_z1 = (coords.z1/tex_h); }
	        			if (coords.z2 == 0) {  div_z2 = 0; } else {  div_z2 = (coords.z2/tex_h); }
	        			if (coords.z3 == 0) {  div_z3 = 0; } else {  div_z3 = (coords.z3/tex_h); }
	        			if (coords.z4 == 0) {  div_z4 = 0; } else {  div_z4 = (coords.z4/tex_h); }
	        			
	        			objWriter.write("vt "+div_z1+" "+div_y1+" "+ "\n");
	        			objWriter.write("vt "+div_z2+" "+div_y2+" "+ "\n");
	        			objWriter.write("vt "+div_z3+" "+div_y3+" "+ "\n");
	        			objWriter.write("vt "+div_z4+" "+div_y4+" "+ "\n");
	        			}
	        		
    	     			
    	     			// up/down, use x and z as texcoords
    	        		if (face_name=="up" || face_name=="down") {
    	        			//use z/x as u/v
    	        			double div_x1, div_x2, div_x3, div_x4;
    	        			double div_z1, div_z2, div_z3, div_z4;
    	        			if (coords.x1 == 0) {  div_x1 = 0; } else {  div_x1 = (coords.x1/tex_w); }
    	        			if (coords.x2 == 0) {  div_x2 = 0; } else {  div_x2 = (coords.x2/tex_w); }
    	        			if (coords.x3 == 0) {  div_x3 = 0; } else {  div_x3 = (coords.x3/tex_w); }
    	        			if (coords.x4 == 0) {  div_x4 = 0; } else {  div_x4 = (coords.x4/tex_w); }
    	        			
    	        			if (coords.z1 == 0) {  div_z1 = 0; } else {  div_z1 = (coords.z1/tex_h); }
    	        			if (coords.z2 == 0) {  div_z2 = 0; } else {  div_z2 = (coords.z2/tex_h); }
    	        			if (coords.z3 == 0) {  div_z3 = 0; } else {  div_z3 = (coords.z3/tex_h); }
    	        			if (coords.z4 == 0) {  div_z4 = 0; } else {  div_z4 = (coords.z4/tex_h); }
    	        		
    	        			objWriter.write("vt "+div_z1+" "+div_x1+" "+ "\n");
    	        			objWriter.write("vt "+div_z2+" "+div_x2+" "+ "\n");
    	        			objWriter.write("vt "+div_z3+" "+div_x3+" "+ "\n");
    	        			objWriter.write("vt "+div_z4+" "+div_x4+" "+ "\n");
    	        			}
    	        		
    	        		// north/south, use x and y as texcoords
    	        		if (face_name=="north" || face_name=="south") {
    	        			//use x/y as u/v
    	        			double div_x1, div_x2, div_x3, div_x4;
    	        			double div_y1, div_y2, div_y3, div_y4;
    	        			if (coords.x1 == 0) {  div_x1 = 0; } else {  div_x1 = (coords.x1/tex_w); }
    	        			if (coords.x2 == 0) {  div_x2 = 0; } else {  div_x2 = (coords.x2/tex_w); }
    	        			if (coords.x3 == 0) {  div_x3 = 0; } else {  div_x3 = (coords.x3/tex_w); }
    	        			if (coords.x4 == 0) {  div_x4 = 0; } else {  div_x4 = (coords.x4/tex_w); }
    	        			
    	        			if (coords.y1 == 0) {  div_y1 = 0; } else {  div_y1 = (coords.y1/tex_h); }
    	        			if (coords.y2 == 0) {  div_y2 = 0; } else {  div_y2 = (coords.y2/tex_h); }
    	        			if (coords.y3 == 0) {  div_y3 = 0; } else {  div_y3 = (coords.y3/tex_h); }
    	        			if (coords.y4 == 0) {  div_y4 = 0; } else {  div_y4 = (coords.y4/tex_h); }
    	        			
    	        			objWriter.write("vt "+div_x1+" "+div_y1+" "+ "\n");
    	        			objWriter.write("vt "+div_x2+" "+div_y2+" "+ "\n");
    	        			objWriter.write("vt "+div_x3+" "+div_y3+" "+ "\n");
    	        			objWriter.write("vt "+div_x4+" "+div_y4+" "+ "\n");
    	        			}
    	        			*/
    	     		
    	     				double lock_u1=0, lock_u2=0, lock_u3=0, lock_u4=0;
    	     				double lock_v1=0, lock_v2=0, lock_v3=0, lock_v4=0;
    	     				
    	     				//if z same, north/south
    	     				//if y same, up/down
    	     				//if x same, east/west
    	     				
    	     				if (match4(coords.z1, coords.z2, coords.z3, coords.z4)) {
    	     					lock_u1 = coords.x1; lock_v1 = coords.y1;
    	     					lock_u2 = coords.x2; lock_v2 = coords.y2;
    	     					lock_u3 = coords.x3; lock_v3 = coords.y3;
    	     					lock_u4 = coords.x4; lock_v4 = coords.y4;
    	     					//System.out.println("ns");
    	     					}
    	     				
    	     				//U/D
    	     				if (match4(coords.y1, coords.y2, coords.y3, coords.y4)) {
    	     					lock_u1 = coords.z1; lock_v1 = coords.x1;
    	     					lock_u2 = coords.z2; lock_v2 = coords.x2;
    	     					lock_u3 = coords.z3; lock_v3 = coords.x3;
    	     					lock_u4 = coords.z4; lock_v4 = coords.x4;
    	     					//System.out.println("ud");
    	     					}
    	     				
    	     				//E/W
    	     				if (match4(coords.x1, coords.x2, coords.x3, coords.x4)) {
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

	
	public int WriteFromBlockstate(String path, String states, int x, int y, int z, JSONObject Culling) throws IOException, ParseException {
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
            JSONObject states_this = StatesToObject(key); 
            	
	    	JSONObject statestoobj = null;
            //If the states match
	    	statestoobj = StatesToObject((String) states);
            if (JSObjectMatches(statestoobj,states_this)) { 
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
		
	
		model_name = (String) state.get("model");
		Object xr_ = state.get("x");
		Object yr_ = state.get("y");
		Object zr_ = state.get("z");
		
		//System.out.println(xr_);
    	//System.out.println(yr_);
    	//System.out.println(zr_);
		
		if (xr_ == null) { xr_ = 0; }
		if (yr_ == null) { yr_ = 0; }
		if (zr_ == null) { zr_ = 0; }
		Double xr = (Double) Double.parseDouble(xr_.toString());
		Double yr = (Double) Double.parseDouble(yr_.toString());
		Double zr = (Double) Double.parseDouble(zr_.toString());
		
		Object uvlock_ = state.get("uvlock");
		if (uvlock_ == null) uvlock_ = false;
		Boolean uvlock = (Boolean) uvlock_;
		
		WriteModel("assets\\minecraft\\models\\" + model_name.substring(model_name.indexOf(":")+1).replace('/', '\\') + ".json", x,y,z, xr, yr, zr, Culling, uvlock); //ADD NAMESPACE HERE. currently just chops off minecraft:
		
		}
		
		if (model.get("multipart") != null) { }
		
		
	return 1;
	}
		
	public boolean JSObjectMatches(JSONObject source, JSONObject target) {
		//if (!source.toString().equals(target.toString())) return false;

		//System.out.println(source);
		//System.out.println(target);
		
		for (Object keyO : source.keySet()) {
	    	String key = (String) keyO;
	    	if (target.get(key) != null) {
	    		String tar = ((String) target.get(key));
	    		String sou = ((String) source.get(key));
	    		if (!tar.equals(sou)) return false;
	    		}
            }
		
		//System.out.println(source);
		//System.out.println(target);
		 return true; // yes
	}
	
	public JSONObject StatesToObject(String states) throws ParseException {
		
		//if (states.equals("")) { System.out.println("fail"); return null; }
		
		//Convert "x=a,y=b" to JSONObject {"x":"a","y":"b"}
		JSONParser jsonParser = new JSONParser();
		
		//Parse states into JSONObject
        String[] split = states.split(",");
        JSONObject states_this = (JSONObject) jsonParser.parse("{}");
        int i = 0;
        do {
        	//Convert array into jobject
        	String[] statesplit = split[i].split("=");
        	
        	states_this.put((String) statesplit[0].replace("\"", ""), (String) statesplit[1].replace("\"", ""));
        	i+=1;
        	} while (i < split.length);
		
		return states_this;
		}

	
}