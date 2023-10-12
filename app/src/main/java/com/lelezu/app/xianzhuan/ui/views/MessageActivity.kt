package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.MessageItemAdapter

class MessageActivity : BaseActivity() {

    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件

    private lateinit var adapter: MessageItemAdapter
    private lateinit var msgIds: List<String>//未读id集合

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_message)
        swiper = findViewById(R.id.srf)
        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {
            // 执行刷新操作
            sysMessageViewModel.getMessageList()
        }

        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = MessageItemAdapter(emptyList(), sysMessageViewModel)
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)

        sysMessageViewModel.liveData.observe(this) {
            // 停止刷新动画
            swiper.isRefreshing = false
            adapter.updateData(it)
            msgIds = it.filter { !it.isRead }.map { it.msgId }
        }
        sysMessageViewModel.getMessageList()


        sysMessageViewModel.isMark.observe(this) {
            showToast("消息已确认！")
            swiper.isRefreshing = true
            sysMessageViewModel.getMessageList()
        }

        sysMessageViewModel.isLogout.observe(this) {


            if (it) {//确认注销成功 退出到登录页面
                showToast("注销成功！")
                cancelOut()

            } else {//取消注销成功

                showToast("取消注销成功！")
                swiper.isRefreshing = true
                sysMessageViewModel.getMessageList()

            }
        }


    }

    override fun onStop() {
        super.onStop()
        sysMessageViewModel.markSysMessage(msgIds)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_message
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_activity_message)
    }

    override fun isShowBack(): Boolean {
        return true
    }
}