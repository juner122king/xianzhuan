package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/7/28 0028
 * @description:注册请求体
 *
 * @property encryptStr String 	加密串,示例值({"deviceToken":"xxx", "userId":"xxx", "mobileToken":"xxx", "mobileAccessToken":"xxx", "recommendUserId":"xxx", "simNum":"xxx", "imsiNum":"xxx", "mac":"xxx", "androidId":"xxx"})

 */
data class RegisterEncrypt(

    var encryptStr: String

)
