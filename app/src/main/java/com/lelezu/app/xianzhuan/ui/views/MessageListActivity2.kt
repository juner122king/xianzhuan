package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings

class MessageListActivity2 : BaseActivity(), View.OnClickListener {

    private lateinit var cou1: TextView
    private lateinit var cou2: TextView
    private lateinit var cou3: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()


        sysMessageViewModel.pendingTotal.observe(this) {


            cou1 = findViewById(R.id.iv_count1)
            cou2 = findViewById(R.id.iv_count2)
            cou3 = findViewById(R.id.iv_count3)

            cou1.text = it.sysCnt.toString()
            cou2.text = it.msgCnt.toString()
            cou3.text = it.announceCnt.toString()

            if (it.sysCnt == 0) cou1.visibility = View.INVISIBLE else cou1.visibility =
                View.VISIBLE
            if (it.msgCnt == 0) cou2.visibility = View.INVISIBLE else cou2.visibility =
                View.VISIBLE
            if (it.announceCnt == 0) cou3.visibility = View.INVISIBLE else cou3.visibility =
                View.VISIBLE


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

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_message_list2
    }

    override fun getContentTitle(): String {
        return "我的消息"
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onClick(p0: View?) {
        // 用户已登录， 跳转到主页登录页面

        when (p0?.id) {
            R.id.ll1 -> {

                startActivity(Intent(this, MessageActivity::class.java))
            }

            R.id.ll2 -> {
                startActivity(Intent(this, ChatListActivity::class.java))//雇主列表
            }

            R.id.ll3 -> {

                val intent = Intent(this, MessageSysActivity::class.java)
                startActivity(intent)
            }


        }

    }
}