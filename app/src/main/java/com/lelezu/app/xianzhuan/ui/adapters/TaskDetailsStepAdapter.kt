package com.lelezu.app.xianzhuan.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Message
import com.lelezu.app.xianzhuan.data.model.TaskStep
import com.lelezu.app.xianzhuan.utils.ImageViewUtil

/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description:
 *
 */
class TaskDetailsStepAdapter(private var items: List<TaskStep>) :
    RecyclerView.Adapter<TaskDetailsStepAdapter.ItemViewHolder>() {

    // 更新数据方法
    fun updateData(newItems: List<TaskStep>) {
        items = newItems
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_step_text)
        val ivPic: ImageView = itemView.findViewById(R.id.iv_pic)
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item__task_details_step, parent, false)
        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.stepDesc
        holder.ivPic.load(item.useCaseImages[0]) //注意 ！！useCaseImages是集合
    }

    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }
}
