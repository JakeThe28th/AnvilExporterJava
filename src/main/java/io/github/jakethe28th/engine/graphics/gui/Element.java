package io.github.jakethe28th.engine.graphics.gui;

import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.github.jakethe28th.engine.EngineObject;
import io.github.jakethe28th.engine.Utility;
import io.github.jakethe28th.engine.graphics.Mesh;
import io.github.jakethe28th.engine.graphics.Renderer;
import io.github.jakethe28th.engine.graphics.Vertex;
import io.github.jakethe28th.engine.graphics.Window;
import io.github.jakethe28th.engine.math.Vector2f;
import io.github.jakethe28th.engine.math.Vector3f;

public class Element {
	LinkedHashMap<String, Element> elements; 
		//Elements held in this element
	
	LinkedHashMap<String, String> properties;
	
	public static final String ELEMENT_TYPE_COLLECTION 	= "collection";
	public static final String ELEMENT_TYPE_BUTTON		= "button";
	public static final String ELEMENT_TYPE_STRING 		= "string";
	public static final String ELEMENT_TYPE_DROPDOWN 	= "dropdown";
	public static final String ELEMENT_TYPE_INLINE 		= "inline_field";
	public static final String ELEMENT_TYPE_WIDGET 		= "widget";
	public static final String ELEMENT_TYPE_NUMBER 		= "number";
	public static final String ELEMENT_TYPE_SEP			= "seperator";
		//Types
	
	public Vector3f col_outline_select = new Vector3f(0.38431372549f, 0.63529411764f, 0.89411764705f);
	public Vector3f col_select = new Vector3f(0.78823529411f, 0.87843137254f, 0.96862745098f);
	public Vector3f col_hover = new Vector3f(0.93725490196f, 0.96470588235f, 0.99607843137f);
	public Vector3f col_outline_hover = new Vector3f(0.66666666666f, 0.82745098039f, 0.99607843137f);
	public Vector3f col_base = new Vector3f(1, 1, 1);
	public Vector3f col_outline = new Vector3f(.75f, .75f, .75f);
	
	public Vector3f col_text = new Vector3f(0, 0, 0);
	
	public int type; //Set this to a type from above
	
	public boolean hovered = false;
	private Window window;
	
	public long lastDraw;
	
	public Element() {
		elements = new LinkedHashMap<String, Element>(); 
		properties = new LinkedHashMap<String, String>();
	}
	
	public Element(String type, String string) {
		elements = new LinkedHashMap<String, Element>(); 
		properties = new LinkedHashMap<String, String>();
	}
	
	public Element set(String setString, Element setElement) {
		//Set a subelement of this element, with the specified path.
		//System.out.println(setString);
		String[] setStringArr = setString.split("\\."); //Parse the string
		
		if (setStringArr.length == 1) {
			//Replace the element of that name in this element.
			elements.put(setStringArr[0], setElement);
		} else {
			//Create key and fill it if it doesn't exist.
			//Go to element in that key and run .set() with this element removed,
			
			Element replace = elements.get(setStringArr[0]);
			if (replace == null) elements.put(setStringArr[0], new Element(
					//Element Init
					));
			
			replace = elements.get(setStringArr[0]);
			
			setString = "";
			String prefix = "";
			for (int i = 1; i < setStringArr.length; i+=1) {
				setString += prefix + setStringArr[i];
				
				prefix = ".";
			}
			replace.set(setString, setElement);	
		}	
		return setElement;
	}
	
	public Element get(String getString) {
		//Get a subelement of this element, with the specified path.
		//System.out.println(getString);
		
		String[] getStringArr = getString.split("\\."); //Parse the string
		
		Element returned = null;
				
				if (getStringArr.length == 1) { returned = elements.get(getStringArr[0]); } else {
					//Go to element in that key and run .get() with this element removed,
					
					Element replace = elements.get(getStringArr[0]);
					if (replace == null) return null;
					
					getString = "";
					String prefix = "";
					for (int i = 1; i < getStringArr.length; i+=1) {
						getString += prefix + getStringArr[i];
						
						prefix = ".";
					}
					returned = replace.get(getString);	
				}	
				
		return returned;
	}
	
