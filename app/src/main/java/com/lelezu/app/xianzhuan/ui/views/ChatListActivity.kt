package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.ChatListAdapter

class ChatListActivity : BaseActivity() {

    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var adapter: ChatListAdapter

    private lateinit var notResulView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initObserve()

    }

    override fun onResume() {
        super.onResume()
        showLoading()
        loginViewModel.apiContactors()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.rv)
        notResulView = findViewById(R.id.ll_not_result)

        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = ChatListAdapter(emptyList(), this)
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setRefreshEnable(false)
        recyclerView.setLoadMoreEnable(false)
    }

    private fun initObserve() {

        loginViewModel.chatList.observe(this) {
            hideLoading()
            if (it.isNotEmpty()) {
                adapter.updateData(it)
            } else showNotResult()
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

    private fun showNotResult() {
        notResulView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE


    }
}