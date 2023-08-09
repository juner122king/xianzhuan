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
        compressedBitmap?.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
        compressedBitmap?.recycle()
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
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
            val maxWidth = 600
            val maxHeight = 800
            val scalingFactor =
                (maxWidth.toFloat() / width).coerceAtMost(maxHeight.toFloat() / height)
            val scaledWidth = (width * scalingFactor).toInt()
            val scaledHeight = (height * scalingFactor).toInt()
            Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, true)
        }
    }

}