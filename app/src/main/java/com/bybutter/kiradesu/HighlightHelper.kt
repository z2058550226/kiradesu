package com.bybutter.kiradesu

import java.util.*
import kotlin.math.roundToInt

/**
 * HighlightHelper
 * <p>
 * <img width="640" height="515" src="https://img-blog.csdnimg.cn/20181119235116552.png" alt=""/>
 */
object HighlightHelper {
    private var minHighlightY: Float = 160f
    private var density: Float = 35f
    private var padding: Float = 1f
    private var relativeHighlight: Float = 100f
    private val relativePercents = floatArrayOf(1f, .6f, .2f)

    fun getStarList(
            previewWidth: Int,
            previewHeight: Int,
            bufferData: ByteArray,
            minHeightLightY: Float = this.minHighlightY,
            density: Float = this.density,
            padding: Float = this.padding,
            relativeHighlight: Float = this.relativeHighlight,
            z: Boolean = true,
            z2: Boolean = false,
    ): List<FloatArray> {
        val result: MutableList<FloatArray> = ArrayList()
        val widthF = previewWidth.toFloat()
        val widthStep = (widthF / density).toInt()
        val heightF = previewHeight.toFloat()
        val heightStep = (heightF / density).toInt()
        var rowIndex = 0
        while (rowIndex < previewHeight) {
            var columnIndex = 0
            while (columnIndex < previewWidth) {
                var yNearbySum: UInt = 0.toUInt()
                for (eElement in relativePercents) {
                    val columnIndexF = columnIndex.toFloat()
                    val indexWidth = widthStep.toFloat() * eElement
                    val leftIndex1 = columnIndexF - indexWidth
                    val lx = if (leftIndex1 < 0.0f) 0 else leftIndex1.toInt()

                    var rightIndex1 = columnIndexF + indexWidth
                    val rightBorder = widthF - indexWidth
                    if (rightIndex1 > rightBorder) {
                        rightIndex1 = rightBorder
                    }
                    val rx = rightIndex1.toInt()

                    val rowIndexF = rowIndex.toFloat()
                    val indexHeight = heightStep.toFloat() * eElement
                    val topIndexTmp = rowIndexF - indexHeight
                    val ty = if (topIndexTmp < 0.0f) 0 else topIndexTmp.toInt()
                    var by = rowIndexF + indexHeight
                    val bottomBorder = heightF - indexHeight
                    if (by > bottomBorder) {
                        by = bottomBorder
                    }
                    val tyFirstIndex = ty * previewWidth
                    val byFirstIndex = by.toInt() * previewWidth
                    yNearbySum += (bufferData[tyFirstIndex + lx].toUByte() and UByte.MAX_VALUE) +
                            (bufferData[tyFirstIndex + rx].toUByte() and UByte.MAX_VALUE) +
                            (bufferData[lx + byFirstIndex].toUByte() and UByte.MAX_VALUE) +
                            (bufferData[byFirstIndex + rx].toUByte() and UByte.MAX_VALUE)
                }
                /**
                 *  | Y . . . . . . . . . Y |
                 *  | . . . . . . . . . . . |
                 *  | . . Y . . . . . Y . . |
                 *  | . . . . . . . . . . . |
                 *  | . . . . Y . Y . . . . |
                 *  | . . . . . x . . . . . |
                 *  | . . . . Y . Y . . . . |
                 *  | . . . . . . . . . . . |
                 *  | . . Y . . . . . Y . . |
                 *  | . . . . . . . . . . . |
                 *  | Y . . . . . . . . . Y |
                 *
                 *  The average value of nearby Y values for the point x
                 */
                var yNearby = (yNearbySum.toFloat() / 12.0f).toInt()


                val columnIndexF = columnIndex.toFloat()
                val lxTmp = columnIndexF - padding
                val lx = if (lxTmp < 0.0f) 0 else lxTmp.toInt()
                var rxTmp = columnIndexF + padding
                val rBorder = widthF - padding
                if (rxTmp > rBorder) {
                    rxTmp = rBorder
                }
                val rx = rxTmp.toInt()
                val rowIndexF = rowIndex.toFloat()
                val tyTmp = rowIndexF - padding
                val ty = if (tyTmp < 0.0f) 0 else tyTmp.toInt()
                var by = rowIndexF + padding
                val f25 = heightF - padding
                if (by > f25) {
                    by = f25
                }
                val tyStartIndex = ty * previewWidth
                val byStartIndex = by.toInt() * previewWidth

                /**
                 * | Y . Y |
                 * | . x . |
                 * | Y . Y |
                 */
                val yNearby2 = ((bufferData[tyStartIndex + lx].toUByte() and UByte.MAX_VALUE) +
                        (bufferData[tyStartIndex + rx].toUByte() and UByte.MAX_VALUE) +
                        (bufferData[lx + byStartIndex].toUByte() and UByte.MAX_VALUE) +
                        (bufferData[byStartIndex + rx].toUByte() and UByte.MAX_VALUE) shr 2).toFloat()

                val yHighlight: Float = yNearby.toFloat() + relativeHighlight
                if (yNearby2 > yHighlight && yNearby2 > minHeightLightY) {
                    val vNearby: Int = getVNearby(bufferData, columnIndex, rowIndex, widthStep, heightStep, previewWidth, previewHeight)
                    val uNearby: Int = getUNearby(bufferData, columnIndex, rowIndex, widthStep, heightStep, previewWidth, previewHeight)
                    if (yNearby < 16) {
                        yNearby = 16
                    }
                    val f30 = (yNearby - 16).toFloat() * 1.164f
                    val f31 = (uNearby - 0x80).toFloat()
                    val red = (1.596f * f31 + f30).roundToInt().coerceIn(0..255)
                    val f32 = (vNearby - 0x80).toFloat()
                    val green = (f30 - f31 * 0.813f - 0.391f * f32).roundToInt().coerceIn(0..255)
                    val blue = (f30 + f32 * 2.018f).roundToInt().coerceIn(0..255)
                    // -0x100_0000 = 0xFF00_0000
                    val nearbyAbgrColor = (blue shl 16) - 0x1000000 + (green shl 8) + red
                    if (yNearby2 > yHighlight && yNearby2 > minHeightLightY) {
                        if (!z) {
                            result.add(floatArrayOf(yNearby2, 1.0f - rowIndexF / heightF, columnIndexF / widthF, nearbyAbgrColor.toFloat()))
                        } else if (z2) {
                            result.add(floatArrayOf(yNearby2, rowIndexF / heightF, columnIndexF / widthF, nearbyAbgrColor.toFloat()))
                        } else {
                            result.add(floatArrayOf(yNearby2, 1.0f - rowIndexF / heightF, 1.0f - columnIndexF / widthF, nearbyAbgrColor.toFloat()))
                        }
                    }
                }
                columnIndex += widthStep
            }
            rowIndex += heightStep
        }
        return result
    }

