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
	
}
