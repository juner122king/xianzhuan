package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.ChatListAdapter
class ChatListActivity : BaseActivity() {

    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var adapter: ChatListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initObserve()

    }


    private fun initView() {
        recyclerView = findViewById(R.id.rv)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = ChatListAdapter(emptyList(),this)
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun initObserve() {

        showLoading()
        loginViewModel.apiContactors()
        loginViewModel.chatList.observe(this) {
            adapter.updateData(it)
            hideLoading()
        }


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chat_list
    }

    override fun getContentTitle(): String? {
        return "雇主列表"
    }

    override fun isShowBack(): Boolean {
        return true
    }
}