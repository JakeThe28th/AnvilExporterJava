package io.github.jakethe28th.anvilexporter;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Utility {

	/**
	Check if two JSONObjects match.
	@param source The source JSONObject
	@param target The target JSONObject
	@return Boolean
	*/
	public static boolean JSObjectMatches(JSONObject source, JSONObject target) {
		//Check if two JSONObjects match (Use in conjunction with StatesToObject())
		
		for (Object keyO : source.keySet()) {
	    	String key = (String) keyO;
	    	if (target.get(key) != null) {
	    		String tar = ((String) target.get(key));
	    		String sou = ((String) source.get(key));
	    		if (!tar.equals(sou)) return false;
	    		}
            }
		
		 return true; // yes
	}
	
	/**
	Convert String "x=a,y=b" to JSONObject {"x":"a","y":"b"}
	@param states The input String.
	@return JSONObject version of states.
	*/
	public static JSONObject StatesToObject(String states) throws ParseException {
		//Convert "x=a,y=b" to JSONObject {"x":"a","y":"b"}
		
		JSONParser jsonParser = new JSONParser();
		
		//Parse states into a String[] array, then parse that into a JSONObject.
        String[] split = states.split(",");
        JSONObject states_this = (JSONObject) jsonParser.parse("{}");
        for (int i = 0; i < split.length; ++i) {
        	String[] statesplit = split[i].split("=");
        	
        	states_this.put((String) statesplit[0].replace("\"", ""), (String) statesplit[1].replace("\"", ""));
        	}
		
		return states_this;
		}
	
	/**
	Finds a string in an array.
	@param arr Array to search
	@param str String to find
	@return Index of the item (int), -1 if no match can be found
	*/
	public static int ArrayIndexOf(List<String> arr, String str) {
	    for (int i = 0; i < arr.size(); i++) {
	        if (str.equals(arr.get(i))) return i;
	    }
	    return -1;
	}
	
	/**
	Merges two JSONObjects
	@param source The source JSONObject
	@param target The target JSONObject
	@return void
	*/
	public static JSONObject deepMerge(JSONObject source, JSONObject target) {
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
	
	/**
	Checks if 4 values match
	@return Boolean
	*/
	public static boolean match4(double a, double b, double c, double d) {
		if (a != b | b != c | c != d) return false;
		return true;
	}
	
}