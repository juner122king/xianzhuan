package com.lelezu.app.xianzhuan.ui.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.ChatMessage
import com.lelezu.app.xianzhuan.utils.ImageViewUtil

/**
 * @author:Administrator
 * @date:2023/8/28 0028
 * @description:
 *
 */
class ChatAdapter(private var items: List<ChatMessage>, private var context: Context) :
    RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {
    fun updateData(newItems: List<ChatMessage>) {
        val sortedMessages = newItems.sortedBy { it.createdDt }.reversed() //根据时间排序

        items = sortedMessages  //任务步骤集合
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.talk_text)
        val ll: LinearLayout = itemView.findViewById(R.id.ll)
        val pic1: ImageView = itemView.findViewById(R.id.iv_pic_1)//对方头像
        val pic2: ImageView = itemView.findViewById(R.id.iv_pic_2)//本人头像
        val iv: ImageView = itemView.findViewById(R.id.talk_iv)//图片内容
        val date: TextView = itemView.findViewById(R.id.date)//图片内容

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.text.text = item.contactContent
        holder.date.text = item.createdDt

        if (item.isMe) {
            ImageViewUtil.loadCircleCrop(holder.pic2, item.senderUserAvatar)
            holder.pic2.visibility = View.VISIBLE
            holder.pic1.visibility = View.GONE
            holder.ll.gravity = Gravity.END
            holder.text.setBackgroundResource(R.drawable.chat_item_bg_me)
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            ImageViewUtil.loadCircleCrop(holder.pic1, item.receiverUserAvatar)//接收者
            holder.pic1.visibility = View.VISIBLE
            holder.pic2.visibility = View.GONE
            holder.ll.gravity = Gravity.START
            holder.text.setBackgroundResource(R.drawable.chat_item_bg)
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.black))
        }



        if (item.isImage) {//是否图片内容
            holder.text.visibility = View.GONE
            holder.iv.visibility = View.VISIBLE
            ImageViewUtil.loadWH(holder.iv, item.contactContent)
        } else {
            holder.text.visibility = View.VISIBLE
            holder.iv.visibility = View.GONE
            holder.text.text = item.contactContent
        }


    }
}