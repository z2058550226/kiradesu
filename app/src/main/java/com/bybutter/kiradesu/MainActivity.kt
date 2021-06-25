package com.bybutter.kiradesu

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bybutter.kiradesu.util.Camera2Loader
import com.bybutter.kiradesu.util.doOnLayout
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.util.Rotation

class MainActivity : AppCompatActivity() {
    private val gpuImageView by lazy { findViewById<GPUImageView>(R.id.surfaceView) }
    private val cameraLoader by lazy { Camera2Loader(this) }
    private var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var i = 0
        cameraLoader.setOnPreviewFrameListener { data, width, height ->
            i++
            if (i == 100) {
                val list = HighlightHelper.getStarList(width, height, data)
                Log.e("SUIKA", "$list")
            }
            gpuImageView.updatePreviewFrame(data, width, height)
        }
        gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
        gpuImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY)
//        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        val cameraIdList = cameraManager.cameraIdList
//        cameraIdList.forEach {
//            val cameraInfo = Camera.CameraInfo()
//            Camera.getCameraInfo(it.toInt(), cameraInfo)
//            val orientation = cameraInfo.orientation
//            val facingBack = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK
//            Log.e("SUIKA", "orientation: $orientation, cameraId: $it, facingBack: $facingBack")
//        }
        switchFilterTo(GITestFilter())
    }

    override fun onResume() {
        super.onResume()
        gpuImageView.doOnLayout {
            cameraLoader.onResume(it.width, it.height)
        }
    }

    override fun onPause() {
        cameraLoader.onPause()
        super.onPause()
    }

    private fun getRotation(orientation: Int): Rotation {
        return when (orientation) {
            90 -> Rotation.ROTATION_90
            180 -> Rotation.ROTATION_180
            270 -> Rotation.ROTATION_270
            else -> Rotation.NORMAL
        }
    }

    private fun switchFilterTo(filter: GPUImageFilter) {
        if (gpuImageView.filter == null || gpuImageView.filter!!.javaClass != filter.javaClass) {
            gpuImageView.filter = filter
//            filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
//            filterAdjuster?.adjust(seekBar.progress)
        }
    }
}