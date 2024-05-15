package com.lelezu.app.xianzhuan.ad;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTAdConfig;

/**
 * @author:Administrator
 * @date:2024/5/15 0015
 * @description:
 */
public class MyCSJAdConfig {

    public static TTAdConfig buildConfig(Context context) {



        return new TTAdConfig.Builder()
                /**
                 * 注：需要替换成在媒体平台申请的appID ，切勿直接复制
                 */.appId("5445700").appName("众手帮")
                /**
                 * 上线前需要关闭debug开关，否则会影响性能
                 */.debug(true)
                /**
                 * 使用聚合功能此开关必须设置为true，默认为false，不会初始化聚合模板，聚合功能会吟唱
                 */.useMediation(false)
//                .customController(getTTCustomController()) //如果您需要设置隐私策略请参考该api
//                .setMediationConfig(new MediationConfig.Builder() //可设置聚合特有参数详细设置请参考该api
//                        .setMediationConfigUserInfoForSegment(getUserInfoForSegment())//如果您需要配置流量分组信息请参考该api
//                        .build())
                .build();
    }
}
