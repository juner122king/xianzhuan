package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.PartnerTeamAdapter


/**
 * 合伙人业绩-我的团队页面
 */
class PartnerTeamActivity : BaseActivity() {

    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件
    private lateinit var adapter: PartnerTeamAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initObserve()
        loadData()
    }

    private fun initView() {
        recyclerView = findViewById(R.id.recyclerView)
        swiper = findViewById(R.id.swiper)
        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {
            // 执行刷新操作
            refresh()
        }
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = PartnerTeamAdapter(mutableListOf())
        adapter.setEmptyView(findViewById(R.id.recycler_layout))//设置空view
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
    private fun initObserve() {
        homeViewModel.teamLiveData.observe(this) {
            // 停止刷新动画
            swiper.isRefreshing = false

            adapter.upData(it)

        }
    }
    private fun refresh() {
        loadData()
    }

    private fun loadData() {
        homeViewModel.apiPartnerTeam()//获取团队
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_partner_team
    }

    override fun getContentTitle(): String? {
        return "我的团队"
    }

    override fun isShowBack(): Boolean {
        return true
    }
}