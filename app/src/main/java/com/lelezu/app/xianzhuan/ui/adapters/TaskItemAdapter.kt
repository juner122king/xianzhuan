package com.lelezu.app.xianzhuan.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:主页任务列表适配器
 *
 */
class TaskItemAdapter(private var items: List<Task>) : RecyclerView.Adapter<TaskItemAdapter.ItemViewHolder>() {

    // 更新数据方法
    fun updateData(newItems: List<Task>) {
        items = newItems
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_task_title)
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_task_list_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.taskTitle
    }

    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }
}
