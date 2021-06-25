package com.bybutter.kiradesu.demo

import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import jp.co.cyberagent.android.gpuimage.GPUImageRenderer
import jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class TestShader : Shader(
    """
        attribute vec3 position;
        attribute vec4 inputTextureCoordinate;
        
        varying highp vec2 textureCoordinate;
        
        void main() {
            gl_Position = vec4(position.xyz, 1.0);
            textureCoordinate = inputTextureCoordinate.xy;
        }
    """.trimIndent(),
    """
        varying highp vec2 textureCoordinate;
        
        uniform sampler2D inputImageTexture;
        
        void main(){
            gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
        }
    """.trimIndent()
) {
    private val glCubeBuffer: FloatBuffer = ByteBuffer.allocateDirect(GPUImageRenderer.CUBE.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer().apply {
            put(CUBE).position(0)
        }
    private val glTextureBuffer: FloatBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer().apply {
            put(TEXTURE_COOR).position(0)
        }

    private var position = -1
    private var inputTextureCoordinate = -1
    private var inputImageTexture = -1

    override fun onInit() {
        createProgram()
        if (program == -1) return
        position = glGetAttribLocation(program, "position")
        inputTextureCoordinate = glGetAttribLocation(program, "inputTextureCoordinate")
        inputImageTexture = glGetUniformLocation(program, "inputImageTexture")
    }

    override fun onDraw() {

    }
}