package com.lelezu.app.xianzhuan.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hjq.toast.ToastUtils;
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author:Administrator
 * @date:2023/9/18 0018
 * @description:
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WxData.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        //...
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            if (resp.errCode == 0) {
                // PaySuccess(this);	// 可在此处，添加应用自己的支付结果统计相关逻辑
                ToastUtils.show("微信支付成功！type:" + resp.errCode);
            } else if (resp.errCode == -2) {
                ToastUtils.show("用户取消支付 type:" + resp.errCode);
            } else {
                ToastUtils.show("支付失败，其他异常情形 type:" + resp.errCode);
            }
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("type", resp.errCode);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }


        this.finish();

    }
}