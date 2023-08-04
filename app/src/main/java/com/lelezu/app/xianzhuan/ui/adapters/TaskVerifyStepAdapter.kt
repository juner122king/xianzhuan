package com.lelezu.app.xianzhuan.ui.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskUploadVerify
import com.lelezu.app.xianzhuan.ui.views.BaseActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil

/**
 * @author:Administrator
 * @date:2023/7/27 0027
 * @description: 任务详情
 *
 */
class TaskVerifyStepAdapter(
    private var items: List<TaskUploadVerify>,
    private var ivDialog: Dialog,
    private var activity: BaseActivity
) : RecyclerView.Adapter<TaskVerifyStepAdapter.ItemViewHolder>() {

    private var auditStatus = 0//任务状态，用改变UI

    private var mPosition: Int = -0 //当前选中的


    // 更新数据方法
    fun updateData(newItems: List<TaskUploadVerify>, status: Int) {
        items = newItems  //任务步骤集合
        this.auditStatus = status
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_step_text)
        val idEt: EditText = itemView.findViewById(R.id.et_id)
        val ivCasePic: ImageView = itemView.findViewById(R.id.iv_case_pic)//示例图片
        val ivUserPic: ImageView = itemView.findViewById(R.id.iv_user_up_pic)//用户图片
        val btmUpPic: View = itemView.findViewById(R.id.tv_up_pic)//上传图片按键
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


        if (item.verifyType == 1) {//验证步骤是否为图片类型

            holder.ivCasePic.visibility = View.VISIBLE
            holder.ivUserPic.visibility = View.VISIBLE
            holder.idEt.visibility = View.GONE


            if (this.auditStatus == 0 || this.auditStatus == 3) {//未报名
                holder.btmUpPic.visibility = View.GONE
                holder.ivUserPic.visibility = View.GONE
            } else {
                holder.ivUserPic.visibility = View.VISIBLE
                holder.btmUpPic.visibility = View.VISIBLE
                holder.btmUpPic.setOnClickListener {
                    //上传图片，打开相册
                    pickImageContract.launch(Unit)
                    mPosition = holder.adapterPosition
                }
            }
            ImageViewUtil.load(holder.ivCasePic, item.useCaseImage)
            ImageViewUtil.load(holder.ivUserPic, item.uploadImage)
            holder.ivCasePic.setOnClickListener {//图片全屏显示
                ivDialog.setContentView(getImageView(item.useCaseImage))
                ivDialog.show()
            }


        } else {
            holder.idEt.visibility = View.VISIBLE
            holder.ivCasePic.visibility = View.GONE
            holder.ivUserPic.visibility = View.GONE
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
            activity.openContextMenu(imageView)
            ShareUtil.putString(ShareUtil.APP_TASK_PIC_DOWN_URL, any.toString())
            true
        }
        return imageView
    }


    private val pickImageContract = activity.registerForActivityResult(PickImageContract()) {
        if (it != null) {
            // 可以根据需要更新对应的数据项或刷新整个列表
            items[mPosition].uploadImage = it.toString()
            notifyItemChanged(mPosition)
        }
    }


    //处理选择图片的请求和结果
    inner class PickImageContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) {
                return intent?.data
            }
            return null
        }
    }

    public fun getItems(): List<TaskUploadVerify> {
        return items
    }
}
