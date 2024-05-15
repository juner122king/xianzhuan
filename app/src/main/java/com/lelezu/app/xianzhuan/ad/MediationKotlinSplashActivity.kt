package com.lelezu.app.xianzhuan.ad

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.CSJAdError
import com.bytedance.sdk.openadsdk.CSJSplashAd
import com.bytedance.sdk.openadsdk.CSJSplashCloseType
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.utils.LogUtils


class MediationKotlinSplashActivity : AppCompatActivity() {
    val TAG = "CSJ-SDK_SplashActivity"
    private var flContent: FrameLayout? = null

    //@[classname]
    private var csjSplashAdListener: TTAdNative.CSJSplashAdListener? = null

    //@[classname]
    private var splashAdListener: CSJSplashAd.SplashAdListener? = null

    //@[classname]
    private var csjSplashAd: CSJSplashAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mediation_activity_splash)
        flContent = findViewById(R.id.fl_content)
        loadAd()
    }

    fun loadAd() {
        /** 1、创建AdSlot对象 */
        //@[classname]
        val adslot = AdSlot.Builder().setCodeId("102872066")//开屏广告位id位
            .setImageAcceptedSize(getScreenWidthInPx(this), getScreenHeightInPx(this)).build()

        /** 2、创建TTAdNative对象 */
        //@[classname]//@[methodname]
        val adNativeLoader =
            TTAdSdk.getAdManager().createAdNative(this@MediationKotlinSplashActivity)

        /** 3、创建加载、展示监听器 */
        initListeners()

        /** 4、加载广告  */
        adNativeLoader.loadSplashAd(adslot, csjSplashAdListener, 3500)
    }

    //@[classname]
    fun showAd(csjSplashAd: CSJSplashAd?) {
        /** 5、渲染成功后，展示广告 */
        this.csjSplashAd = csjSplashAd
        csjSplashAd?.setSplashAdListener(splashAdListener)
        csjSplashAd?.let {
            it.splashView?.let { splashView ->
                flContent?.addView(splashView)
            }
        }
    }

    private fun initListeners() {
        // 广告加载监听器
        //@[classname]
        csjSplashAdListener = object : TTAdNative.CSJSplashAdListener {
            //@[classname]
            override fun onSplashLoadSuccess(ad: CSJSplashAd?) {
                LogUtils.i(TAG, "onSplashAdLoad")
            }

            //@[classname]
            override fun onSplashLoadFail(error: CSJAdError?) {
                LogUtils.i(TAG, "onError code = ${error?.code} msg = ${error?.msg}")
                this@MediationKotlinSplashActivity.finish()
            }

            //@[classname]
            override fun onSplashRenderSuccess(ad: CSJSplashAd?) {
                LogUtils.i(TAG, "onSplashRenderSuccess")
                showAd(ad)
            }

            //@[classname]
            override fun onSplashRenderFail(ad: CSJSplashAd?, error: CSJAdError?) {
                LogUtils.i(TAG, "onError code = ${error?.code} msg = ${error?.msg}")
            }
        }
        //@[classname]
        splashAdListener = object : CSJSplashAd.SplashAdListener {
            //@[classname]
            override fun onSplashAdShow(p0: CSJSplashAd?) {
                LogUtils.i(TAG, "onSplashAdShow")
            }

            //@[classname]
            override fun onSplashAdClick(p0: CSJSplashAd?) {
                LogUtils.i(TAG, "onSplashAdClick")
            }

            //@[classname]
            override fun onSplashAdClose(p0: CSJSplashAd?, closeType: Int) {
                //@[classname]
                when (closeType) {
                    CSJSplashCloseType.CLICK_SKIP -> {
                        LogUtils.i(TAG, "开屏广告点击跳过")
                        //@[classname]
                    }

                    CSJSplashCloseType.COUNT_DOWN_OVER -> {
                        LogUtils.i(TAG, "开屏广告点击倒计时结束")
                        //@[classname]
                    }

                    CSJSplashCloseType.CLICK_JUMP -> {
                        LogUtils.i(TAG, "点击跳转")
                    }
                }
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /** 6、在onDestroy中销毁广告  */
        csjSplashAd?.mediationManager?.destroy()
    }

    private fun getScreenWidthInPx(context: Context): Int {
        val dm = context.applicationContext.resources.displayMetrics
        return dm.widthPixels
    }

    private fun getScreenHeightInPx(context: Context): Int {
        val dm = context.applicationContext.resources.displayMetrics
        return dm.heightPixels
    }



}