	public Element setProperty(String property, String value) {
		properties.put(property, value);
		return this;
		}
	
	public String getProperty(String property) { return properties.get(property); }
	
	public void print(int depth) {
		String depthS = "";
		for (int i = 0; i < depth*2; i+=1) { depthS += "-"; }
		
		//Loop through element and subelements, and print their names and properties to the console.
		//Loop properties, print key and value
		//Loop elements, run print with depth + 1
		
		//Properties loop
		for (String key : properties.keySet()) {
			   System.out.println(depthS + " " + key + ":" + properties.get(key));
			}
		
		//Elements loop.
		for (String key : elements.keySet()) {
		   System.out.println(depthS + " " + key + ":");
		   elements.get(key).print(depth+1);
		}
	}
	
	public void exportToJson() 	{}
	public void importFromJson() 	{}
		//Import / export this Element hierarchy to JSON.
	
	public int[] draw(Renderer render, Window window, Element parent, int y, int x, int depth) throws FontFormatException, IOException {
		int y_size = 0;
		int x_size = 0;
		
		hovered = false;
		lastDraw = System.currentTimeMillis();
		
		for (String key : elements.keySet()) {
			elements.get(key).hovered = false;
		}
		
		
		this.window = window;
		
		float FAR_PLANE = depth*10;
		
		if (properties.get("type").equals(ELEMENT_TYPE_COLLECTION)) { 
			
			try { drawCollection(render, window, depth);
				} catch (FontFormatException | IOException e) { e.printStackTrace(); }
			
			int yy = Integer.valueOf(properties.get("y1"));;
			int xx = Integer.valueOf(properties.get("x1"));;
			for (String key : elements.keySet()) {
				   //System.out.println(depthS + " " + key + ":");
					yy+= elements.get(key).draw(render, window, this, yy, xx, depth+1)[0];
				}	
			}
		
		if (properties.get("type").equals(ELEMENT_TYPE_DROPDOWN)) { 
			int padding = 6;
			boolean inside = false;
			properties.put("clicked_inside", "false");
			
			if (properties.get("select_function") == null) properties.put("select_function", "close");
			
			
			int[] dp = drawDropdown(render, window, parent, y, x, FAR_PLANE);
			y_size+= dp[0];
			x_size+= dp[1];
			
			if (properties.get("clicked_inside").equals("true")) inside = true;
			
			
			if (parent.getProperty("type").equals(ELEMENT_TYPE_DROPDOWN)) {
				x += (Integer.valueOf(parent.getProperty("x2"))-Integer.valueOf(parent.getProperty("x1")));
				dp[0] = 0;
				padding = 1;
				}
			
			if (getProperty("selected") == null) properties.put("selected", "false");
			if (getProperty("selected").equals("true")) {
			
			int yy = y + dp[0] + 1;
			int xx = x + padding;
			int height = 0;
			int width = 0;
			//Fill in height
			for (String key : elements.keySet()) {
				   //System.out.println(depthS + " " + key + ":");
					if (elements.get(key).getProperty("type").equals(ELEMENT_TYPE_BUTTON)) {
						int[] i = elements.get(key).getSize(render, window, this, yy, xx, (-depth)-1);
						height+= i[0];
						if (width < i[1]) width = i[1];
					}
					
					if (elements.get(key).getProperty("type").equals(ELEMENT_TYPE_DROPDOWN)) {
						int[] i = elements.get(key).getSize(render, window, this, yy, xx, (-depth)-1);
						height+= i[0];
						if (width < 20) width = 20;
					}
				}	
			
			this.setProperty("x1", xx + "");
			this.setProperty("y1", yy + "");
			this.setProperty("x2", (xx+width) + "");
			this.setProperty("y2", (yy+height) + "");
			
			EngineObject panel = genPanel(xx, yy, xx+width, yy+height, FAR_PLANE, this.col_outline, col_base);
			
			render.renderGui(window, panel);
			
			yy = y + dp[0] + 1;
			for (String key : elements.keySet()) {
				//System.out.println(depthS + " " + key + ":");
				if (elements.get(key).getProperty("type").equals(ELEMENT_TYPE_BUTTON)) {
					
					//If this isn't the selected element in the dropdown, force it to be unselected.
					if (!key.equals(getProperty("selected_element"))) elements.get(key).setProperty("selected", "false");
					
					//Draw the element.
					int[] i = elements.get(key).draw(render, window, this, yy, xx, depth+1);
					yy+= i[0];
					
					//If this element has been selected since it was reset, set it to the dropdown's selected element.
					if (elements.get(key).getProperty("selected").equals("true") && !key.equals(getProperty("selected_element")) && getProperty("last_state_change").equals("false")) {
						properties.put("selected_element", key);
						if (properties.get("select_function").equals("close")) properties.put("selected", "false");
						} 
					}
				
				if (elements.get(key).getProperty("type").equals(ELEMENT_TYPE_DROPDOWN)) {
					int[] i = elements.get(key).draw(render, window, this, yy, xx, depth+1);
					yy+= i[0];
					if (elements.get(key).getProperty("clicked_inside").equals("true")) inside = true;
					}
				}	
			
			if (window.mouseButton(Window.MB_LEFT)) {
			//If hovering over button:
			if (properties.get("last_state_change").equals("false")) {
			if (Utility.pointInRect(Integer.valueOf(properties.get("x1")), 
									Integer.valueOf(properties.get("y1")), 
									Integer.valueOf(properties.get("x2")), 
									Integer.valueOf(properties.get("y2")),
									(int) window.getMousePos().x, (int) window.getMousePos().y)) {
									//If the left mouse button has been up and is now down:
									if (!inside) inside = true;
									}
									if (!inside) properties.put("selected", "false");
									properties.put("last_state_change", "true");
									} } else properties.put("last_state_change", "false");
								
			if (inside) properties.put("clicked_inside", "true");
				}
			}
		
		if (properties.get("type").equals(ELEMENT_TYPE_INLINE)) { 
			
			//try { drawCollection(render, window, depth);
			//	} catch (FontFormatException | IOException e) { e.printStackTrace(); }
			
			//int yy = y; //Integer.valueOf(properties.get("y1"));;
			int xx = x;
			for (String key : elements.keySet()) {
				   //System.out.println(depth + " " + key + ":");
					int[] i = elements.get(key).draw(render, window, this, y, xx, depth+1);
					xx += i[1];
					if (y_size < i[0]) y_size = i[0];
				}	
			}
		
		if (properties.get("type").equals(ELEMENT_TYPE_BUTTON)) { 
			int[] i = drawButton(render, window, parent, y, x, FAR_PLANE);
			y_size += i[0];
			x_size += i[1];
		}
		
		if (properties.get("type").equals(ELEMENT_TYPE_STRING)) { 
			int[] i = drawString(render, window, parent, y, x, FAR_PLANE);
			y_size += i[0];
			x_size += i[1];
		}
		
		for (String key : elements.keySet()) {
			if (elements.get(key).hovered) hovered = true;
		}
		
		int[] ret = {y_size, x_size};
		return ret;
	}
	
