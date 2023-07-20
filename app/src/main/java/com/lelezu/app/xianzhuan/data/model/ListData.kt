package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description: 列表类型数据模型
 *
 */
data class ListData(
    val current: Int,
    val pages: Int,
    val records: List<Task>, // records 字段可以是任意类型的列表
    val size: Int,
    val total: Int
)