package com.bybutter.kiradesu

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFalseColorFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import java.nio.FloatBuffer

class GITestFilter : GPUImageFilter(
    """
        attribute vec4 position;
        attribute vec4 inputTextureCoordinate;
        attribute vec4 inputTextureCoordinate2;
         
        varying vec2 textureCoordinate;
        varying vec2 textureCoordinate2;
         
        void main()
        {
            gl_Position = position;
            textureCoordinate = inputTextureCoordinate.xy;
            textureCoordinate2 = inputTextureCoordinate2.xy;
        }
    """.trimIndent(),
    NO_FILTER_FRAGMENT_SHADER,
) {
    private val demo = GPUImageFalseColorFilter()

    override fun onDraw(textureId: Int, cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer)

    }
}