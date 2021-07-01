package com.bybutter.kiradesu.demo

import android.content.Context
import android.content.res.Resources
import android.opengl.GLES20.*
import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.RawRes
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun FloatArray.asFloatBuffer(): FloatBuffer = ByteBuffer.allocateDirect(this.size * 4)
    .order(ByteOrder.nativeOrder()).asFloatBuffer()
    .apply {
        put(this@asFloatBuffer).position(0)
    }

inline fun vertexAttributeF(
    attribute: Int,
    size: Int,
    normalize: Boolean = false,
    stride: Int = 0,
    vertexBuffer: FloatBuffer,
    block: () -> Unit
) {
    vertexBuffer.position(0)
    glVertexAttribPointer(attribute, size, GL_FLOAT, normalize, stride, vertexBuffer)
    glEnableVertexAttribArray(attribute)
    block()
    glDisableVertexAttribArray(attribute)
}

fun Context.createProgram(@RawRes vsSourceId: Int, @RawRes fragSourceId: Int): Int {
    val vs = resources.openRawResource(vsSourceId).bufferedReader(Charsets.UTF_8).readText()
    val frag = resources.openRawResource(fragSourceId).bufferedReader(Charsets.UTF_8).readText()
    return createProgram(vs, frag)
}

fun createProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
    val success = intArrayOf(0)

    val vertexShader = glCreateShader(GL_VERTEX_SHADER)
    glShaderSource(vertexShader, vertexShaderSource)
    glCompileShader(vertexShader)
    glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success, 0)
    if (success[0] == 0) {
        val infoLog = glGetShaderInfoLog(vertexShader)
        Log.e("SUIKA", "Vertex shader compile fail: $infoLog")
        return -1
    }

    val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(fragmentShader, fragmentShaderSource)
    glCompileShader(fragmentShader)
    glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, success, 0)
    if (success[0] == 0) {
        val infoLog = glGetShaderInfoLog(fragmentShader)
        Log.e("SUIKA", "Fragment shader compile fail: $infoLog")
        return -1
    }

    val program = glCreateProgram()
    glAttachShader(program, vertexShader)
    glAttachShader(program, fragmentShader)
    glLinkProgram(program)
    glDeleteShader(vertexShader)
    glDeleteShader(fragmentShader)
    glGetProgramiv(program, GL_LINK_STATUS, success, 0)
    if (success[0] == 0) {
        val infoLog = glGetProgramInfoLog(program)
        Log.e("SUIKA", "Link program fail: $infoLog")
        return -1
    }

    return program
}