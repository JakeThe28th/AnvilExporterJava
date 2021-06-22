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
		
		System.out.println("Filename Hierarchy: " + (String) path);
		return path;
		}

	public static int ArrayIndexOf(List<String> arr, String str) {
	    for (int i = 0; i < arr.size(); i++) {
	        if (str.equals(arr.get(i))) return i;
	    }
	    return -1;
	}
	
	public int WriteModel(String path, int x, int y, int z, Double rot_x, Double rot_y, Double rot_z, JSONObject Culling) {
		
	path = FileHierarchy(path);
	if (path == null) System.out.println("Model file does not exist."); 
	
	
	System.out.println("Filename: " + (String) path);
	

	//if (rot_y == 90) {
	//	rot_z = rot_x;
	//	rot_x = (double) 0;
	//	}
	
	System.out.println(rot_x);
	System.out.println(rot_y);
	System.out.println(rot_z);
	
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
        	Double from_z = (Double) Double.parseDouble(from_z_.toString());
        	
        	Double to_x = (Double) Double.parseDouble(to_x_.toString());
        	Double to_y = (Double) Double.parseDouble(to_y_.toString());
        	Double to_z = (Double) Double.parseDouble(to_z_.toString());
        	
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
        	
        	System.out.println("data/minecraft/textures/" + tex2 + ".png");
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
        	coords.scale(1, 1, -1);
        	
        	objWriter.write("v "+((coords.x1/16)+x) +" "+((coords.y1/16)+y)+" "+((coords.z1/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x2/16)+x) +" "+((coords.y2/16)+y)+" "+((coords.z2/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x3/16)+x) +" "+((coords.y3/16)+y)+" "+((coords.z3/16)+z)+ "\n");
        	objWriter.write("v "+((coords.x4/16)+x) +" "+((coords.y4/16)+y)+" "+((coords.z4/16)+z)+ "\n");
  	     
    	     
    	     JSONArray uv = (JSONArray) face.get("uv");
    	    
    	     
    	     //NOTE: add support for different model texture resolutions
    	     int tex_w = 16;
    	     int tex_h = 16;
    	     
    	     //Base UV values
    	     float uv_x1 = 0;
	    	 float uv_y1 = 0;
	    	 float uv_x2 = 16;
	    	 float uv_y2 = 16;
	    	 
	    	 //If UV is set in JSON:
    	     if (uv != null) {
    	    	 uv_x1 = (long) uv.get(0);
    	    	 uv_y1 = (long) uv.get(1);
    	    	 uv_x2 = (long) uv.get(2);
    	    	 uv_y2 = (long) uv.get(3);
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
    	     objWriter.write("vt "+uv_x1+" "+uv_y2+" "+ "\n");
    	     objWriter.write("vt "+uv_x2+" "+uv_y2+" "+ "\n");
    	     objWriter.write("vt "+uv_x2+" "+uv_y1+" "+ "\n");
    	     objWriter.write("vt "+uv_x1+" "+uv_y1+" "+ "\n");
    	     
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
		System.out.println("Blockstate: " + path);
		
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
		
		System.out.println(xr_);
    	System.out.println(yr_);
    	System.out.println(zr_);
		
		if (xr_ == null) { xr_ = 0; }
		if (yr_ == null) { yr_ = 0; }
		if (zr_ == null) { zr_ = 0; }
		Double xr = (Double) Double.parseDouble(xr_.toString());
		Double yr = (Double) Double.parseDouble(yr_.toString());
		Double zr = (Double) Double.parseDouble(zr_.toString());
		WriteModel("assets\\minecraft\\models\\" + model_name.substring(model_name.indexOf(":")+1).replace('/', '\\') + ".json", x,y,z, xr, yr, zr, Culling); //ADD NAMESPACE HERE. currently just chops off minecraft:
		
		}
		
		if (model.get("multipart") != null) { }
		
		
	return 1;
	}
		
	public boolean JSObjectMatches(JSONObject source, JSONObject target) {
		//if (!source.toString().equals(target.toString())) return false;

		System.out.println(source);
		System.out.println(target);
		
		for (Object keyO : source.keySet()) {
	    	String key = (String) keyO;
	    	if (target.get(key) != null) {
	    		String tar = ((String) target.get(key));
	    		String sou = ((String) source.get(key));
	    		if (!tar.equals(sou)) return false;
	    		}
            }
		
		System.out.println(source);
		System.out.println(target);
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