    private fun getVNearby(bufferData: ByteArray, columnIndex: Int, rowIndex: Int, widthStep: Int, heightStep: Int, previewWidth: Int, previewHeight: Int): Int {
        var nearbySum = 0
        for (percent in relativePercents) {
            val columnIndexF = columnIndex.toFloat()
            val indexWidth = widthStep.toFloat() * percent
            val lxTmp = columnIndexF - indexWidth
            val lx = if (lxTmp < 0.0f) 0 else lxTmp.toInt()
            var rxTmp = columnIndexF + indexWidth
            val rightBorder = previewWidth.toFloat() - indexWidth
            if (rxTmp > rightBorder) {
                rxTmp = rightBorder
            }
            val rx = rxTmp.toInt()
            val rowIndexF = rowIndex.toFloat()
            val indexHeight = heightStep.toFloat() * percent
            val tyTmp = rowIndexF - indexHeight
            val ty = if (tyTmp < 0.0f) 0 else tyTmp.toInt()
            var byTmp = rowIndexF + indexHeight
            val bottomBorder = previewHeight.toFloat() - indexHeight
            if (byTmp > bottomBorder) {
                byTmp = bottomBorder
            }
            val area = previewWidth * previewHeight
            val tuvStartIndex = (ty shr 1) * previewWidth + area
            val lxEven = lx and -2
            val rxEven = rx and -2
            val buvStartIndex = area + (byTmp.toInt() shr 1) * previewWidth
            nearbySum += ((bufferData[tuvStartIndex + lxEven].toUByte() and UByte.MAX_VALUE) +
                    (bufferData[tuvStartIndex + rxEven].toUByte() and UByte.MAX_VALUE) +
                    (bufferData[lxEven + buvStartIndex].toUByte() and UByte.MAX_VALUE) +
                    (bufferData[buvStartIndex + rxEven].toUByte() and UByte.MAX_VALUE)).toInt()
        }
        return (nearbySum.toFloat() / 4.0f / relativePercents.size.toFloat()).toInt()
    }

    private fun getUNearby(bufferData: ByteArray, columnIndex: Int, rowIndex: Int, widthStep: Int, heightStep: Int, previewWidth: Int, previewHeight: Int): Int {
        var nearbySum = 0
        for (percent in relativePercents) {
            val columnIndexF = columnIndex.toFloat()
            val indexWidth = widthStep.toFloat() * percent
            val lxTmp = columnIndexF - indexWidth
            val lx = if (lxTmp < 0.0f) 0 else lxTmp.toInt()
            var rxTmp = columnIndexF + indexWidth
            val rightBorder = previewWidth.toFloat() - indexWidth
            if (rxTmp > rightBorder) {
                rxTmp = rightBorder
            }
            val rx = rxTmp.toInt()
            val rowIndexF = rowIndex.toFloat()
            val indexHeight = heightStep.toFloat() * percent
            val tyTmp = rowIndexF - indexHeight
            val ty = if (tyTmp < 0.0f) 0 else tyTmp.toInt()
            var byTmp = rowIndexF + indexHeight
            val bottomBorder = previewHeight.toFloat() - indexHeight
            if (byTmp > bottomBorder) {
                byTmp = bottomBorder
            }

            val area = previewWidth * previewHeight
            val tuvStartIndex = (ty shr 1) * previewWidth + area
            val lxOdd = lx and -2 + 1
            val rxOdd = rx and -2 + 1
            val buvStartIndex = area + (byTmp.toInt() shr 1) * previewWidth
            nearbySum += ((bufferData[tuvStartIndex + lxOdd].toUByte() and UByte.MAX_VALUE) +
                    (bufferData[tuvStartIndex + rxOdd].toUByte() and UByte.MAX_VALUE) +
                    (bufferData[lxOdd + buvStartIndex].toUByte() and UByte.MAX_VALUE) +
                    (bufferData[buvStartIndex + rxOdd].toUByte() and UByte.MAX_VALUE)).toInt()
        }
        return (nearbySum.toFloat() / 4.0f / relativePercents.size.toFloat()).toInt()
    }
}