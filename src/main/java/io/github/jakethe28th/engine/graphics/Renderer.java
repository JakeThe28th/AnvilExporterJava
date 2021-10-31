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
import io.github.jakethe28th.engine.graphics.gui.Sprite;
import io.github.jakethe28th.engine.math.Transformation;
import io.github.jakethe28th.engine.math.Vector3f;

public class Renderer {

    private Shader shader;
    private Shader shaderGui;
    private Transformation transformation;
    
    //projection
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private Matrix4f projectionMatrix;

    public Renderer() throws Exception {
    	transformation = new Transformation();
    	init();
    }

    public void init() throws Exception {
    		shader = new Shader();
    		shader.createVertexShader(Utility.loadAsString("assets/engine/shaders/vertex.vs"));
    		shader.createFragmentShader(Utility.loadAsString("assets/engine/shaders/fragment.fs"));
    		shader.link();
    		
    		// Create uniforms for modelView and projection matrices and texture
            shader.createUniform("projectionMatrix");
            shader.createUniform("modelViewMatrix");
            shader.createUniform("texture_sampler");
            
            //GUI shaders
            shaderGui = new Shader();
            shaderGui.createVertexShader(Utility.loadAsString("assets/engine/shaders/gui.vs"));
            shaderGui.createFragmentShader(Utility.loadAsString("assets/engine/shaders/gui.fs"));
            shaderGui.link();

            // Create uniforms for Ortographic-model projection matrix and base colour
            shaderGui.createUniform("projModelMatrix");
            //shaderGui.createUniform("color");
            shaderGui.createUniform("texture_sampler");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, EngineObject[] EngineObjects) throws Exception {
        clear();

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
          
          shaderGui.bind();
          
          shader.setUniform("texture_sampler", 0);

          Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
          for (EngineObject gameItem : EngineObjects) {
              Mesh mesh = gameItem.getMesh();
              // Set orthographic and model matrix for this HUD item
              Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(gameItem, ortho);
              shaderGui.setUniform("projModelMatrix", projModelMatrix);
             // shaderGui.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColour());

              // Render the mesh for this HUD item
              mesh.render();
          }

          shaderGui.unbind();
          
    }
    
    public void renderSpriteGui(Window window, Sprite sheet, int id, Vector3f pos, Vector3f scale, Vector3f rot, Vector3f col, float alpha)
    {
    	if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
    	
    	if (!sheet.isNull(id)) {
        
        shaderGui.bind();
        
        shader.setUniform("texture_sampler", 0);

        Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
        //
            Mesh mesh = sheet.getMesh(id);
            // Set orthographic and model matrix for this HUD item
            Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(EngineObject.SCALE_MODE_SKEW, new org.joml.Vector3f(pos.x, pos.y, pos.z), new org.joml.Vector3f(scale.x, scale.y, scale.z), new org.joml.Vector3f(rot.x, rot.y, rot.z), ortho);
            shaderGui.setUniform("projModelMatrix", projModelMatrix);
            // shaderGui.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColour());

            // Render the mesh for this HUD item
            sheet.draw(id);

        shaderGui.unbind();
        
    	}
    	
    	}

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
    }
}