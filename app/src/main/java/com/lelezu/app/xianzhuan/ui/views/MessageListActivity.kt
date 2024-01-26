package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings

class MessageListActivity : BaseActivity(), View.OnClickListener {


    private lateinit var cou1: TextView
    private lateinit var cou2: TextView
    private lateinit var cou3: TextView
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        sysMessageViewModel.pendingTotal.observe(this) {
            cou1 = findViewById(R.id.iv_count1)
            cou2 = findViewById(R.id.iv_count2)
            cou3 = findViewById(R.id.iv_count3)

            cou1.text = it.taskCnt.toString()
            cou2.text = it.auditCnt.toString()
            cou3.text = (it.msgCnt + it.sysCnt + it.announceCnt).toString()

            if (it.taskCnt == 0) cou1.visibility = View.INVISIBLE else cou1.visibility =
                View.VISIBLE
            if (it.auditCnt == 0) cou2.visibility = View.INVISIBLE else cou2.visibility =
                View.VISIBLE
            if ((it.msgCnt + it.sysCnt + it.announceCnt) == 0) cou3.visibility =
                View.INVISIBLE else cou3.visibility = View.VISIBLE


        }


    }

    override fun onResume() {
        super.onResume()

        //执行获取待处理消息接口
        sysMessageViewModel.pending()

    }

    private fun initView() {

        findViewById<View>(R.id.ll1).setOnClickListener(this)
        findViewById<View>(R.id.ll2).setOnClickListener(this)
        findViewById<View>(R.id.ll3).setOnClickListener(this)
        findViewById<View>(R.id.tv_btm).setOnClickListener(this)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_message_list
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_activity_messagelist)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onClick(p0: View?) {
        // 用户已登录， 跳转到主页登录页面

        when (p0?.id) {
            R.id.ll1 -> {
                goToMyTask(1)//跳到我的任务待提交
            }

            R.id.ll2 -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link6)
                intent.putExtra(WebViewSettings.URL_TITLE, "我的店铺")//
                startActivity(intent)
            }

            R.id.ll3 -> {
                val intent = Intent(MyApplication.context, MessageListActivity2::class.java)
                startActivity(intent)
            }

            R.id.tv_btm -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link5)
                intent.putExtra(WebViewSettings.URL_TITLE, "选择任务分类")
                startActivity(intent)
            }

        }

    }
}