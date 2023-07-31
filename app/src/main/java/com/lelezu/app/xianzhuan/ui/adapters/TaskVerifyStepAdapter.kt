package com.lelezu.app.xianzhuan.ui.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskStep
import com.lelezu.app.xianzhuan.data.model.TaskUploadVerify
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil

/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description: 任务详情
 *
 */
class
TaskVerifyStepAdapter(
    private var items: List<TaskUploadVerify>,
    private var ivDialog: Dialog,
    private var activity: Activity
) : RecyclerView.Adapter<TaskVerifyStepAdapter.ItemViewHolder>() {


    // 更新数据方法
    fun updateData(newItems: List<TaskUploadVerify>) {
        items = newItems  //任务步骤集合
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_step_text)
        val idEt: EditText = itemView.findViewById(R.id.et_id)
        val ivCasePic: ImageView = itemView.findViewById(R.id.iv_case_pic)//示例图片
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item__task_verify_step, parent, false)
        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.verifyDesc


        if (item.verifyType == 1) {
            holder.ivCasePic.visibility = View.VISIBLE
            holder.ivCasePic.load(item.useCaseImage) //
            holder.ivCasePic.setOnClickListener {//图片全屏显示
                ivDialog.setContentView(getImageView(item.useCaseImage))
                ivDialog.show()
            }

        } else {
            holder.idEt.visibility = View.VISIBLE
        }

    }

    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }

    private fun getImageView(any: Any): ImageView {
        val imageView = ImageView(activity)
        imageView.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ImageViewUtil.load(imageView, any)

        imageView.setOnClickListener {
            ivDialog.dismiss()
        }

        //1.注册菜单
        activity.registerForContextMenu(imageView)
        imageView.setOnLongClickListener {
            //显示选项  保存图
            //2.打开菜单
            activity.openContextMenu(imageView);
            ShareUtil.putString(ShareUtil.APP_TASK_PIC_DOWN_URL, any.toString())
            true
        }
        return imageView
    }

}
