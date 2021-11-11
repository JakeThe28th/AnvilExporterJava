package io.github.jakethe28th.engine.graphics;

import org.lwjgl.system.MemoryUtil;

import io.github.jakethe28th.engine.math.Vector2f;
import io.github.jakethe28th.engine.math.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private int 		vaoId;
    private int 		pbo; //pos
    private int 		cbo; //color
    private int 		tbo; //texture
    private int 		ibo; //index
    private int 		vertexCount;
    private Vertex[] 	vertices = new Vertex[] {};
    private int[] 		indices = new int[] {};
    private Texture texture;
    
    public Vector3f min_xyz;
    public Vector3f max_xyz;
    
    public int tex_mag_filter;
    
    public Mesh(Vertex[] vertices, int[] indices, Texture texture) {
    	this.min_xyz = new Vector3f(0, 0, 0);
    	this.max_xyz = new Vector3f(0, 0, 0);
    	this.texture = texture;
    	buildMesh(vertices, indices);
    	
    	tex_mag_filter = GL_NEAREST;
    }
    /*
    public Mesh(float[] verticesArr, float[] colorArr, int[] indices) {
    	vertices = new Vertex[verticesArr.length];
    	int i = 0;
    	while (i < (verticesArr.length/3)) {
    	vertices[i]= new Vertex(
    				new Vector3f(verticesArr[(i*3)+0],verticesArr[(i*3)+1],verticesArr[(i*3)+2]),
    				new Vector3f(colorArr[(i*3)+0],colorArr[(i*3)+1],colorArr[(i*3)+2]),
    				new Vector2f(0, 0));
    				i+=1;
    	}
    	
    	buildMesh(vertices, indices);
    }*/
    

    //Add vertices to array
    public void addVertices(Vertex[] vertices, int[] indices) {
    	Vertex[] newVertices = new Vertex[ vertices.length + this.vertices.length];
    	int[] newIndices = new int[indices.length + this.indices.length];
    	
    	System.arraycopy(this.vertices, 0, newVertices, 0, this.vertices.length);
    	System.arraycopy(this.indices, 0, newIndices, 0, this.indices.length);
    	
    	//System.out.println(newVertices.length);
    	
    	System.arraycopy(vertices, 0, newVertices, this.vertices.length, vertices.length);
    	System.arraycopy(indices, 0, newIndices, this.indices.length, indices.length);
    	
    	this.vertices = newVertices;
    	this.indices = newIndices;
    	//buildMesh(this.vertices, this.indices);
    }
    
    public void flip() {
    	if (vertices.length == 0) return;
    	if (vertices != null) {
    	Vertex[] newVertices = new Vertex[this.vertices.length];

    	int i = 0;
    	while (i < this.vertices.length) {
    		
    		
    		Vector3f coords = vertices[i].getPosition();
    		Vector2f texcoords = vertices[i].getTextureCoord();
    		Vector3f colors = vertices[i].getColor();
    		
    		newVertices[i] = new Vertex(new Vector3f(coords.x, -coords.y, coords.z),
    									new Vector3f(colors.x, colors.y, colors.z), 
    									new Vector2f(texcoords.x, texcoords.y));
    		i+=1;
    		}
    	
    	this.vertices = newVertices;
    	//buildMesh(this.vertices, this.indices);
    	}
    }
    
    public void offset(float x, float y, float z) {
    	if (vertices != null) {
    	Vertex[] newVertices = new Vertex[this.vertices.length];

    	int i = 0;
    	while (i < this.vertices.length) {
    		
    		
    		Vector3f coords = vertices[i].getPosition();
    		Vector2f texcoords = vertices[i].getTextureCoord();
    		
    		newVertices[i] = new Vertex(new Vector3f(coords.x+x, coords.y+y, coords.z+z),
    									new Vector3f(1, 1, 1), 
    									new Vector2f(texcoords.x, texcoords.y));
    		i+=1;
    		}
    	
    	this.vertices = newVertices;
    	//buildMesh(this.vertices, this.indices);
    	}
    }
    
    //No arguments
    public void buildMesh() { buildMesh(this.vertices, this.indices); }
    
    //There's a memory leak in this.
    
    //Create VBOs for the mesh.
    public void buildMesh(Vertex[] vertices, int[] indices) {
    	cleanUp();
    	
    	FloatBuffer posBuffer = null;
    	FloatBuffer colBuffer = null;
    	FloatBuffer texBuffer = null;
    	IntBuffer   indBuffer = null;
    	
    	//System.out.println("Berty");
    	
    	this.vertices = vertices;
    	this.indices = indices;
        try {
            vertexCount = indices.length;

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO and Color VBO
            pbo = glGenBuffers();
            posBuffer = MemoryUtil.memAllocFloat((vertices.length)*3);
            
            cbo = glGenBuffers();
            colBuffer = MemoryUtil.memAllocFloat((vertices.length)*3);
            
            tbo = glGenBuffers();
            texBuffer = MemoryUtil.memAllocFloat((vertices.length)*3);
            
            //Set each value in buffer
            for (int i = 0; i < vertices.length; i++) {
            	posBuffer.put(vertices[i].getPosition().x);
            	posBuffer.put(vertices[i].getPosition().y);
            	posBuffer.put(vertices[i].getPosition().z);
            	
            	colBuffer.put(vertices[i].getColor().x);
            	colBuffer.put(vertices[i].getColor().y);
            	colBuffer.put(vertices[i].getColor().z);
            	
            	texBuffer.put(vertices[i].getTextureCoord().x);
            	texBuffer.put(vertices[i].getTextureCoord().y);
            	}
            
          	posBuffer.flip(); //End writing to posBuffer
          	colBuffer.flip(); //End writing to colBuffer
          	texBuffer.flip(); //End writing to colBuffer
            
          	//do stuff with pos buffer IDK
          	glBindBuffer(GL_ARRAY_BUFFER, pbo);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //do stuff with color buffer IDK
            glBindBuffer(GL_ARRAY_BUFFER, cbo);
            glBufferData(GL_ARRAY_BUFFER, colBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
            
            //do stuff with texture buffer IDK
            glBindBuffer(GL_ARRAY_BUFFER, tbo);
            glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
            
            // Index VBO
            ibo = glGenBuffers();
            indBuffer = MemoryUtil.memAllocInt(indices.length);
            indBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
            
        } finally {
            if (posBuffer != null) {  MemoryUtil.memFree(posBuffer);  }
            if (colBuffer != null) {  MemoryUtil.memFree(colBuffer);  }
            if (texBuffer != null) {  MemoryUtil.memFree(texBuffer);  }
            if (indBuffer != null) {  MemoryUtil.memFree(indBuffer);  }
        }
    }

    public int 	getVaoId() 			{ return vaoId; 		}
    public int 	getVertexCount()	{ return vertexCount; 	}
    
    public Texture 	getTexture()	{ return texture; 	}
    
    public Vertex[] getVertices()	{ return vertices;		}
    public int[] 	getIndices()	{ return indices;		}
    
    public void minMax() { 
    	if (vertices.length == 0) return;
    	int i = 0;
    	//Initialize values (0 = bad)
    	min_xyz.x = vertices[0].getPosition().x;
    	min_xyz.y = vertices[0].getPosition().y;
    	min_xyz.z = vertices[0].getPosition().z;
    	
    	max_xyz.x = vertices[0].getPosition().x;
    	max_xyz.y = vertices[0].getPosition().y;
    	max_xyz.z = vertices[0].getPosition().z;
    	
    	while (i < vertices.length) {
    	if (vertices[i].getPosition().x < min_xyz.x)  min_xyz.x = vertices[i].getPosition().x;
    	if (vertices[i].getPosition().y < min_xyz.y)  min_xyz.y = vertices[i].getPosition().y;
    	if (vertices[i].getPosition().z < min_xyz.z)  min_xyz.z = vertices[i].getPosition().z;
    	
    	if (vertices[i].getPosition().x > max_xyz.x)  max_xyz.x = vertices[i].getPosition().x;
    	if (vertices[i].getPosition().y > max_xyz.y)  max_xyz.y = vertices[i].getPosition().y;
    	if (vertices[i].getPosition().z > max_xyz.z)  max_xyz.z = vertices[i].getPosition().z;
    	i+=1;
    } }
    
    public void setTexture(Texture texture) {this.texture = texture; }
    
    public void render() {
    	// Activate first texture unit
    	glActiveTexture(GL_TEXTURE0);
    	// Bind the texture
    	if (texture != null) { glBindTexture(GL_TEXTURE_2D, texture.getId()); 
    					} else glDisable(GL_TEXTURE_2D);
    	
    	//no more blurry
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, tex_mag_filter);
    	
        // Draw the mesh
        glBindVertexArray(getVaoId());

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glBindVertexArray(0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(pbo);
        glDeleteBuffers(cbo);
        glDeleteBuffers(ibo);
        glDeleteBuffers(tbo);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}