package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings

class AutoOutActivity : BaseActivity(), OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<View>(R.id.tv_agreement).setOnClickListener(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_auto_out
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_activity_out)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_agreement -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link100)
                intent.putExtra(WebViewSettings.URL_TITLE, getString(R.string.text_agreement))
                intent.putExtra(WebViewSettings.isProcessing, false)
                startActivity(intent)
            }

        }
    }
}