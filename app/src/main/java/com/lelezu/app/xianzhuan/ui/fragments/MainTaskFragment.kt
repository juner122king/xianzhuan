package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView
import com.lelezu.app.xianzhuan.utils.ToastUtils

/**
 *
 * 主页本地任务列表
 */
class MainTaskFragment : Fragment(), RefreshRecycleView.IOnScrollListener {


    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var adapter: TaskItemAdapter
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件

    private var current: Int = 1;//当前选择page1加载页


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_tesk_list, container, false)
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((activity?.application as MyApplication).taskRepository)
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

        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = TaskItemAdapter(mutableListOf(), requireActivity())
        adapter.setEmptyView(view.findViewById(R.id.recycler_layout))
        recyclerView.adapter = adapter

        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)


        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel._taskList.observe(viewLifecycleOwner) {
            loadDone(it)
        }
        //错误信息监听
        homeViewModel.errMessage.observe(requireActivity()) {
            // 停止刷新动画
            swiper.isRefreshing = false
            ToastUtils.showToast(requireActivity(), it, 0)
        }

        // 初始加载
        refresh()

    }


    private fun loadDone(it: MutableList<Task>) {

        // 停止刷新动画
        swiper.isRefreshing = false
        if (recyclerView.isLoadMore()) adapter.addData(it)
        else adapter.upData(it)

    }

    override fun onRefresh() {
//        refresh()
    }

    private fun refresh() {
        current = 1
        loadData()
    }

    override fun onLoadMore() {
        current = current.inc()//页数+1
        loadData()
    }

    override fun onLoaded() {

    }

    private fun loadData() {
        homeViewModel.getTaskList(TaskRepository.queryCondLATEST, current)
    }


    companion object {

        @JvmStatic
        fun newInstance() = MainTaskFragment()
    }
}