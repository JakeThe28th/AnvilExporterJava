package io.github.jakethe28th.anvilexporter;

import java.io.File;  // Import the File class
import java.io.FileReader;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lwjgl.glfw.GLFW;

import io.github.jakethe28th.engine.EngineObject;
import io.github.jakethe28th.engine.graphics.Camera;
import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.Renderer;
import io.github.jakethe28th.engine.graphics.Texture;
import io.github.jakethe28th.engine.graphics.Vertex;
import io.github.jakethe28th.engine.graphics.Window;
import io.github.jakethe28th.engine.graphics.gui.Element;
import io.github.jakethe28th.engine.graphics.gui.Sprite;
import io.github.jakethe28th.engine.math.Vector2f;
import io.github.jakethe28th.engine.math.Vector3f;

import java.io.FileWriter;   // Import the FileWriter class

import net.querz.nbt.tag.*;
import net.querz.nbt.io.*;
import net.querz.io.*;
import net.querz.mca.*;

import io.github.jakethe28th.minecraft.obj.*;

public class AnvilExporter {

	public static int v_count = 0;
	public static int vt_count = 0;
	
	public static final float FAR_PLANE =  -999.9999f;
	
	public static Random ran = new Random();
	
	public String region_folder = "region_testing";
	
	public void Main() throws IOException, ParseException {
		
		 System.out.println("Free memory (bytes): " + 
				  (Runtime.getRuntime().maxMemory() - 
		 				((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()))));
		
		try {
			
		boolean endProgram = false;
		
		Window window = new Window(600, 600, "AnvilExporter");
		Renderer render = new Renderer();
		Texture texture;
		
		texture = new Texture("assets/engine/textures/jesse.png");
		
		Mesh myMesh = new Mesh(new Vertex[] {
				new Vertex(new Vector3f(-0.5f, -0.5f,  0.0f), new Vector3f(1, 0.5f, 1), new Vector2f(0.0f, 0.0f)),
				new Vertex(new Vector3f(-0.5f, 0.5f,  0.0f), new Vector3f(0.2f, 0.7f, 1), new Vector2f(0, 1.0f)),
				new Vertex(new Vector3f(0.5f, -0.5f,  0.0f), new Vector3f(1, 1, 0.2f), new Vector2f(1.0f, 0.0f)),
				new Vertex(new Vector3f(0.5f, 0.5f,  0.0f), new Vector3f(1, 0.5f, 1), new Vector2f(1.0f, 1.0f)),
				new Vertex(new Vector3f(-0.5f, 0.5f,  0.0f), new Vector3f(0, 0, 0), new Vector2f(0, 1.0f)),
				new Vertex(new Vector3f(0.5f, -0.5f,  0.0f), new Vector3f(0, 0, 0), new Vector2f(1.0f, 0.0f))
							}, new int[] {
									 // Front face
								    0, 1, 2, 3, 4, 5
								}, texture);
								
		
		EngineObject myObject = new EngineObject(myMesh);
		myObject.setPosition(0, 0, -2);
		myObject.setScale(1, 1, 1);
		
		Camera camera = new Camera();
		
		MCAFile mcaFile = null;
		mcaFile = MCAUtil.read("region_testing\\r.0.0.mca");
		
		Exporter exporter = new Exporter("", "export");
		
		exporter.addChunkToQueue(mcaFile.getChunk(0, 0), 0, 0, 0);
		//exporter.addChunkToQueue(mcaFile.getChunk(0, 1), 0, 0, 1);
		//exporter.addChunkToQueue(mcaFile.getChunk(1, 1), 0, 1, 1);
		//exporter.addChunkToQueue(mcaFile.getChunk(1, 0), 0, 1, 0);

		Map<String, ChunkMap> chunkMap = new HashMap<String, ChunkMap>();

	    Mesh exporterMesh = exporter.myMesh;
	    
	    
	    EngineObject exporterObject = new EngineObject(exporterMesh);
		exporterObject.setScaleMode(EngineObject.SCALE_MODE_RESIZE);
		exporterObject.setPosition(window.getWidth()/2, window.getHeight()/2, 0);
		exporterObject.setScale(10, 10, 10f);
		exporterObject.setOrigin(0, 0, 0);
		exporterObject.setRotation(45, 0, 0);
		exporterMesh.setTexture(new Texture("export.png"));
		
		Element panel_top_left = new Element();
			panel_top_left.set("inline", new Element());
			panel_top_left.set("inline.file", new Element());
			panel_top_left.set("inline.view", new Element());
			panel_top_left.set("inline.options", new Element());
			panel_top_left.set("inline.help", new Element());
			
			panel_top_left.set("inline.file.open_world", new Element());
			
			panel_top_left.get("inline").setProperty("type", Element.ELEMENT_TYPE_INLINE);

			panel_top_left.get("inline.file").setProperty("type", "dropdown");
			panel_top_left.get("inline.file").setProperty("string", "File");
			setTheme(panel_top_left, "inline.file");
				
				panel_top_left.get("inline.file.open_world").setProperty("type", "button");
				panel_top_left.get("inline.file.open_world").setProperty("string", "Open world");
				setTheme(panel_top_left, "inline.file.open_world");
				
			
			
			panel_top_left.get("inline.view").setProperty("type", "dropdown");
			panel_top_left.get("inline.view").setProperty("string", "View");
			setTheme(panel_top_left, "inline.view");
			
			
			panel_top_left.get("inline.options").setProperty("type", "dropdown");
			panel_top_left.get("inline.options").setProperty("string", "Options");
			setTheme(panel_top_left, "inline.options");
			
			panel_top_left.get("inline.help").setProperty("type", "dropdown");
			panel_top_left.get("inline.help").setProperty("string", "Help");
			setTheme(panel_top_left, "inline.help");
			
			
		Element panel_top_right = new Element();
		panel_top_right.set("inline", new Element());
		panel_top_right.get("inline").setProperty("type", Element.ELEMENT_TYPE_INLINE);

		panel_top_right.set("inline.preview_options", new Element());
		panel_top_right.get("inline.preview_options").setProperty("type", "dropdown");
		panel_top_right.get("inline.preview_options").setProperty("string", "Preview options");
		setTheme(panel_top_right, "inline.preview_options");
		
		
		Element panel_right = new Element();	
		
		panel_top_left.setProperty("type", Element.ELEMENT_TYPE_COLLECTION);
		panel_top_left.col_base = Utility.hexToRGB("#303030"); 
		panel_top_left.col_outline = Utility.hexToRGB("#171717"); 
		
		panel_top_right.setProperty("type", Element.ELEMENT_TYPE_COLLECTION);
		panel_top_right.col_base = Utility.hexToRGB("#303030"); 
		panel_top_right.col_outline = Utility.hexToRGB("#171717"); 
		
		panel_right.setProperty("type", Element.ELEMENT_TYPE_COLLECTION);
		panel_right.col_base = Utility.hexToRGB("#2F2F2F"); 
		panel_right.col_outline = Utility.hexToRGB("#171717"); 
		
		
		Boolean queue = true;
		int offx = 0, offz = 0;
			while (!endProgram) {
				int ww = window.getWidth();
				int wh = window.getHeight();
				
				render.clear();

				
				panel_top_left.setProperty("x1", "0");
				panel_top_left.setProperty("y1", "0");
				panel_top_left.setProperty("x2", (ww-(ww/3)) + "");
				panel_top_left.setProperty("y2", "30");
				
				panel_top_left.draw(render, window, null, 0, 0, 10);
				
				
				panel_top_right.setProperty("x1", (ww-(ww/3)) + "");
				panel_top_right.setProperty("y1", "0");
				panel_top_right.setProperty("x2", ww + "");
				panel_top_right.setProperty("y2", "30");
				
				panel_top_right.draw(render, window, null, 0, 0, 5);
				
				
				panel_right.setProperty("x1", (ww-(ww/3)) + "");
				panel_right.setProperty("y1", "30");
				panel_right.setProperty("x2", ww + "");
				panel_right.setProperty("y2", wh + "");
				
				panel_right.draw(render, window, null, 0, 0, 0);
				
				if (panel_top_left.get("inline.file.open_world").getProperty("selected") != null)
				if (panel_top_left.get("inline.file.open_world").getProperty("selected").equals("true")) {
					panel_top_left.get("inline.file.open_world").setProperty("selected", "false");
					chunkMap.clear();
					//chunkMap = new HashMap<String, ChunkMap>();
					
					 
		            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		                
		            File selectedFile = null;
		            
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new File(System.getenv("APPDATA") + "/.minecraft/saves"));
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    	selectedFile = chooser.getSelectedFile();
            			System.out.println(selectedFile.getAbsolutePath());
                    }
                    
					if (selectedFile != null) region_folder = selectedFile.getParent() + "\\region";
					System.out.println(region_folder);
					//open_world();
					}
				
				/*
				//Exporter stepping logic
				if (exporter.chunks.size() > 0 ) { 
					exporterMesh.flip();
					exporter.step();
					exporterMesh.flip();
				
					exporterMesh.cleanUp();
					exporterMesh.buildMesh();
				
					exporterMesh.minMax();
					float xOrig = -(exporterMesh.max_xyz.x+exporterMesh.min_xyz.x)/2;
					float yOrig = -(exporterMesh.max_xyz.y+exporterMesh.min_xyz.y)/2;
					float zOrig = -(exporterMesh.max_xyz.z+exporterMesh.min_xyz.z)/2;
					
					//exporterObject.setScale(10, 10, 10f);
					
					float ratio = (window.getWidth()/3) / ((exporterMesh.max_xyz.x-exporterMesh.min_xyz.x));
					exporterObject.setPosition((window.getWidth()/2)+(window.getWidth()/3), window.getHeight()/2, 0);
					exporterObject.setScale(ratio, ratio, ratio); 
						
				
					exporterObject.setOrigin(xOrig, yOrig, zOrig);
					queue = true;
					} else if (queue == true) {
						exporter.end();
						exporterMesh.setTexture(new Texture("export.png"));
					
						queue = false;
						System.out.println("Done exporting");
						
						
						}
						*/
				
				//Center the export on the right of the window
				float ratio = (window.getWidth()/4) / ((exporterMesh.max_xyz.x-exporterMesh.min_xyz.x));
				exporterObject.setPosition((window.getWidth()/2)+(window.getWidth()/3), exporterObject.getPosition().y, 0);
				exporterObject.setScale(ratio, ratio, ratio); 
			
				
				render2dPreview(window, chunkMap, render, offx, offz, 2);
				
				
				
				
				//render.render(window, camera, new EngineObject[] { myObject, });
				render.renderGui(window, new EngineObject[] { exporterObject, });
				
				
				
				//exporterMesh.buildMesh();
				
				//render.renderSpriteGui(window, sprii, id,  new Vector3f(0,0,0),  new Vector3f(2,2,0),  new Vector3f(0,0,0),  new Vector3f(1,1,1), id);
				
				myObject.setRotation(myObject.getRotation().x+1, myObject.getRotation().y+1, myObject.getRotation().z+1);
				window.loop();

				boolean flipping = false;
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_E)) {
					exporterObject.setPosition(exporterObject.getPosition().x, (float) (exporterObject.getPosition().y+5), exporterObject.getPosition().z);
					}
				if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
					exporterObject.setPosition(exporterObject.getPosition().x, (float) (exporterObject.getPosition().y-5), exporterObject.getPosition().z);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
					exporterObject.setPosition(exporterObject.getPosition().x-5, (float) (exporterObject.getPosition().y), exporterObject.getPosition().z);
					}
				if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
					exporterObject.setPosition(exporterObject.getPosition().x+5, (float) (exporterObject.getPosition().y), exporterObject.getPosition().z);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE) && !flipping) {
					exporterMesh.flip();
					exporterMesh.buildMesh();
					flipping = true;
					exporterMesh.setTexture(new Texture("export.png"));
					} else flipping = false;

				if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
					exporterObject.setScale(exporterObject.getScale().x+1, (float) (exporterObject.getScale().y+1), exporterObject.getScale().z);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
					exporterObject.setScale(exporterObject.getScale().x-1, (float) (exporterObject.getScale().y-1), exporterObject.getScale().z);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
					exporterObject.setRotation(exporterObject.getRotation().x, exporterObject.getRotation().y+2, 0);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
					exporterObject.setRotation(exporterObject.getRotation().x, exporterObject.getRotation().y-2, 0);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_C)) {
					exporterObject.setRotation(exporterObject.getRotation().x-2, exporterObject.getRotation().y, 0);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_V)) {
					exporterObject.setRotation(exporterObject.getRotation().x+2, exporterObject.getRotation().y, 0);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
					offz += 16;
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
					offz -= 16;
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
					offx += 16;
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
					offx -= 16;
					}
				
			
				if (window.shouldClose()) endProgram = true;
				
				//System.out.println("x" + exporterObject.getPosition().x+1 + "y" +  (float) (exporterObject.getPosition().y));
			}
			window.end();
			render.cleanup();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	
	public void render2dPreview(Window window, Map<String, ChunkMap> chunkMap, Renderer render, int offset_x, int offset_y, float scale) throws IOException { 
		//render in bounds
		//translate (camera)
		//etc
		
		//float scale = 1;
		//float offset_x = 0;
		//float offset_y = 0;
		
		int ww = window.getWidth();
			ww -= (ww/3);
			ww += 100;
			
		int wh = window.getHeight();
			wh += 100;
		
		int chunks_x_count = (int) Math.floor(ww/(16*scale));
		int chunk_start_x =  (int) Math.floor(offset_x/(16*scale));
		
		int chunks_y_count = (int) Math.floor(wh/(16*scale));
		int chunk_start_y =  (int) Math.floor(offset_y/(16*scale));
		
		int ix = chunk_start_x;
		int iy = chunk_start_y;
		
		int iix = 0;
		int iiy = 0;
		
		long time =  System.currentTimeMillis() + 50;
		
		while (iix < chunks_x_count) {
			while (iiy < chunks_y_count) {
				
				
				String mcaName = (int) Math.floor(((float) ix)/32) + "." + (int) Math.floor(((float) iy)/32);
				if (time > System.currentTimeMillis()) {
					//if (time < System.currentTimeMillis()) return;
					
					if (chunkMap.get(mcaName) == null) {
						System.out.println("MCA " + mcaName + " is NULL");
						chunkMap.put(mcaName, new ChunkMap((int) Math.floor(((float) ix)/32), (int) Math.floor(((float) iy)/32), region_folder));
						}
					
					//System.out.println("CHUNK " + ix + " " + iy + " is " + mcaName);
					//System.out.println("CHUNKD " + (ix/32) + " " + (iy/32) + " is ");
					//System.out.println("CHUNKF " + Math.floor(ix/32) + " " + Math.floor(iy/32) + " is ");
					
				}
					
				ChunkMap map = chunkMap.get(mcaName);
					
				if (map != null) {
					if (time > System.currentTimeMillis() || map.isChunkProcessed(ix, iy)) {
				
				int chunk_spr = map.getChunkSprite(ix, iy);
				
				if (chunk_spr >=0) {
				render.renderSpriteGui(window, map.getSprite(), chunk_spr, new Vector3f((ix*(16*scale))-offset_x, (iy*(16*scale))-offset_y , FAR_PLANE), new Vector3f(scale, scale, 0), new Vector3f(0,0,0), new Vector3f(1,1,1), 1);
				} } }
				
				
				
			iy+=1;
			iiy+=1;
			}
			ix+=1;
			iiy = 0;
			iix+=1;
			iy = chunk_start_y;
		}
		
	}

	private static void setTheme(Element element, String str) {
		element.get(str).col_base = Utility.hexToRGB("#303030"); 
		element.get(str).col_outline = Utility.hexToRGB("#171717"); 
		element.get(str).col_hover = Utility.hexToRGB("#42525E"); 
		element.get(str).col_outline_hover = Utility.hexToRGB("#416179"); 
		element.get(str).col_select = Utility.hexToRGB("#416179"); 
		element.get(str).col_outline_select = Utility.hexToRGB("#316C9B");
		element.get(str).col_text = Utility.hexToRGB("#D0D0D0");
		}
	
	public static void program(Mesh exporterMesh) throws Exception {
		
			
		
	}
	
	/*
	drawPreview2d(float cam_x, float cam_y, float cam_zoom) {
		guiUtility.drawRegion(x, z, xcoord, zcoord, scale)
		
		regions[x][z], if sprite_region = null makeSprite(region)
		
		
	}
	*/
}