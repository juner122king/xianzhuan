package com.lelezu.app.xianzhuan.utils

import android.view.ViewTreeObserver
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
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
    /**
     * 固定宽高，自动裁剪
     * */
    fun load(imageView: ImageView, any: Any?) {

        // 添加 ViewTreeObserver，确保在布局完成后获取宽度和高度
        imageView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // 移除监听器，避免重复调用
                imageView.viewTreeObserver.removeOnPreDrawListener(this)

                // 获取 ImageView 的宽度和高度
                val width = imageView.width
                val height = imageView.height

                // 使用获取到的宽度和高度替代 h 和 w 变量的值
                val url = "$any?x-oss-process=image/resize,m_fill,h_$height,w_$width"

                val roundedCorners =
                    context.resources.getDimensionPixelSize(R.dimen.im_rounded).toFloat()
                imageView.load(url) {
                    crossfade(true)
                    transformations(
                        RoundedCornersTransformation(
                            roundedCorners, roundedCorners, roundedCorners, roundedCorners
                        )
                    )
                    scale(Scale.FIT)
                }

                return true
            }
        })


    }

    //任务详情里的全屏图加载
    fun loadFall(imageView: ImageView, any: Any) {
        imageView.load(any) {
            crossfade(true)//淡出淡入
        }

    }


    fun loadWH(imageView: ImageView, any: Any) {

        imageView.load(any) {
            crossfade(true)
            scale(Scale.FILL)
        }
    }

    /**
     *
     * @param imageView ImageView
     * @param any Any
     *
     * 缩放策略参考：https://help.aliyun.com/zh/oss/user-guide/resize-images-4?spm=a2c4g.11186623.0.0.4c226bd7XIYECn
     */
    fun loadCircleCrop(imageView: ImageView, any: Any?) {

        // 添加 ViewTreeObserver，确保在布局完成后获取宽度和高度
        imageView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // 移除监听器，避免重复调用
                imageView.viewTreeObserver.removeOnPreDrawListener(this)

                // 获取 ImageView 的宽度和高度
                val width = imageView.width
                val height = imageView.height

                LogUtils.i("图片加载-头像", "ImageView 的宽度：$width，高度：$height")

                // 使用获取到的宽度和高度替代 h 和 w 变量的值
                val url = "$any?x-oss-process=image/resize,m_fixed,h_$height,w_$width"

                val imageLoader =
                    ImageLoader.Builder(imageView.context).memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED).build()

                imageView.load(url, imageLoader = imageLoader) {
                    crossfade(true)
                    //placeholder(R.drawable.placeholder) 设置占位图
                    transformations(CircleCropTransformation()) // 圆形切图
                    scale(Scale.FILL)
                }
                return true
            }
        })
    }


}