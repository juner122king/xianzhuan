package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.data.model.TaskSubmit
import com.lelezu.app.xianzhuan.ui.adapters.TaskDetailsStepAdapter
import com.lelezu.app.xianzhuan.ui.adapters.TaskVerifyStepAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel2
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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
    private lateinit var adapterVerify: TaskVerifyStepAdapter//验证列表

    private lateinit var task: Task
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        adapterVerify = TaskVerifyStepAdapter(emptyList(), ivDialog, this, homeViewModel)
        taskVerifyRV.adapter = adapterVerify
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        taskVerifyRV.layoutManager = LinearLayoutManager(this)

        //底部两个按键
        findViewById<TextView>(R.id.tv_btm1).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_btm2).setOnClickListener(this)







        taskDetails(intent.getStringExtra("taskId")!!)
        //监听任务信息变化
        homeViewModel.task.observe(this) {
            //初始化页面数据
            setData(it)
        }
        //换任务 监听
        homeViewModel.shuffleList.observe(this) {

            taskDetails(it[0].taskId)
        }


        //报名监听
        homeViewModel.isApply.observe(this) {
            ToastUtils.showToast(this, if (it) "报名成功" else "报名失败", 0)
            if (it) {
                finish()
            }
        }

        //错误信息监听
        homeViewModel.errMessage.observe(this) {
            ToastUtils.showToast(this, it, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        openPhoto()
    }

    private fun taskDetails(taskId: String) {

        homeViewModel.getTaskDetails(taskId)
    }


    @SuppressLint("SetTextI18n")
    private fun setData(task: Task) {
        putTask(task)

        changeView(task)//根据任务状态id改变页面


        val string = "${task.earnedCount}人已完成任务，任务可报名${task.limitTimes}次"

        findViewById<TextView>(R.id.tv_task_title).text = task.taskTitle //任务标题
        findViewById<TextView>(R.id.tv_task_des_c).text = task.taskDesc //任务说明
        findViewById<TextView>(R.id.tv_time).text = "限时${task.operateTime}小时完成" //
        findViewById<TextView>(R.id.tv_nub).text = "剩余${task.rest}单" //
        findViewById<TextView>(R.id.tv_shang_ji).text = "${task.unitPrice}元" //

        findViewById<TextView>(R.id.tv_info).text = string


        adapterDetails.updateData(task.taskStepList)
        adapterVerify.updateData(task.taskUploadVerifyList, task.auditStatus)

        lvModel.getUserInfo(task.userId)//获取商家信息
        lvModel.userInfo.observe(this) {

            ImageViewUtil.load(
                findViewById(R.id.iv_user_pic), it?.headImageUrl ?: String
            )
            findViewById<TextView>(R.id.tv_name).text = it!!.nickname
        }

    }


    override fun onCreateContextMenu(
        menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(0, 1, 0, "保存")
        menu?.add(0, 2, 1, "取消")

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


    private fun changeView(task: Task) {
//        setTitleText("状态：${task.auditStatus} TID：${task.taskId}")
        when (task.auditStatus) {
            //	任务状态(0-未报名，1-待提交，2-审核中，3-审核通过，4-审核被否，5-已取消，默认：0-未报名)
            0 -> {
                findViewById<View>(R.id.ll_status).visibility = View.GONE
                findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                setBto2Text(getString(R.string.btm_hgrw), getString(R.string.btm_ljbm))
            }

            1 -> {
                findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                findViewById<View>(R.id.ll_status).visibility = View.GONE
                setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_ljtj))
            }

            2 -> {
                findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                setStatusText("状态：审核中")
                setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_xgtj))
            }

            3 -> {
                findViewById<View>(R.id.ll_btm).visibility = View.GONE
                setStatusText("状态：审核通过")
            }

            4 -> {
                setStatusText("状态：审核不通过")
                findViewById<TextView>(R.id.tv_status_text).text = "原因：${task.rejectReason}"
                findViewById<TextView>(R.id.tv_status_text).visibility = View.VISIBLE
                setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_xgtj))
                findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
            }

            5 -> {
                setStatusText("手动取消")
                setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_zctj))
                findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
            }


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
            R.id.tv_btm1 -> {
                when (getTask().auditStatus) {
                    0 -> homeViewModel.getShuffle()//换个任务
                    else -> Toast.makeText(
                        this, "功能开发中...", Toast.LENGTH_SHORT
                    ).show() //取系雇主
                }

            }

            R.id.tv_btm2 -> {
                when (getTask().auditStatus) {
                    0 -> homeViewModel.apiTaskApply(getTask().taskId!!)//报名
                    else -> getTaskSubmit()//提交
                }


            }
        }
    }


    //判断用户是否填数据完数据
    private fun getTaskSubmit() {

        homeViewModel.apiTaskSubmit(getTask().applyLogId, adapterVerify.getItems())
        homeViewModel.isUp.observe(this) {
            if (it) {
                ToastUtils.showToast(this, "提交成功", 0)
                finish()
            } else {
                ToastUtils.showToast(this, "提交失败！", 0)
            }
        }


    }


    private fun putTask(t: Task) {
        task = t//
    }

    private fun getTask(): Task {
        return task
    }


    private fun setStatusText(str: String) {
        findViewById<View>(R.id.ll_status).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_status).text = str
    }


    //设置底部button文字
    private fun setBto2Text(str1: String, str2: String) {
        findViewById<TextView>(R.id.tv_btm1).text = str1
        findViewById<TextView>(R.id.tv_btm2).text = str2
    }


    private val rc: Int = 123
    private fun openPhoto() {
        // 检查图片权限
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 请求权限
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), rc
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == rc) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，继续进行文件操作
                //上传图片，打开相册
            } else {
                // 用户拒绝了权限，处理拒绝权限的情况
                ToastUtils.showToast(this, "没有读取图片权限，请退出重试！", 0)
            }
        }
    }

}