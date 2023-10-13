package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:
 *
 */
data class ConfValue(

    val pics: List<Pics>, val isEnabled: Boolean, val type: Int, val images: List<String>

) {
    class Pics(
        val type: Int, val img: String, val url: String?, val sort: Int
    )
    // 添加一个成员函数用于提取 img 值
    fun extractImgUrls(): List<String> {
        val imageUrls = mutableListOf<String>()
        pics.forEach { pics ->
            pics.img?.let {
                imageUrls.add(it)
            }
        }
        return imageUrls
    }
}
