package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.ApiConstants.ZJ_BUSINESS_POS_ID
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.zj.zjsdk.ad.ZjAdError
import com.zj.zjsdk.ad.ZjTaskAd
import com.zj.zjsdk.ad.ZjTaskAdListener

class ZJTaskHistoryActivity : BaseActivity() {

    private lateinit var zjTaskAd: ZjTaskAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showLoading()

        zjTaskAd = ZjTaskAd(this,
            ZJ_BUSINESS_POS_ID,
            ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID),
            object : ZjTaskAdListener {
                override fun onZjAdLoaded() {
                    hideLoading()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, zjTaskAd.loadHistoryFragmentAd()).commit()
                }

                override fun onZjAdError(zjAdError: ZjAdError) {
                    hideLoading()
                    showToast("领奖记录加载失败！")
                }
            })


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_zjtask_history
    }

    override fun getContentTitle(): String? {
        return getString(R.string.title_ZJTaskHistory)
    }

    override fun isShowBack(): Boolean {
        return true
    }
}