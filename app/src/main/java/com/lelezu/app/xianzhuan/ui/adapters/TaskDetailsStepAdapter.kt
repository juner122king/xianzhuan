package com.lelezu.app.xianzhuan.ui.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskStep
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.views.BaseActivity
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.wxapi.WxLogin


/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description: 任务步骤
 *
 */
class TaskDetailsStepAdapter(
    private var items: List<TaskStep>,
    private var ivDialog: Dialog,
    private var activity: BaseActivity
) : RecyclerView.Adapter<TaskDetailsStepAdapter.ItemViewHolder>() {

    private var isSignUp: Boolean = false ////根据任务报名与否，区别显示

    private var taskPlatform: Int = 1 //"任务平台 1:APP  2:WX小程序  3:支付宝小程序"

    // 更新数据方法
    fun updateData(newItems: List<TaskStep>, auditStatus: Int, tp: Int) {
        items = newItems  //任务步骤集合
        isSignUp = auditStatus != 0
        taskPlatform = tp
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val step: TextView = itemView.findViewById(R.id.tv_step)
        val title: TextView = itemView.findViewById(R.id.tv_step_text)
        val ivCasePic: ImageView = itemView.findViewById(R.id.iv_case_pic)//示例图片
        val fCasePic: View = itemView.findViewById(R.id.f_case_pic)//示例图片区域
        val line: View = itemView.findViewById(R.id.line)//垂直线

        val tvTr: TextView = itemView.findViewById(R.id.tv_tr)//角标说明


        val viewUrl: View = itemView.findViewById(R.id.tv_web_url)//链接区域
        val llminiP: View = itemView.findViewById(R.id.ll_mini_p)//小程序关联区域

        val view_type5: View = itemView.findViewById(R.id.view_type5)//搜索应用示例图区域

        val iv_miniapp_pic: ImageView = itemView.findViewById(R.id.iv_miniapp_pic)//搜索应用示例图区域
        val tv_miniapp_name: TextView = itemView.findViewById(R.id.tv_miniapp_name)//搜索应用示例图区域

        val tvGoLink: TextView = itemView.findViewById(R.id.tv_go_link)//
        val tvCopyLink: TextView = itemView.findViewById(R.id.tv_copy_link)

        val tv_mini_p: TextView = itemView.findViewById(R.id.tv_mini_p)//关联小程序按钮
        val tv_mini_2: ImageView = itemView.findViewById(R.id.tv_mini_2)// 已关联小程序按钮

        val iv_no_sign_up: ImageView = itemView.findViewById(R.id.iv_no_sign_up)// 未报名时显示的图片


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
        holder.step.text = ("${position + 1}").toString()


        //根据任务报名未报名的情况，区别显示,且只处理第一步骤
        if (!isSignUp && position == 0) {//
            holder.iv_no_sign_up.visibility = View.VISIBLE

            holder.fCasePic.visibility = View.GONE
            holder.viewUrl.visibility = View.GONE
            holder.llminiP.visibility = View.GONE
            holder.view_type5.visibility = View.GONE
        } else {
            holder.iv_no_sign_up.visibility = View.GONE
            //根据步骤类型，区别显示
            when (item.stepType) {  //步骤类型
                1, 4 -> { //图文步骤
                    holder.viewUrl.visibility = View.GONE
                    holder.llminiP.visibility = View.GONE
                    holder.view_type5.visibility = View.GONE
                    if (item.useCaseImage != null) {
                        holder.fCasePic.visibility = View.VISIBLE
                        ImageViewUtil.load(holder.ivCasePic, item.useCaseImage)
                        holder.ivCasePic.setOnClickListener {//图片全屏显示
                            ivDialog.setContentView(getImageView(item.useCaseImage))
                            ivDialog.show()
                        }
                    }

                    if (taskPlatform == 2) {//WX小程序任务才能点击复制关键词
                        holder.viewUrl.visibility = View.VISIBLE
                        holder.tvGoLink.visibility = View.GONE
                        holder.tvCopyLink.visibility = View.VISIBLE

                        holder.tvCopyLink.text = "点击复制关键词"
                        holder.tvCopyLink.setOnClickListener {
                            if (item.searchTerms != "") activity.plainText(item.searchTerms)
                            else {
                                activity.showToast("searchTerms字段为空！")
                            }

                        }
                    }


                }

                2 -> { //链接步骤
                    holder.fCasePic.visibility = View.GONE
                    holder.llminiP.visibility = View.GONE
                    holder.viewUrl.visibility = View.VISIBLE
                    holder.view_type5.visibility = View.GONE
                    holder.tvGoLink.setOnClickListener {
                        val intent = Intent(activity, WebViewActivity::class.java)
                        intent.putExtra(WebViewSettings.LINK_KEY, item.webUrl)
                        intent.putExtra(WebViewSettings.isProcessing, false)
                        activity.startActivity(intent)

                    }
                    holder.tvCopyLink.setOnClickListener {
                        activity.plainText(item.webUrl)
                    }
                }

                3 -> { //关联小程序步骤
                    holder.fCasePic.visibility = View.GONE
                    holder.viewUrl.visibility = View.GONE
                    holder.view_type5.visibility = View.GONE
                    holder.llminiP.visibility = View.VISIBLE

                    if (!item.hasComplete) {//未关注
                        holder.tv_mini_p.visibility = View.VISIBLE
                        holder.tv_mini_2.visibility = View.GONE

                        holder.tv_mini_p.setOnClickListener {

                            when (taskPlatform) {

                                2 -> {//跳WX小程序
                                    WxLogin.subscribeMiniProgram(
                                        activity.application, item.webUrl, item.userName
                                    )
                                }

                                3 -> {//跳支付宝小程序
                                    val intent = Intent(
                                        Intent.ACTION_VIEW, Uri.parse(item.webUrl)
                                    )
                                    activity.startActivity(intent)
                                }
                            }
                        }
                    } else {
                        holder.tv_mini_p.visibility = View.GONE
                        holder.tv_mini_2.visibility = View.VISIBLE
                    }
                }

                5 -> {//搜索应用示例图
                    holder.fCasePic.visibility = View.GONE
                    holder.viewUrl.visibility = View.GONE
                    holder.llminiP.visibility = View.GONE
                    holder.view_type5.visibility = View.VISIBLE

                    ImageViewUtil.load(holder.iv_miniapp_pic, item.useCaseImage)
                    holder.tv_miniapp_name.text = item.searchAppName


                }

                7 -> {//跳到小程序搜索栏
                    holder.fCasePic.visibility = View.GONE
                    holder.llminiP.visibility = View.GONE
                    holder.viewUrl.visibility = View.VISIBLE
                    holder.view_type5.visibility = View.GONE
                    holder.tvGoLink.visibility = View.VISIBLE
                    holder.tvGoLink.setOnClickListener {
                        val intent = Intent(
                            Intent.ACTION_VIEW, Uri.parse(item.webUrl)
                        )
                        activity.startActivity(intent)

                    }
                    holder.tvCopyLink.visibility = View.INVISIBLE

                }

            }
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
        ImageViewUtil.loadFall(imageView, any)

        imageView.setOnClickListener {
            ivDialog.dismiss()
        }

        //1.注册菜单
        activity.registerForContextMenu(imageView)
        imageView.setOnLongClickListener {
            //显示选项  保存图
            //2.打开菜单
            activity.openContextMenu(imageView)


            ShareUtil.putString(ShareUtil.APP_TASK_PIC_DOWN_URL, any.toString())//保存图片url
            true
        }
        return imageView

    }

}
