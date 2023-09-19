package com.lelezu.app.xianzhuan.wxapi;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hjq.toast.ToastUtils;
import com.lelezu.app.xianzhuan.MyApplication;
import com.lelezu.app.xianzhuan.data.model.RechargeRes;
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


}