	//Render this Element hierarchy to the screen.
	
	//Render this Element hierarchy to the screen.
	
	public void drawCollection(Renderer render, Window window, float FAR_PLANE) throws FontFormatException, IOException {
		if (properties.get("x1") == null | properties.get("x2") == null) return; 
		if (properties.get("y1") == null | properties.get("y2") == null) return; 
		
		int left_x = 	Integer.valueOf(properties.get("x1"));
		int right_x =	Integer.valueOf(properties.get("x2"));
		int top_y = 	Integer.valueOf(properties.get("y1"));
		int bottom_y = 	Integer.valueOf(properties.get("y2"));
		
		EngineObject panel = genPanel(left_x, top_y, right_x, bottom_y, FAR_PLANE, this.col_outline, col_base);
		
		render.renderGui(window, panel);
	}

	public int[] drawButton(Renderer render, Window window, Element parent, int y, int x, float FAR_PLANE) throws FontFormatException, IOException {
		int[] ret = {0,0};
		if (parent == null) return ret;
		//if (parent.getProperty("x1") == null | parent.getProperty("x2") == null)
		if (properties.get("string") == null) return ret; 

		int string_size = 18;
		int y_size = 0;
		int x_size = 0;
		String drawString = properties.get("string");
		
		int[] dim = TextRenderer.global().getStringDimensions(drawString,  0.2f, string_size, new Vector3f(1,1,1));
		
		int[] dim2 = {0, 0};
		if (properties.get("keybind") != null) {
			dim2 = TextRenderer.global().getStringDimensions(properties.get("keybind"),  0.2f, string_size, new Vector3f(1,1,1));
			}
		
		//add a width
		//if size is less than width conform to x1 and x2
		//else conform to x + width
		
		int padding = 6;
		int icon_width = 15;
		if (parent.getProperty("type").equals(ELEMENT_TYPE_DROPDOWN)) padding = 0;
		
		
		int left_x = 	x+padding;
		int right_x =	((x+((dim[0] + dim2[0])*2))-padding)+icon_width;
		
		if (parent.getProperty("x1") != null & parent.getProperty("x2") != null) {
			left_x = 	Integer.valueOf(parent.getProperty("x1"))+padding;
			right_x =	Integer.valueOf(parent.getProperty("x2"))-padding;
		}
	
		if (properties.get("width") != null) {
			left_x = 	x+padding;
			right_x =	(x+Integer.valueOf(parent.getProperty("width")))-padding;
		}
		
		int top_y = 	y+padding;
		int bottom_y = 	top_y+dim[1];
		
		//Default color
		Vector3f outline_color = this.col_outline;
		Vector3f color = col_base;

		//If hovering over button:
		if (Utility.pointInRect(left_x, top_y, right_x, bottom_y, (int) window.getMousePos().x, (int) window.getMousePos().y)) {
			//windows/highlighted
			outline_color = this.col_outline_hover;
			color = this.col_hover;
			
			//If the left mouse button has been up and is now down:
			if (window.mouseButton(Window.MB_LEFT)) {
				//Null handling
				if (properties.get("last_state_change") == null) properties.put("last_state_change", "false");
				if (properties.get("selected") == null) properties.put("selected", "false"); 
				
				if (properties.get("last_state_change").equals("false")) {
					//If selected is true, make it false. If it's false, make it true.
					if (properties.get("selected").equals("true")) properties.put("selected", "false"); 
					else if (properties.get("selected").equals("false")) properties.put("selected", "true");
				
				properties.put("last_state_change", "true");
				} } else properties.put("last_state_change", "false");
			};
		
		//Override the color if selected.
		if (properties.get("selected") != null) if (properties.get("selected").equals("true")) {
			//windows/selected
			outline_color = this.col_outline_select;
			color = this.col_select;
		}
		
		EngineObject panel = genPanel(left_x, top_y, right_x, bottom_y, FAR_PLANE, outline_color, color);
		
		render.renderGui(window, panel);
		
		//Text alignment.
		int text_x = left_x + (icon_width + 10);
		if (properties.get("align") != null) {
			if (properties.get("align").equals("left")) text_x = left_x + icon_width;
			if (properties.get("align").equals("right")) text_x = ((right_x - icon_width) - dim[0]);
			if (properties.get("align").equals("center") | properties.get("align").equals("centre")) text_x = ((left_x + right_x)/2) - (dim[0]/2);	
			}
		
		TextRenderer.global().drawString(drawString, render, window, 0.2f, string_size, new Vector3f(text_x, top_y, FAR_PLANE + 6), new Vector3f(1, 1, 1), col_text, 1);
		
		y_size = bottom_y - y;
		x_size = right_x - x;
		ret[0] = (y_size);
		ret[1] = (x_size);
		return ret;
	}
	
