package com.bybutter.kiradesu.demo

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary
import jp.co.cyberagent.android.gpuimage.util.OpenGlUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.coroutines.CoroutineContext

class DemoRender(private val glSurfaceView: GLSurfaceView) : GLSurfaceView.Renderer, CoroutineScope {
    private val handlerThread = HandlerThread("suika").apply { start() }
    private val handler = Handler(handlerThread.looper)
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + handler.asCoroutineDispatcher("gl_dispatcher")

    private var glRgbBuffer: IntBuffer? = null
    private var glTextureId = OpenGlUtils.NO_TEXTURE
    private var imageWidth = 0
    private var imageHeight = 0

    private val shader = TextureShader()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        shader.onInit()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.e("SUIKA", "set view port width: $width, height: $height")
        glViewport(0, 0, width, height)
    }

    private var time = 0

    override fun onDrawFrame(gl: GL10?) {
        shader.onDraw()
//        glSurfaceView.requestRender()
    }

    fun release() {
    }

    fun updateFrame(data: ByteArray, width: Int, height: Int) = launch {
        val rgbBuffer = glRgbBuffer ?: IntBuffer.allocate(width * height).also {
            glRgbBuffer = it
        }
        GPUImageNativeLibrary.YUVtoRBGA(data, width, height, rgbBuffer!!.array())
        glTextureId = OpenGlUtils.loadTexture(glRgbBuffer, width, height, glTextureId)

        if (imageWidth != width) {
            imageWidth = width
            imageHeight = height
            // adjustImageScaling()
        }

        glSurfaceView.requestRender()
//        Log.e("SUIKA", "request render $time")
    }
}