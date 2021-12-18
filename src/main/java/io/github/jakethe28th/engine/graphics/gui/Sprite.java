package io.github.jakethe28th.engine.graphics.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.Texture;
import io.github.jakethe28th.engine.graphics.Vertex;
import io.github.jakethe28th.engine.math.Vector2f;
import io.github.jakethe28th.engine.math.Vector3f;

public class Sprite {

	//Init
	//Draw sprite from sheet at x, y, at coords x y, depth z
	
	//create sprite from image, add to sprite sheet,
	//shave a map of the sprite's x/y on sheet and use an ID to access?
	
	public Map<Integer, HashMap<String, Integer>> sprites = new HashMap<Integer, HashMap<String, Integer>>();
	//Whenever a new sprite is added, at it's ID, start x, start y, width, and height to a map.
	//ID > x,y,w,h
	
	public Map<Integer, Mesh> meshes = new HashMap<Integer, Mesh>();
	
	public int width = 1;
	public int height = 1;
	
	int max_y =0; // max y value, use to add new sprites
	int max_x =0; // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	int cur_y =0;
	int cur_x =0;
	
	int max_id = 0;
	
	public BufferedImage sheet;
	Graphics2D context;
	
	public Mesh mesh;
	
	public Texture tex;
	
	
	//keep a backup of the sprite image itself as a bufferEDIMAGE?
	
	public Sprite (int w, int h) {
		//sprites.put("dog", "type of animal");
		//System.out.println(sprites.get("dog"));
		
		this.sheet = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		// Obtain the Graphics2D context associated with the BufferedImage.
		this.context = this.sheet.createGraphics();
		
		this.width = w;
		this.height = h;
	}
	
	public int addSprite(String filename) {
		int id = -1;
		
		try {
			
		id = this.max_id;
			 this.max_id +=1;
				 
		//make map for sprite
		HashMap<String, Integer> newSprite = new HashMap<String, Integer>();
		sprites.put(id, newSprite);
		
		
		BufferedImage newImage = ImageIO.read(new File(filename));
		
		if (this.cur_x + newImage.getWidth() > this.width) {
			this.cur_y = this.max_y;
			
			this.cur_x = 0; this.max_x = 0;
			}
		
		
		
		newSprite.put("id", id);
			newSprite.put("x", this.cur_x); //x
			newSprite.put("y", this.cur_y); //y
			newSprite.put("w", newImage.getWidth()); //w
			newSprite.put("h", newImage.getHeight()); //h
			
			float xx = this.cur_x;
			float yy = this.cur_y;
			
			float w = newImage.getWidth();
			float h = newImage.getHeight();
			
			//System.out.println(xx);
			//System.out.println(yy);
			//System.out.println(w);
			//System.out.println(h);
			//System.out.println("w : " + this.width);
			//System.out.println("h : " + this.height);
			
			float u1 = xx/this.width;
			float u2 = (xx+w)/this.width;
			float v1 = yy/this.height;
			float v2 = (yy+h)/this.height;
			
			//System.out.println(u1);
			//System.out.println(u2);
			//System.out.println(v1);
			//System.out.println(v2);
			
			int meshID = id;
			meshes.put(id, new Mesh(new Vertex[] {
					new Vertex(new Vector3f(0, 0,  0.0f), new Vector3f(1, 1, 1), 	new Vector2f(u1, v1)),
					new Vertex(new Vector3f(0, h,  0.0f), new Vector3f(1, 1, 1),    new Vector2f(u1, v2)),
					new Vertex(new Vector3f(w, h,  0.0f), new Vector3f(1, 1, 1), 	new Vector2f(u2, v2)),
					new Vertex(new Vector3f(w, 0,  0.0f), new Vector3f(1, 1, 1), 	new Vector2f(u2, v1))
								}, new int[] {
										 // Front face
									    0, 1, 2, 2, 3, 0
									}, null));
			newSprite.put("Mesh", meshID);
			
			context.drawImage(newImage, this.cur_x, this.cur_y, null);
			
			this.cur_x += newImage.getWidth();
			this.max_x = this.cur_x;
			
			//update max y
			if (this.cur_y + newImage.getHeight() > this.max_y)
					this.max_y = this.cur_y + newImage.getHeight();
			
			tex.cleanup();
			this.tex = new Texture(sheet);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Failed to add Sprite");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}
	
	public int addSprite(BufferedImage newImage) {
		int id = -1;
		
		try {
			
		id = this.max_id;
			 this.max_id +=1;
				 
		//make map for sprite
		HashMap<String, Integer> newSprite = new HashMap<String, Integer>();
		sprites.put(id, newSprite);
		
		
		//BufferedImage newImage = ImageIO.read(new File(filename));
		
		if (this.cur_x + newImage.getWidth() > this.width) {
			this.cur_y = this.max_y;
			
			this.cur_x = 0; this.max_x = 0;
			}
		
		
		
		newSprite.put("id", id);
			newSprite.put("x", this.cur_x); //x
			newSprite.put("y", this.cur_y); //y
			newSprite.put("w", newImage.getWidth()); //w
			newSprite.put("h", newImage.getHeight()); //h
			
			float xx = this.cur_x;
			float yy = this.cur_y;
			
			float w = newImage.getWidth();
			float h = newImage.getHeight();
			
			//System.out.println(xx);
			//System.out.println(yy);
			//System.out.println(w);
			//System.out.println(h);
			//System.out.println("w : " + this.width);
			//System.out.println("h : " + this.height);
			
			float u1 = xx/this.width;
			float u2 = (xx+w)/this.width;
			float v1 = yy/this.height;
			float v2 = (yy+h)/this.height;
			
			//System.out.println(u1);
			//System.out.println(u2);
			//System.out.println(v1);
			//System.out.println(v2);
			
			int meshID = id;
			meshes.put(id, new Mesh(new Vertex[] {
					new Vertex(new Vector3f(0, 0,  0.0f), new Vector3f(1, 1, 1), 	new Vector2f(u1, v1)),
					new Vertex(new Vector3f(0, h,  0.0f), new Vector3f(1, 1, 1),    new Vector2f(u1, v2)),
					new Vertex(new Vector3f(w, h,  0.0f), new Vector3f(1, 1, 1), 	new Vector2f(u2, v2)),
					new Vertex(new Vector3f(w, 0,  0.0f), new Vector3f(1, 1, 1), 	new Vector2f(u2, v1))
								}, new int[] {
										 // Front face
									    0, 1, 2, 2, 3, 0
									}, null));
			newSprite.put("Mesh", meshID);
			
			context.drawImage(newImage, this.cur_x, this.cur_y, null);
			
			this.cur_x += newImage.getWidth();
			this.max_x = this.cur_x;
			
			//update max y
			if (this.cur_y + newImage.getHeight() > this.max_y)
					this.max_y = this.cur_y + newImage.getHeight();
			
			this.tex = new Texture(sheet);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Failed to add Sprite");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}
	
	public void deleteSprite(int id) {} //Clean up texture space, delete from map, clean up VBOS.
	
	public void cleanup() {} //delete all sprites, and texture
	
	public boolean isNull(int id) { if (sprites.get(id) == null) return true; return false; }
	
	public void draw(int id) {	
		meshes.get(id).setTexture(tex);
		meshes.get(id).render();
	}
	
	public Mesh getMesh(int id) {
		return meshes.get(id);
		}
	
	public void save(String filename) {
		try {
			ImageIO.write(sheet, "png", new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Failed to save Sprite");
		}
	}
}
