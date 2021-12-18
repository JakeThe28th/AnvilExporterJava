package io.github.jakethe28th.engine.graphics.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import io.github.jakethe28th.engine.graphics.Renderer;
import io.github.jakethe28th.engine.graphics.Window;
import io.github.jakethe28th.engine.graphics.gui.Sprite;
import io.github.jakethe28th.engine.math.Vector3f;

/**
 * 
 * @author JakeThe28th / Jake_28
 *
 * A text renderer based on the Sprite class, using Graphics2D to draw text.
 *
 */
public class TextRenderer {
		//Stores fonts
		//Stores sheets for each resolution of those fonts
		//Can load fonts
		//Can draw fonts at any size and any resolution
		//Can load fonts from files
	
	float current_size = 18;
	String current_font_filename;
	
	HashMap<String, HashMap<Float, Sprite>> fonts;
	HashMap<String, HashMap<Float, HashMap<Character, Integer>>> charIds;
	private int lastCharW;
	private int spaceWidth = (int) current_size/4;
	
	private static TextRenderer self = null;
	public static TextRenderer global() throws FontFormatException, IOException { 
		if (self == null) {
			self = new TextRenderer("assets\\engine\\fonts\\carlito\\Carlito-Regular.ttf");
        }
		return self; }
	
	void setFontSize(int size) {
		this.current_size = size;
		this.spaceWidth = (int) current_size/4;
	}
	
	public TextRenderer (String fname) throws FontFormatException, IOException {
		
		this.fonts = new HashMap<String, HashMap<Float, Sprite>>();
		this.charIds = new HashMap<String, HashMap<Float, HashMap<Character, Integer>>>();
		
		this.current_font_filename = fname;
	}
	
	void charExists(Character character) throws FontFormatException, IOException {
		//If there isn't a map entry for the current font, create one.
		if (fonts.get(current_font_filename) == null) {
			HashMap<Float, Sprite> fontMap = new HashMap<Float, Sprite>(); 
			fonts.put(current_font_filename, fontMap);}
				
		//Same for the character map // sizes
		if (charIds.get(current_font_filename) == null) {
			HashMap<Float, HashMap<Character, Integer>> charMap = new HashMap<Float, HashMap<Character, Integer>>();
			charIds.put(current_font_filename, charMap);}
				
				
		//Get values once we know they exist.
		HashMap<Float, Sprite> fontMap = fonts.get(current_font_filename);
		HashMap<Float, HashMap<Character, Integer>> charMapSizes = charIds.get(current_font_filename);
				
		//If there isn't a Sprite for the current font and size, make one.
		if (fontMap.get(current_size) == null) {
			Sprite fontTable = new Sprite((int) (16*current_size), (int) (16*current_size));
			fontMap.put(current_size, fontTable); }
				
		//Same for the character map
		if (charMapSizes.get(current_size) == null) {
				HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
				charMapSizes.put(current_size, charMap);}
			
		//Get the Sprite once we know it exists.
		Sprite fontTable = fontMap.get(current_size);
		HashMap<Character, Integer> charMap = charMapSizes.get(current_size);
				
		//If this character hasn't been drawn to the Sprite, draw it.
		if (charMap.get(character) == null) {			
			BufferedImage img_text = new BufferedImage((int) current_size, (int) (current_size*1.25), BufferedImage.TYPE_INT_ARGB);

			// Obtain the Graphics2D context associated with the BufferedImage.
			Graphics2D g = img_text.createGraphics();
					
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File(current_font_filename)).deriveFont(current_size);
			g.setFont(font);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(character.toString(), 0, current_size);
					
		if (current_size > 3 ) {
			final int[] pixels = ((DataBufferInt) img_text.getRaster().getDataBuffer()).getData();
					
			//Loop through pixels and remove columns without an opaque or semi-opaque pixel.
			int h = 0, xxl = -1, xxh = -1, xx = 1, yy = 1;
			for (xx = 1; xx < img_text.getWidth(); xx+=1) { //Iter X until image width reached.
				for (h = 0, yy = 1; h == 0 && yy < img_text.getHeight(); yy+=1) { //Iter Y until image height or until opaque pixel reached
					if (pixels[(yy*img_text.getWidth())+xx] != 0) {
						h = 1; 
						if (xxl == -1) xxl = xx; 
						if (xxh < xx) xxh = xx; 	
						} } }
					
			//Empty and space handling.
			if (xxh == -1 || xxl == -1) {
				xxh = (int) current_size; xxl = 0;
				if ( character == ' ' ) { xxh = this.spaceWidth; xxl = 0; }}
				
			if (xxh == xxl) xxh = xxl + 1;
					
			g.dispose();
			//System.out.println(xxl);
			//System.out.println(xxh);
			int offsetY = (int) (img_text.getHeight()/8);
			img_text = img_text.getSubimage(xxl, offsetY, (xxh-xxl), img_text.getHeight()-offsetY);
			}
					
			int id = fontTable.addSprite(img_text);
			charMap.put(character, id);
			}
				
