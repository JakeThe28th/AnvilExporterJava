fragment {
	#version 330

	in  vec4 exColor;
	out vec4 fragColor;
	in  vec2 outTexCoord;
	in  float outBrightness;

	uniform sampler2D texture_sampler;

	void main()
	{
	    //fragColor = texture(texture_sampler, outTexCoord);
    	fragColor = texture(texture_sampler, outTexCoord) * exColor;
    	fragColor = mix(fragColor, exColor, outBrightness);
	}

}

vertex {
	#version 330

	layout (location = 0) in vec3 position;
	layout (location = 1) in vec4 inColor;
	layout (location = 2) in vec2 texCoord;
	layout (location = 3) in float inBrightness;

	out vec4 exColor;

	out vec2 outTexCoord;
	out float outBrightness;

	uniform mat4 projModelMatrix;

	void main()
	{
    	gl_Position = projModelMatrix * vec4(position, 1.0);
    	exColor = inColor;
    	outTexCoord = texCoord;
    	outBrightness = inBrightness;
	}

}
type {orthographic}