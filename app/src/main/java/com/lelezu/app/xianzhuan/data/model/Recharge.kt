package com.lelezu.app.xianzhuan.data.model

/**
 * @author:Administrator
 * @date:2023/9/11 0011
 * @description:用户充值返回体
 *
 */
data class Recharge(

    var rechargeAmount: String,//	充值金额，必须为大于10元的整数，最高限制9999, 单位: 元
    var quitUrlType: Int,//	退出返回的页面, 1-充值页面(默认) 2-会员页面
    var type: Int,//	支付方式，0-微信，1-支付宝，默认：0-微信

)