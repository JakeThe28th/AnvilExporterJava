vertex {

	#version 330

	layout (location = 0) in vec3 position;
	layout (location = 1) in vec4 inColor;
	layout (location = 2) in vec2 texCoord;

	out vec4 exColor;
	uniform mat4 modelViewMatrix;
	uniform mat4 projectionMatrix;

	out vec2 outTexCoord;

	void main() {

 	 gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
	exColor = inColor;
	outTexCoord = texCoord;
	} 
	
}
fragment {

	#version 330

	in  vec4 exColor;
	out vec4 fragColor;
	in  vec2 outTexCoord;

	uniform sampler2D texture_sampler;

	void main()
	{
		fragColor = texture(texture_sampler, outTexCoord) * exColor;
	}
 
}
type {
perspective
}