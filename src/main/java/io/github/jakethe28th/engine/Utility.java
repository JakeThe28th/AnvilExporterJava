package io.github.jakethe28th.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStreamReader;

public class Utility {
	public static String loadAsString(String path) {
		StringBuilder result = new StringBuilder();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = "";
			while ((line = reader.readLine()) != null ) {
				result.append(line).append("\n");
			}
		} catch (IOException e) {
				System.err.println("Failed to find file: " + path);
		}
		
		return result.toString();
	} 

	public static byte Nibble4(byte[] arr, int index){
			return (byte) (index%2 == 0 ? arr[index/2]&0x0F : (arr[index/2]>>4)&0x0F);
			//https://minecraft.fandom.com/wiki/Chunk_format#Block_format
		}
	
	public static boolean pointInRect(int x1, int y1, int x2, int y2, int px, int py) {
		
		int tempx = 0;
		if (x1 > x2) {
			tempx = x1;
			x1 = x2;
			x2 = tempx;
			}
		
		int tempy = 0;
		if (y1 > y2) {
			tempy = y1;
			y1 = y2;
			y2 = tempy;
			}
		
		boolean x = false;
		boolean y = false;
		if (x1 < px & x2 > px) x = true;
		if (y1 < py & y2 > py) y = true;
		
		if (x & y) return true;
		return false;
	}
	
	public static String loadFromGLSL(String str, String name) {
		//Format: name { content } name { content }
		//Content can include { } brackets, so need to keep track of how many are open / closed.
		//Basically, add characters to nameTemp until you reach a {,
		//check if the characters = the name you're searching for,
		//go through the { } content until you can resolve the last },
		//if that part was for the current name, then add it to the temp contents
		//if it wasn't, clear the name, and repeat
		//then return the contents
		
		int i = 0;
		int count = 0;
		String get = "";
		String nameTemp = "";
		String temp = "";
		while (i < str.length()) {
			
			if (str.charAt(i) == '{') {
				count += 1;
				nameTemp = nameTemp.replace(" ", "").replace("\n", "").replace("\r", "");
				i+=1; //skip over {
				while (count != 0) {
					
					if (nameTemp.equals(name)) temp = temp + str.charAt(i);
					
					i+=1;
					
					if (str.charAt(i) == '{') count +=1;
					if (str.charAt(i) == '}') count -=1;
					
					
				} 
				nameTemp = "";
				i+=1; //skip over }
			}
			
			nameTemp = nameTemp + str.charAt(i);
			//System.out.println(nameTemp);
			
			
			i+=1;
		}
		
		/*
		System.out.println("Output: ");
		System.out.println(name);
		System.out.println(temp);
		System.out.println(count);
		*/
		return temp;
	}
	
}
