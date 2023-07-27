package com.lelezu.app.xianzhuan.utils

import android.widget.ImageView
import coil.load
import coil.transform.RoundedCornersTransformation

/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description:
 *
 */
object ImageViewUtil {

    fun load(imageView: ImageView,any: Any) {
        imageView.load(any) {
            crossfade(true)
            transformations(RoundedCornersTransformation(10f, 10f, 10f, 10f))
        }
    }
}