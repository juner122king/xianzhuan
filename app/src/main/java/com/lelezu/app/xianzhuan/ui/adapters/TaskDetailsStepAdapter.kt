package com.lelezu.app.xianzhuan.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskStep
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil


/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description: 任务步骤
 *
 */
class TaskDetailsStepAdapter(
    private var items: List<TaskStep>, private var ivDialog: Dialog, private var activity: Activity
) : RecyclerView.Adapter<TaskDetailsStepAdapter.ItemViewHolder>() {

    // 更新数据方法
    fun updateData(newItems: List<TaskStep>) {
        items = newItems  //任务步骤集合
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val step: TextView = itemView.findViewById(R.id.tv_step)
        val title: TextView = itemView.findViewById(R.id.tv_step_text)
        val ivCasePic: ImageView = itemView.findViewById(R.id.iv_case_pic)//示例图片
        val fCasePic: View = itemView.findViewById(R.id.f_case_pic)//示例图片区域
        val ivUserPic: ImageView = itemView.findViewById(R.id.iv_user_up_pic)//用户图片
        val line: View = itemView.findViewById(R.id.line)//垂直线

        val tvTr: TextView = itemView.findViewById(R.id.tv_tr)//角标说明


        val viewUrl: View = itemView.findViewById(R.id.tv_web_url)//链接区域

        val tvGoLink: TextView = itemView.findViewById(R.id.tv_go_link)//
        val tvCopyLink: TextView = itemView.findViewById(R.id.tv_copy_link)
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item__task_details_step, parent, false)
        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvTr.text = "说明图"

        holder.title.text = item.stepDesc
        holder.ivUserPic.visibility = View.GONE
        holder.step.text = ("${position + 1}").toString()

        if (item.stepType == 1) {
            holder.viewUrl.visibility = View.GONE
            if (item.useCaseImage != null) {
                holder.fCasePic.visibility = View.VISIBLE
                ImageViewUtil.load(holder.ivCasePic, item.useCaseImage)
                holder.ivCasePic.setOnClickListener {//图片全屏显示
                    ivDialog.setContentView(getImageView(item.useCaseImage))
                    ivDialog.show()
                }
            }
        } else {
            holder.fCasePic.visibility = View.GONE
            holder.viewUrl.visibility = View.VISIBLE
        }


        // 判断是否为整个 RecyclerView 的最后一个项
        if (position == itemCount - 1) {
            // 针对最后一个项进行特殊处理
            // 例如，可以设置 line 的可见性为 View.GONE
            holder.line.visibility = View.GONE
        } else {
            // 其他项的处理
            // 例如，设置 line 的可见性为 View.VISIBLE
            holder.line.visibility = View.VISIBLE
            //动态设置垂直线高度
            val layoutParams = holder.line.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            holder.line.layoutParams = layoutParams
            holder.line.requestLayout()
        }

        holder.tvGoLink.setOnClickListener {

            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(WebViewSettings.LINK_KEY, item.webUrl)
            intent.putExtra(WebViewSettings.isProcessing, false)
            activity.startActivity(intent)

        }
        holder.tvCopyLink.setOnClickListener {
            val textToCopy = item.webUrl

            val clipboardManager =
                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label", textToCopy)
            clipboardManager.setPrimaryClip(clipData)

            // 可以在这里显示一个提示，表示文本已经复制到剪贴板
            Toast.makeText(activity, "已复制到剪贴板", Toast.LENGTH_SHORT).show()

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


            ShareUtil.putString(ShareUtil.APP_TASK_PIC_DOWN_URL, any.toString())//保存图片url
            true
        }
        return imageView

    }

}
