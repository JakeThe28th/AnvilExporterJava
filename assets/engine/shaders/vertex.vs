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
	