package com.bybutter.kiradesu.demo

import android.opengl.GLES20.*

class TextureShader : Shader(
    """
    attribute vec4 vPosition;
    attribute vec2 inputTexCoordinate;
    
    varying vec2 texCoordinate;
    
    void main(){
        gl_Position = vPosition;
        texCoordinate = inputTexCoordinate;
    }
""".trimIndent(),
    """
    varying vec2 texCoordinate;
     uniform sampler2D inputImageTexture;
     void main(){
        gl_FragColor = texture2D(inputImageTexture * texCoordinate);
     }
""".trimIndent()
) {
    private var vPosition = -1
    private var inputTexCoordinate = -1
    private var inputImageTexture = -1

    override fun onInit() {
        glClearColor(0.375f, 0.8125f, 1f, 1f)
        createProgram()

        vPosition = glGetAttribLocation(program, "vPosition")
        inputTexCoordinate = glGetAttribLocation(program, "inputTexCoordinate")
        inputImageTexture = glGetUniformLocation(program, "inputImageTexture")
    }

    override fun onDraw() {
        glClear(GL_COLOR_BUFFER_BIT)
        use()


    }
}