package com.lelezu.app.xianzhuan.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Message

/**
 * @author:Administrator
 * @date:2023/7/25 0020
 * @description:消息列表适配器
 *
 */
class MessageItemAdapter(private var items: List<Message>) : RecyclerView.Adapter<MessageItemAdapter.ItemViewHolder>() {

    // 更新数据方法
    fun updateData(newItems: List<Message>) {
        items = newItems
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val content: TextView = itemView.findViewById(R.id.tv_content)
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sys_message_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.msgTitle
        holder.content.text = item.msgContent
    }

    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }
}