package com.bybutter.kiradesu.demo

import android.opengl.GLES20.*

class TriangleShader : Shader(
    """
    attribute vec3 vPosition;
    attribute vec3 colorInput;
    varying vec4 vColor;
    void main() {
        gl_Position = vec4(vPosition.xyz, 1f);
        vColor = vec4(colorInput, 1f);
    }
""".trimIndent(),
    """
    precision mediump float;
    varying vec4 vColor;
    void main() {
        gl_FragColor = vColor;
    }
""".trimIndent()
) {
    companion object {
        private val vertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0f, 0.5f, 0.0f
        )
        private val colors = floatArrayOf(
            1.0f, .3f, .8f,
            .3f, .8f, 1f,
            .8f, 1f, .3f
        )
    }

    private val vertexBuffer = vertices.asFloatBuffer()
    private val inputColorBuffer = colors.asFloatBuffer()

    override fun onInit() {
        glClearColor(0.375f, 0.8125f, 1f, 1f)
        createProgram()
    }

    override fun onDraw() {
        glClear(GL_COLOR_BUFFER_BIT)
        use()
        val position = glGetAttribLocation(program, "vPosition")
        glVertexAttribPointer(position, 3, GL_FLOAT, false, 0, vertexBuffer)
        glEnableVertexAttribArray(position)

        val inputColor = glGetAttribLocation(program, "colorInput")
        glVertexAttribPointer(inputColor, 3, GL_FLOAT, false, 0, inputColorBuffer)
        glEnableVertexAttribArray(inputColor)

        glDrawArrays(GL_TRIANGLES, 0, 3)

        glDisableVertexAttribArray(position)
        glDisableVertexAttribArray(inputColor)
    }
}