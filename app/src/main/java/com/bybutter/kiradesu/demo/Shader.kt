package com.bybutter.kiradesu.demo

import android.opengl.GLES20
import android.util.Log

abstract class Shader(
    private val vertexSource: String,
    private val fragmentSource: String,
) {
    companion object {
        val CUBE = floatArrayOf(
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
        )
        val TEXTURE_COOR = floatArrayOf(
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
        )
    }

    var program = -1

    protected fun createProgram() {
        val success = intArrayOf(0)

        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, vertexSource)
        GLES20.glCompileShader(vertexShader)
        GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, success, 0)
        if (success[0] == 0) {
            val infoLog = GLES20.glGetShaderInfoLog(vertexShader)
            Log.e("SUIKA", "Vertex shader compile fail: $infoLog")
            return
        }

        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, fragmentSource)
        GLES20.glCompileShader(fragmentShader)
        GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, success, 0)
        if (success[0] == 0) {
            val infoLog = GLES20.glGetShaderInfoLog(fragmentShader)
            Log.e("SUIKA", "Fragment shader compile fail: $infoLog")
            return
        }

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, success, 0)
        if (success[0] == 0) {
            val infoLog = GLES20.glGetProgramInfoLog(program)
            Log.e("SUIKA", "Link program fail: $infoLog")
            return
        }

        Log.e("SUIKA", "program compile success")

        this.program = program
    }

    abstract fun onInit()
    abstract fun onDraw()

    fun use() {
        GLES20.glUseProgram(program)
    }
}