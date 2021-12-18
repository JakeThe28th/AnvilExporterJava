package io.github.jakethe28th.engine.graphics.gui;

import io.github.jakethe28th.engine.EngineObject;
import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.Vertex;
import io.github.jakethe28th.engine.math.Vector2f;
import io.github.jakethe28th.engine.math.Vector3f;

public class Value {
//Render the value itself
	
	public static final int VALUE_TYPE_BUTTON = 0;
	public static final int VALUE_TYPE_STRING = 1;
	public static final int VALUE_TYPE_NUMBER = 2;
	
	int valueType;
	
	String name = "Name"; 			// Value name
	
	int buttonState = 0;			// 0 / neutral
									// 1 / hover
									// 2 / held
	
	String stringValue = "";
	double doubleValue = 0.0d;
	
	
	// Properties
	boolean buttonIsToggle = false;
	
	boolean editable = true;		// If the value is greyed out
	boolean displayValue = true;	// If the value should be displayed
	
	public void draw(int x1, int y1, int x2, int y2, Vector3f color, Vector3f accent, Vector3f outline, float FAR_PLANE) {
		
		EngineObject panel_right_button =  new EngineObject(new Mesh(new Vertex[] {
				new Vertex(new Vector3f(x1, y1, (FAR_PLANE+4)), 	new Vector3f(outline.x, outline.y, outline.z), 		new Vector2f(0, 0)),
				new Vertex(new Vector3f(x1, y2,	(FAR_PLANE+4)), 	new Vector3f(outline.x, outline.y, outline.z),   	new Vector2f(0, 0)),
				new Vertex(new Vector3f(x2,	y2, (FAR_PLANE+4)), 	new Vector3f(outline.x, outline.y, outline.z), 		new Vector2f(0, 0)),
				new Vertex(new Vector3f(x2, y1, (FAR_PLANE+4)), 	new Vector3f(outline.x, outline.y, outline.z), 		new Vector2f(0, 0)),
				
				new Vertex(new Vector3f(x1+1, y1+1, (FAR_PLANE+4)), 	new Vector3f(color.x, color.y, color.z), 		new Vector2f(0, 0)),
				new Vertex(new Vector3f(x1+1, y2-1,	(FAR_PLANE+4)), 	new Vector3f(color.x, color.y, color.z),   		new Vector2f(0, 0)),
				new Vertex(new Vector3f(x2-1, y2-1, (FAR_PLANE+4)), 	new Vector3f(color.x, color.y, color.z), 		new Vector2f(0, 0)),
				new Vertex(new Vector3f(x2-1, y1+1, (FAR_PLANE+4)), 	new Vector3f(color.x, color.y, color.z), 		new Vector2f(0, 0))
				
		
		}, 	new int[] { 0, 1, 2, 2, 3, 0, 
						4, 5, 6, 6, 7, 4 }, null));
		
	}
	
	public void drawButton() { }
	public void drawString() { }
	public void drawNumber() { } 
	
}
