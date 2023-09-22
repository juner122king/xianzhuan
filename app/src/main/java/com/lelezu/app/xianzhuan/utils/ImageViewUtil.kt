package com.lelezu.app.xianzhuan.utils

import android.widget.ImageView
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.lelezu.app.xianzhuan.MyApplication.Companion.context
import com.lelezu.app.xianzhuan.R

/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description:
 *
 */
object ImageViewUtil {

    fun load(imageView: ImageView, any: Any?) {
        val roundedCorners = context.resources.getDimensionPixelSize(R.dimen.im_rounded).toFloat()
        if (any != null) {
            imageView.load(any) {
                crossfade(true)
                transformations(
                    RoundedCornersTransformation(
                        roundedCorners, roundedCorners, roundedCorners, roundedCorners
                    )
                )
            }
        }
    }

    fun loadWH(imageView: ImageView, any: Any) {

        imageView.load(any) {
            crossfade(true)
            scale(Scale.FILL)
        }

    }

    fun loadCircleCrop(imageView: ImageView, any: Any?) {
        if (any != null) {
            imageView.load(any) {
                crossfade(true)
                //placeholder(R.drawable.placeholder) 设置占位图
                transformations(CircleCropTransformation())//圆形切图
                scale(Scale.FILL)
            }
        }
    }



}