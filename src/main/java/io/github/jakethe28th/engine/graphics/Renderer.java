package io.github.jakethe28th.engine.graphics;


import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;

import io.github.jakethe28th.engine.EngineObject;
import io.github.jakethe28th.engine.Utility;
import io.github.jakethe28th.engine.math.Transformation;
import io.github.jakethe28th.engine.math.Vector3f;
import io.github.jakethe28th.engine.math.Vector4f;

public class Renderer {

    private Shader shader;
    private Shader shader_ortho;
    private Shader shader_ortho_flat;
    private Transformation transformation;
    
    //projection
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private Matrix4f projectionMatrix;

    public Renderer() throws Exception {
    	transformation = new Transformation();
    	init();
    	
    	//add a shader file handler and use shaders via filenames aka flat textured shaded
    }

    private static Shader setUpShader(String path) throws Exception {
    	String type = Utility.loadFromGLSL(Utility.loadAsString(path), "type");
    	Shader shadert = new Shader();
			shadert.createVertexShader(Utility.loadFromGLSL(Utility.loadAsString(path), "vertex"));
			shadert.createFragmentShader(Utility.loadFromGLSL(Utility.loadAsString(path), "fragment"));
			shadert.link();
		
			System.out.println(type);
			if (type.contains("orthographic")) {
				// Create uniforms for Ortographic-model projection matrix and base colour
		    	shadert.createUniform("projModelMatrix");
		    	//shader_ortho.createUniform("color");
		    	shadert.createUniform("texture_sampler");
				} else {
				// Create uniforms for modelView and projection matrices and texture
				shadert.createUniform("projectionMatrix");
				shadert.createUniform("modelViewMatrix");
				shadert.createUniform("texture_sampler");
				}
			
			shadert.type = type;
    	
			return shadert;
    }

    public void init() throws Exception {
    		shader = setUpShader("assets/engine/shaders/shader.glsl");
            
            //GUI shaders
            shader_ortho = setUpShader("assets/engine/shaders/ortho.glsl");
            shader_ortho_flat = setUpShader("assets/engine/shaders/ortho.glsl");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, EngineObject[] EngineObjects) throws Exception {
        //clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        
        shader.bind();
        
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shader.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shader.setUniform("texture_sampler", 0);
        // Render each gameItem
        for(EngineObject gameItem : EngineObjects) {
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shader.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            gameItem.getMesh().render();
        }
        
        shader.unbind();  
    }

    public void renderGui (Window window, EngineObject[] EngineObjects) {
    	//clear();
    	
          if (window.isResized()) {
              glViewport(0, 0, window.getWidth(), window.getHeight());
              window.setResized(false);
          }
          
          shader_ortho.bind();
          
          shader.setUniform("texture_sampler", 0);

          Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
          for (EngineObject gameItem : EngineObjects) {
              Mesh mesh = gameItem.getMesh();
              // Set orthographic and model matrix for this HUD item
              Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(gameItem, ortho);
              shader_ortho.setUniform("projModelMatrix", projModelMatrix);
             // shader_ortho.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColour());

              // Render the mesh for this HUD item
              mesh.render();
          }

          shader_ortho.unbind();
          
    }
    
    public void renderGui (Window window, EngineObject EngineObject) {
    	//clear();
    	
          if (window.isResized()) {
              glViewport(0, 0, window.getWidth(), window.getHeight());
              window.setResized(false);
          }
          
          shader_ortho_flat.bind();
          
          shader_ortho_flat.setUniform("texture_sampler", 0);

          Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
          Mesh mesh = EngineObject.getMesh();
          // Set orthographic and model matrix for this HUD item
          Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(EngineObject, ortho);
          shader_ortho_flat.setUniform("projModelMatrix", projModelMatrix);
         // shader_ortho.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColour());

          // Render the mesh for this HUD item
          mesh.render();
          shader_ortho_flat.unbind();
          
    }
    
    public void renderSpriteGui(Window window, Sprite sheet, int id, Vector3f pos, Vector3f scale, Vector3f rot, Vector4f col, float alpha)
    {
    	if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
    	
    	if (!sheet.isNull(id)) {
        
        shader_ortho.bind();
        
        shader.setUniform("texture_sampler", 0);

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        //
            Mesh mesh = sheet.getMesh(id);
            mesh.setColor(col);
            mesh.buildMesh();
            
            // Set orthographic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(EngineObject.SCALE_MODE_SKEW, new org.joml.Vector3f(pos.x, pos.y, pos.z), new org.joml.Vector3f(scale.x, scale.y, scale.z), new org.joml.Vector3f(rot.x, rot.y, rot.z), ortho);
            shader_ortho.setUniform("projModelMatrix", projModelMatrix);
            // shader_ortho.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColour());

            // Render the mesh for this HUD item
            sheet.draw(id);

        shader_ortho.unbind();
        
    	}
    	
    	}

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
    }
}