	public int[] drawDropdown(Renderer render, Window window, Element parent, int y, int x, float FAR_PLANE) throws FontFormatException, IOException {
		int[] ret = {0,0};
		if (parent == null) return ret;
		//if (parent.getProperty("x1") == null | parent.getProperty("x2") == null)
		if (properties.get("string") == null) return ret; 
		
		String style = "down";
		if (parent.getProperty("type").equals(ELEMENT_TYPE_DROPDOWN)) style = "right";

		int string_size = 18;
		int y_size = 0;
		int x_size = 0;
		String drawString = properties.get("string");
		
		int[] dim = TextRenderer.global().getStringDimensions(drawString,  0.2f, string_size, new Vector3f(1, 1 ,1));

		//add a width
		//if size is less than width conform to x1 and x2
		//else conform to x + width
		
		int padding = 6;
		int icon_width = 15;
		if (parent.getProperty("type").equals(ELEMENT_TYPE_DROPDOWN)) padding = 0;
		
		
		int left_x = 	x+padding;
		int right_x =	((x+(dim[0]*2))-padding)+icon_width+10;
		
		if (parent.getProperty("x1") != null & parent.getProperty("x2") != null) {
			left_x = 	Integer.valueOf(parent.getProperty("x1"))+padding;
			right_x =	Integer.valueOf(parent.getProperty("x2"))-padding;
		}
	
		if (properties.get("width") != null) {
			left_x = 	x+padding;
			right_x =	(x+Integer.valueOf(parent.getProperty("width")))-padding;
		}
		
		int top_y = 	y+padding;
		int bottom_y = 	top_y+dim[1];
		
		//Default color
		Vector3f outline_color = this.col_outline;
		Vector3f color = col_base;

		//If hovering over button:
		if (Utility.pointInRect(left_x, top_y, right_x, bottom_y, (int) window.getMousePos().x, (int) window.getMousePos().y)) {
			//windows/highlighted
			outline_color = this.col_outline_hover;
			color = this.col_hover;
			
			//If the left mouse button has been up and is now down:
			if (window.mouseButton(Window.MB_LEFT)) {
				properties.put("clicked_inside", "true");
				
				//Null handling
				if (properties.get("last_state_change") == null) properties.put("last_state_change", "false");
				if (properties.get("selected") == null) properties.put("selected", "false"); 
				
				if (properties.get("last_state_change").equals("false")) {
					//If selected is true, make it false. If it's false, make it true.
					if (properties.get("selected").equals("true")) properties.put("selected", "false"); 
					else if (properties.get("selected").equals("false")) properties.put("selected", "true");
				
				properties.put("last_state_change", "true");
				} } else properties.put("last_state_change", "false");
			};
		
		//Override the color if selected.
		if (properties.get("selected") != null) if (properties.get("selected").equals("true")) {
			//windows/selected
			outline_color = this.col_outline_select;
			color = this.col_select;
		}
		
		EngineObject panel = genPanel(left_x, top_y, right_x, bottom_y, FAR_PLANE, outline_color, color);
		
		render.renderGui(window, panel);
		
		//Text alignment.
		int text_x = left_x + (icon_width + 10);
		if (properties.get("align") != null) {
			if (properties.get("align").equals("left")) text_x = left_x + icon_width;
			if (properties.get("align").equals("right")) text_x = ((right_x - icon_width) - dim[0]);
			if (properties.get("align").equals("center") | properties.get("align").equals("centre")) text_x = ((left_x + right_x)/2) - (dim[0]/2);	
			}
		
		TextRenderer.global().drawString(drawString, render, window, 0.2f, string_size, new Vector3f(text_x, top_y, FAR_PLANE + 6), new Vector3f(1,1,1), col_text, 1);
		
		y_size = bottom_y - y;
		x_size = right_x - x;
		

		int lx =  right_x - (icon_width + 2);
		int ty = top_y+6;
		int by = bottom_y-5;
		int rx = right_x-8;
		
		EngineObject arrow = null;
		
		if (style.equals("down")) {
			 arrow =  new EngineObject(new Mesh(new Vertex[] {
				new Vertex(new Vector3f(lx-1	 ,ty-1,  (FAR_PLANE+5)), 	color, new Vector2f(0, 0)),
				new Vertex(new Vector3f(rx+1	 ,ty-1, (FAR_PLANE+5)), 	color, new Vector2f(0, 0)),
				new Vertex(new Vector3f((rx+lx)/2,by+1, (FAR_PLANE+5)), 	color, new Vector2f(0, 0)),
				
				new Vertex(new Vector3f(lx		,  ty, 	(FAR_PLANE+6)), 	outline_color, new Vector2f(0, 0)),
				new Vertex(new Vector3f(rx		,  ty, 	(FAR_PLANE+6)), 	outline_color, new Vector2f(0, 0)),
				new Vertex(new Vector3f((rx+lx)/2, by,	(FAR_PLANE+6)), 	outline_color, new Vector2f(0, 0))
			}, 	new int[] { 0, 1, 2, 3, 4, 5}, null)); }
		
		if (style.equals("right")) {
			 arrow =  new EngineObject(new Mesh(new Vertex[] {
				new Vertex(new Vector3f(lx-1	 ,ty-1,  (FAR_PLANE+5)), 	color, new Vector2f(0, 0)),
				new Vertex(new Vector3f(lx+1	 ,by-1, (FAR_PLANE+5)), 	color, new Vector2f(0, 0)),
				new Vertex(new Vector3f(rx-1,(by+ty)/2, (FAR_PLANE+5)), 	color, new Vector2f(0, 0)),
				
				new Vertex(new Vector3f(lx		,  ty, 	(FAR_PLANE+6)), 	outline_color, new Vector2f(0, 0)),
				new Vertex(new Vector3f(lx		,  by, 	(FAR_PLANE+6)), 	outline_color, new Vector2f(0, 0)),
				new Vertex(new Vector3f(rx, (by+ty)/2,	(FAR_PLANE+6)), 	outline_color, new Vector2f(0, 0))
			}, 	new int[] { 0, 1, 2, 3, 4, 5}, null)); }
		
		render.renderGui(window, arrow);
		
		ret[0] = (y_size);
		ret[1] = (x_size);
		return ret;
	}
	
