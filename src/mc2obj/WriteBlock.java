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
	public void WriteModel(String path, int x, int y, int z) {
	path = "data\\minecraft\\" + path;
		
	System.out.println(path);
	
	
	  
	 //JSON parser object to parse read file
     JSONParser jsonParser = new JSONParser();
	
	 try (FileReader reader = new FileReader(path))
     {
		main.main.v_count += 10;
		 
         //Read JSON file
         Object obj = jsonParser.parse(reader);

         JSONObject model = (JSONObject) obj;
         JSONObject textures = (JSONObject) model.get("textures");
         System.out.println(textures.get("particle"));
          
         mc2obj.Quad coords = new mc2obj.Quad(835, 5, 5, 5, 5, 5, 5, 5, 5, 5 , 5, 5);
         
         System.out.println(coords.x1);
         
         int x1 = 0; int y1 = 0;
         
         
         
         
         
         
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