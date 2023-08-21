package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.repository.TaskRepository.Companion.queryCondCOMBO
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel

class TaskSearchResultActivity : BaseActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var amountTextView: TextView
    private lateinit var notResulView: View
    private lateinit var adapter: TaskItemAdapter



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView = findViewById(R.id.rv_search_result)
        amountTextView = findViewById(R.id.tv_result_amount)
        notResulView = findViewById(R.id.ll_not_result)


        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = TaskItemAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 观察 ViewModel 中的任务列表数据变化
        homeViewModel._taskList.observe(this) { itemList ->
            // 数据变化时更新 RecyclerView


            if (itemList.isNotEmpty()) {
                adapter.upData(itemList)
                val num = itemList.size
                amountTextView.text = "共有${num}个结果"

            } else showNotResult()  //搜索结果为0 返回三个随机任务
        }

        homeViewModel.shuffleList.observe(this) { itemList ->
            // 数据变化时更新 RecyclerView
            adapter.upData(itemList)
        }


        // 获取搜索结果
        homeViewModel.getTaskList(getCond())



        //数据为空的时候
        homeViewModel.errMessage.observe(this){
            showNotResult()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_task_search_result
    }

    override fun getContentTitle(): String? {
        return getString(R.string.title_activity_search_result)
    }

    override fun isShowBack(): Boolean {
        return true
    }


    //设置条件
    private fun getCond(): TaskQuery {


        val bundle = intent.extras
        val highPrice = bundle?.getFloat("highPrice")
        val lowPrice = bundle?.getFloat("lowPrice")
        val taskTypeId = bundle?.getString("taskTypeId")

        return TaskQuery(queryCondCOMBO, null, highPrice, lowPrice, 9999, null, taskTypeId)
    }


    private fun showNotResult() {
        amountTextView.visibility = View.GONE
        notResulView.visibility = View.VISIBLE

        homeViewModel.getShuffle()


    }

}