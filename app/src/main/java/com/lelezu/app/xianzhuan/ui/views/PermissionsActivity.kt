package com.lelezu.app.xianzhuan.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lelezu.app.xianzhuan.R

class PermissionsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }

    override fun getLayoutId(): Int {
        return R.layout.activity_permissions
    }

    override fun getContentTitle(): String? {
        return "权限管理"
    }

    override fun isShowBack(): Boolean {
        return true
    }
}