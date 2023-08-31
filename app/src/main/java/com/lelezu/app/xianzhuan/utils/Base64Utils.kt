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


    fun zipPic(uri: Uri): String? {
        val bitmap = decodeUriToBitmap(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        bitmap?.recycle()
        val byteArray = byteArrayOutputStream.toByteArray()
        Log.i("H5调原生:", "byteArray长度:${byteArray.size}")
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }


    fun zipPic2(uri: Uri): String? {
        val mimeType = context?.contentResolver?.getType(uri)
        val bitmap = decodeUriToBitmap(uri)
        val compressedBitmap = compressBitmap(bitmap)
        bitmap?.recycle()
        val byteArrayOutputStream = ByteArrayOutputStream()

        return if (mimeType != null && mimeType.contains("jpeg")) {
            compressedBitmap?.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
            "image/jpeg;base64," + Base64.encodeToString(
                byteArrayOutputStream.toByteArray(), Base64.NO_WRAP
            )
        } else if (mimeType != null && mimeType.contains("png")) {
            compressedBitmap?.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream)
            "image/png;base64," + Base64.encodeToString(
                byteArrayOutputStream.toByteArray(), Base64.NO_WRAP
            )
        } else {
            // Unsupported image format
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

    fun compressBitmap(bitmap: Bitmap?): Bitmap? {
        return bitmap?.let {
            val width = it.width
            val height = it.height
            val maxWidth = 600
            val maxHeight = 800
            val scalingFactor =
                (maxWidth.toFloat() / width).coerceAtMost(maxHeight.toFloat() / height)
            val scaledWidth = (width * scalingFactor).toInt()
            val scaledHeight = (height * scalingFactor).toInt()
            Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, true)
        }
    }

    fun encodeToString(originalData: String): String {

        return Base64.encodeToString(originalData.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
    }

}