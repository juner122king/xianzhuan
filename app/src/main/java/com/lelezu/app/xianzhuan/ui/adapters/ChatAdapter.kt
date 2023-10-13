package com.lelezu.app.xianzhuan.ui.adapters

import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.ChatMessage
import com.lelezu.app.xianzhuan.ui.views.BaseActivity
import com.lelezu.app.xianzhuan.ui.views.TaskDetailsActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil

/**
 * @author:Administrator
 * @date:2023/8/28 0028
 * @description:
 *
 */
class ChatAdapter(
    private var items: List<ChatMessage>,
    private var ivDialog: Dialog,
    private var context: BaseActivity
) : RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {


    fun updateData(newItems: List<ChatMessage>) {
        val sortedMessages = newItems.sortedBy { it.createdDt }//根据时间排序

        items = sortedMessages  //任务步骤集合
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.talk_text)
        val ll: LinearLayout = itemView.findViewById(R.id.ll)
        val pic1: ImageView = itemView.findViewById(R.id.iv_pic_1)//对方头像
        val pic2: ImageView = itemView.findViewById(R.id.iv_pic_2)//本人头像
        val iv: ImageView = itemView.findViewById(R.id.talk_iv)//图片内容
        val date: TextView = itemView.findViewById(R.id.date)//

        val click_view: View = itemView.findViewById(R.id.click_view)//任务内容
        val ll_view: View = itemView.findViewById(R.id.ll_view)//图片或文字

        val tv_task_title: TextView = itemView.findViewById(R.id.tv_task_title)//任务信息标题
        val tv_taskLabel: TextView = itemView.findViewById(R.id.tv_taskLabel)//
        val tv_taskLabel2: TextView = itemView.findViewById(R.id.tv_taskLabel2)//
        val tv_shang_ji: TextView = itemView.findViewById(R.id.tv_shang_ji)//
        val tv_task_earnedCount: TextView = itemView.findViewById(R.id.tv_task_earnedCount)//
        val tv_task_rest: TextView = itemView.findViewById(R.id.tv_task_rest)//


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


        when (item.type) {

            0 -> { //文本消息
                holder.text.visibility = View.VISIBLE
                holder.iv.visibility = View.GONE

                holder.click_view.visibility = View.GONE
                holder.ll_view.visibility = View.VISIBLE


                holder.text.text = item.contactContent
            }

            1 -> {//图片消息
                holder.text.visibility = View.GONE
                holder.iv.visibility = View.VISIBLE
                holder.click_view.visibility = View.GONE
                holder.ll_view.visibility = View.VISIBLE

                ImageViewUtil.loadWH(holder.iv, item.contactContent)
                holder.iv.setOnClickListener {//图片全屏显示
                    ivDialog.setContentView(getImageView(item.contactContent))
                    ivDialog.show()
                }
            }

            2 -> {//任务消息
                holder.click_view.visibility = View.VISIBLE
                holder.ll_view.visibility = View.GONE
                holder.click_view.setOnClickListener {

                    val intent = Intent(context, TaskDetailsActivity::class.java)
                    intent.putExtra("taskId", items[position].contactContent)
                    context.startActivity(intent)
                }
                holder.tv_task_title.text = items[position].taskAppVo.taskTitle //任务标题
                holder.tv_taskLabel.text = items[position].taskAppVo.taskTypeTitle //任务标签1
                holder.tv_taskLabel2.text = items[position].taskAppVo.taskLabel //任务标签2

                holder.tv_shang_ji.text = "${items[position].taskAppVo.unitPrice}元"//任务金额
                holder.tv_task_earnedCount.text = "${items[position].taskAppVo.earnedCount}人已赚"//多人已赚
                holder.tv_task_rest.text = "剩余${items[position].taskAppVo.rest}个"//剩余数

            }

        }

    }

    private fun getImageView(any: Any): ImageView {
        val imageView = ImageView(context)
        imageView.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ImageViewUtil.load(imageView, any)

        imageView.setOnClickListener {
            ivDialog.dismiss()
        }

        //1.注册菜单
        context.registerForContextMenu(imageView)
        imageView.setOnLongClickListener {
            //显示选项  保存图
            //2.打开菜单
            context.openContextMenu(imageView)
            ShareUtil.putString(ShareUtil.APP_TASK_PIC_DOWN_URL, any.toString())
            true
        }
        return imageView
    }

}