package com.lelezu.app.xianzhuan.ui.adapters

import android.app.Dialog
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.LongTaskVos
import com.lelezu.app.xianzhuan.data.model.TaskUploadVerify
import com.lelezu.app.xianzhuan.ui.views.BaseActivity
import com.lelezu.app.xianzhuan.ui.views.TaskDetailsActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil


/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description: 任务详情
 *
 */
class TaskLongVerifyStepAdapter(
    private var items: List<LongTaskVos>,
    private var ivDialog: Dialog,
    private var activity: TaskDetailsActivity,
    private val pickImageContract: ActivityResultLauncher<Unit>,
) : RecyclerView.Adapter<TaskLongVerifyStepAdapter.ItemViewHolder>() {


    private var statuspic = mapOf(
        1 to R.drawable.icon_lt_s1,
        2 to R.drawable.icon_lt_s2,
        3 to R.drawable.icon_lt_s3,
        4 to R.drawable.icon_lt_s4,
        5 to R.drawable.icon_lt_s5,
        6 to R.drawable.icon_lt_s16,

        // 可以继续添加其他映射关系
    )

    private val adapterVerifys: MutableList<TaskVerifyStepAdapter> = mutableListOf() //验证列表集合


    private var actionPosition: Int = 0 //当前选中的


    /**
     * 更新数据方法
     * @param newItems List<LongTaskVos>
     */
    fun updateData(newItems: List<LongTaskVos>) {
        items = newItems  //任务步骤集合
        isCanSubmit()
        for (i in newItems.indices) {
            val adapterVerify = TaskVerifyStepAdapter(
                emptyList(), ivDialog, activity, pickImageContract, i
            )
            adapterVerifys.add(adapterVerify)
        }
        notifyDataSetChanged()
    }

    //是否有长任务步骤可以提交
    private fun isCanSubmit() {
        for (i in items.indices) {
            if (items[i].auditStatus == 1) {
                //首个待提交的步骤
                actionPosition = i
                break
            }
        }
    }


    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val stepAmount: TextView = itemView.findViewById(R.id.tv_stepAmount) //奖励
        val status: ImageView = itemView.findViewById(R.id.tv_stepAmount2) //状态
        val tv_step: TextView = itemView.findViewById(R.id.tv_step) //步骤
        val rv: RecyclerView = itemView.findViewById(R.id.rv_task_verify) //任务步骤
        val repick: TextView = itemView.findViewById(R.id.tv_long_task_repick) //再次提交按钮


    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item__task_long_details_step, parent, false)

        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val text1 =
            "<font color='#222222'>按要求完成任务,奖励</font><font color='#F9706A'>￥${items[position].stepAmount}</font>"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.stepAmount.text = Html.fromHtml(text1, Html.FROM_HTML_MODE_COMPACT)
        } else {
            holder.stepAmount.text = Html.fromHtml(text1)
        }

        holder.tv_step.text = "奖励${position + 1}"

        if (items[position].auditStatus != 0) {
            holder.status.setImageResource(statuspic[items[position].auditStatus]!!)
            holder.status.visibility = View.VISIBLE
        } else {
            holder.status.visibility = View.INVISIBLE
        }

        adapterVerifys[position].updateData(
            items[position].taskUploadVerifyList,
            items[position].auditStatus,
            actionPosition == position
        )

        holder.rv.adapter = adapterVerifys[position]
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        holder.rv.layoutManager = LinearLayoutManager(activity)


        if (items[position].auditStatus == 4) {//不通过
            holder.repick.visibility = View.VISIBLE
            holder.repick.setOnClickListener {//再次提交
                activity.longTaskReSubmit(position)
            }
        } else {
            holder.repick.visibility = View.GONE
        }


    }


    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }

    fun getItems(): List<LongTaskVos> {
        return items
    }

    fun getTaskUploadVerifyList(position: Int): List<TaskUploadVerify> {
        return getAdapterVerify(position).getItems()
    }

    fun getAdapterVerify(position: Int): TaskVerifyStepAdapter {
        return adapterVerifys[position]
    }


}