	public int[] drawString(Renderer render, Window window, Element parent, int y, int x, float FAR_PLANE) throws FontFormatException, IOException {
		int[] ret = {0,0};
		if (parent == null) return ret;
		//if (parent.getProperty("x1") == null | parent.getProperty("x2") == null)
		if (properties.get("string") == null) return ret; 

		int string_size = 18;
		int y_size = 0;
		int x_size = 0;
		String drawString = properties.get("string");
		
		int[] dim = TextRenderer.global().getStringDimensions(drawString,  0.2f, string_size, new Vector3f(1,1,1));
		
		int left_x, right_x;
		if (!parent.getProperty("type").equals(ELEMENT_TYPE_COLLECTION)
				&& parent.getProperty("x1") != null && parent.getProperty("x2") != null) {
			left_x = 	Integer.valueOf(parent.getProperty("x1"))+2;
			right_x =	Integer.valueOf(parent.getProperty("x2"))-2;
			}
		left_x = 	x+5;
		right_x =	x+TextRenderer.global().getStringDimensions(drawString, .2f, string_size, new Vector3f(1,1,1))[0];
		int top_y = 	y+2;
		int bottom_y = 	y+dim[1];
		
		
		int icon_width = 15;
		
		//Text alignment.
		int text_x = left_x;
		if (properties.get("align") != null) {
			if (properties.get("align").equals("left")) text_x = left_x + icon_width;
			if (properties.get("align").equals("right")) text_x = ((right_x - icon_width) - dim[0]);
			if (properties.get("align").equals("center") | properties.get("align").equals("centre")) text_x = ((left_x + right_x)/2) - (dim[0]/2);	
			}
		
		TextRenderer.global().drawString(drawString, render, window, 0.2f, string_size, new Vector3f(text_x, top_y, FAR_PLANE + 6), new Vector3f(1,1,1), col_text, 1);
		
		y_size = bottom_y - y;
		x_size = right_x - x;
		ret[0] = (y_size + 5);
		ret[1] = (x_size + 5);
		return ret;
	}
	
