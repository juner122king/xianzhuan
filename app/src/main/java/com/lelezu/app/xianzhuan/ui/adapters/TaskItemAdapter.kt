package com.lelezu.app.xianzhuan.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.views.TaskDetailsActivity

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:主页任务列表适配器
 *
 */
class TaskItemAdapter(private var items: MutableList<Task>, var activity: Context) :
    RecyclerView.Adapter<TaskItemAdapter.ItemViewHolder>() {


    // 更新数据方法
    fun upData(newItems: MutableList<Task>) {

        items = newItems
        notifyDataSetChanged()

    }

    fun addData(newItems: MutableList<Task>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_task_title)
        val shangJiTv: TextView = itemView.findViewById(R.id.tv_shang_ji)
        val tvEarnedCount: TextView = itemView.findViewById(R.id.tv_task_earnedCount)
        val tvTR: TextView = itemView.findViewById(R.id.tv_task_rest)
        val clickVIew: View = itemView.findViewById(R.id.click_view)
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_task_list_item_layout, parent, false)


        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.taskTitle
        holder.shangJiTv.text = "${item.unitPrice}元"
        holder.tvEarnedCount.text = "${item.earnedCount}人已赚"
        holder.tvTR.text = "剩余${item.rest}个"


        //整个itemView能点击
        holder.clickVIew.setOnClickListener {
            val intent = Intent(activity, TaskDetailsActivity::class.java)
            intent.putExtra("taskId", items[position].taskId)
            activity.startActivity(intent)
        }

    }

    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }

}
