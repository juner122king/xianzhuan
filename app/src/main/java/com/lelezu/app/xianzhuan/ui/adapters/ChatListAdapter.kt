package com.lelezu.app.xianzhuan.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.ChatList
import com.lelezu.app.xianzhuan.ui.views.BaseActivity
import com.lelezu.app.xianzhuan.ui.views.ChatActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
/**
 * @author:Administrator
 * @date:2023/8/28 0028
 * @description:
 *
 */
class ChatListAdapter(
    private var items: List<ChatList>,
    private var context: BaseActivity
) : RecyclerView.Adapter<ChatListAdapter.ItemViewHolder>() {


    fun updateData(newItems: List<ChatList>) {

        items = newItems  //任务步骤集合
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.iv_name)
        val pic: ImageView = itemView.findViewById(R.id.iv_head)//对方头像
        val count: TextView = itemView.findViewById(R.id.iv_count)//未读消息数量
        val clickVIew: View = itemView.findViewById(R.id.click_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.nickname
        holder.count.text = item.unreadCount.toString()
        ImageViewUtil.loadCircleCrop(holder.pic, item.avatar)


        if (item.unreadCount > 0) {
            holder.count.visibility = View.VISIBLE
        } else {
            holder.count.visibility = View.INVISIBLE
        }

        //整个itemView能点击
        holder.clickVIew.setOnClickListener {
            val intent = Intent(holder.clickVIew.context, ChatActivity::class.java)
            intent.putExtra("userId", item.userId)
            context.startActivity(intent)
        }

    }


}