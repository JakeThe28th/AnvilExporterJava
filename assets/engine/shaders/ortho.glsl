fragment {
	#version 330

	in  vec3 exColor;
	out vec4 fragColor;
	in  vec2 outTexCoord;

	uniform sampler2D texture_sampler;

	void main()
	{
	    //fragColor = texture(texture_sampler, outTexCoord);
    	fragColor = texture(texture_sampler, outTexCoord) * vec4(exColor, 1.0);
	}

}

vertex {
	#version 330

	layout (location = 0) in vec3 position;
	layout (location = 1) in vec3 inColor;
	layout (location = 2) in vec2 texCoord;

	out vec3 exColor;

	out vec2 outTexCoord;

	uniform mat4 projModelMatrix;

	void main()
	{
    	gl_Position = projModelMatrix * vec4(position, 1.0);
    	exColor = inColor;
    	outTexCoord = texCoord;
	}

}