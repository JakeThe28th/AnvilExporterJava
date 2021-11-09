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