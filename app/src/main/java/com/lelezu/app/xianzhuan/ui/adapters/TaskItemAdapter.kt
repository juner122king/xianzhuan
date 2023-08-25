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
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.views.TaskDetailsActivity
import com.lelezu.app.xianzhuan.utils.ShareUtil.TAGMYTASK

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:主页任务列表适配器
 *
 */
class TaskItemAdapter(
    private var items: MutableList<Task>,
    var activity: Context,
    private var isShowTopView: Boolean = false
) : EmptyAdapter<RecyclerView.ViewHolder>() {

    private val statusMap = mapOf(
        0 to "未报名", 1 to "待提交", 2 to "审核中", 3 to "审核通过", 4 to "审核被否", 5 to "已取消"
        // 可以继续添加其他映射关系
    )


    private val statusTimeMap = mapOf(
        0 to "未报名",
        1 to "剩余时间：",
        2 to "提交时间：",
        3 to "通过时间：",
        4 to "审核时间：",
        5 to "已取消"
        // 可以继续添加其他映射关系
    )

    private val colorMap = mapOf(
        0 to R.color.colorControlActivated,
        1 to R.color.colorControlActivated,
        2 to R.color.colorControlActivated,
        3 to R.color.pass,
        4 to R.color.colorControlActivated,
        5 to R.color.colorControlActivated,
    )


    // 更新数据方法
    fun upData(newItems: MutableList<Task>) {

        items = newItems
        notifyDataSetChanged()

    }
    // 更新数据方法
    fun setEmpty() {

        items = mutableListOf()
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
        val tvTaskLabel: TextView = itemView.findViewById(R.id.tv_taskLabel)
        val tvTaskLabel2: TextView = itemView.findViewById(R.id.tv_taskLabel2)
        val tvEarnedCount: TextView = itemView.findViewById(R.id.tv_task_earnedCount)
        val tvTR: TextView = itemView.findViewById(R.id.tv_task_rest)
        val clickVIew: View = itemView.findViewById(R.id.click_view)

        val lTopView: View = itemView.findViewById(R.id.ll_top_view)//topView
        val line: View = itemView.findViewById(R.id.line)//topView
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)//时间
        val tvTaskStatus: TextView = itemView.findViewById(R.id.tv_task_status)//状态
        val doneView: View = itemView.findViewById(R.id.tvv)//去完成按钮
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(
            if (isShowTopView) R.layout.task_list_item_layout else R.layout.home_task_list_item_layout,
            parent,
            false
        )
        return ItemViewHolder(view)


    }


    // 绑定数据到 ItemViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = items[position]

            // 绑定普通视图的数据和事件
            holder.nameTextView.text = item.taskTitle
            holder.shangJiTv.text = "${item.unitPrice}元"
            holder.tvEarnedCount.text = "${item.earnedCount}人已赚"
            holder.tvTR.text = "剩余${item.rest}个"
            if (item.taskLabel == null) {
                holder.tvTaskLabel.visibility = View.GONE
                holder.tvTaskLabel2.visibility = View.GONE
            } else {
                holder.tvTaskLabel.text = item.taskTypeTitle
                holder.tvTaskLabel.visibility = View.VISIBLE
                holder.tvTaskLabel2.text = item.taskLabel
                holder.tvTaskLabel2.visibility = View.VISIBLE
            }


            //整个itemView能点击
            holder.clickVIew.setOnClickListener {
                val intent = Intent(activity, TaskDetailsActivity::class.java)
                intent.putExtra("taskId", items[position].taskId)
                intent.putExtra("applyLogId", items[position].applyLogId)
                intent.putExtra(TAGMYTASK, isShowTopView)
                activity.startActivity(intent)
            }

            //处理topView显示

            if (isShowTopView) {

                val statusText = statusMap[item.auditStatus] ?: "未知状态"
                val statusTimeText = statusTimeMap[item.auditStatus] ?: "未知"
                // 设置文本颜色
                val statusColor = colorMap[item.auditStatus] ?: R.color.colorControlActivated
                val resolvedColor = ContextCompat.getColor(activity, statusColor)


                holder.lTopView.visibility = View.VISIBLE
                holder.line.visibility = View.VISIBLE
                holder.tvTime.text = statusTimeText + item.operateTime
                holder.tvTaskStatus.text = statusText
                holder.tvTaskStatus.setTextColor(resolvedColor)

                holder.doneView.visibility = View.GONE

            } else {
                holder.lTopView.visibility = View.GONE
                holder.line.visibility = View.GONE
                holder.doneView.visibility = View.VISIBLE
            }
        }

    }

    // 返回数据项数量
    override fun getItemCount(): Int {

        return items.size
    }
}