	public int[] getSize(Renderer render, Window window, Element parent, int y, int x, int depth) throws FontFormatException, IOException {
		int y_size = 0;
		int x_size = 0;
		
		float FAR_PLANE = depth*10;
		
		if (properties.get("type").equals(ELEMENT_TYPE_BUTTON)) { 
			int[] i = getSizeButton(render, window, parent, y, x, FAR_PLANE);
			y_size += i[0];
			x_size += i[1];
		}
		
		if (properties.get("type").equals(ELEMENT_TYPE_DROPDOWN)) {
			int[] i = getSizeButton(render, window, parent, y, x, FAR_PLANE);
			y_size += i[0];
			x_size += i[1];
		}
		
		if (properties.get("type").equals(ELEMENT_TYPE_STRING)) { 
			int[] i = getSizeString(render, window, parent, y, x, FAR_PLANE);
			y_size += i[0];
			x_size += i[1];
		}
		int[] ret = {y_size, x_size};
		return ret;
	}
	
	public int[] getSizeButton(Renderer render, Window window, Element parent, int y, int x, float FAR_PLANE) throws FontFormatException, IOException {
		int[] ret = {0,0};
		if (parent == null) return ret;
		//if (parent.getProperty("x1") == null | parent.getProperty("x2") == null)
		if (properties.get("string") == null) return ret; 

		int string_size = 18, y_size = 0, x_size = 0;
		String drawString = properties.get("string");
		
		int[] dim = TextRenderer.global().getStringDimensions(drawString,  0.2f, string_size, new Vector3f(1,1,1));
		
		int[] dim2 = {0, 0};
		if (properties.get("keybind") != null) {
			dim2 = TextRenderer.global().getStringDimensions(properties.get("keybind"),  0.2f, string_size, new Vector3f(1,1,1));
			}

		int padding = 6;
		if (parent.getProperty("type").equals(ELEMENT_TYPE_DROPDOWN)) padding = 0;
		
		
		int right_x =	(x+((dim[0] + dim2[0])*2))-padding;
		
		if (parent.getProperty("x1") != null & parent.getProperty("x2") != null) {
			right_x =	Integer.valueOf(parent.getProperty("x2"))-padding;
		}
	
		if (properties.get("width") != null) {
			right_x =	(x+Integer.valueOf(parent.getProperty("width")))-padding;
		}
		
		int top_y = 	y+padding;
		int bottom_y = 	top_y+dim[1];
		
		y_size = bottom_y - y;
		x_size = right_x - x;
		ret[0] = (y_size);
		ret[1] = (x_size);
		return ret;
	}
	
