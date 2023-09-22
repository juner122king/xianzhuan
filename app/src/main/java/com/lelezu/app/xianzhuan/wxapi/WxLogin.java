package com.lelezu.app.xianzhuan.wxapi;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hjq.toast.ToastUtils;
import com.lelezu.app.xianzhuan.MyApplication;
import com.lelezu.app.xianzhuan.data.model.RechargeRes;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WxLogin {

    private static int mTargetScene = SendMessageToWX.Req.WXSceneSession;


    public static void longWx(Application context) {
        IWXAPI api = ((MyApplication) context).getApi();

        if (!api.isWXAppInstalled()) {
            ToastUtils.show("您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = WxData.SCOPE;
        req.state = WxData.STATE;
        api.sendReq(req);
    }

    public static void localWx(Application context, String path) {
        Log.i("微信分享", " path = " + path);
        IWXAPI api = ((MyApplication) context).getApi();
        if (!api.isWXAppInstalled()) {
            ToastUtils.show("您还未安装微信客户端");
            return;
        }
        if (path.isEmpty()) {
            ToastUtils.show("分享失败，请检验图片链接是否正确！");
            return;
        }
        try {

            WXImageObject imgObj = new WXImageObject();
            imgObj.setImagePath(path);

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = mTargetScene;
            api.sendReq(req);


        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show("分享失败，请检验图片链接是否正确！");

        }
    }


    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    /**
     * 调起支付
     */
    public static void onPrePay(Application context, RechargeRes.PrepayPaymentResp resp) {
        IWXAPI api = ((MyApplication) context).getApi();
        PayReq request = new PayReq();

        request.appId = resp.getAppId();

        request.partnerId = resp.getPartnerId();

        request.prepayId = resp.getPrepayId();

        request.packageValue = "Sign=WXPay";

        request.nonceStr = resp.getNonceStr();

        request.timeStamp = resp.getTimestamp();

        request.sign = resp.getSign();

        api.sendReq(request);
    }


    /**
     * 调起关注小程序
     */
    /**
     * @param context
     * @param path     拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
     * @param userName 小程序原始id
     */
    public static void subscribeMiniProgram(Application context, String path, String userName) {
        IWXAPI api = ((MyApplication) context).getApi();

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = userName; // 填小程序原始id
        req.path = path;                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }


    /**
     * 调起微信客服
     */
    public static void customer(Application context) {
        IWXAPI api = ((MyApplication) context).getApi();

        // 判断当前版本是否支持拉起客服会话
        if (api.getWXAppSupportAPI() >= Build.SUPPORT_OPEN_CUSTOMER_SERVICE_CHAT) {

            WXOpenCustomerServiceChat.Req req = new WXOpenCustomerServiceChat.Req();
            req.corpId = "wwf4df797b7fac069a";                                  // 企业ID
            req.url = "https://work.weixin.qq.com/kfid/kfcc8e365cff384962d";    // 客服URL
            api.sendReq(req);
        }else {
            ToastUtils.show("打开客服失败，当前微信版本不支持！");
        }
    }


}