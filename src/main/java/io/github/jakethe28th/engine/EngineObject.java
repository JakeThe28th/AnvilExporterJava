package io.github.jakethe28th.engine;

import org.joml.Vector3f;
import io.github.jakethe28th.engine.graphics.Mesh;

public class EngineObject {

    private Mesh mesh;
    private Vector3f position;

    private Vector3f scale;

    private Vector3f rotation;
    
    private Vector3f origin;
    
    public static final int SCALE_MODE_SKEW = 0;
    public static final int SCALE_MODE_RESIZE = 1;
    
    public int scale_mode = 1;

    public EngineObject(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        origin = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
        rotation = new Vector3f(0, 0, 0);
        
    }

    public Mesh getMesh() { return mesh; }
    
    public Vector3f getPosition() {  return position; }
    public Vector3f getScale() 			{ return scale; }
    public Vector3f getRotation() 		{ return rotation; }
    public Vector3f getOrigin() {  return origin; }
    
    public void setScale(float x, float y, float z) 	{ this.scale.x = x; this.scale.y = y; this.scale.z = z; }
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    } 
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    
    public void setOrigin(float x, float y, float z) {
        this.origin.x = x;
        this.origin.y = y;
        this.origin.z = z;
    }
    
    
    public void setScaleMode(int scale_mode) { this.scale_mode = scale_mode; }

    
}