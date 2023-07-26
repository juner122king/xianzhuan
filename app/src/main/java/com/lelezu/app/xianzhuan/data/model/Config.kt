package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/26 0026
 * @description:  系统配置信息
 * confDesc	配置说明	string
confKey	配置键	string
confName	配置键	string
confType	配置类型	string
confValue	配置值或内容	object
message	业务说明	string
 *
 */
data class Config(
    val confDesc: String,
    val confKey: String,
    val confName: String,
    val confType: ConfValue,
    val confValue: String

)
