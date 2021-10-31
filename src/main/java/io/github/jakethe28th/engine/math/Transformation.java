package io.github.jakethe28th.engine.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import io.github.jakethe28th.engine.EngineObject;
import io.github.jakethe28th.engine.graphics.Camera;

public class Transformation {

    private final Matrix4f projectionMatrix;

    private final Matrix4f modelViewMatrix;
    
    private final Matrix4f viewMatrix;
    
    private final Matrix4f orthoMatrix;
    
    private final Matrix4f modelMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        
        modelMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar);
    }
    
    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();
        
        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public Matrix4f getModelViewMatrix(EngineObject gameItem, Matrix4f viewMatrix) {
        Vector3f rotation = gameItem.getRotation();
        modelViewMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale().x, gameItem.getScale().y, gameItem.getScale().z).
                translate(gameItem.getOrigin());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }
    
    public final Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top) {
        orthoMatrix.identity();
        orthoMatrix.setOrtho(left, right, bottom, top, -1f, 1f);
        return orthoMatrix;
    }
    
    public Matrix4f getOrthoProjModelMatrix(EngineObject gameItem, Matrix4f orthoMatrix) {
        Vector3f rotation = gameItem.getRotation();       
        
        modelMatrix.identity();

        modelMatrix.translate(gameItem.getPosition());
        	
        //Scale after rotation. (Useful to flatten stuff, 
        //so that it fits in the -1 to 1 coordinate range for orthographic.
        if ( gameItem.scale_mode == EngineObject.SCALE_MODE_SKEW ) {
        	modelMatrix.scale(gameItem.getScale().x, gameItem.getScale().y, gameItem.getScale().z); }
        	
        modelMatrix.rotateX((float)Math.toRadians(-rotation.x)).
        	rotateY((float)Math.toRadians(-rotation.y)).
        	rotateZ((float)Math.toRadians(-rotation.z));
        
        if ( gameItem.scale_mode == EngineObject.SCALE_MODE_RESIZE ) {
        	modelMatrix.scale(gameItem.getScale().x, gameItem.getScale().y, gameItem.getScale().z); }
        
      	//Translate before rotation / scaling, for the origin point
        modelMatrix.translate(gameItem.getOrigin());
        
        Matrix4f orthoMatrixCurr = new Matrix4f(orthoMatrix);
        return orthoMatrixCurr.mul(modelMatrix);
    }
    
    public Matrix4f getOrthoProjModelMatrix(int scale_mode, Vector3f position, Vector3f scale, Vector3f rotation, Matrix4f orthoMatrix) {
        
        modelMatrix.identity();

        modelMatrix.translate(position);
        	
        //Scale after rotation. (Useful to flatten stuff, 
        //so that it fits in the -1 to 1 coordinate range for orthographic.
        if ( scale_mode == EngineObject.SCALE_MODE_SKEW ) {
        	modelMatrix.scale(scale.x, scale.y, scale.z); }
        	
        modelMatrix.rotateX((float)Math.toRadians(-rotation.x)).
        	rotateY((float)Math.toRadians(-rotation.y)).
        	rotateZ((float)Math.toRadians(-rotation.z));
        
        if ( scale_mode == EngineObject.SCALE_MODE_RESIZE ) {
        	modelMatrix.scale(scale.x, scale.y, scale.z); }
        
      	//Translate before rotation / scaling, for the origin point
        //modelMatrix.translate(gameItem.getOrigin());
        
        Matrix4f orthoMatrixCurr = new Matrix4f(orthoMatrix);
        return orthoMatrixCurr.mul(modelMatrix);
    }
}