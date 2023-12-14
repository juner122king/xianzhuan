package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description: 首页Banner
 *   "bannerId": "1",
 *             "bannerImg": "http://cdn.zhongshoubang.com/test/dxz/admin/af5efbac089346f79acf5f2d77d0d06e.png",
 *             "bannerName": "合伙人",
 *             "jumpUrl": "/statics/dxz/h5/pages/apprentice/apprenticeRule/index",
 *             "type": 0,
 *             "isShow": true,
 *             "sortSeq": 1
 *
 *
 */

data class DBanner(
    val bannerId: String,
    val bannerImg: String,
    val bannerName: String,
    val jumpUrl: String,
    val type: Int,
    val isShow: String,
    val sortSeq: Int,
)