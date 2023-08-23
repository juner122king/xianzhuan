package com.lelezu.app.xianzhuan.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.ContextMenu
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.adapters.TaskDetailsStepAdapter
import com.lelezu.app.xianzhuan.ui.adapters.TaskVerifyStepAdapter
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.TAGMYTASK

class TaskDetailsActivity : BaseActivity(), OnClickListener {


    //vip头像等级框图片
    private var pic = mapOf(
        1 to R.drawable.icon_head1, 2 to R.drawable.icon_head, 4 to R.drawable.icon_head2
        // 可以继续添加其他映射关系
    )
    private lateinit var ivDialog: Dialog


    private lateinit var taskDetailsRV: RecyclerView //步骤列表
    private lateinit var taskVerifyRV: RecyclerView //验证列表


    private lateinit var adapterDetails: TaskDetailsStepAdapter//步骤列表
    private lateinit var adapterVerify: TaskVerifyStepAdapter//验证列表

    private lateinit var task: Task


    private var isMyTask: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isMyTask = intent.getBooleanExtra(TAGMYTASK, false)//是否为我的任务详情，默认不是


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

        findViewById<View>(R.id.tv_agreement).setOnClickListener(this)



        //获取上个页面返回的TaskId再请求一次
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
            showToast(if (it) "报名成功" else "报名失败")
            if (it) {
                isMyTask = true//报名成功后，页面UI变化逻辑变为我的任务详情逻辑
                taskDetails(task.taskId,task.applyLogId)
            }
        }


        //检查图片权限
        checkPermissionRead()

    }

    private fun taskDetails(taskId: String,applyId:String?=null) {
        homeViewModel.getTaskDetails(taskId,applyId)
    }


    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setData(task: Task) {

        LogUtils.d("auditStatus=${task.auditStatus}  ,taskStatus=${task.taskStatus}")
        LogUtils.d(task.toString())
        putTask(task)

        changeView(task)//根据任务状态id改变页面

        findViewById<TextView>(R.id.tv_task_title).text = task.taskTitle //任务标题
        findViewById<TextView>(R.id.tv_task_des_c).text = task.taskDesc //任务说明


        val text1 =
            "<font color='#999999'>限时</font><font color='#FF5431'>${task.deadlineTime}</font><font color='#999999'>小时完成</font>"
        val text2 =
            "<font color='#999999'>剩余</font><font color='#FF5431'>${task.rest}</font><font color='#999999'>单</font>"

        val string = "${task.earnedCount}人已完成任务，任务可报名${task.limitTimes}次"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            findViewById<TextView>(R.id.tv_time).text =
                Html.fromHtml(text1, Html.FROM_HTML_MODE_COMPACT)
            findViewById<TextView>(R.id.tv_nub).text =
                Html.fromHtml(text2, Html.FROM_HTML_MODE_COMPACT)
        } else {
            findViewById<TextView>(R.id.tv_time).text = Html.fromHtml(text1)
            findViewById<TextView>(R.id.tv_nub).text = Html.fromHtml(text2)
        }

        findViewById<TextView>(R.id.tv_shang_ji).text = "${task.unitPrice}元" //

        findViewById<TextView>(R.id.tv_user_vip).text = "分享赚${task.shareAmount}元" //

        findViewById<TextView>(R.id.tv_info).text = string


        adapterDetails.updateData(task.taskStepList)
        adapterVerify.updateData(task.taskUploadVerifyList, task.auditStatus)

        loginViewModel.getUserInfo(task.userId)//获取商家信息
        loginViewModel.userInfo.observe(this) {

            ImageViewUtil.loadCircleCrop(
                findViewById(R.id.iv_user_pic), it?.headImageUrl ?: String
            )

            findViewById<ImageView>(R.id.iv_user_pic2).background = pic[it.vipLevel]?.let { it1 ->
                getDrawable(
                    it1
                )
            }
            findViewById<TextView>(R.id.tv_name).text = it!!.nickname
        }


        //支持设备 ["0"]-安卓, ["1"]-苹果, []-全部
        if (task.supportDevices.size == 1) {
            when (task.supportDevices[0]) {
                "0" -> findViewById<View>(R.id.iv_and).visibility = View.VISIBLE
                else -> findViewById<View>(R.id.iv_ios).visibility = View.VISIBLE
            }
        } else {
            findViewById<View>(R.id.iv_and).visibility = View.VISIBLE
            findViewById<View>(R.id.iv_ios).visibility = View.VISIBLE
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

        menu.getItem(1).setOnMenuItemClickListener {
            ivDialog.dismiss()
            true
        }

    }


    private fun changeView(task: Task) {

//        if (isMyTask) {  //我的任务详情，根据auditStatus改变UI
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
//        }else{
//            //任务大厅的任务详情
//            findViewById<View>(R.id.ll_status).visibility = View.GONE
//            findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
//            setBto2Text(getString(R.string.btm_hgrw), getString(R.string.btm_ljbm))
//
//        }
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
                    0 -> homeViewModel.apiTaskApply(getTask().taskId)//报名
                    else -> getTaskSubmit()//提交
                }
            }

            R.id.tv_agreement -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link14)
                intent.putExtra(WebViewSettings.URL_TITLE, "接单规则")
                startActivity(intent)
            }
        }
    }


    //判断用户是否填数据完数据
    private fun getTaskSubmit() {

        homeViewModel.apiTaskSubmit(getTask().applyLogId, adapterVerify.getItems())
        homeViewModel.isUp.observe(this) {
            if (it) {
                showToast("提交成功")
                finish()
            } else {
                showToast("提交失败！")
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


}