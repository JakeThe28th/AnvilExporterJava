package io.github.jakethe28th.engine.graphics;

import io.github.jakethe28th.engine.math.Vector2f;
import io.github.jakethe28th.engine.math.Vector3f;
import io.github.jakethe28th.engine.math.Vector4f;

public class Vertex {
	private Vector3f position;
	private Vector4f color;
	private Vector2f textureCoord;
	private float brightness;
	
	public Vertex(Vector3f position, Vector4f color) {
		this.position = position;
		this.color = color;
		this.textureCoord = new Vector2f(1,1);
		this.brightness = 0f;
	}
	
	public Vertex(Vector3f position, Vector4f color, Vector2f textureCoord) {
		this.position = position;
		this.color = color;
		this.textureCoord = textureCoord;
		this.brightness = 0f;
	}
	
	public Vertex(Vector3f position, Vector4f color, Vector2f textureCoord, float brightness) {
		this.position = position;
		this.color = color;
		this.textureCoord = textureCoord;
		this.brightness = brightness;
	}

	public Vector3f getPosition() 		{ return position; }
	public Vector4f getColor() 			{ return color; }
	public Vector2f getTextureCoord() 	{ return textureCoord; }
	public float 	getBrightness() 	{ return brightness; }
}