package com.lelezu.app.xianzhuan.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.views.RefreshRecycleView
import com.lelezu.app.xianzhuan.utils.ToastUtils

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), RefreshRecycleView.IOnScrollListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RefreshRecycleView
    private lateinit var adapter: TaskItemAdapter

    private var current: Int = 1;//推荐任务当前加载页


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((activity?.application as MyApplication).taskRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Banner图初始化
        var viewFlipper = view.findViewById<ViewFlipper>(R.id.vp_banner)
        viewFlipper.startFlipping()


        recyclerView = view.findViewById(R.id.recyclerView)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = TaskItemAdapter(mutableListOf(), requireActivity())
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.setListener(this)
        recyclerView.setRefreshEnable(true)
        recyclerView.setLoadMoreEnable(true)


        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel._taskList.observe(viewLifecycleOwner) {
            // 数据变化时更新 RecyclerView


            if (recyclerView.isLoadMore()) {
                if (it.isEmpty()) {
                    ToastUtils.showToast(requireContext(), "没有更多了！", 0)
                } else {
                    adapter.addData(it)
                }
            } else {
                adapter.upData(it)
            }
        }


        // 初始加载
        loadData()

    }

    private fun loadData() {
        homeViewModel.getTaskList(TaskQuery("TOP", current, null, null, null, null, null))
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = MainFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    override fun onRefresh() {
        current = 1
        loadData()
    }

    override fun onLoadMore() {
        current = current.inc()//页数+1
        loadData()
    }

    override fun onLoaded() {

    }


}