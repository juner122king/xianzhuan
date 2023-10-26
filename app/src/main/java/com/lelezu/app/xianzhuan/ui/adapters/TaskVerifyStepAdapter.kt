package com.lelezu.app.xianzhuan.ui.adapters

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskUploadVerify
import com.lelezu.app.xianzhuan.ui.views.BaseActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
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
    private var activity: BaseActivity,
    private val pickImageContract: ActivityResultLauncher<Unit>,
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
        val step: TextView = itemView.findViewById(R.id.tv_step)
        val idEt: EditText = itemView.findViewById(R.id.et_id)
        val ivCasePic: ImageView = itemView.findViewById(R.id.iv_case_pic)//示例图片
        val fCasePic: View = itemView.findViewById(R.id.f_case_pic)//示例图片区域
        val ivUserPic: ImageView = itemView.findViewById(R.id.iv_user_up_pic)//用户图片
        val btmUpPic: View = itemView.findViewById(R.id.tv_up_pic)//上传图片按键
        val line: View = itemView.findViewById(R.id.line)//垂直线
        val tvTr: TextView = itemView.findViewById(R.id.tv_tr)//角标说明

        val viewUrl: View = itemView.findViewById(R.id.tv_web_url)//链接区域
    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item__task_details_step, parent, false)
        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.tvTr.text = "示例图"
        val item = items[position]
        holder.title.text = item.verifyDesc
        holder.step.text = ("${position + 1}").toString()
        holder.viewUrl.visibility = View.GONE

        when (item.verifyType) {//验证步骤类型

            1 -> {
                holder.fCasePic.visibility = View.VISIBLE
                holder.ivUserPic.visibility = View.VISIBLE
                holder.idEt.visibility = View.GONE
                ImageViewUtil.load(holder.ivCasePic, item.useCaseImage)
                ImageViewUtil.load(holder.ivUserPic, item.uploadValue)
                holder.ivCasePic.setOnClickListener {//图片全屏显示
                    ivDialog.setContentView(getImageView(item.useCaseImage))
                    ivDialog.show()
                }


                if (this.auditStatus == 0 || this.auditStatus == 3) {//未报名
                    holder.btmUpPic.visibility = View.GONE
                    holder.ivUserPic.visibility = View.GONE

                } else {
                    holder.ivUserPic.visibility = View.VISIBLE
                    holder.btmUpPic.visibility = View.VISIBLE
                    holder.btmUpPic.setOnClickListener {
                        mPosition = holder.adapterPosition//保存选中的Position

                        onPickImage()

                    }
                }
            }

            2 -> {
                holder.idEt.visibility = View.VISIBLE
                holder.idEt.hint = item.verifyDesc
                holder.idEt.setText(item.uploadValue)
                holder.fCasePic.visibility = View.GONE
                holder.ivUserPic.visibility = View.GONE

                when (this.auditStatus) {

                    0 -> {
                        holder.idEt.isEnabled = false
                    }

                    3 -> {
                        holder.idEt.isEnabled = false
                    }

                    else -> {
                        holder.idEt.isEnabled = true
                        holder.idEt.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence?, start: Int, count: Int, after: Int
                            ) {
                                // 在文本改变之前调用
                            }

                            override fun onTextChanged(
                                s: CharSequence?, start: Int, before: Int, count: Int
                            ) {
                                // 在文本改变时调用
                            }

                            override fun afterTextChanged(s: Editable?) {
                                // 在文本改变之后调用
                                if (s != null) {
                                    val text = s.toString()
//                                    ToastUtils.show("文本改变")
                                    item.uploadValue = text //更新用户输入的值
                                }
                            }
                        })
                    }

                }
            }

            0 -> {
                holder.idEt.visibility = View.GONE
                holder.fCasePic.visibility = View.GONE
                holder.ivUserPic.visibility = View.GONE
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
            ShareUtil.putString(ShareUtil.APP_TASK_PIC_DOWN_URL, any.toString())
            true
        }
        return imageView
    }


    private fun onPickImage() {

        MyPermissionUtil.openAlbumApply(activity, object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                //获取权限成功
                //打开相册
                pickImageContract.launch(Unit)
            }

            override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                //权限失败
                ToastUtils.show("您已拒绝授权，相册打开失败！")
            }
        })
    }

    fun setLink(link: String) {
        items[mPosition].uploadValue = link
        notifyItemChanged(mPosition)

    }

    fun getItems(): List<TaskUploadVerify> {
        return items
    }
}
