package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import com.lelezu.app.xianzhuan.R

class AutoOutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
       return  R.layout.activity_auto_out
    }

    override fun getContentTitle(): String? {
      return getString(R.string.title_activity_out)
    }

    override fun isShowBack(): Boolean {
        return true
    }
}