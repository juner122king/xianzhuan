package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.adapters.TaskDetailsStepAdapter
import com.lelezu.app.xianzhuan.ui.adapters.TaskVerifyStepAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel2
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils

class TaskDetailsActivity : BaseActivity(), OnClickListener {

    private lateinit var ivDialog: Dialog

    private lateinit var taskDetailsRV: RecyclerView //步骤列表
    private lateinit var taskVerifyRV: RecyclerView //验证列表


    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((application as MyApplication).taskRepository)
    }

    private val lvModel: LoginViewModel2 by viewModels {
        LoginViewModel2.LoginViewFactory((application as MyApplication).userRepository)
    }

    private lateinit var adapterDetails: TaskDetailsStepAdapter//步骤列表
    private lateinit var adapterVerify: TaskVerifyStepAdapter//步骤列表


    private lateinit var taskId: String//任务ID

    private var aStatus = 0 //任务状态
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        taskId = intent.getStringExtra("taskId")!!

        //开始--示例图打开功能
        ivDialog = Dialog(this, R.style.FullActivity)
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT
        ivDialog.window?.attributes = attributes



        taskDetailsRV = findViewById(R.id.rv_task_step)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapterDetails = TaskDetailsStepAdapter(emptyList(), ivDialog, this)
        taskDetailsRV.adapter = adapterDetails
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        taskDetailsRV.layoutManager = LinearLayoutManager(this)



        taskVerifyRV = findViewById(R.id.rv_task_verify)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapterVerify = TaskVerifyStepAdapter(emptyList(), ivDialog, this)
        taskVerifyRV.adapter = adapterVerify
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        taskVerifyRV.layoutManager = LinearLayoutManager(this)


        //监听任务信息变化
        homeViewModel.task.observe(this) {
            //初始化页面数据
            setData(it)

        }

        homeViewModel.getTaskDetails(getTaskDId()!!)

        homeViewModel.isApply.observe(this) {
            ToastUtils.showToast(this, if (it) "报名成功" else "报名失败", 0)
        }

        //换任务 监听
        homeViewModel.shuffleList.observe(this) {

            homeViewModel.getTaskDetails(it[0].taskId)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setData(task: Task) {
        putTaskDId(task.taskId)
        val string = "${task.earnedCount}人已完成任务，任务可报名${task.limitTimes}次"
        aStatus = task.auditStatus//
        var tvBtm1 = ""
        var tvBtm2 = "换个任务"
        when (aStatus) {
            0 -> {
                tvBtm1 = "立即报名"
                findViewById<TextView>(R.id.tv_btm2).setOnClickListener(this)
                findViewById<TextView>(R.id.tv_btm1).setOnClickListener(this)
            }

            1 -> {
                tvBtm1 = "提交"
            }

            2 -> {
                tvBtm1 = "审核中"
            }
        }
        findViewById<TextView>(R.id.tv_btm2).text = tvBtm1

        findViewById<TextView>(R.id.tv_task_title).text = task.taskTitle //任务标题
        findViewById<TextView>(R.id.tv_task_des_c).text = task.taskDesc //任务说明
        findViewById<TextView>(R.id.tv_time).text = "限时${task.operateTime}小时完成" //
        findViewById<TextView>(R.id.tv_nub).text = "剩余${task.rest}单" //
        findViewById<TextView>(R.id.tv_shang_ji).text = "${task.unitPrice}元" //

        findViewById<TextView>(R.id.tv_info).text = string




        adapterDetails.updateData(task.taskStepList)
        adapterVerify.updateData(task.taskUploadVerifyList)

//        lvModel.getUserInfo(task.userId)//获取商家信息
//        lvModel.userInfo.observe(this) {
//
//            ImageViewUtil.load(
//                findViewById(R.id.iv_user_pic), it?.headImageUrl ?: String
//            )
//            findViewById<TextView>(R.id.tv_name).text = it!!.nickname
//        }


    }


    override fun onCreateContextMenu(
        menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(0, 1, 0, "保存")
        menu?.add(0, 2, 1, "取消")
        0
        menu!!.getItem(0).setOnMenuItemClickListener {
            Toast.makeText(
                this,
                "保存图片：${ShareUtil.getString(ShareUtil.APP_TASK_PIC_DOWN_URL)}",
                Toast.LENGTH_SHORT
            ).show()
            //进行保存图片操作
            true
        }

        menu!!.getItem(1).setOnMenuItemClickListener {
            ivDialog.dismiss()
            true
        }

    }


    override fun getLayoutId(): Int {

        return R.layout.activity_task_details
    }

    override fun getContentTitle(): String? {
        return getString(R.string.title_task_details)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_btm1 -> homeViewModel.getShuffle()//换个任务
            R.id.tv_btm2 -> homeViewModel.apiTaskApply(getTaskDId()!!)//报名
        }
    }

    private fun getTaskDId(): String? {
        return taskId
    }

    private fun putTaskDId(id: String) {
        taskId = id
    }

//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == RESULT_OK) {
//            val uri: Uri? = data?.data
//            adapter.notifyItemChanged(data!!.getIntExtra("position", -1))//刷新对应项
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//
//    }

//    private fun selectPic(position: Int) {
//        //打开系统图片
//        // 打开相机相册
//        // 在Activity Action里面有一个“ACTION_GET_CONTENT”字符串常量，
//        // 该常量让用户选择特定类型的数据，并返回该数据的URI.我们利用该常量，
//        // 然后设置类型为“image/*”，就可获得Android手机内的所有image。*/
//        // 打开相机相册
//        // 在Activity Action里面有一个“ACTION_GET_CONTENT”字符串常量，
//        // 该常量让用户选择特定类型的数据，并返回该数据的URI.我们利用该常量，
//        // 然后设置类型为“image/*”，就可获得Android手机内的所有image。*/
//        val intent = Intent()/* 开启Pictures画面Type设定为image *//* 开启Pictures画面Type设定为image */
//
//        intent.type =
//            "image/*"/* 使用Intent.ACTION_GET_CONTENT这个Action *//* 使用Intent.ACTION_GET_CONTENT这个Action */
//        intent.action = Intent.ACTION_GET_CONTENT/* 取得相片后返回本画面 *//* 取得相片后返回本画面 */
//
//        intent.putExtra("position",position)//位置
//
//        activity.startActivityForResult(intent, 1)
//    }
}