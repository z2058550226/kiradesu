package com.bybutter.kiradesu.demo

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bybutter.kiradesu.util.Camera2Loader
import com.bybutter.kiradesu.util.doOnLayout

class DemoActivity : AppCompatActivity() {
    private val glSurfaceView by lazy { GLSurfaceView(this) }

    private val cameraLoader by lazy { Camera2Loader(this) }
    private val demoRender by lazy { DemoRender(glSurfaceView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(glSurfaceView)

        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(demoRender)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        cameraLoader.setOnPreviewFrameListener { data, width, height ->
            demoRender.updateFrame(data, width, height)
        }
        glSurfaceView.requestRender()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.doOnLayout {
            cameraLoader.onResume(it.width, it.height)
        }
    }

    override fun onDestroy() {
        demoRender.release()
        super.onDestroy()
    }
}