package com.lelezu.app.xianzhuan.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Partner
import com.lelezu.app.xianzhuan.ui.views.PartnerCenterItemActivity

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:合伙人后台结算记录
 *
 */
class PartnerReItemAdapter(
    private var items: MutableList<Partner>,
) : EmptyAdapter<RecyclerView.ViewHolder>() {

    private val statusMap = mapOf(
        0 to "待结算",
        1 to "已结算",
        2 to "封号没收",
        // 可以继续添加其他映射关系
    )


    private val colorMap = mapOf(
        0 to R.color.text_check,
        1 to R.color.B3B3B3,
        2 to R.color.colorControlActivated,
    )


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

    fun addData(newItems: MutableList<Partner>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }


    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_price: TextView = itemView.findViewById(R.id.tv_price)
        val tv_time: TextView = itemView.findViewById(R.id.tv_time)
        val tv_status: TextView = itemView.findViewById(R.id.tv_status)
        val click_view: View = itemView.findViewById(R.id.click_view)
        var c = context
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_partner_center, parent, false
        )
        return ItemViewHolder(parent.context, view)


    }


    // 绑定数据到 ItemViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = items[position]
            holder.tv_price.text = "￥${item.earning}"
            holder.tv_time.text = "时间：${item.createdDate}"
            holder.tv_status.text = statusMap[item.settlementStatus] ?: "未知状态"

            // 设置文本颜色
            val statusColor = colorMap[item.settlementStatus] ?: R.color.colorControlActivated
            val resolvedColor = ContextCompat.getColor(holder.c, statusColor)
            holder.tv_status.setTextColor(resolvedColor)
            holder.click_view.setOnClickListener {


                val intent = Intent(holder.c, PartnerCenterItemActivity::class.java)

                intent.putExtra("T0", item.settlementStatus)
                intent.putExtra("T1", item.earning)
                intent.putExtra("T2", item.taskCount)
                intent.putExtra("T3", item.completeTaskCnt)
                intent.putExtra("T4", item.teamLevel)
                intent.putExtra("T5", item.rate)
                intent.putExtra("T6", item.performance)
                intent.putExtra("T7", item.reward)
                intent.putExtra("T8", item.createdDate)

                holder.c.startActivity(intent)

            }
        }

    }

    // 返回数据项数量
    override fun getItemCount(): Int {

        return items.size
    }


}
