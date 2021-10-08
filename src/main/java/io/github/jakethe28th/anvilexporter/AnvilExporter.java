package io.github.jakethe28th.anvilexporter;

import java.io.File;  // Import the File class
import java.io.FileReader;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Iterator;
import java.util.Random;

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
	
	public static Random ran = new Random();
	
	public static void Main() throws IOException, ParseException {
		
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
				new Vertex(new Vector3f(-0.5f, 0.5f,  0.0f), new Vector3f(0.2f, 0.7f, 1), new Vector2f(0, 1.0f)),
				new Vertex(new Vector3f(0.5f, -0.5f,  0.0f), new Vector3f(1, 1, 0.2f), new Vector2f(1.0f, 0.0f))
							}, new int[] {
									 // Front face
								    0, 1, 2, 3, 4, 5
								}, texture);
								
		
		EngineObject myObject = new EngineObject(myMesh);
		myObject.setPosition(0, 0, -2);
		myObject.setScale(1, 1, 1);
		
		
		Camera camera = new Camera();
		

		//window ^
		  
		//JSONObject temp;
		WriteBlock mod = new  WriteBlock("brewing_stand", new Sprite(16, 16));

		JSONParser jsonParser = new JSONParser();
		String states = "facing=east,half=bottom,open=false";
		JSONObject culling = (JSONObject) jsonParser.parse("{\"east\":1,\"west\":1,\"north\":1,\"south\":1}");
		  
		mod.WriteFromBlockstate("assets\\minecraft\\blockstates\\birch_trapdoor.json", states, 0, 0, 0, culling, "minecraft");

		
		MCAFile mcaFile = null;
		mcaFile = MCAUtil.read("region_testing\\r.0.0.mca");
		
		//Chunk chunk = mcaFile.getChunk(0, 0);

		Exporter exporter = new Exporter("", "export");
		

		
		//exporter.exportChunk(mcaFile.getChunk(0, 0), 0, 0, 0);
		//exporter.exportChunk(mcaFile.getChunk(0, 1), 0, 0, 1);
		//exporter.exportChunk(mcaFile.getChunk(1, 1), 0, 1, 1);
		//exporter.exportChunk(mcaFile.getChunk(1, 0), 0, 1, 0);
		
		
		int expx = -1;
		int expz = -1;
		int i = 0;
		while (i < (4*4)) {
			
			int rx = (int) Math.floor((double) expx/32);
			int rz = (int) Math.floor((double) expz/32);
			//if (expx <0) rx = (int) Math.ceil(expx/32);
			//if (expz <0) rz = (int) Math.ceil(expx/32);
			String mcafilename = "r."+rx+"."+rz+".mca";
			
			long fm = (Runtime.getRuntime().maxMemory() - 
	 				 (Runtime.getRuntime().totalMemory()-
	 						Runtime.getRuntime().freeMemory()))/1000000;
			
			System.out.println("Free memory (MEGA - bytes): " + fm);
			
			mcaFile = MCAUtil.read("region_testing\\" + mcafilename);
			exporter.exportChunk(mcaFile.getChunk(expx, expz), 0, expx, expz);
			
			expx += 1;
			if (expx > 2) { expx = 0; expz+=1; }
			i+=1;
		}
		

		
		exporter.end();
	    mod.end();
	    
	    Mesh exporterMesh = exporter.myMesh;
	    
	   // Mesh exporterMesh = exporter.myMesh;
	    
	    	//window v
	    
		exporterMesh.cleanUp();
		exporterMesh.flip();
		//exporterMesh.offset(0, 0, 0);
		exporterMesh.buildMesh();
	    
	    EngineObject exporterObject = new EngineObject(exporterMesh);
		exporterObject.setScaleMode(EngineObject.SCALE_MODE_SKEW);
		exporterObject.setPosition(300, 900, 0);
		exporterObject.setScale(10, 10, 0.000001f);
		exporterObject.setOrigin(31, 0, 31);
		exporterMesh.setTexture(new Texture("export.png"));
		
		exporterMesh.minMax();
		System.out.println("Min X " + exporterMesh.min_xyz.x + " Y " + exporterMesh.min_xyz.y + " Z " + exporterMesh.min_xyz.z);
		System.out.println("Max X " + exporterMesh.max_xyz.x + " Y " + exporterMesh.max_xyz.y + " Z " + exporterMesh.max_xyz.z);
			
			while (!endProgram) {

				//if this.current_region != null drawPreview2D(cam_x, cam_y, cam_zoom)
				
				render.render(window, camera, new EngineObject[] { myObject, });
				render.renderGui(window, new EngineObject[] { exporterObject, });
				
				myObject.setRotation(myObject.getRotation().x+1, myObject.getRotation().y+1, myObject.getRotation().z+1);
				exporterObject.setRotation(45, exporterObject.getRotation().y+1, 0);
				window.loop();

				boolean flipping = false;
				if (window.mouseButton(Window.MB_LEFT)) {
					window.mouseInput();
					float moveX = (window.getMouseDisplacement().x/5);
					float moveY = (window.getMouseDisplacement().y/5);
					
					camera.setRotation((camera.getRotation().x) + moveX, (camera.getRotation().y) + moveY, 0);

					window.setMousePos(window.getWidth()/2,window.getHeight()/2);
					window.mouseInput();
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_E)) {
					exporterObject.setPosition(exporterObject.getPosition().x, (float) (exporterObject.getPosition().y+1), exporterObject.getPosition().z);
					}
				if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
					exporterObject.setPosition(exporterObject.getPosition().x, (float) (exporterObject.getPosition().y-1), exporterObject.getPosition().z);
					}
				
				if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
					exporterObject.setPosition(exporterObject.getPosition().x-1, (float) (exporterObject.getPosition().y), exporterObject.getPosition().z);
					}
				if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
					exporterObject.setPosition(exporterObject.getPosition().x+1, (float) (exporterObject.getPosition().y), exporterObject.getPosition().z);
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
	
	public static void program(Mesh exporterMesh) throws Exception {
		
			
		
	}
	
	/*
	drawPreview2d(float cam_x, float cam_y, float cam_zoom) {
		guiUtility.drawRegion(x, z, xcoord, zcoord, scale)
		
		regions[x][z], if sprite_region = null makeSprite(region)
		
		
	}
	*/
}