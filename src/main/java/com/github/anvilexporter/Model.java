package com.github.anvilexporter;

import com.github.anvilexporter.engine.Vector2f;
import com.github.anvilexporter.engine.Vector3f;

public class Model {
	
	/**
	 * Combine this model with another one
	 * @param model
	 */
	public void combine(Model model) { }
	
	/**
	 * Export this model to an obj file at the specified path
	 * @param filepath
	 */
	public void export(String filepath) { }
	
	/**
	 * Adds a quad to this model with the specified position, texture coordinates, vertex colors, and normals.
	 * And also rotate, scale, and translate it as needed
	 */
	public void addQuad(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4,
						Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector2f uv4,
						Vector3f c1, Vector3f c2, Vector3f c3, Vector3f c4,
						Vector3f n1, Vector3f n2, Vector3f n3, Vector3f n4,
						double xx_rot, double yy_rot, double zz_rot,
						double xx_scale, double yy_scale, double zz_scale,
						double xx_position, double yy_position, double zz_position) { }

}
