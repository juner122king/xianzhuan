package com.lelezu.app.xianzhuan.dun163api


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link100
import com.netease.nis.quicklogin.helper.UnifyUiConfig

/**
 * @author liuxiaoshuai
 * @date 2022/3/18
 * @desc
 * @email liulingfeng@mistong.com
 */
object UiConfigs {
//

    fun getDConfig(context: Context): UnifyUiConfig {
        // dialog的宽度(dp值)
        val dialogWidth = (getScreenWidth(context) * 0.8).toInt()
        // dialog的高度(dp值)
        val dialogHeight = (getScreenHeight(context) * 0.5).toInt()
        return UnifyUiConfig.Builder()
            .setStatusBarColor(Color.parseColor("#ffffff"))
            .setStatusBarDarkColor(true)
            .setNavigationHeight(48)
            .setNavigationTitle("欢迎来到一键登录")
            .setNavigationIcon("login_demo_close")
            .setNavigationIconGravity(Gravity.END) // 设置导航栏按钮方向
            .setNavTitleBold(true)
            .setNavigationIconMargin(16.0f.dip2px(context))
            .setHideLogo(true)
            .setMaskNumberColor(Color.BLACK)
            .setMaskNumberSize(25)
            .setMaskNumberTypeface(Typeface.SERIF)
            .setMaskNumberTopYOffset(60)
            .setSloganSize(13)
            .setSloganColor(Color.parseColor("#9A9A9A"))
            .setSloganTopYOffset(90)
            .setLoginBtnText("易盾一键登录")
            .setLoginBtnTextColor(Color.WHITE)
            .setLoginBtnBackgroundRes("login_demo_auth_bt")
            .setLoginBtnWidth(240)
            .setLoginBtnHeight(45)
            .setLoginBtnTextSize(15)
            .setLoginBtnTopYOffset(130)
            .setPrivacyTextStart("我已阅读并同意")
            .setProtocolText("用户协议")
            .setProtocolLink(link100)
            .setPrivacyTextEnd("")
            .setPrivacyTextColor(Color.parseColor("#292929"))
            .setPrivacyProtocolColor(Color.parseColor("#3F51B5"))
            .setPrivacySize(13)
            .setPrivacyBottomYOffset(24)
            .setPrivacyMarginLeft(40)
            .setPrivacyMarginRight(40)
            .setPrivacyTextMarginLeft(8)
            .setPrivacyTextGravityCenter(true)
            .setPrivacyTextLayoutGravity(Gravity.CENTER)
            .setPrivacyCheckBoxWidth(20)
            .setPrivacyCheckBoxHeight(20)
            .setPrivacyState(false)//设置隐私栏协议复选框勾选状态

            .setHidePrivacySmh(true)
            .setCheckedImageName("login_demo_check_cus")
            .setUnCheckedImageName("login_demo_uncheck_cus")
            .setProtocolPageNavTitle("移动服务及隐私协议", "联通服务及隐私协议", "电信服务及隐私协议")
            .setProtocolPageNavColor(Color.parseColor("#FFFFFF"))
            // 设置dialog模式
            .setDialogMode(true, dialogWidth, dialogHeight, 0, 0, false)
            .setProtocolDialogMode(true) // 设置协议详情页是否dialog模式
            .setBackgroundImage("login_demo_dialog_bg") // 设置背景
            .setProtocolBackgroundImage("login_demo_dialog_bg") // 设置协议详情页背景
            .setActivityTranslateAnimation("yd_dialog_fade_in", "yd_dialog_fade_out") // 设置进出动画
            .setBackPressedAvailable(false)
            .build(context)
    }

}
