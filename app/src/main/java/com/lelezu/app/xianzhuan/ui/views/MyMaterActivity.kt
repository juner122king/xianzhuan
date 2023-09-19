package com.lelezu.app.xianzhuan.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.lelezu.app.xianzhuan.R
import com.zj.task.sdk.b.f

class MyMaterActivity : BaseActivity() {

    lateinit var et: EditText
    lateinit var but1: TextView
    lateinit var but2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initObserve()
    }

    private fun initObserve() {

        homeViewModel.isBind.observe(this) {
            if (it) {
                showToast("绑定成功！")
                finish()
            } else {
                showToast("绑定失败！")
            }
        }
    }

    private fun initView() {

        et = findViewById(R.id.et)
        but1 = findViewById(R.id.but1)
        but2 = findViewById(R.id.but2)

        but1.setOnClickListener {
            finish()
        }
        but2.setOnClickListener {

            val userId = et.text.toString()
            homeViewModel.apiGetMater(userId)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_mater
    }

    override fun getContentTitle(): String? {
        return "师傅"
    }

    override fun isShowBack(): Boolean {
        return true
    }
}