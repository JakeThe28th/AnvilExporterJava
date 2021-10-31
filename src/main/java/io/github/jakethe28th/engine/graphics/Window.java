package io.github.jakethe28th.engine.graphics;

import org.joml.Vector2d;
import org.joml.Vector2f;
//import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
		int width, height;
		boolean resized;
		public String title;
	
		// The window handle
		public long window;
		
		//Constants
	    public static final int MB_LEFT = 0;
	    public static final int MB_RIGHT = 1;
	    
	    //Other mouse stuff
	    private final Vector2d previousPos;
	    private final Vector2d currentPos;
	    private final Vector2f displVec;

	    private boolean inWindow = false;
	    private boolean leftButtonPressed = false;
	    private boolean rightButtonPressed = false;
		
		public Window(int w, int h, String title) { 
			previousPos = new Vector2d(-1, -1);
		    currentPos = new Vector2d(0, 0);
		    displVec = new Vector2f();
			
		    
			init(w, h, title);
			mouseInit();
			}

		private void init(int w, int h, String title) {
			this.title = title;
			
			// Setup an error callback. The default implementation
			// will print the error message in System.err.
			GLFWErrorCallback.createPrint(System.err).set();

			// Initialize GLFW. Most GLFW functions will not work before doing this.
			if ( !glfwInit() )
				throw new IllegalStateException("Unable to initialize GLFW");

			// Configure GLFW
			glfwDefaultWindowHints(); // optional, the current window hints are already the default
			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
			glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
			
			// Create the window
			window = glfwCreateWindow(w, h, title, NULL, NULL);
			if ( window == NULL )
				throw new RuntimeException("Failed to create the GLFW window");

			createCallbacks();
			
			// Get the thread stack and push a new frame
			try ( MemoryStack stack = stackPush() ) {
				IntBuffer pWidth = stack.mallocInt(1); // int*
				IntBuffer pHeight = stack.mallocInt(1); // int*

				// Get the window size passed to glfwCreateWindow
				glfwGetWindowSize(window, pWidth, pHeight);

				// Get the resolution of the primary monitor
				GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

				// Center the window
				glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
				);
			} // the stack frame is popped automatically

			// Make the OpenGL context current
			glfwMakeContextCurrent(window);
			// Enable v-sync
			glfwSwapInterval(1);

			// Make the window visible
			glfwShowWindow(window);
			
			// This line is critical for LWJGL's interoperation with GLFW's
			// OpenGL context, or any context that is managed externally.
			// LWJGL detects the context that is current in the current thread,
			// creates the GLCapabilities instance and makes the OpenGL
			// bindings available for use.
			GL.createCapabilities();

			// Set the clear color
			glClearColor(0.3f, 0.0f, 0.0f, 0.0f);
			
			//Enable Z-testing
			glEnable(GL_DEPTH_TEST);
			
			//Enable Alpha testing
			glEnable(GL_ALPHA_TEST);
			
			//alpha blending
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			//no blurry
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		}

		public void loop() {
			glfwSetWindowTitle(window, title);
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		public void end() {
			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);

			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
		
		public long getWindow() { return window; }
		
		public boolean isKeyPressed(int keyCode) { return glfwGetKey(window, keyCode) == GLFW_PRESS; }
	    
		
	    public String getTitle() 				{ return title; }
	    public int getWidth() 					{ return width; }
	    public int getHeight() 					{ return height;  }
	    public boolean isResized() 				{ return resized; }
	    public boolean shouldClose()			{ return glfwWindowShouldClose(window); }
	    
	    public void setResized(boolean resized) { this.resized = resized; }
	   
		
		private void createCallbacks() {
			// Setup a key callback. It will be called every time a key is pressed, repeated or released.
						glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
							if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
								glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
						});
							
			 // Setup resize callback
				        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
				            this.width = width;
				            this.height = height;
				            this.setResized(true);
				        });
			
		}

		public long getWindowHandle() {
			return window;
		}
		
		
		
		//      |
		//Mouse V

		public void setMousePos(int x, int y) {
			currentPos.x = x;
            currentPos.y = y;
			glfwSetCursorPos(window, x, y);
		}	
		
		public void mouseInit() {
		        glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
		            currentPos.x = xpos;
		            currentPos.y = ypos;
		        });
		        glfwSetCursorEnterCallback(window, (windowHandle, entered) -> {
		            inWindow = entered;
		        });
		        glfwSetMouseButtonCallback(window, (windowHandle, button, action, mode) -> {
		            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
		            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
		        });
		    }
		
		public Vector2f getMouseDisplacement() {
	        return displVec;
	    }
		
		public void mouseInput() {
			displVec.x = 0;
			displVec.y = 0;
			if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
				double deltax = currentPos.x - previousPos.x;
				double deltay = currentPos.y - previousPos.y;
				boolean rotateX = deltax != 0;
				boolean rotateY = deltay != 0;
				if (rotateX) { displVec.y = (float) deltax; }
				if (rotateY) { displVec.x = (float) deltay; }
		        	}
		    previousPos.x = currentPos.x;
		    previousPos.y = currentPos.y;
		    }
		
		public boolean mouseButton(int mb) {
		   if (mb == MB_LEFT) return leftButtonPressed;
		   if (mb == MB_RIGHT) return rightButtonPressed;
		   return false;
		   }
	
}
