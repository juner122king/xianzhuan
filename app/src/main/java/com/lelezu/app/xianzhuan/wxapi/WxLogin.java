package com.lelezu.app.xianzhuan.wxapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.lelezu.app.xianzhuan.R;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WxLogin {
    private static final int THUMB_SIZE = 150;

    private static int mTargetScene = SendMessageToWX.Req.WXSceneSession;
    public static IWXAPI api;
    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    /**
     * @param context
     */
    public static void initWx(Context context) {

        mContext = context;
        api = WXAPIFactory.createWXAPI(context, WxData.WEIXIN_APP_ID, true);
        api.registerApp(WxData.WEIXIN_APP_ID);

    }

    public static void longWx() {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mContext, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = WxData.SCOPE;
        req.state = WxData.STATE;
        api.sendReq(req);
    }


    public static void webWx(String url) {
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mContext, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        if (url.isEmpty()) {
            Toast.makeText(mContext, "分享失败，请检验图片链接是否正确！", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;  //需要确保url能正常打开
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = "WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
            msg.description = "WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.banner1);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
            bmp.recycle();
            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = mTargetScene;
            api.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "分享失败，请检验图片链接是否正确！", Toast.LENGTH_SHORT).show();

        }
    }


    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}