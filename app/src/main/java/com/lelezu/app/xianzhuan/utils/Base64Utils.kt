package com.lelezu.app.xianzhuan.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import java.io.ByteArrayOutputStream

/**
 * @author:Administrator
 * @date:2023/8/9 0009
 * @description:
 *
 */
object Base64Utils {


    fun zipPic(uri: Uri): String? {
        val bitmap = decodeUriToBitmap(uri)
        val compressedBitmap = compressBitmap(bitmap)
        bitmap?.recycle()
        val byteArrayOutputStream = ByteArrayOutputStream()
        compressedBitmap?.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream)
        compressedBitmap?.recycle()
        val byteArray = byteArrayOutputStream.toByteArray()
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
            "image/jpeg;base64," + Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
        } else if (mimeType != null && mimeType.contains("png")) {
            compressedBitmap?.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream)
            "image/png;base64," + Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
        } else {
            // Unsupported image format
            null
        }
    }

    private fun decodeUriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun compressBitmap(bitmap: Bitmap?): Bitmap? {
        return bitmap?.let {
            val width = it.width
            val height = it.height
            val maxWidth = 300
            val maxHeight = 400
            val scalingFactor =
                (maxWidth.toFloat() / width).coerceAtMost(maxHeight.toFloat() / height)
            val scaledWidth = (width * scalingFactor).toInt()
            val scaledHeight = (height * scalingFactor).toInt()
            Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, true)
        }
    }

}