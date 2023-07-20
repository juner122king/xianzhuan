package com.lelezu.app.xianzhuan.dun163api;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

public class PhoneLoginActivity extends AppCompatActivity {
    private boolean prefetchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final QuickLogin quickLogin = QuickLogin.getInstance();
        quickLogin.init(this, "23b26346411243b79a760cefaadabe09");
        quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                //预取号成功
                prefetchResult = true;
                Log.i("易盾SDK",YDToken+":"+mobileNumber);
            }

            @Override
            public void onGetMobileNumberError(String YDToken, String msg) {
                Log.e("易盾SDK",YDToken+":"+msg);
            }
        });

        Button btn = new Button(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefetchResult) {
                    quickLogin.onePass(new QuickLoginTokenListener() {
                        @Override
                        public void onGetTokenSuccess(String YDToken, String accessCode) {

                            //一键登录成功 运营商token：accessCode获取成功
                            //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                        }

                        @Override
                        public void onGetTokenError(String YDToken, String msg) {

                        }
                    });
                }
            }
        });
    }
}
