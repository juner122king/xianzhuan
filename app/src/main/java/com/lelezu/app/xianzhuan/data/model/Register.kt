package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:注册请求体
 *
 * @property deviceToken String 前端SDK获取的设备token
 * @property mobileAccessToken String  	前端SDK获取的accessToken
 * @property mobileToken String 	前端SDK获取的token
 * @property recommendUserId String? 	推荐用户ID
 * @property userId String 	用户ID
 * @property sim_num String?
 * @property imsi_num String?
 * @property mac String?
 * @property android_id String?
 * @property oAid String? 加密字段
 * @property channel String? channel
 * @constructor
 */
data class Register(

    var deviceToken: String,
    var mobileAccessToken: String,
    var mobileToken: String,
    var recommendUserId: String="",
    var userId: String,
    var simNum: String="",
    var imsiNum: String="",
    var mac: String="",
    var androidId: String="",
    var oAid: String="",
    var channel: String

)
