package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView
import com.lelezu.app.xianzhuan.utils.LogUtils

private const val ARG_PARAM = "TaskQuery"
private const val ARG_PARAM2 = "isMyTask"

/**
 * @author:Administrator
 * @date:2023/8/22 0022
 * @description:
 *
 */
class TaskListFragment : BaseFragment(), RefreshRecycleView.IOnScrollListener {

    private lateinit var recyclerView: RefreshRecycleView //下拉刷新RecycleView
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件
    private lateinit var adapter: TaskItemAdapter
    private lateinit var taskQuery: TaskQuery // 获取任任务列表的请求体

    private var isMyTask: Boolean = false// 列表的显示类型
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isMyTask = it.getBoolean(ARG_PARAM2)
            taskQuery = when {
                SDK_INT >= 33 -> it.getParcelable(ARG_PARAM, TaskQuery::class.java)!!
                else -> it.getParcelable(ARG_PARAM)!!
            }
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


        adapter = TaskItemAdapter(mutableListOf(), requireActivity(), isMyTask)
        adapter.setEmptyView(view.findViewById(R.id.recycler_layout))//设置空view
        recyclerView.adapter = adapter


        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(false)

    }


    //


    private fun observeList() {
        homeViewModel.taskList.observe(viewLifecycleOwner) {
            loadDone(it)
        }
        homeViewModel.emptyListMessage.observe(viewLifecycleOwner) {

            // 停止刷新动画
            onStopSwiperRefreshing()

            // 如果列表为空，禁加载更多
            recyclerView.setLoadMoreEnable(false)
            //结果返回为空，就设置空
            adapter.setEmpty()
        }
    }

    private fun loadDone(it: MutableList<Task>) {

        LogUtils.i("loadDone:" + it.size.toString())
        // 停止刷新动画
        onStopSwiperRefreshing()

        //暂时去掉加载更多，目前每次获取100条
//        if (recyclerView.isLoadMore()) adapter.addData(it)
//        else
        adapter.upData(it)

    }

    private fun loadData() {
        onShowSwiperRefreshing()
        homeViewModel.getTaskList(
            taskQuery, isMyTask
        )
    }

    private fun refresh() {
        taskQuery.current = 1
        loadData()
    }

    override fun onRefresh() {
    }

    override fun onLoadMore() {
        taskQuery.current = taskQuery.current.inc()//页数+1
        loadData()

    }

    override fun onLoaded() {

    }

    companion object {
        @JvmStatic
        fun newInstance(taskQuery: TaskQuery, isMyTask: Boolean = false) =
            TaskListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM, taskQuery)
                    putBoolean(ARG_PARAM2, isMyTask)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        LogUtils.i("${taskQuery.taskStatus}onResume")
        observeList()
    }

    override fun onStart() {
        super.onStart()
        refresh()
        LogUtils.i("${taskQuery.taskStatus}onStart")

    }

    override fun onPause() {
        super.onPause()
        LogUtils.i("${taskQuery.taskStatus}onPause")
        onStopSwiperRefreshing()
    }

    override fun onStop() {
        super.onStop()
        LogUtils.i("${taskQuery.taskStatus}onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.i("${taskQuery.taskStatus}onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.i("${taskQuery.taskStatus}onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        LogUtils.i("${taskQuery.taskStatus}onDetach")
    }

    private fun onStopSwiperRefreshing() {
        swiper.isRefreshing = false
    }

    private fun onShowSwiperRefreshing() {
        swiper.isRefreshing = true
    }

}
