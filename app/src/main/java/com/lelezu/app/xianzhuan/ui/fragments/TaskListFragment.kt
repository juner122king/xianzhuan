package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView
import com.lelezu.app.xianzhuan.utils.LogUtils

private const val ARG_PARAM1 = "auditStatus"

/**
 * @author:Administrator
 * @date:2023/8/22 0022
 * @description:
 *
 */
class TaskListFragment : BaseFragment(), RefreshRecycleView.IOnScrollListener {

    private lateinit var recyclerView: RefreshRecycleView //下拉刷新RecycleView
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件

    private var current: Int = 1//当前加载页

    private lateinit var adapter: TaskItemAdapter

    private var auditStatus = 1//当前选择的子项状态 默认加载待提交
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            auditStatus = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rv_layout, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        swiper = view.findViewById(R.id.swiper)


        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {
            // 执行刷新操作
            refresh()
        }


        adapter = TaskItemAdapter(mutableListOf(), requireActivity(), true)
        adapter.setEmptyView(view.findViewById(R.id.recycler_layout))//设置空view
        recyclerView.adapter = adapter


        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)
        setSwipeRefreshLayout(swiper)

        observeList()


    }

    override fun onResume() {
        super.onResume()
        // 初始加载
        refresh()
    }

    private fun observeList() {
        homeViewModel.myTaskList.observe(viewLifecycleOwner) {
            LogUtils.i(it.toString())
            loadDone(it)
        }

    }

    private fun loadDone(it: MutableList<Task>) {

        // 停止刷新动画
        swiper.isRefreshing = false
        if (recyclerView.isLoadMore()) adapter.addData(it)
        else adapter.upData(it)

    }

    private fun loadData() {
        homeViewModel.getMyTaskList(auditStatus, current)
    }

    private fun refresh() {
        current = 1
        loadData()
    }

    override fun onRefresh() {
    }

    override fun onLoadMore() {
        current = current.inc()//页数+1
        loadData()
    }

    override fun onLoaded() {
    }

    companion object {

        @JvmStatic
        fun newInstance(auditStatus: Int) =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, auditStatus)
                }
            }
    }
}
