package com.lelezu.app.xianzhuan.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.PartnerReItemAdapter
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter

class PartnerCenterActivity : BaseActivity() {


    private lateinit var recyclerView: RefreshRecycleView //下拉刷新RecycleView
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件
    private lateinit var adapter: PartnerReItemAdapter
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
        adapter = PartnerReItemAdapter(mutableListOf())
        adapter.setEmptyView(findViewById(R.id.recycler_layout))//设置空view
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun initObserve() {
        homeViewModel.partnerLiveData.observe(this) {
            findViewById<TextView>(R.id.tv_num1).text = it.performance//团队业绩
            findViewById<TextView>(R.id.tv_num2).text = it.teamNewCount//新增人数
            findViewById<TextView>(R.id.tv_num3).text = it.teamCount//团队人数
            findViewById<TextView>(R.id.tv_lv).text = it.teamLevel//等级

        }
        homeViewModel.partnerListLiveData.observe(this) {
            adapter.upData(it)

        }
        homeViewModel.emptyListMessage.observe(this) {

            // 停止刷新动画
            swiper.isRefreshing = false

            //结果返回为空，就设置空
            adapter.setEmpty()
        }
    }


    private fun refresh() {
        loadData()
    }

    private fun loadData() {
        homeViewModel.apiPartnerBackList()//结算记录
        homeViewModel.apiPartnerBack()//获取后台数据
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_partner_center
    }

    override fun getContentTitle(): String? {
        return getString(R.string.btm_hhr)
    }

    override fun isShowBack(): Boolean {
        return true
    }
}