	public int[] getSizeString(Renderer render, Window window, Element parent, int y, int x, float FAR_PLANE) throws FontFormatException, IOException {
		int[] ret = {0,0};
		if (parent == null) return ret;
		//if (parent.getProperty("x1") == null | parent.getProperty("x2") == null)
		if (properties.get("string") == null) return ret; 

		int string_size = 18, y_size = 0, x_size = 0;
		String drawString = properties.get("string");
		
		int[] dim = TextRenderer.global().getStringDimensions(drawString,  0.2f, string_size, new Vector3f(1,1,1));
		
		int right_x;
		if (!parent.getProperty("type").equals(ELEMENT_TYPE_COLLECTION)
				&& parent.getProperty("x1") != null && parent.getProperty("x2") != null) {
			right_x =	Integer.valueOf(parent.getProperty("x2"))-2;
			}

		right_x =	x+TextRenderer.global().getStringDimensions(drawString, .2f, string_size, new Vector3f(1,1,1))[0];
		int bottom_y = 	y+dim[1];
		
		y_size = bottom_y - y;
		x_size = right_x - x;
		ret[0] = (y_size + 5);
		ret[1] = (x_size + 5);
		return ret;
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
		
		if (Utility.pointInRect(left_x, top_y, right_x, bottom_y, (int) window.getMousePos().x, (int) window.getMousePos().y)) {
			hovered = true;
			}
		
		return panel;
	}
}