package com.lelezu.app.xianzhuan.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Partner
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.views.TaskDetailsActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.TAGMYTASK

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:合伙人后台结算记录
 *
 */
class PartnerTeamAdapter(
    private var items: MutableList<Partner>,
) : EmptyAdapter<RecyclerView.ViewHolder>() {


    // 更新数据方法
    fun upData(newItems: MutableList<Partner>) {

        items = newItems
        notifyDataSetChanged()

    }

    // 更新数据方法
    fun setEmpty() {
        items = mutableListOf()
        notifyDataSetChanged()
    }


    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_user_pic: ImageView = itemView.findViewById(R.id.iv_user_pic)
        val tv_user_name: TextView = itemView.findViewById(R.id.tv_user_name)
        val tv_user_id: TextView = itemView.findViewById(R.id.tv_user_id)

        val tv_teamCount: TextView = itemView.findViewById(R.id.tv_teamCount)
        val tv_performance: TextView = itemView.findViewById(R.id.tv_performance)
        var c = context
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_partner_tame, parent, false
        )
        return ItemViewHolder(parent.context, view)


    }


    // 绑定数据到 ItemViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = items[position]
            ImageViewUtil.loadCircleCrop(holder.iv_user_pic, item.avatar)
            holder.tv_user_name.text = "${item.nickName}"
            holder.tv_user_id.text = "UID:${item.partnerId}"
            holder.tv_teamCount.text = "团队总人数：${item.teamCount}"
            holder.tv_performance.text = "本月业绩：${item.performance}"

        }

    }

    // 返回数据项数量
    override fun getItemCount(): Int {

        return items.size
    }


}
