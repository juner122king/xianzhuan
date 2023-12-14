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
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.StringUtils.colorMap
import com.lelezu.app.xianzhuan.ui.StringUtils.statusMap
import com.lelezu.app.xianzhuan.ui.StringUtils.statusTimeMap
import com.lelezu.app.xianzhuan.ui.views.TaskDetailsActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.TAGMYTASK

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:主页任务列表适配器
 *
 */
class TaskItemAdapter(
    private var items: MutableList<Task>, private var isShowTopView: Boolean = false,
) : EmptyAdapter<RecyclerView.ViewHolder>() {


    //vip头像等级框图片
    private var pic = mapOf(
        1 to R.drawable.icon_head1, 2 to R.drawable.icon_head, 4 to R.drawable.icon_head2
        // 可以继续添加其他映射关系
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
    class ItemViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_task_title)
        val shangJiTv: TextView = itemView.findViewById(R.id.tv_shang_ji)
        val tvTaskLabel: TextView = itemView.findViewById(R.id.tv_taskLabel)
        val tvTaskLabel2: TextView = itemView.findViewById(R.id.tv_taskLabel2)
        val tvEarnedCount: TextView = itemView.findViewById(R.id.tv_task_earnedCount)
        val tvTR: TextView = itemView.findViewById(R.id.tv_task_rest)
        val clickVIew: View = itemView.findViewById(R.id.click_view)

        val lTopView: View = itemView.findViewById(R.id.ll_top_view)//topView
        val line: View = itemView.findViewById(R.id.line)//topView
        val doneView: View = itemView.findViewById(R.id.tvv)//去完成按钮

        val iv_user_pic_head: ImageView = itemView.findViewById(R.id.iv_user_pic2)//任务发布人等级框
        val iv_user_pic: ImageView = itemView.findViewById(R.id.iv_user_pic)//任务发布人头像
        val iv_ding: View = itemView.findViewById(R.id.iv_ding)//置顶任务icon
        var c = context

    }

    class ItemViewHolder2(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_task_title)
        val shangJiTv: TextView = itemView.findViewById(R.id.tv_shang_ji)
        val tvTaskLabel: TextView = itemView.findViewById(R.id.tv_taskLabel)
        val tvTaskLabel2: TextView = itemView.findViewById(R.id.tv_taskLabel2)
        val clickVIew: View = itemView.findViewById(R.id.click_view)

        val tvTime: TextView = itemView.findViewById(R.id.tv_time)//时间
        val tv_task_step_text: TextView = itemView.findViewById(R.id.tv_task_step_text)//完成步骤数
        val tvTaskStatus: TextView = itemView.findViewById(R.id.tv_task_status)//状态
        val doneView: View = itemView.findViewById(R.id.tvv)//去完成按钮

        val iv_user_pic: ImageView = itemView.findViewById(R.id.iv_user_pic)//任务发布人头像
        val iv_ding: View = itemView.findViewById(R.id.iv_ding)//置顶任务icon
        var c = context

    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val view = LayoutInflater.from(parent.context).inflate(
            if (isShowTopView) R.layout.task_list_item_layout else R.layout.home_task_list_item_layout,
            parent,
            false
        )
        return if (isShowTopView) ItemViewHolder2(
            parent.context, view
        ) else ItemViewHolder(parent.context, view)


    }


    // 绑定数据到 ItemViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = items[position]
            //设置头像与等级框
            holder.iv_user_pic_head
            ImageViewUtil.loadCircleCrop(
                holder.iv_user_pic, item.userInfo.avatar ?: String
            )

            holder.iv_user_pic_head.background = pic[item.userInfo.vipLevel]?.let { it1 ->
                holder.iv_user_pic_head.context.getDrawable(
                    it1
                )
            }

            if (item.isTop && !isShowTopView) {//置顶图标
                holder.iv_ding.visibility = View.VISIBLE
            } else {
                holder.iv_ding.visibility = View.GONE
            }


            // 绑定普通视图的数据和事件
            holder.nameTextView.text = item.taskTitle
            holder.shangJiTv.text = "+${item.unitPrice}元"
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
                val intent = Intent(holder.c, TaskDetailsActivity::class.java)
                intent.putExtra("taskId", items[position].taskId)
                intent.putExtra("applyLogId", items[position].applyLogId)
                intent.putExtra(TAGMYTASK, isShowTopView)
                holder.c.startActivity(intent)
            }

            //处理topView显示


            holder.lTopView.visibility = View.GONE
            holder.line.visibility = View.GONE
            holder.doneView.visibility = View.VISIBLE


        }


        //我的任务样式
        if (holder is ItemViewHolder2) {
            val item = items[position]

            ImageViewUtil.loadCircleCrop(
                holder.iv_user_pic, item.userInfo.avatar ?: String
            )
            if (item.isTop && !isShowTopView) {//置顶图标
                holder.iv_ding.visibility = View.VISIBLE
            } else {
                holder.iv_ding.visibility = View.GONE
            }


            // 绑定普通视图的数据和事件
            holder.nameTextView.text = item.taskTitle
            holder.shangJiTv.text = "+${item.unitPrice}元"

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
                val intent = Intent(holder.c, TaskDetailsActivity::class.java)
                intent.putExtra("taskId", items[position].taskId)
                intent.putExtra("applyLogId", items[position].applyLogId)
                intent.putExtra(TAGMYTASK, isShowTopView)
                holder.c.startActivity(intent)
            }

            //处理topView显示
            val statusText = statusMap[item.auditStatus] ?: "未知状态"

            val statusTimeText = statusTimeMap[item.auditStatus] ?: "未知"
            // 设置文本颜色
            val statusColor = colorMap[item.auditStatus] ?: R.color.colorControlActivated
            val resolvedColor = ContextCompat.getColor(holder.c, statusColor)

            when (item.auditStatus) {
                5, 6 -> {
                    holder.tvTime.visibility = View.GONE
                    if (item.taskStatus == 5) {
                        holder.tvTime.text = "任务已结束"
                    }
                }

                1, 7 -> {
                    holder.tvTime.text = "需在:" + item.operateTime + "前完成任务、提交验证信息"
                    holder.tvTime.visibility = View.VISIBLE
                }

                else -> {
                    holder.tvTime.visibility = View.VISIBLE
                    holder.tvTime.text = statusTimeText + item.operateTime
                }
            }
            holder.tvTaskStatus.text = statusText
            holder.tv_task_step_text.text = item.stepCntNo

            holder.tvTaskStatus.setTextColor(resolvedColor)
            holder.tv_task_step_text.setTextColor(resolvedColor)
            holder.doneView.visibility = View.VISIBLE


        }

    }

    // 返回数据项数量
    override fun getItemCount(): Int {

        return items.size
    }


}