		int id = charMap.get(character);
				
		this.lastCharW = fontTable.sprites.get(id).get("w");
		}

	
	void renderChar(Character character, Renderer render, Window window, Vector3f pos, Vector3f scale, Vector3f rot, Vector3f color, float alpha) throws FontFormatException, IOException {
		charExists(character); //Make sure there is a render of this character, in this font, and size.
		
		//Get values once we know they exist.
		HashMap<Float, Sprite> fontMap = fonts.get(current_font_filename);
		HashMap<Float, HashMap<Character, Integer>> charMapSizes = charIds.get(current_font_filename);
		
		//Get the Sprite once we know it exists.
		Sprite fontTable = fontMap.get(current_size);
		HashMap<Character, Integer> charMap = charMapSizes.get(current_size);
		
		int id = charMap.get(character);
		
		//Render the character.
		render.renderSpriteGui(window, fontTable, id, pos, scale, rot, color, alpha);
		
		this.lastCharW = fontTable.sprites.get(id).get("w");
		}
	
	int[] getCharDimensions(Character character) throws FontFormatException, IOException {
		charExists(character); //Make sure there is a render of this character, in this font, and size.
		
		//Get values once we know they exist.
		HashMap<Float, Sprite> fontMap = fonts.get(current_font_filename);
		HashMap<Float, HashMap<Character, Integer>> charMapSizes = charIds.get(current_font_filename);
		
		//Get the Sprite once we know it exists.
		Sprite fontTable = fontMap.get(current_size);
		HashMap<Character, Integer> charMap = charMapSizes.get(current_size);
		
		int id = charMap.get(character);

		this.lastCharW = fontTable.sprites.get(id).get("w");
		
		return new int[] {fontTable.sprites.get(id).get("w"), fontTable.sprites.get(id).get("h")};
		
	}
	
	public int[] getStringDimensions(String string, float spacing, int fontSize, Vector3f scale) throws FontFormatException, IOException {
		//if (fontSize == 0) return 0;
		
		setFontSize(fontSize);
		
		int xx = 0;
		int yy = 0;
		
		for (int i = 0; i < string.length(); i++) {
			int[] dim = getCharDimensions(string.charAt(i));
			xx+=(spacing*current_size)+lastCharW;
			if (dim[1] > yy) yy = dim[1];
		}
		
		return new int[] {xx, yy};
	}
	
	public void drawString(String string,  Renderer render, Window window, float spacing, int fontSize, Vector3f pos, Vector3f scale, Vector3f color, float alpha) throws FontFormatException, IOException {
		if (fontSize == 0) return;
		
		setFontSize(fontSize);
		
		int xx = (int) pos.x;
		int yy = (int) pos.y;
		
		for (int i = 0; i < string.length(); i++) {
			renderChar(string.charAt(i), render, window, new Vector3f(xx, yy, pos.z), scale, new Vector3f(0,0,0), color, alpha);
			xx+=(spacing*current_size)+lastCharW;
		}
	}
	
	
	public void getWidth(String string, float spacing, int fontSize) throws FontFormatException, IOException {
		if (fontSize == 0) return;
		
		setFontSize(fontSize);
		
		int xx = 0, yy = 0;
		
		for (int i = 0; i < string.length(); i++) {
			charExists(string.charAt(i));
			
			//Get values once we know they exist.
			HashMap<Float, Sprite> fontMap = fonts.get(current_font_filename);
			HashMap<Float, HashMap<Character, Integer>> charMapSizes = charIds.get(current_font_filename);
			
			//Get the Sprite once we know it exists.
			Sprite fontTable = fontMap.get(current_size);
			HashMap<Character, Integer> charMap = charMapSizes.get(current_size);
			
			int id = charMap.get(string.charAt(i));
			
			this.lastCharW = fontTable.sprites.get(id).get("w");
			
			xx+=(spacing*current_size)+lastCharW;
		}
	}
		
		
	public void getHeight(String string, float spacing, int fontSize) { }

}
