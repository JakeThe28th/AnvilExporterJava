package mc2obj;

import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class

import java.io.FileNotFoundException;
import java.io.FileReader;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//delete quad somehow?

//Input: block model, xyz positon, optional rotation, mtl file, obj file
public class WriteBlock {
	
	public String filename;
	public FileWriter objWriter;
	public FileWriter mtlWriter;
	
	public int v_count;
	public int vt_count;
	
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
		        
		    	objWriter.write("# obj made with AnvilExporterJava");
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
			        
			    	mtlWriter.write("# obj made with AnvilExporterJava");
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
            
            System.out.println(key);
            
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
	

	public int WriteModel(String path, int x, int y, int z, int rot_x, int rot_y, int rot_z, JSONObject Culling) {
	path = "data\\minecraft\\" + path; //Add namespace option????? maybe???
	
	
	v_count+=4;
		
	System.out.println(path);
	
	File tmpDir = new File(path);
	boolean exists = tmpDir.exists();
	
	if (!exists) {
		System.out.println("Model does not exist.");
		return -1; // if path no, leave
		}
	
	
	try {
	
	//JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();
	
	System.out.println(filename);
	  
	FileReader reader_ = new FileReader(path);
	JSONObject model = (JSONObject) jsonParser.parse(reader_);
	
	String parent = (String) model.get("parent");
	do {
		FileReader parentReader = new FileReader("data\\minecraft\\assets\\minecraft\\models\\" + parent.substring(parent.indexOf(":")+1) + ".json"); //ADD NAME SPACE HERE. currently just chops off minecraft:
		Object parent_model = jsonParser.parse(parentReader);
		
		model.remove("parent");
		
		System.out.println(model);
        System.out.println(parent_model);
		
        System.out.println("merge");
        
        if (parent_model != null) model = deepMerge((JSONObject) model, (JSONObject) parent_model);
		
        System.out.println(model);
        
		parent = (String) model.get("parent");
		} while (parent != null);
		
		
		//v_count += 4;
		 
		objWriter.write("# obj made with AnvilExporterJava \n");
         
         JSONArray elements = (JSONArray) model.get("elements");
         JSONObject textures = (JSONObject) model.get("textures");

         
         int i = 0;
         String face_name = "";
         
         do {
        				
        	System.out.println(elements.get(i));
        	JSONObject element = (JSONObject) elements.get(i);
        	JSONArray from = (JSONArray) element.get("from");
        	JSONArray to = (JSONArray) element.get("to");
        	
        	Object  from_x = (Object)from.get(0);
        	Object  from_y = (Object)from.get(1);
        	Object  from_z = (Object)from.get(2);
        	
        	Object  to_x = (Object)to.get(0);
        	Object  to_y = (Object)to.get(1);
        	Object  to_z = (Object)to.get(2);
        	
        	System.out.println(from_x + " fr_x");
        	
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
        		
        	String tex = ((String) face.get("texture")).substring(1);
        	System.out.println(tex);
        	
        	String tex2 = (String) textures.get(tex);
        	System.out.println("data/minecraft/textures/" + tex2 + ".png");
        	
        	mtlWriter.write("newmtl " + tex2 + "\n");
        	mtlWriter.write("map_Kd  " + "data/minecraft/textures/" + tex2 + ".png" + "\n");
        	
        	objWriter.write("usemtl " + tex2 + "\n");
        	
        	/*
        	case "south": 	
				#region North vertices			
					var pos_s = {
						x1 : from_x,	y1 : to_y,		z1 : to_z,
						x2 : to_x,		y2 : to_y,		z2 : to_z,
						x3 : to_x,		y3 : from_y,	z3 : to_z,
						x4 : from_x,	y4 : from_y,	z4 : to_z,
						}
				#endregion
					break;
					
				case "north": 
				#region South vertices
					var pos_s = {
						x1 : from_x,	y1 : to_y,		z1 : from_z,
						x2 : to_x,		y2 : to_y,		z2 : from_z,
						x3 : to_x,		y3 : from_y,	z3 : from_z,
						x4 : from_x,	y4 : from_y,	z4 : from_z,
						}
				#endregion
				break;
				
				case "west": 
				#region West vertices
					var pos_s = {
						x1 : from_x,	y1 : to_y,		z1 : to_z,
						x2 : from_x,	y2 : to_y,		z2 : from_z,
						x3 : from_x,	y3 : from_y,	z3 : from_z,
						x4 : from_x,	y4 : from_y,	z4 : to_z,
						}
					#endregion
					break;
					
				case "east": 
				#region East vertices
					var pos_s = {
						x1 : to_x,	y1 : to_y,		z1 : to_z,
						x2 : to_x,	y2 : to_y,		z2 : from_z,
						x3 : to_x,	y3 : from_y,	z3 : from_z,
						x4 : to_x,	y4 : from_y,	z4 : to_z,
						}
				#endregion
					break;
					
				case "up": 		
				#region Up vertices
					var pos_s = {
						x1 : from_x,	y1 : to_y,	z1 : to_z,
						x2 : to_x,		y2 : to_y,	z2 : to_z,
						x3 : to_x,		y3 : to_y,	z3 : from_z,
						x4 : from_x,	y4 : to_y,	z4 : from_z,
						}
				#endregion
					break;
					
				case "down": 		
				#region Down vertices
					var pos_s = {
						x1 : from_x,	y1 : from_y,	z1 : to_z,
						x2 : to_x,		y2 : from_y,	z2 : to_z,
						x3 : to_x,		y3 : from_y,	z3 : from_z,
						x4 : from_x,	y4 : from_y,	z4 : from_z,
						}
				#endregion
					break;
				}
        	*/
        	
        	System.out.println(face_name);
        	System.out.println(i_faces);
        	switch (face_name) {
        		case "north":
        			coords = new mc2obj.Quad(	  from_x, to_y, from_z,
        			 							  to_x, to_y, from_z, 
        			 							  to_x, from_y, from_z, 
        			 							  from_x, from_y, from_z);
        			break;
        		case "east":
        			coords = new mc2obj.Quad(		to_x, to_y, to_z,
        											to_x, to_y, from_z, 
        											to_x, from_y, from_z, 
        											to_x, from_y, to_z);
        			break;
        		case "south":
        			coords = new mc2obj.Quad(		from_x, to_y, to_z,
        											to_x, to_y, to_z, 
        											to_x, from_y, to_z, 
        											from_x, from_y, to_z);
        			break;
        		case "west":
        			coords = new mc2obj.Quad(	  from_x, to_y, to_z,
        										  from_x, to_y, from_z, 
        			 							  from_x, from_y, from_z, 
        			 							  from_x, from_y, to_z);
        			break;
        		case "up":
        			coords = new mc2obj.Quad(	from_x, to_y, to_z,
							  					to_x, to_y, to_z, 
							  					to_x, to_y, from_z, 
							  					from_x, to_y, from_z);
        			break;
        		case "down":
        			coords = new mc2obj.Quad(	from_x, from_y, to_z,
        			 							to_x, from_y, to_z, 
        			 							to_x, from_y, from_z, 
        			 							from_x, from_y, from_z);
        			System.out.println(from_y);
        			break;
             
        		}
        	 
             //System.out.println(coords.x1);
             
        	 objWriter.write("v "+coords.x1+" "+coords.y1+" "+coords.z1+ "\n");
        	 objWriter.write("v "+coords.x2+" "+coords.y2+" "+coords.z2+ "\n");
        	 objWriter.write("v "+coords.x3+" "+coords.y3+" "+coords.z3+ "\n");
        	 objWriter.write("v "+coords.x4+" "+coords.y4+" "+coords.z4+ "\n");
    	     
    	     
    	     JSONArray uv = (JSONArray) face.get("uv");
    	     System.out.println(face);
    	     
    	     int tex_w = 16;
    	     int tex_h = 16;
    	     float uv_x1 = (long) uv.get(0);
    	     float uv_y1 = (long) uv.get(1);
    	     float uv_x2 = (long) uv.get(2);
    	     float uv_y2 = (long) uv.get(3);
    	     
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
    	     
    	     
    	     /*
    	     myWriter.write("vt 0 0 \n");
    	     myWriter.write("vt 0 0 \n");
    	     myWriter.write("vt 0 0 \n");
    	     myWriter.write("vt 0 0 \n");
    	     */
    	     
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
         
        
         
         //Iterate over employee array
         //employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );

     } catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     } catch (ParseException e) {
         e.printStackTrace();
     }
	  
	 return 1;
	}
	
