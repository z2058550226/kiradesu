package com.bybutter.kiradesu

import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.Log
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.util.OpenGlUtils
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class GIStarFilterOri : GPUImageFilter(
        """
            attribute vec4 position;
            attribute vec4 inputTextureCoordinate;
            varying vec2 textureCoordinate;
            
            void main() {    
                gl_Position = vec4(position.xyz, 1.0);    textureCoordinate = inputTextureCoordinate.xy;
            }
        """.trimIndent(),
        """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            uniform lowp float mixCOEF;
            uniform lowp float isOrigin;
            uniform lowp float isRenderColor;
            uniform lowp vec4 colorRGBTexture;
            
            void main() {
                highp vec4 abc = texture2D(inputImageTexture, textureCoordinate);
                if(isOrigin > 0.5){
                    gl_FragColor = abc;
                }else{
                    gl_FragColor = vec4(abc.rgb,abc.a*mixCOEF);
                }
            }
        """.trimIndent()
) {
    var a = floatArrayOf(-1.0f, -1.0f, 0.0f, 1.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f)
    var nearbyAbgrColorArr: IntArray? = null
    var c = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    var bitmapTexture2 = 0
    var bitmapTexture3 = 0
    var bitmapTexture4 = 0
    var bitmapTexture5 = 0
    var bitmapTexture6 = 0
    private var starCount = 0
    private var mixCOEF = 0
    private var colorRGBTexture = 0
    private var isOrigin = 0
    private var isRenderColor = 0
    var mixCOEFValue = 1.0f
        set(value) {
            field = value
            setMixCOEFValue()
        }
    private var isOriginValue = 1.0f
    private var p = false
    private var q = floatArrayOf(0.8f, 0.8f, 0.0f, 1.0f)

    override fun onInit() {
        super.onInit()
        mixCOEF = GLES20.glGetUniformLocation(program, "mixCOEF")
        colorRGBTexture = GLES20.glGetUniformLocation(program, "colorRGBTexture")
        isOrigin = GLES20.glGetUniformLocation(program, "isOrigin")
        isRenderColor = GLES20.glGetUniformLocation(program, "isRenderColor")
        setMixCOEFValue()
        setIsOriginValue()
    }

    fun draw(textureId: Int, glCubeBuffer: FloatBuffer, glTextureBuffer: FloatBuffer) {
        GLES20.glUseProgram(program)
        runPendingOnDrawTasks()
        val glGetError = GLES20.glGetError()
        if (glGetError != 0) {
            Log.e("GLES", "1 glGetError: 0x" + Integer.toHexString(glGetError))
        }
        if (isInitialized) {
            GLES20.glUniform1f(mixCOEF, 1.0f)
            GLES20.glUniform1f(isOrigin, 1.0f)
            if (textureId != -1) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
                GLES20.glUniform1i(uniformTexture, 0)
            }
            val glGetError2 = GLES20.glGetError()
            if (glGetError2 != 0) {
                Log.e("GLES", "2 glGetError: 0x" + Integer.toHexString(glGetError2))
            }
            glCubeBuffer.position(0)
            GLES20.glVertexAttribPointer(attribPosition, 2, GLES20.GL_FLOAT, false, 0, glCubeBuffer)
            GLES20.glEnableVertexAttribArray(attribPosition)
            glTextureBuffer.position(0)
            GLES20.glVertexAttribPointer(attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, glTextureBuffer)
            GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
            val glGetError3 = GLES20.glGetError()
            if (glGetError3 != 0) {
                Log.e("GLES", "3 glGetError: 0x" + Integer.toHexString(glGetError3))
            }
            GLES20.glDrawArrays(5, 0, 4)
            GLES20.glDisableVertexAttribArray(attribPosition)
            GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
            if (starCount > 0) {
                GLES20.glEnable(GLES20.GL_BLEND)
                GLES20.glBlendFunc(1, 771)
                GLES20.glUniform1f(mixCOEF, mixCOEFValue)
                GLES20.glUniform1f(isOrigin, 0.0f)
                synchronized(this) {
                    val asFloatBuffer = ByteBuffer.allocateDirect(a.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                    asFloatBuffer.put(a)
                    val asFloatBuffer2 = ByteBuffer.allocateDirect(c.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                    asFloatBuffer2.put(c)
                    var i3 = 0
                    while (true) {
                        if (i3 >= starCount) {
                            break
                        }
                        a(nearbyAbgrColorArr!![i3])
                        asFloatBuffer.position(a.size / starCount * i3 + 0)
                        GLES20.glVertexAttribPointer(attribPosition, 4, GLES20.GL_FLOAT, false, 0, asFloatBuffer as Buffer)
                        GLES20.glEnableVertexAttribArray(attribPosition)
                        asFloatBuffer2.position(0)
                        GLES20.glVertexAttribPointer(attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, asFloatBuffer2 as Buffer)
                        GLES20.glEnableVertexAttribArray(attribTextureCoordinate)
                        val currentTimeMillis = System.currentTimeMillis()
                        GLES20.glDrawArrays(5, 0, 4)
                        if (System.currentTimeMillis() - currentTimeMillis > 3) {
                            break
                        }
                        i3++
                    }
                }
                GLES20.glDisableVertexAttribArray(attribPosition)
                GLES20.glDisableVertexAttribArray(attribTextureCoordinate)
                GLES20.glDisable(GLES20.GL_BLEND)
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }

    fun a(i2: Int) {
        val i3 = 0xFF0000 and i2 shr 16
        val i4 = 0xFF00 and i2 shr 8
        val i5 = i2 and 255
        if (p) {
            GLES20.glUniform1f(isRenderColor, 1.0f)
            GLES20.glActiveTexture(33986)
            GLES20.glBindTexture(3553, bitmapTexture2)
            GLES20.glUniform1i(uniformTexture, 2)
        } else {
            GLES20.glUniform1f(isRenderColor, 0.0f)
        }
        if (i3 <= 170 || i4 <= 170 || i5 >= 100) {
            val i6 = i4 + 10
            if (i3 <= i6 || i3 <= i5 + 10 || i4 <= 30) {
                val i7 = i3 + 10
                if (i4 <= i7 || i4 <= i5 + 10 || i3 <= 30) {
                    if (i5 <= i7 || i5 <= i6 || i3 >= 100) {
                        if (p) {
                            GLES20.glUniform4fv(colorRGBTexture, 1, floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f), 0)
                            return
                        }
                        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapTexture2)
                        GLES20.glUniform1i(uniformTexture, 2)
                    } else if (p) {
                        GLES20.glUniform4fv(colorRGBTexture, 1, floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f), 0)
                    } else {
                        GLES20.glActiveTexture(GLES20.GL_TEXTURE6)
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapTexture6)
                        GLES20.glUniform1i(uniformTexture, 6)
                    }
                } else if (p) {
                    GLES20.glUniform4fv(colorRGBTexture, 1, floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f), 0)
                } else {
                    GLES20.glActiveTexture(33989)
                    GLES20.glBindTexture(3553, bitmapTexture5)
                    GLES20.glUniform1i(uniformTexture, 5)
                }
            } else if (p) {
                GLES20.glUniform4fv(colorRGBTexture, 1, floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f), 0)
            } else {
                GLES20.glActiveTexture(33988)
                GLES20.glBindTexture(3553, bitmapTexture4)
                GLES20.glUniform1i(uniformTexture, 4)
            }
        } else if (p) {
            GLES20.glUniform4fv(colorRGBTexture, 1, floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f), 0)
        } else {
            GLES20.glActiveTexture(33987)
            GLES20.glBindTexture(3553, bitmapTexture3)
            GLES20.glUniform1i(uniformTexture, 3)
        }
    }

    @Synchronized
    fun setStarData(starDataList: List<FloatArray>, radiusFactor: Float, stickerRatio: Float, previewHeightF: Float, previewWidthF: Float, rotateAngle: Int) {
        starCount = starDataList.size
        nearbyAbgrColorArr = IntArray(starCount)
        val result = FloatArray(starDataList.size * 16)
        for ((index, starData) in starDataList.withIndex()) {
            val yNearby = starData[0]
            val heightPercent = starData[1]
            val widthPercent = starData[2]
            nearbyAbgrColorArr!![index] = starData[3].toInt()
            a(heightPercent, widthPercent, yNearby, radiusFactor, stickerRatio, previewHeightF, previewWidthF, result, index, starDataList.size, rotateAngle)
        }
        a = result
    }

    fun a(
        heightPercent: Float,
        widthPercent: Float,
        yNearby: Float,
        radiusFactor: Float,
        stickerRatio: Float,
        previewHeightF: Float,
        previewWidthF: Float,
        resultArr: FloatArray,
        index: Int,
        starCount: Int,
        rotateAngle: Int
    ) {
        val y = (heightPercent * previewHeightF).toInt()
        val x = (widthPercent * previewWidthF).toInt()
        val diameter = ((radiusFactor * previewHeightF).toInt().toFloat() * ((yNearby - 200.0f) / 55.0f)).toInt().toFloat()
        val yF = y.toFloat()
        val radius = diameter * 0.5f
        val top = (yF - radius).toInt()
        val xF = x.toFloat()
        val radiusHeight = (diameter * stickerRatio).toInt().toFloat() * 0.5f
        val left = (xF - radiusHeight).toInt()
        val tlRotatePoint = rotateCoordinate(y, x, top, left, rotateAngle)
        val tlRotateY = tlRotatePoint[0]
        val tlRotateX = tlRotatePoint[1]
        val bottom = (yF + radius).toInt()
        val blRotatePoint = rotateCoordinate(y, x, bottom, left, rotateAngle)
        val blRotateY = blRotatePoint[0]
        val blRotateX = blRotatePoint[1]
        val right = (xF + radiusHeight).toInt()
        val trRotatePoint = rotateCoordinate(y, x, top, right, rotateAngle)
        val trRotateY = trRotatePoint[0]
        val trRotateX = trRotatePoint[1]
        val brRotatePoint = rotateCoordinate(y, x, bottom, right, rotateAngle)
        val brRotateY = brRotatePoint[0]
        val brRotateX = brRotatePoint[1]
        val startIndex = index * 16
        resultArr[startIndex] = trRotateY.toFloat() / previewHeightF
        resultArr[startIndex + 1] = trRotateX.toFloat() / previewWidthF
        resultArr[startIndex + 2] = 0.0f
        resultArr[startIndex + 3] = 1.0f
        resultArr[startIndex + 4] = brRotateY.toFloat() / previewHeightF
        resultArr[startIndex + 5] = brRotateX.toFloat() / previewWidthF
        resultArr[startIndex + 6] = 0.0f
        resultArr[startIndex + 7] = 1.0f
        resultArr[startIndex + 8] = tlRotateY.toFloat() / previewHeightF
        resultArr[startIndex + 9] = tlRotateX.toFloat() / previewWidthF
        resultArr[startIndex + 10] = 0.0f
        resultArr[startIndex + 11] = 1.0f
        resultArr[startIndex + 12] = blRotateY.toFloat() / previewHeightF
        resultArr[startIndex + 13] = blRotateX.toFloat() / previewWidthF
        resultArr[startIndex + 14] = 0.0f
        resultArr[startIndex + 15] = 1.0f
        a(resultArr, resultArr, index, starCount)
    }

    private fun rotateCoordinate(y: Int, x: Int, ry: Int, rx: Int, rotateAngle: Int): IntArray {
        val rotateRadian: Double = (rotateAngle * PI / 180f)
        val cos = cos(rotateRadian)
        val sin = sin(rotateRadian)
        val rh = (ry - y).toDouble()
        val rw = (rx - x).toDouble()
        return intArrayOf((cos * rh - sin * rw + y).toInt(), (rh * sin + rw * cos + x).toInt())
    }

    fun a(fArr: FloatArray, fArr2: FloatArray, index: Int, i3: Int): FloatArray? {
        // 16
        val length = fArr.size / i3
        var subIndex = index * length
        while (subIndex < (index + 1) * length) {
            if (subIndex % 2 != 0) {
                if (fArr[subIndex] >= 0.5f) {
                    fArr2[subIndex] = -(fArr[subIndex] - 0.5f) / 0.5f
                } else {
                    fArr2[subIndex] = (0.5f - fArr[subIndex]) / 0.5f
                }
                subIndex += 2
            } else if (fArr[subIndex] >= 0.5f) {
                fArr2[subIndex] = (fArr[subIndex] - 0.5f) / 0.5f
            } else {
                fArr2[subIndex] = -(0.5f - fArr[subIndex]) / 0.5f
            }
            subIndex++
        }
        return fArr2
    }

    private fun setIsOriginValue() {
        setFloat(isOrigin, isOriginValue)
    }

    private fun setMixCOEFValue() {
        setFloat(mixCOEF, mixCOEFValue)
    }

    fun a(bitmap: Bitmap?) {
        runOnDraw(Runnable {
            if (bitmapTexture2 == -1) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
                val bitmap = bitmap
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmapTexture2 = OpenGlUtils.loadTexture(bitmap, -1, false)
                    return@Runnable
                }
                return@Runnable
            }
            if (bitmap != null && !bitmap.isRecycled) {
                GLES20.glDeleteTextures(1, intArrayOf(bitmapTexture2), 0)
                bitmapTexture2 = OpenGlUtils.loadTexture(bitmap, -1, false)
            }
        })
    }

    fun b(bitmap: Bitmap?) {
        runOnDraw(Runnable {
            if (bitmapTexture3 == -1) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
                val bitmap = bitmap
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmapTexture3 = OpenGlUtils.loadTexture(bitmap, -1, false)
                    return@Runnable
                }
                return@Runnable
            }
            if (bitmap != null && !bitmap.isRecycled) {
                GLES20.glDeleteTextures(1, intArrayOf(bitmapTexture3), 0)
                bitmapTexture3 = OpenGlUtils.loadTexture(bitmap, -1, false)
            }
        })
    }

    fun c(bitmap: Bitmap?) {
        runOnDraw(Runnable {
            if (bitmapTexture4 == -1) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE4)
                val bitmap = bitmap
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmapTexture4 = OpenGlUtils.loadTexture(bitmap, -1, false)
                    return@Runnable
                }
                return@Runnable
            }
            if (bitmap != null && !bitmap.isRecycled) {
                GLES20.glDeleteTextures(1, intArrayOf(bitmapTexture4), 0)
                bitmapTexture4 = OpenGlUtils.loadTexture(bitmap, -1, false)
            }
        })
    }

    fun d(bitmap: Bitmap?) {
        runOnDraw(Runnable {
            if (bitmapTexture5 == -1) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE5)
                val bitmap = bitmap
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmapTexture5 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false)
                    return@Runnable
                }
                return@Runnable
            }
            if (bitmap != null && !bitmap.isRecycled) {
                GLES20.glDeleteTextures(1, intArrayOf(bitmapTexture5), 0)
                bitmapTexture5 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false)
            }
        })
    }

    fun e(bitmap: Bitmap?) {
        runOnDraw(Runnable {
            if (bitmapTexture6 == -1) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE6)
                val bitmap = bitmap
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmapTexture6 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false)
                    return@Runnable
                }
                return@Runnable
            }
            if (bitmap != null && !bitmap.isRecycled) {
                GLES20.glDeleteTextures(1, intArrayOf(bitmapTexture6), 0)
                bitmapTexture6 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false)
            }
        })
    }
}