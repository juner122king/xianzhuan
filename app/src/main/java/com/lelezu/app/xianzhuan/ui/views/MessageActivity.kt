package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.ui.adapters.MessageItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.SysMessageViewModel

class MessageActivity : BaseActivity() {


    private val sysMessageViewModel: SysMessageViewModel by viewModels {
        SysMessageViewModel.ViewFactory((application as MyApplication).sysInformRepository)
    }
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件

    private lateinit var adapter: MessageItemAdapter

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
        adapter = MessageItemAdapter(emptyList(),sysMessageViewModel)
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)



        sysMessageViewModel.liveData.observe(this) {
            // 数据变化时更新 RecyclerView
            // 停止刷新动画
            swiper.isRefreshing = false
            adapter.updateData(it)


        }
        sysMessageViewModel.getMessageList()


        sysMessageViewModel.isMark.observe(this){

            sysMessageViewModel.getMessageList()
        }


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_message
    }

    override fun getContentTitle(): String? {
        return getString(R.string.title_activity_message)
    }

    override fun isShowBack(): Boolean {
        return true
    }
}