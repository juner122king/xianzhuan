package com.lelezu.app.xianzhuan.ui

import com.lelezu.app.xianzhuan.R

/**
 * @author:Administrator
 * @date:2023/12/28 0028
 * @description:
 *
 */
object StringUtils {

    val statusMap = mapOf(
        //key对应Task的auditStatus
        0 to "未报名",
        1 to "待提交",
        2 to "审核中",
        3 to "审核通过",
        4 to "审核被否",
        5 to "手动取消",
        6 to "超时取消",
        7 to "长单任务进行中",
        // 可以继续添加其他映射关系
    )
     val statusTimeMap = mapOf(
        0 to "未报名",
        1 to "剩余时间：",
        2 to "提交时间：",
        3 to "通过时间：",
        4 to "审核时间：",
        5 to "手动取消",
        6 to "超时取消",
        7 to "需在",
        // 可以继续添加其他映射关系
    )


    val colorMap = mapOf(
        0 to R.color.colorControlActivated,
        1 to R.color.colorControlActivated1,
        2 to R.color.colorControlActivated,
        3 to R.color.pass,
        4 to R.color.colorControlActivated,
        5 to R.color.colorControlActivated,
        6 to R.color.colorControlActivated,
        7 to R.color.colorControlActivated1,

    )
}
