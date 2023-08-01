package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView
import com.lelezu.app.xianzhuan.utils.ToastUtils

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 *
 * 悬赏大厅
 */
class DashFragment : Fragment(), RefreshRecycleView.IOnScrollListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var adapter1: TaskItemAdapter
    private lateinit var adapter2: TaskItemAdapter
    private lateinit var adapter3: TaskItemAdapter
    private lateinit var adapter4: TaskItemAdapter

    private var page: Int = 0;//当前选择page  0为第一项：置顶

    private var current1: Int = 1;//当前选择page1加载页
    private var current2: Int = 1;//当前选择page2加载页
    private var current3: Int = 1;//当前选择page3加载页
    private var current4: Int = 1;//当前选择page4加载页

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
        val context = requireContext()
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_task_list)
        recyclerView = view.findViewById(R.id.rv_task)

        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)

        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter1 = TaskItemAdapter(mutableListOf(), context)
        adapter2 = TaskItemAdapter(mutableListOf(), context)
        adapter3 = TaskItemAdapter(mutableListOf(), context)
        adapter4 = TaskItemAdapter(mutableListOf(), context)
        recyclerView.adapter = adapter1


        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                page = tab.position
                //因供用一个recyclerView，所以当切换page时要设置相应的adapter
                when (page) {
                    0 -> recyclerView.adapter = adapter1
                    1 -> recyclerView.adapter = adapter2
                    2 -> recyclerView.adapter = adapter3
                    3 -> recyclerView.adapter = adapter4
                }
                loadData(false)

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {


            }

            override fun onTabReselected(tab: TabLayout.Tab) {


            }
        })


        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel._taskList.observe(viewLifecycleOwner) {
            loadDone(it)
        }
        loadData(true)//正常加载

    }


    private fun loadDone(it: MutableList<Task>) {

        if (it.isEmpty()) {
            ToastUtils.showToast(requireContext(), "没有更多了！", 0)
        } else {
            when (page) {
                0 -> if (recyclerView.isLoadMore()) adapter1.addData(it)
                else adapter1.upData(it)

                1 -> if (recyclerView.isLoadMore()) adapter2.addData(it)
                else adapter2.upData(it)

                2 -> if (recyclerView.isLoadMore()) adapter3.addData(it)
                else adapter3.upData(it)

                3 -> if (recyclerView.isLoadMore()) adapter4.addData(it)
                else adapter4.upData(it)
            }
        }
    }

    private fun loadData(isLoad: Boolean) {
        when (page) {
            0 -> if (adapter1.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(TaskQuery("TOP", current1, null, null, null, null, null))
            }

            1 -> if (adapter2.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(
                    TaskQuery(
                        "SIMPLE", current2, null, null, null, null, null
                    )
                )
            }

            2 -> if (adapter3.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(
                    TaskQuery(
                        "HIGHER", current3, null, null, null, null, null
                    )
                )
            }

            else -> if (adapter4.itemCount == 0 || isLoad) {
                homeViewModel.getTaskList(
                    TaskQuery(
                        "LATEST", current4, null, null, null, null, null
                    )
                )
            }
        }
    }

    override fun onRefresh() {
        //重置页码
        when (page) {
            0 -> current1 = 1
            1 -> current2 = 1
            2 -> current3 = 1
            3 -> current4 = 1
        }
        loadData(true)
    }


    override fun onLoadMore() {
        when (page) {
            0 -> current1 = current1.inc()
            1 -> current2 = current2.inc()
            2 -> current3 = current3.inc()
            3 -> current4 = current4.inc()
        }
        loadData(true)
    }

    override fun onLoaded() {
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


}