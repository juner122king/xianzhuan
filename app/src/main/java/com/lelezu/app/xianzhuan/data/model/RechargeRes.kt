package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/9/11 0011
 * @description:用户充值返回体
 *
 */
data class RechargeRes(

    var prepayPaymentResp: PrepayPaymentResp, var tradeNo: String//充值单号

) {
    data class PrepayPaymentResp(
        var appId: String, //	支付应用
        var nonceStr: String,//随机数
        var packageValue: String,//订单详情扩展字符串
        var partnerId: String,//支付商户ID
        var prepayId: String,//微信预支付ID
        var sign: String,//	签名
        var signType: String,//	签名方式
        var timestamp: String,//	时间戳
    )

}