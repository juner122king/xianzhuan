package com.lelezu.app.xianzhuan.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import java.io.ByteArrayOutputStream

/**
 * @author:Administrator
 * @date:2023/8/9 0009
 * @description:H5图片上传压缩
 *
 */
object Base64Utils {


    fun zipPic(uri: Uri, quality: Int): ByteArray? {
        return try {
            val bitmap = decodeUriToBitmap(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            bitmap?.recycle()
            val byteArray = byteArrayOutputStream.toByteArray()
            Log.i("zipPic:", "处理前的长度:${byteArray.size},质量:${quality}%")

            // 如果大于500kb，就压缩压缩50%
            if (byteArray.size > 1024 * 500) {
                val compressedByteArray = compressTo2MB(byteArray, quality)
                if (compressedByteArray != null) {
                    Log.i("zipPic:", "大于500kb压缩50%后的长度:${compressedByteArray.size}")

                    compressedByteArray
                } else {
                    null
                }
            } else {
                // 否则，不进行压缩
                Log.i("zipPic:", "小500kb,不进行压缩的长度:${byteArray.size}")

                byteArray
            }
        } catch (e: Exception) {
            null
        }
    }


    private fun decodeUriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun zipPic2(uri: Uri, quality: Int = 100): String? {
        return try {
            val bitmap = decodeUriToBitmap(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            bitmap?.recycle()
            val byteArray = byteArrayOutputStream.toByteArray()
            Log.i("zipPic:", "处理前的长度:${byteArray.size},质量:${quality}%")

            // 如果大于500kb，就压缩压缩50%
            if (byteArray.size > 1024 * 500) {
                val compressedByteArray = compressTo2MB(byteArray, quality)
                if (compressedByteArray != null) {
                    Log.i("zipPic:", "大于500kb压缩50%后的长度:${compressedByteArray.size}")

                    Base64.encodeToString(compressedByteArray, Base64.NO_WRAP)
                } else {
                    null
                }
            } else {
                // 否则，不进行压缩
                Log.i("zipPic:", "小500kb,不进行压缩的长度:${byteArray.size}")
                Base64.encodeToString(byteArray, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            null
        }
    }


    private fun compressTo2MB(inputByteArray: ByteArray, quality: Int): ByteArray? {
        val targetSize = 1024 * 500 // 目标大小为1MB

        var compressedByteArray: ByteArray? = null
        var scaleFactor = 1.0f

        while (scaleFactor >= 0.1f) {
            val scaledBitmap = scaleBitmap(inputByteArray, scaleFactor)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val scaledByteArray = outputStream.toByteArray()
            if (scaledByteArray.size <= targetSize) {
                compressedByteArray = scaledByteArray
                break
            }
            scaleFactor -= 0.1f
        }

        return compressedByteArray
    }

    private fun scaleBitmap(inputByteArray: ByteArray, scaleFactor: Float): Bitmap? {
        val options = BitmapFactory.Options()
        options.inSampleSize = (1 / scaleFactor).toInt()
        return BitmapFactory.decodeByteArray(inputByteArray, 0, inputByteArray.size, options)
    }



}