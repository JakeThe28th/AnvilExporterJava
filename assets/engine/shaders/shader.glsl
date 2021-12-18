vertex

	#version 330

	layout (location = 0) in vec3 position;
	layout (location = 1) in vec3 inColor;
	layout (location = 2) in vec2 texCoord;

	out vec3 exColor;
	uniform mat4 modelViewMatrix;
	uniform mat4 projectionMatrix;

	out vec2 outTexCoord;

	void main() {

 	 gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
	exColor = inColor;
	outTexCoord = texCoord;
	} 
	
end vertex

fragment

	#version 330

	in  vec3 exColor;
	out vec4 fragColor;
	in  vec2 outTexCoord;

	uniform sampler2D texture_sampler;

	void main()
	{
		//fragColor = vec4(exColor, 1.0);
		fragColor = texture(texture_sampler, outTexCoord) * vec4(exColor, 1.0);
	}
 
 end fragment