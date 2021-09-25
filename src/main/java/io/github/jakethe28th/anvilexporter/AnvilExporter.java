package io.github.jakethe28th.anvilexporter;

import java.io.File;  // Import the File class
import java.io.FileReader;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Iterator;
import java.util.Random;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.github.jakethe28th.engine.EngineObject;
import io.github.jakethe28th.engine.graphics.Camera;
import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.Renderer;
import io.github.jakethe28th.engine.graphics.Texture;
import io.github.jakethe28th.engine.graphics.Vertex;
import io.github.jakethe28th.engine.graphics.Window;
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
		  
		  //JSONObject temp;
		  WriteBlock mod = new  WriteBlock("brewing_stand");
		  //mod.WriteModel("assets\\\\minecraft\\\\models\\\\block\\\\acacia_fence_gate_wall_open.json", 182, 2, 4, 0, 0, 0, null);

		  JSONParser jsonParser = new JSONParser();
		  String states = "facing=east,half=bottom,open=false";
		  JSONObject culling = (JSONObject) jsonParser.parse("{\"east\":1,\"west\":1,\"north\":1,\"south\":1}");
		  
		  //System.out.println(states);
		  //System.out.println(culling);
		  
		  mod.WriteFromBlockstate("assets\\minecraft\\blockstates\\birch_trapdoor.json", states, 0, 0, 0, culling);
		  
		  //mod.end();
		  
		  System.out.println(v_count);
	    
		  
		//  /*
		  
		MCAFile mcaFile = null;
		mcaFile = MCAUtil.read("region_testing\\r.0.0.mca");
		
		//Chunk chunk = mcaFile.getChunk(0, 0);

		Exporter exporter = new Exporter("", "export");
		

		
		//exporter.exportChunk(mcaFile.getChunk(0, 0), 0, 0, 0);
		//exporter.exportChunk(mcaFile.getChunk(0, 1), 0, 0, 1);
		//exporter.exportChunk(mcaFile.getChunk(1, 1), 0, 1, 1);
		//exporter.exportChunk(mcaFile.getChunk(1, 0), 0, 1, 0);
		
		
		int expx = -2;
		int expz = -2;
		int i = 0;
		while (i < (4*4)) {
			
			int rx = (int) Math.floor((double) expx/32);
			int rz = (int) Math.floor((double) expz/32);
			//if (expx <0) rx = (int) Math.ceil(expx/32);
			//if (expz <0) rz = (int) Math.ceil(expx/32);
			String mcafilename = "r."+rx+"."+rz+".mca";
			
			mcaFile = MCAUtil.read("region_testing\\" + mcafilename);
			System.out.println(mcafilename);
			System.out.println(expx + " . " + expz);
			exporter.exportChunk(mcaFile.getChunk(expx, expz), 0, expx, expz);
			
			expx += 1;
			if (expx > 2) { expx = 0; expz+=1; }
			i+=1;
		}
		

		
		exporter.end();
	    mod.end();
	  }
	
	public static void program() throws Exception {
		boolean endProgram = false;
		
		Window window = new Window(600, 600, "AnvilExporter");
		Renderer render = new Renderer();
		Texture texture = new Texture("assets/engine/textures/jesse.png");
			
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
		
		while (!endProgram) {
			
			render.render(window, camera, new EngineObject[] { myObject, });
			render.renderGui(window, new EngineObject[] { });
			
			myObject.setRotation(myObject.getRotation().x+1, myObject.getRotation().y+1, myObject.getRotation().z+1);
			window.loop();

			if (window.mouseButton(Window.MB_LEFT)) {
				window.mouseInput();
				float moveX = (window.getMouseDisplacement().x/5);
				float moveY = (window.getMouseDisplacement().y/5);
				
				camera.setRotation((camera.getRotation().x) + moveX, (camera.getRotation().y) + moveY, 0);

				window.setMousePos(window.getWidth()/2,window.getHeight()/2);
				window.mouseInput();
				}

		
			if (window.shouldClose()) endProgram = true;
		}
		window.end();
		render.cleanup();
	}
}