	public int WriteFromBlockstate(String path, JSONObject states, int x, int y, int z, JSONObject Culling) throws IOException, ParseException {
		path = "data\\minecraft\\" + path; //Add namespace option????? maybe???
			
		System.out.println(path);
		
		File tmpDir = new File(path);
		boolean exists = tmpDir.exists();
		
		if (!exists) {
			System.out.println("Blockstate file does not exist.");
			return -1; // if path no, leave
			}
		
		//JSON parser object to parse read file
	    JSONParser jsonParser = new JSONParser();
		FileReader reader_ = new FileReader(path);
		JSONObject model = (JSONObject) jsonParser.parse(reader_);
		
		if (model.get("variants") != null) {
		JSONObject variants = (JSONObject) model.get("variants");
		
		String model_name = "";
		JSONObject state = null;
		for (Object keyO : variants.keySet()) {
	    	String key = (String)keyO;
            Object value = variants.get(key);
            
            //System.out.println(key);
            
            String[] split = key.split(",");
            JSONObject states_this = (JSONObject) jsonParser.parse("{}");
            
            
            int i = 0;
            do {
            	
            	String[] statesplit = split[i].split("=");
            	states_this.put((String) statesplit[0], (String) statesplit[1]);
            	
            	i+=1;
            	} while (i < split.length);
            
            //System.out.println(states_this);
            
            //https://stackoverflow.com/questions/31239853/is-there-a-way-i-can-empty-the-whole-jsonobject-java

            System.out.println(JSObjectMatches(states,states_this));
            
            if (JSObjectMatches(states,states_this)) { 
            	state = (JSONObject) variants.get(key);
            	//model_name = (String) variants.get(key);
            	break;
            	}
            //System.out.println(states_this);
            //System.out.println(states);
            
            states_this.keySet().clear();

	 	}
		
		model_name = (String) state.get("model");
		System.out.println("ae");
		WriteModel("assets\\minecraft\\models\\" + model_name.substring(model_name.indexOf(":")+1).replace('/', '\\') + ".json", x,y,z, 0,0,0, Culling); //ADD NAME SPACE HERE. currently just chops off minecraft:
		
		System.out.println(model_name);
		}
		
		if (model.get("multipart") != null) { }
		
		
	return 1;
	}
		
	public boolean JSObjectMatches(JSONObject source, JSONObject target) {
		if (!source.toString().equals(target.toString())) return false;
		 return true; // yes
	}

	
}