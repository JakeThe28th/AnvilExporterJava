package io.github.jakethe28th.anvilexporter;

import java.awt.FontFormatException;
import java.io.File;  // Import the File class
import java.io.FileReader;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
import io.github.jakethe28th.engine.graphics.gui.TextRenderer;
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
	
	public Exporter exporter;
	public MCAFile mcaFile;
	
	public String region_folder = "region_testing";
	public int startX = 0;
	public int endX = 0;
	public int startY = 0;
	public int endY = 0;
	public String mouseFocus;
	public boolean selecting_chunks = false;
	public float preview_scale = 3f;
	
	//GUI stuff
	public Element panel_top_left = new Element();
	public Element panel_top_right = new Element();
	public Element panel_right = new Element();	
	
	//Rendering to the Window
	public Window window;
	public Renderer render;
	
	//AE stuff
	Map<String, ChunkMap> chunkMap = new HashMap<String, ChunkMap>();

	public void Main() throws IOException, ParseException {
		 System.out.println("Free memory (bytes): " + 
				  (Runtime.getRuntime().maxMemory() - 
		 				((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()))));
		
		try {
			
		boolean endProgram = false;
		
		window = new Window(600, 600, "AnvilExporter");
		render = new Renderer();
		
		MCAFile mcaFile = null;
		mcaFile = MCAUtil.read("region_testing\\r.0.0.mca");
		
		this.exporter = new Exporter("", "export");
		
		//exporter.addChunkToQueue(mcaFile.getChunk(0, 0), 0, 0, 0);
		//exporter.addChunkToQueue(mcaFile.getChunk(0, 1), 0, 0, 1);
		//exporter.addChunkToQueue(mcaFile.getChunk(1, 1), 0, 1, 1);
		//exporter.addChunkToQueue(mcaFile.getChunk(1, 0), 0, 1, 0);

	    Mesh exporterMesh = exporter.myMesh;
	    EngineObject exporterObject = new EngineObject(exporterMesh);

		
		initGui();
		
		Boolean queue = false;
		int offx = 0, offz = 0;
		
			while (!endProgram) {
				int ww = window.getWidth();
				int wh = window.getHeight();
				render.clear();
				
				mouseFocus = "chunks";
				
				updateGui(ww, wh);

					
				//Exporter stepping logic
				if (exporter.chunks.size() > 0 ) { 
					exporterMesh = exporter.myMesh;
					exporterObject.setMesh(exporterMesh);
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
					exporterObject.setPosition((window.getWidth()/2)+(window.getWidth()/3), window.getHeight()/2, 500.0f);
					exporterObject.setScale(ratio, ratio, 1); 
						
				
					exporterObject.setOrigin(xOrig, yOrig, zOrig);
					queue = true;
					} else if (queue == true) {
						exporterMesh = exporter.myMesh;
						exporterObject.setMesh(exporterMesh);
						exporter.reset();

						exporterObject.setScaleMode(EngineObject.SCALE_MODE_SKEW);
						//exporterObject.setPosition(window.getWidth()/2, window.getHeight()/2, 0);
						//exporterObject.setScale(10, 10, 10f);
						//exporterObject.setOrigin(0, 0, 0);
						exporterObject.setRotation(45, 0, 0);
						exporterMesh.setTexture(new Texture("export.png"));
						exporterMesh.cleanUp();
						exporterMesh.buildMesh();
					
						queue = false;
						System.out.println("Done exporting");
						}
						
				
				//Center the export on the right of the window
				float ratio = (window.getWidth()/4) / ((exporterMesh.max_xyz.x-exporterMesh.min_xyz.x));
				exporterObject.setPosition((window.getWidth()/2)+(window.getWidth()/3), exporterObject.getPosition().y, 500.0f);
				exporterObject.setScale(ratio, ratio, 1); 
			
				
				render2dPreview(window, render, offx, offz, preview_scale);
				
				//render.render(window, camera, new EngineObject[] { myObject, });
				render.renderGui(window, new EngineObject[] { exporterObject, });
				
				//exporterMesh.buildMesh();
				
				window.loop();

				boolean flipping = false;
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_E)) 		{ exporterObject.setPosition(exporterObject.getPosition().x, (float) (exporterObject.getPosition().y+5), exporterObject.getPosition().z); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) 		{ exporterObject.setPosition(exporterObject.getPosition().x, (float) (exporterObject.getPosition().y-5), exporterObject.getPosition().z); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_A)) 		{ exporterObject.setPosition(exporterObject.getPosition().x-5, (float) (exporterObject.getPosition().y), exporterObject.getPosition().z); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_D)) 		{ exporterObject.setPosition(exporterObject.getPosition().x+5, (float) (exporterObject.getPosition().y), exporterObject.getPosition().z); }
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE) && !flipping) {
					exporterMesh.flip(); exporterMesh.buildMesh(); flipping = true;
					exporterMesh.setTexture(new Texture("export.png"));
					} else flipping = false;

				if (window.isKeyPressed(GLFW.GLFW_KEY_W)) 		{ exporterObject.setScale(exporterObject.getScale().x+1, (float) (exporterObject.getScale().y+1), exporterObject.getScale().z); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_S)) 		{ exporterObject.setScale(exporterObject.getScale().x-1, (float) (exporterObject.getScale().y-1), exporterObject.getScale().z); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) 		{ exporterObject.setRotation(exporterObject.getRotation().x, exporterObject.getRotation().y+2, 0); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_X)) 		{ exporterObject.setRotation(exporterObject.getRotation().x, exporterObject.getRotation().y-2, 0); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_C)) 		{ exporterObject.setRotation(exporterObject.getRotation().x-2, exporterObject.getRotation().y, 0); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_V)) 		{ exporterObject.setRotation(exporterObject.getRotation().x+2, exporterObject.getRotation().y, 0); }
				if (window.isKeyPressed(GLFW.GLFW_KEY_UP))  	{ offz += 16; }
				if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN))	{ offz -= 16; }
				if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT))	{ offx += 16; }
				if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT))	{ offx -= 16; }
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_L))	{ preview_scale -= .1; }
				if (window.isKeyPressed(GLFW.GLFW_KEY_O))	{ preview_scale += .1; }
				
			
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
	
	public void render2dPreview(Window window, Renderer render, int offset_x, int offset_y, float scale) throws IOException { 
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
				Vector3f realpos = new Vector3f((ix*(16*scale))-offset_x, (iy*(16*scale))-offset_y, 0f);
				
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
				render.renderSpriteGui(window, map.getSprite(), chunk_spr, new Vector3f(realpos.x, realpos.y, FAR_PLANE), new Vector3f(scale, scale, 0), new Vector3f(0,0,0), new Vector3f(1,1,1), 1);
				} } }
				
			//Selecting chunks handling
			if (ix >= startX & ix <= endX) if (iy >= startY & iy <= endY) {
				EngineObject p = genPanel(0, 0, 15, 15, 0, new Vector3f(0.75f, 0.75f, 0.5f), new Vector3f(1,1,1));
				p.setScale(scale, scale, 0);
				p.setPosition(realpos.x, realpos.y, FAR_PLANE+5);
				render.renderGui(window, p);
				//System.out.print("e3rwegty");
			}
			
			if (mouseFocus.equals("chunks")) {
			if (io.github.jakethe28th.engine.
					Utility.pointInRect((int) realpos.x,     			(int) realpos.y, 
										(int) (realpos.x+(16*scale)), 	(int) (realpos.y+(16*scale)), 
										(int) (window.getMousePos().x),	(int) (window.getMousePos().y))) {
						
			if (window.mouseButton(Window.MB_LEFT)) {
				if (!selecting_chunks) {
					startX = ix;//+(offx/16);
					endX = ix;
					startY = iy;//+(offz/16);
					endY = iy;
					System.out.print("\n Started Selecting");
					}
				
				selecting_chunks = true;
				if (ix >= endX) { endX = ix; }
				if (ix < endX) { startX = ix; }

				if (iy >= endY) { endY = iy; }
				if (iy < endY) { startY = iy; }
				
				System.out.print("\n" +  startX + " Y: " + startY + " end: " + endX + " Y: " + endY);
				
			
				int t = 0;
				if (startX > endX) {
					t = endX;
					endX = startX;
					startX = t;
				}
				
				if (startY > endY) {
					t = endY;
					endY = startY;
					startY = t;
				} }  else selecting_chunks = false;
			
			EngineObject p = genPanel(0, 0, 15, 15, 0, new Vector3f(0.75f, 0.75f, 0.5f), new Vector3f(1,1,1));
			p.setScale(scale, scale, 0);
			p.setPosition(realpos.x, realpos.y, FAR_PLANE+5);
			render.renderGui(window, p);
				
			} }
				
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
	
	public void initGui() {
		panel_top_left.set("inline", new Element());
		panel_top_left.set("inline.file", new Element());
		panel_top_left.set("inline.view", new Element());
		panel_top_left.set("inline.options", new Element());
		panel_top_left.set("inline.help", new Element());
		
		panel_top_left.set("inline.file.open_world", new Element());
		panel_top_left.set("inline.file.export_selection", new Element());
		
		panel_top_left.get("inline").setProperty("type", Element.ELEMENT_TYPE_INLINE);
		
		panel_top_left.get("inline.file").setProperty("type", "dropdown");
		panel_top_left.get("inline.file").setProperty("string", "File");
		setTheme(panel_top_left, "inline.file");
			
			panel_top_left.get("inline.file.open_world").setProperty("type", "button");
			panel_top_left.get("inline.file.open_world").setProperty("string", "Open world");
			setTheme(panel_top_left, "inline.file.open_world");
			
			panel_top_left.get("inline.file.export_selection").setProperty("type", "button");
			panel_top_left.get("inline.file.export_selection").setProperty("string", "Export Selected");
			setTheme(panel_top_left, "inline.file.export_selection");
		
		panel_top_left.get("inline.view").setProperty("type", "dropdown");
		panel_top_left.get("inline.view").setProperty("string", "View");
		setTheme(panel_top_left, "inline.view");
		
		panel_top_left.get("inline.options").setProperty("type", "dropdown");
		panel_top_left.get("inline.options").setProperty("string", "Options");
		setTheme(panel_top_left, "inline.options");
		
		panel_top_left.get("inline.help").setProperty("type", "dropdown");
		panel_top_left.get("inline.help").setProperty("string", "Help");
		setTheme(panel_top_left, "inline.help");
	
	panel_top_right.set("inline", new Element());
	panel_top_right.get("inline").setProperty("type", Element.ELEMENT_TYPE_INLINE);

	panel_top_right.set("inline.preview_options", new Element());
	panel_top_right.get("inline.preview_options").setProperty("type", "dropdown");
	panel_top_right.get("inline.preview_options").setProperty("string", "Preview options");
	setTheme(panel_top_right, "inline.preview_options");
	
	
	panel_top_left.setProperty("type", Element.ELEMENT_TYPE_COLLECTION);
	panel_top_left.col_base = Utility.hexToRGB("#303030"); 
	panel_top_left.col_outline = Utility.hexToRGB("#171717"); 
	
	panel_top_right.setProperty("type", Element.ELEMENT_TYPE_COLLECTION);
	panel_top_right.col_base = Utility.hexToRGB("#303030"); 
	panel_top_right.col_outline = Utility.hexToRGB("#171717"); 
	
	panel_right.setProperty("type", Element.ELEMENT_TYPE_COLLECTION);
	panel_right.col_base = Utility.hexToRGB("#2F2F2F"); 
	panel_right.col_outline = Utility.hexToRGB("#171717"); 
	
		
	}
	
	public void updateGui(int ww, int wh) throws FontFormatException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		int d = (int) 0;
		
		panel_top_left.setProperty("x1", "0");
		panel_top_left.setProperty("y1", "0");
		panel_top_left.setProperty("x2", (ww-(ww/3)) + "");
		panel_top_left.setProperty("y2", "30");
		
		panel_top_left.draw(render, window, null, 0, 0, d+10);
		
		panel_top_right.setProperty("x1", (ww-(ww/3)) + "");
		panel_top_right.setProperty("y1", "0");
		panel_top_right.setProperty("x2", ww + "");
		panel_top_right.setProperty("y2", "30");
		
		panel_top_right.draw(render, window, null, 0, 0, d+5);
		
		panel_right.setProperty("x1", (ww-(ww/3)) + "");
		panel_right.setProperty("y1", "30");
		panel_right.setProperty("x2", ww + "");
		panel_right.setProperty("y2", wh + "");
		
		panel_right.draw(render, window, null, 0, 0, d);
		
		//Open world
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
			}
		
		if (panel_top_left.get("inline.file.export_selection").getProperty("selected") != null)
			if (panel_top_left.get("inline.file.export_selection").getProperty("selected").equals("true")) {
				panel_top_left.get("inline.file.export_selection").setProperty("selected", "false");
				
				int lx = endX;
				int ly = endY;
				
				int ix = startX;
				int iy = startY;
				
				int ri = 0;
				
				//if (lx > 0 && ly > 0) {
				while (ix <= lx) {
					while (iy <= ly) {
						try {
							String mcaName = (int) Math.floor(((float) ix)/32) + "." + (int) Math.floor(((float) iy)/32);
								if (chunkMap.get(mcaName) == null) {
									System.out.println("MCA " + mcaName + " is NULL");
									chunkMap.put(mcaName, new ChunkMap((int) Math.floor(((float) ix)/32), (int) Math.floor(((float) iy)/32), region_folder));
									}
							ChunkMap map = chunkMap.get(mcaName);
								
							if (map != null) {		
								exporter.addChunkToQueue(map.getChunk(ix, iy), 0, ix, iy);
								ri++;
								}
							
						} catch (ParseException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					iy+=1;
					}
					iy = startY;
					ix+=1;
				}
				//}
				
				System.out.println("\n Exporter queued " + ri);
				}
		
		if (panel_top_left.hovered | panel_top_right.hovered | panel_right.hovered) mouseFocus = "gui";
		//System.out.print("\n" + mouseFocus);

	}
	
	public EngineObject genPanel(int x1, int y1, int x2, int y2, float FAR_PLANE, Vector3f color_outline, Vector3f color) { 
		int left_x = x1;
		int right_x = x2;
		int top_y = y1;
		int bottom_y = y2;
		Vector3f coo = color_outline;
		Vector3f col = color;
		
		//new Vector3f(col, col, col);
		
		EngineObject panel =  new EngineObject(new Mesh(new Vertex[] {
				new Vertex(new Vector3f(left_x-1	, 	top_y-1,  			(FAR_PLANE+2)), 	coo, new Vector2f(0, 0)),
				new Vertex(new Vector3f(left_x-1	, 	bottom_y+1, 		(FAR_PLANE+2)), 	coo, new Vector2f(0, 0)),
				new Vertex(new Vector3f(right_x+1	,	bottom_y+1, 		(FAR_PLANE+2)), 	coo, new Vector2f(0, 0)),
				new Vertex(new Vector3f(right_x+1	,	top_y-1,   			(FAR_PLANE+2)), 	coo, new Vector2f(0, 0)),
			
				new Vertex(new Vector3f(left_x	, 	top_y,  				(FAR_PLANE+4)), 	col, new Vector2f(0, 0)),
				new Vertex(new Vector3f(left_x	, 	bottom_y, 				(FAR_PLANE+4)), 	col, new Vector2f(0, 0)),
				new Vertex(new Vector3f(right_x	,	bottom_y, 				(FAR_PLANE+4)), 	col, new Vector2f(0, 0)),
				new Vertex(new Vector3f(right_x	,	top_y,   				(FAR_PLANE+4)), 	col, new Vector2f(0, 0))
		
		}, 	new int[] { 0, 1, 2, 2, 3, 0, 
				4, 5, 6, 6, 7, 4 }, null));
		
		return panel;
	}
	
}