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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//delete quad somehow?

//Input: block model, xyz positon, optional rotation, mtl file, obj file
public class WriteBlock {
	
	public String filename;
	public FileWriter myWriter;
	
	public int v_count;
	public int vt_count;
	
	public void end() {
		try {
		myWriter.close();
		System.out.println("Closed MC2OBJ inst");
		 	} catch (IOException e) {
		      System.out.println("An error occurred when closing filewriter..");
		      e.printStackTrace();
		    }
		    
	}
	
	public WriteBlock(String filename_) {
		filename = filename_;
		
		
		//Create file.
		  try {
		      File myObj = new File(filename);
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
		    	FileWriter myWriter_ = new FileWriter(filename);
		    	myWriter = myWriter_;
		        
		        myWriter.write("# obj made with AnvilExporterJava");
		        //myWriter.close();
		        //System.out.println("Successfully wrote to the file.");
		      } catch (IOException e) {
		        System.out.println("An error occurred writing.");
		        e.printStackTrace();
		      }
	}
	
	
	public void WriteModel(String path, int x, int y, int z) {
	path = "data\\minecraft\\" + path;
		
	System.out.println(path);
	
	System.out.println(filename);
	  
	 //JSON parser object to parse read file
     JSONParser jsonParser = new JSONParser();
	
	 try (FileReader reader = new FileReader(path))
     {
		v_count += 4;
		 
		myWriter.write("# obj made with AnvilertyuExporterJava \n");
		
		
		
		
         //Read JSON file
         Object obj = jsonParser.parse(reader);

         JSONObject model = (JSONObject) obj;
         JSONArray elements = (JSONArray) model.get("elements");
         System.out.println(elements.get(1));//textures.get("particle"));
         
         int i = 0;
         String face_name = "";
         
         do {
        				
        	System.out.println(elements.get(i));
        	JSONObject element = (JSONObject) elements.get(i);
        	JSONArray from = (JSONArray) element.get("from");
        	JSONArray to = (JSONArray) element.get("to");
        	
        	long from_x = (long) from.get(0);
        	long from_y = (long) from.get(1);
        	long from_z = (long) from.get(2);
        	
        	long to_x = (long) to.get(0);
        	long to_y = (long) to.get(1);
        	long to_z = (long) to.get(2);
        	
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
             
             myWriter.write("v "+coords.x1+" "+coords.y1+" "+coords.z1+ "\n");
    	     myWriter.write("v "+coords.x2+" "+coords.y2+" "+coords.z2+ "\n");
    	     myWriter.write("v "+coords.x3+" "+coords.y3+" "+coords.z3+ "\n");
    	     myWriter.write("v "+coords.x4+" "+coords.y4+" "+coords.z4+ "\n");
    	     
    	     /*
    	     myWriter.write("vt "+coords.x1+" "+coords.y1+" "+coords.z1+ "\n");
    	     myWriter.write("vt "+coords.x2+" "+coords.y2+" "+coords.z2+ "\n");
    	     myWriter.write("vt "+coords.x3+" "+coords.y3+" "+coords.z3+ "\n");
    	     myWriter.write("vt "+coords.x4+" "+coords.y4+" "+coords.z4+ "\n");
    	     */
    	     
    	     myWriter.write("vt 0 0 \n");
    	     myWriter.write("vt 0 0 \n");
    	     myWriter.write("vt 0 0 \n");
    	     myWriter.write("vt 0 0 \n");
    	     
    	     myWriter.write("f " 
    	    		 		+ (v_count-3) + "/" + (v_count-3) + " " 
    	    		 		+ (v_count-2) + "/" + (v_count-2) + " " 
    	    		 		+ (v_count-1) + "/" + (v_count-1) + " " 
    	    		 		+ v_count + "/" + v_count + " \n"); 
            
    	     v_count +=4;
    	     
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
	  

	}
	

	
}