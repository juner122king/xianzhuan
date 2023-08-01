package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 *
 * 悬赏大厅
 */
class DashFragment : Fragment(),RefreshRecycleView.IOnScrollListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var adapter: TaskItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dash, container, false)
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((activity?.application as MyApplication).taskRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_task_list)
        recyclerView = view.findViewById(R.id.rv_task)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = TaskItemAdapter(mutableListOf(),requireActivity())
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)


        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.i("悬赏大厅", "onTabSelected:" + tab.text);

                var queryCond = "TOP"

                queryCond = when (tab.position) {
                    0 -> "TOP"
                    1 -> "SIMPLE"
                    2 -> "HIGHER"
                    else -> "LATEST"
                }
                homeViewModel.getTaskList(TaskQuery(queryCond))
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {


            }
            override fun onTabReselected(tab: TabLayout.Tab) {


            }
        })

        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel._taskList.observe(viewLifecycleOwner) { itemList ->
            // 数据变化时更新 RecyclerView
            adapter.upData(itemList)
        }

        // 异步获取数据并更新 RecyclerView
        homeViewModel.getTaskList(TaskQuery("TOP"))


    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = DashFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    override fun onRefresh() {
        // 异步获取数据并更新 RecyclerView
        homeViewModel.getTaskList(TaskQuery("TOP"))
    }

    override fun onLoadMore() {

    }

    override fun onLoaded() {
    }
}