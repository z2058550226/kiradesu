package com.bybutter.kiradesu.demo

import android.opengl.GLES20.*

class TriangleShader : Shader(
    """
    attribute vec2 position;
    attribute vec3 inputColor;
    varying highp vec3 vColor;
    void main() {
        gl_Position = vec4(position.xy, 0.0, 0.0);
        vColor = inputColor;
    }
""".trimIndent(),
    """
    varying highp vec3 vColor;
    void main(){
        gl_FragColor = vec4(vColor, 1.0);
    }
""".trimIndent()
) {
    companion object {
        private val vertices = floatArrayOf(
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0f, 0.5f
        )
        private val colorArr = floatArrayOf(
            1.0f, 0f, 0f,
            0f, 1.0f, 0f,
            0f, 0f, 1.0f,
        )
    }

    private var position = -1
    private var inputColor = -1
    private val vertexBuffer = vertices.asFloatBuffer()
    private val colorBuffer = colorArr.asFloatBuffer()

    override fun onInit() {
        createProgram()
    }

    override fun onDraw() {
        use()
        position = glGetAttribLocation(program, "position")
        vertexBuffer.position(0)
        glVertexAttribPointer(position, 2, GL_FLOAT, false, 0, vertexBuffer)
        glEnableVertexAttribArray(position)

        inputColor = glGetAttribLocation(program, "inputColor")
        colorBuffer.position(0)
        glVertexAttribPointer(inputColor, 3, GL_FLOAT, false, 0, colorBuffer)
        glEnableVertexAttribArray(inputColor)

        glDrawArrays(GL_TRIANGLES, 0, 3)

        glDisableVertexAttribArray(position)
        glDisableVertexAttribArray(inputColor)
    }
}