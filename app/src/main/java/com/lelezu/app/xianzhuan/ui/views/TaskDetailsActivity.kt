package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.StringUtils.statusMap
import com.lelezu.app.xianzhuan.ui.adapters.TaskDetailsStepAdapter
import com.lelezu.app.xianzhuan.ui.adapters.TaskLongVerifyStepAdapter
import com.lelezu.app.xianzhuan.ui.adapters.TaskVerifyStepAdapter
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil.TAGMYTASK
import java.text.SimpleDateFormat
import java.util.Date

class TaskDetailsActivity : BaseActivity(), OnClickListener {

    private lateinit var err_view: View
    private lateinit var onNetwork_view: View//有网络时显示的页面
    private lateinit var iv_but_re: View //刷新按钮

    private val tasklimitTimesMap = mapOf(
        1 to "每日1次",
        2 to "每人1次",
        3 to "每人3次",
        // 可以继续添加其他映射关系
    )


    private lateinit var countDownTimer: CountDownTimer //提交时间倒计时

    //vip头像等级框图片
    private var pic = mapOf(
        1 to R.drawable.icon_head1, 2 to R.drawable.icon_head, 4 to R.drawable.icon_head2
        // 可以继续添加其他映射关系
    )

    private lateinit var taskDetailsRV: RecyclerView //步骤列表
    private lateinit var taskVerifyRV: RecyclerView //验证列表
    private lateinit var taskLVerifyRV: RecyclerView //长任务验证列表

    private lateinit var adapterDetails: TaskDetailsStepAdapter//步骤列表
    private lateinit var adapterVerify: TaskVerifyStepAdapter//验证列表

    private lateinit var adapterLVerify: TaskLongVerifyStepAdapter//长任务验证列表

    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件


    private lateinit var task: Task

    private var isMyTask: Boolean = false

    private var isDoneTask: Boolean = false//是否加载完成任务页面

    private lateinit var dialog: Dialog//协议弹窗
    private lateinit var dialog2: Dialog//报名成功弹窗
    private lateinit var dialog3: Dialog//举报弹窗

    private lateinit var dialog4: Dialog//禁止报名弹窗

    private lateinit var comdown: TextView//任务提交倒计时

    private lateinit var tv_sub: TextView //关注数
    private lateinit var tv_fan: TextView //粉丝数

    private var applyTs: Int = -1//报名时当前任务taskStatus


    private var isLongTask: Boolean = false//当前加载的任务是否为长单任务
    private var longActionStep: Int = -1//当前长单任务操作步骤

    //验证图片选取回调处理
    private val pickImageContract = registerForActivityResult(PickImageContract()) {
        if (it != null) {
            // 获取内容URI对应的文件路径
            // 创建一个 Handler，关联到主线程的 Looper
            val handler = Handler(Looper.getMainLooper())
            // 在后台线程中执行上传逻辑
            val thread = Thread {
                handler.post {
                    // 在主线程中调用 showLoading()
                    showToast("图片上传中...")
                    showLoading()
                }
                homeViewModel.apiUpload(it, isLongTask)
            }
            thread.start()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initObserve()

        checkNetWorkView()


    }

    override fun onResume() {
        super.onResume()

        if (checkMiniSub()) {//是否加载完成任务页面 且该任务是小程序任务 且已经报名 且已关注小程序
            ToastUtils.show("执行小程序任务校验")
            homeViewModel.miniTaskComplete(getTask().applyLogId)//校验小程序任务是否完成
        }


    }


    private fun initView() {
        setBarBackGrounds(R.color.topViewBg)//设置状态栏颜色

        titleColor(R.color.white)
        bgColor(R.color.topViewBg)
        setIconBack(R.color.white)

        err_view = findViewById(R.id.err_view)
        iv_but_re = findViewById(R.id.iv_but_re)

        onNetwork_view = findViewById(R.id.ll_on_network)

        swiper = findViewById(R.id.swiper)
        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {

            //检查 task 属性是否已初始化
            if (::task.isInitialized) {
                refresh()// 执行刷新操作
            }

        }

        taskDetailsRV = findViewById(R.id.rv_task_step)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapterDetails = TaskDetailsStepAdapter(emptyList(), ivDialog, this)
        taskDetailsRV.adapter = adapterDetails
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        taskDetailsRV.layoutManager = LinearLayoutManager(this)


        taskVerifyRV = findViewById(R.id.rv_task_verify)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapterVerify = TaskVerifyStepAdapter(emptyList(), ivDialog, this, pickImageContract)

        taskVerifyRV.adapter = adapterVerify
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        taskVerifyRV.layoutManager = LinearLayoutManager(this)




        taskLVerifyRV = findViewById(R.id.rv_task_l_verify)
        // 创建适配器，并将其绑定到 RecyclerView 上
        adapterLVerify = TaskLongVerifyStepAdapter(emptyList(), ivDialog, this, pickImageContract)
        taskLVerifyRV.adapter = adapterLVerify
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        taskLVerifyRV.layoutManager = LinearLayoutManager(this)


        //底部两个按键
        findViewById<TextView>(R.id.tv_btm1).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_btm2).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_btm3).setOnClickListener(this)//举报按钮

        findViewById<View>(R.id.tv_agreement).setOnClickListener(this)
        findViewById<View>(R.id.tv_agreement2).setOnClickListener(this)

        findViewById<View>(R.id.tv_user_vip).setOnClickListener(this)

        tv_sub = findViewById(R.id.tv_sub)
        tv_fan = findViewById(R.id.tv_fan)
        comdown = findViewById(R.id.tv_comdown)

        isMyTask = intent.getBooleanExtra(TAGMYTASK, false)//是否为我的任务详情，默认不是


        //从H5唤起
        var taskIdH5 = getH5Query(intent)
        if (taskIdH5 != null) {
            //获取上个页面返回的TaskId请求一次
            taskDetails(taskIdH5)
        } else {
            //获取上个页面返回的TaskId请求一次
            taskDetails(intent.getStringExtra("taskId")!!, intent.getStringExtra("applyLogId"))
        }

    }

    private fun initObserve() {
        //监听任务信息变化
        homeViewModel.task.observe(this) {
            LogUtils.i("长任务debug", "获取任务详情接口回调")
            // 停止刷新动画
            swiper.isRefreshing = false
            //初始化页面数据
            setData(it)
        }
        //换任务 监听
        homeViewModel.shuffleList.observe(this) {
            hideLoading()
            taskDetails(it[0].taskId)
        }

        //报名监听
        homeViewModel.isApply.observe(this) {
            hideLoading()
            LogUtils.i("长任务debug", "报名接口回调 it:$it")
            if (it) {
                isMyTask = true//报名成功后，页面UI变化逻辑变为我的任务详情逻辑

                showBmDialog("${getTask().deadlineTime}")//弹窗显示

                taskDetails(getTask().taskId, getTask().applyLogId)


            } else showToast("报名失败")
        }

        //举报监听
        homeViewModel.isReport.observe(this) {
            hideLoading()
            if (it) {
                showToast("举报成功")

            } else showToast("举报失败")
        }

        //任务提交监听
        homeViewModel.isUp.observe(this) {
            if (it) {
                showToast("提交成功，雇主将在24小时内审核。")
                finish()
                goToMyTask()
            } else {
                showToast("提交失败！")
            }
        }

        //验证图片选取监听
        homeViewModel.upLink.observe(this) { link ->

            hideLoading()
            cancelToast()
            showToast("图片上传成功")
            adapterVerify.setLink(link)
        }

        //长任务验证图片选取监听
        homeViewModel.upltLink.observe(this) { link ->

            hideLoading()
            cancelToast()
            showToast("图片上传成功")
            adapterLVerify.getAdapterVerify(getLongActionStep()).setLink(link)
        }

        loginViewModel.follow.observe(this) {
            hideLoading()

            tv_fan.text = "${it.fanCnt}粉丝"
            tv_sub.text = "${it.concernCnt}关注"

        }

        //小程序校验接口监听
        homeViewModel.isCompleteMini.observe(this) {

            if (it) {
                //执行完成，刷新详情页面
                taskDetails(getTask().taskId, getTask().applyLogId)
            }
        }

        homeViewModel.limit.observe(this) {
            if (it.isLimit) {
                applyTs = getTask().auditStatus
                LogUtils.i("长任务debug", "执行报名接口")
                homeViewModel.apiTaskApply(getTask().taskId) //报名
            } else {
                showPostDialog(it.endTime)
            }
        }


    }


    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setData(task: Task) {

        putTask(task)
        //需要作长 短任务区分  以 task.longTaskVos 是否为空作依据  不为空则为长任务
        task.longTaskVos?.let { longTaskVos ->
            // 在这里，longTaskVos 不为 null，可以安全地访问它的属性或方法
            adapterLVerify.updateData(longTaskVos)



            taskLVerifyRV.visibility = View.VISIBLE
            taskVerifyRV.visibility = View.GONE

            //显示长单任务icon
            findViewById<View>(R.id.iv_lt_icon).visibility = View.VISIBLE
            isLongTask = true


        } ?: run {
            // 在这里，longTaskVos 为 null，执行相应的操作
            // 例如，可以在这里处理 longTaskVos 为 null 的情况
            // 或者执行其他操作

            adapterVerify.updateData(task.taskUploadVerifyList, task.auditStatus)
            taskVerifyRV.visibility = View.VISIBLE
            taskLVerifyRV.visibility = View.GONE

            //隐藏长单任务icon
            findViewById<View>(R.id.iv_lt_icon).visibility = View.GONE
            isLongTask = false

        }

        changeView(task)//根据任务状态id改变页面

        findViewById<TextView>(R.id.tv_task_title).text = task.taskTitle //任务标题
        findViewById<TextView>(R.id.tv_task_des_c).text = task.taskDesc //任务说明
        val text1 =
            "<font color='#999999'>限时</font><font color='#FF5431'>${task.deadlineTime}</font><font color='#999999'>小时完成</font>"
        val text2 =
            "<font color='#999999'>剩余</font><font color='#FF5431'>${task.rest}</font><font color='#999999'>单</font>"

        val string = "${task.earnedCount}人已完成任务，${tasklimitTimesMap[task.limitTimes]}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            findViewById<TextView>(R.id.tv_time).text =
                Html.fromHtml(text1, Html.FROM_HTML_MODE_COMPACT)
            findViewById<TextView>(R.id.tv_nub).text =
                Html.fromHtml(text2, Html.FROM_HTML_MODE_COMPACT)
        } else {
            findViewById<TextView>(R.id.tv_time).text = Html.fromHtml(text1)
            findViewById<TextView>(R.id.tv_nub).text = Html.fromHtml(text2)
        }

        findViewById<TextView>(R.id.tv_shang_ji).text = "${task.unitPrice}" //

        findViewById<TextView>(R.id.tv_user_vip).text = "分享赚${task._shareAmount}" //

        findViewById<TextView>(R.id.tv_info).text = string


        adapterDetails.updateData(task.taskStepList, task.auditStatus, task.taskPlatform)




        loginViewModel.getUserInfo(task.userId)//获取商家信息
        loginViewModel.userInfo.observe(this) {

            ImageViewUtil.loadCircleCrop(
                findViewById(R.id.iv_user_pic), it?.headImageUrl ?: String,
            )

            findViewById<ImageView>(R.id.iv_user_pic2).background = pic[it.vipLevel]?.let { it1 ->
                getDrawable(
                    it1
                )
            }
            findViewById<TextView>(R.id.tv_name).text = it!!.nickname
        }

        // 支持设备 ["0"]-安卓, ["1"]-苹果, []-全部
        if (task.supportDevices.size == 1) {
            when (task.supportDevices[0]) {
                "0" -> findViewById<View>(R.id.iv_and).visibility = View.VISIBLE
                else -> findViewById<View>(R.id.iv_ios).visibility = View.VISIBLE
            }
        } else {
            findViewById<View>(R.id.iv_and).visibility = View.VISIBLE
            findViewById<View>(R.id.iv_ios).visibility = View.VISIBLE
        }


        if (task.operateTime != null && task.operateTime != "null" && task.auditStatus != 3) { //审核通过就不显示

            // 开始定时任务
            startCountdown()
            comdown.visibility = View.VISIBLE
        } else comdown.visibility = View.GONE


        //点击雇主头像
        findViewById<View>(R.id.f_user_pic).setOnClickListener {

            val intent = Intent(this, MasterActivity::class.java)
            intent.putExtra("userId", task.userId)
            startActivity(intent)

        }
        //获取关注和粉丝数
        loginViewModel.follows(task.userId)


        if (getTask().taskType == "2" && getTask().auditStatus == 3) {//小程序任务且 任务状态为审核完成
            ToastUtils.show("任务已完成！")
        }


    }

    private fun taskDetails(taskId: String, applyId: String? = null) {


        homeViewModel.getTaskDetails(taskId, applyId)
    }

    private fun startCountdown() {
        // 设置目标日期时间
        val targetDateTime = getTask().operateTime
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val targetDate = dateFormat.parse(targetDateTime).time

        // 计算倒计时时间差（毫秒）
        val currentTime = System.currentTimeMillis()
        val timeDifference = targetDate - currentTime
        // 创建并启动倒计时
        countDownTimer = object : CountDownTimer(timeDifference, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                //添加完成任务提交验收倒计时
                comdown.text =
                    "完成任务提交验收剩余：" + calculateRemainingTime(getTask().operateTime.toString())
            }

            override fun onFinish() {
                comdown.visibility = View.GONE
            }
        }

        countDownTimer.start()

    }


    //计算剩余时间
    private fun calculateRemainingTime(targetDateTime: String): String {


        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = Date()
        val targetDate = dateFormat.parse(targetDateTime)

        if (currentDate.before(targetDate)) {
            val timeDifference = targetDate.time - currentDate.time
            val hours = timeDifference / 3600000
            val minutes = (timeDifference % 3600000) / 60000
            val seconds = ((timeDifference % 3600000) % 60000) / 1000

            return "${hours}小时${minutes}分${seconds}秒"
        } else {

            return "已过期"
        }
    }

    private fun changeView(task: Task) {
        if (isMyTask) {  //接单规则是否显示
            findViewById<View>(R.id.tv_agreement).visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.tv_agreement).visibility = View.VISIBLE
        }

        if (task.taskPlatform != 1 && task.auditStatus != 0) {//小程序任务不显示底部按钮
            findViewById<View>(R.id.ll_btm).visibility = View.GONE
            findViewById<View>(R.id.ll_status).visibility = View.GONE
        } else {

            isCanSubmit()
            val auditStatusString = statusMap[task.auditStatus]
            when (task.auditStatus) {
                //	任务状态(0-未报名，1-待提交，2-审核中，3-审核通过，4-审核被否，5-已取消，默认：0-未报名)
                0 -> {
                    findViewById<View>(R.id.ll_status).visibility = View.GONE
                    findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                    setBto2Text(getString(R.string.btm_hgrw), getString(R.string.btm_ljbm))
                    findViewById<View>(R.id.tv_btm2).visibility = View.VISIBLE
                }

                1 -> {


                    findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                    findViewById<View>(R.id.ll_status).visibility = View.GONE
                    if (isLongTask) {
                        setBto2Text(
                            getString(R.string.btm_lxgz), "完成奖励${getLongActionStep() + 1}提交"
                        )
                    } else {
                        setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_ljtj))
                    }

                    findViewById<View>(R.id.tv_btm2).visibility = View.VISIBLE
                }

                2 -> {
                    findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                    findViewById<View>(R.id.ll_status).visibility = View.VISIBLE

                    setStatusText("状态：$auditStatusString")
                    setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_xgtj))

                    //新逻辑，审核中不能修改提交
                    findViewById<View>(R.id.tv_btm2).visibility = View.GONE

                }

                3 -> {
                    findViewById<View>(R.id.ll_btm).visibility = View.GONE
                    findViewById<View>(R.id.ll_status).visibility = View.VISIBLE
                    setStatusText("状态：$auditStatusString")


                    val resolvedColor = ContextCompat.getColor(
                        this, R.color.pass
                    )
                    findViewById<TextView>(R.id.tv_status).setTextColor(resolvedColor)
                }

                4 -> {
                    setStatusText("状态：$auditStatusString")
                    findViewById<View>(R.id.ll_status).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tv_status_text).text = "原因：${task.rejectReason}"
                    findViewById<TextView>(R.id.tv_status_text).visibility = View.VISIBLE
                    setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_xgtj))
                    findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                    findViewById<View>(R.id.tv_btm2).visibility = View.VISIBLE
                }

                5, 6 -> {
                    setStatusText("状态：$auditStatusString")
                    setBto2Text(getString(R.string.btm_lxgz), getString(R.string.btm_csbm))
                    findViewById<View>(R.id.ll_status).visibility = View.VISIBLE
                    findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE

                    if (isLongTask) {//长任务隐藏重新报名
                        findViewById<View>(R.id.tv_btm2).visibility = View.INVISIBLE
                    } else findViewById<View>(R.id.tv_btm2).visibility = View.VISIBLE

                    if (task.taskStatus == 5) {//任务已结束
                        setStatusText("状态：任务过期")
                        findViewById<View>(R.id.ll_btm).visibility = View.GONE
                        val resolvedColor = ContextCompat.getColor(
                            this, R.color.red
                        )
                        findViewById<TextView>(R.id.tv_status).setTextColor(resolvedColor)
                    }

                }

                7 -> { //长单任务 进行中

                    findViewById<View>(R.id.ll_btm).visibility = View.VISIBLE
                    findViewById<View>(R.id.tv_btm2).visibility = View.VISIBLE
                    findViewById<View>(R.id.ll_status).visibility = View.VISIBLE
                    setStatusText("状态：$auditStatusString")

                    if (getLongActionStep() == -1) {//没有步骤auditStatus等于1
                        setBto2Text(
                            getString(R.string.btm_lxgz), "待悬赏主审核"
                        )
                    } else {
                        setBto2Text(
                            getString(R.string.btm_lxgz), "完成奖励${getLongActionStep() + 1}提交"
                        )
                    }

                }
            }

        }


    }


    private fun refresh() {
        //获取上个页面返回的TaskId再请求一次

        taskDetails(getTask().taskId, getTask().applyLogId)
    }


    override fun getLayoutId(): Int {

        return R.layout.activity_task_details2
    }

    override fun getContentTitle(): String {
        return getString(R.string.title_task_details)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_btm1 -> {

                when (getTask().auditStatus) {//换个任务
                    0 -> {
                        showLoading()
                        homeViewModel.getShuffle()
                    }

                    else -> {

                        val intent = Intent(this, ChatActivity::class.java)
                        intent.putExtra("userId", getTask().userId)
                        intent.putExtra("taskId", getTask().taskId)
                        startActivity(intent)


                    }
                }
            }

            R.id.tv_btm2 -> {
                when (getTask().auditStatus) {
                    0, 5, 6 -> {
                        //先判断是否可以报名
                        homeViewModel.limitTask() //报名前的验证接口

                    }

                    else -> {
                        //提交
                        if (isLongTask) {
                            isCanSubmit()
                            longTaskReSubmit(getLongActionStep())
                        } else {
                            //普通任务
                            homeViewModel.apiTaskSubmit(
                                getTask().applyLogId, adapterVerify.getItems(), isLongTask
                            )
                        }
                    }
                }
            }

            R.id.tv_btm3 -> {//举报

                showReportDialog(getTask().userId, getTask().taskId)

            }

            R.id.tv_agreement -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link14)
                intent.putExtra(WebViewSettings.URL_TITLE, "接单规则")
                intent.putExtra(WebViewSettings.isProcessing, false)
                startActivity(intent)
            }

            R.id.tv_agreement2 -> {//打开《悬赏任务发布者声明》
                showAgreementDialog()
            }

            R.id.tv_user_vip -> {//H5收徒页面
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link17)
                intent.putExtra(WebViewSettings.TAG, getTask().taskId)
                intent.putExtra(WebViewSettings.URL_TITLE, "接单规则")
                startActivity(intent)
            }
        }
    }


    // 显示用户协议弹窗
    private fun showAgreementDialog() {

        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_agreement)
        val ok: View = dialog.findViewById(R.id.btnAgree)

        ok.setOnClickListener {
            //确定
            dialog.dismiss()
        }
        // 显示弹窗
        dialog.show()

    }

    // 显示报名成功弹窗
    private fun showBmDialog(time: String) {
        dialog2 = Dialog(this)
        dialog2.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog2.setContentView(R.layout.dialog_bmcg)
        val content: TextView = dialog2.findViewById(R.id.tv_content)
        val ok: TextView = dialog2.findViewById(R.id.ok)

        content.text = "请在${time}小时内完成并提交任务"

        ok.setOnClickListener {
            //确定
            dialog2.dismiss()
            //超时任务才跳我的任务页面   或者为重新报名后
            if (applyTs == 6) {
                applyTs = -1
                finish()
                goToMyTask()
            }
            if (getTask().auditStatus == 5 || getTask().auditStatus == 6) {

                finish()
                goToMyTask(1)
            }
        }


        // 显示弹窗
        dialog2.show()
    }


    // 显示举报弹窗
    private fun showReportDialog(userId: String, taskId: String) {
        dialog3 = Dialog(this)
        dialog3.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog3.setContentView(R.layout.dialog_report)

        val ok: TextView = dialog3.findViewById(R.id.ok)
        val cancel: TextView = dialog3.findViewById(R.id.cancel)

        val tVuserId: TextView = dialog3.findViewById(R.id.tv_content)//

        val rb1: RadioButton = dialog3.findViewById(R.id.rb1)//举报内容1
        tVuserId.text = "被举报人ID:${userId}"

        ok.setOnClickListener {
            //确定
            dialog3.dismiss()

            val reportContent =
                if (rb1.isChecked) getString(R.string.task_report_content1) else getString(R.string.task_report_content2)


            LogUtils.i("TD", "userId:${userId},taskId:${taskId}")
            homeViewModel.taskReport(reportContent, userId, taskId) //报名


        }
        cancel.setOnClickListener {
            //确定
            dialog3.dismiss()
        }


        // 显示弹窗
        dialog3.show()
    }

    private fun putTask(t: Task) {
        task = t//
        isDoneTask = true
    }

    private fun getTask(): Task {

        return task
    }


    //判断是否已关注小程序
    private fun checkMiniSub(): Boolean {
        return isDoneTask && getTask().taskType == "2" && getTask().auditStatus == 1 && getTask().taskStepList.any { (it.stepType == 3 && it.hasComplete) || it.stepType == 7 }

    }


    private fun setStatusText(str: String) {
        findViewById<View>(R.id.ll_status).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_status).text = str
    }


    //设置底部button文字
    private fun setBto2Text(str1: String, str2: String) {
        findViewById<TextView>(R.id.tv_btm1).text = str1
        findViewById<TextView>(R.id.tv_btm2).text = str2

        if (MyApplication.isMarketVersion && str1 == getString(R.string.btm_lxgz)) {
            findViewById<TextView>(R.id.tv_btm1).visibility = View.INVISIBLE
        } else {
            findViewById<TextView>(R.id.tv_btm1).visibility = View.VISIBLE
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        // 在Activity销毁时停止倒计时，避免内存泄漏
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    private fun checkNetWorkView() {

        if (!ShareUtil.isConnected()) {//是否有网络

            showERRView()
        }


    }


    private fun showERRView() {
        LogUtils.i("webview", "网页加载失败！")
        ToastUtils.show("网页加载失败！请检查网络！")
        err_view.visibility = View.VISIBLE
        onNetwork_view.visibility = View.GONE

        iv_but_re.setOnClickListener {
            checkNetWorkView()
        }
    }

    private fun showWebView() {
        err_view.visibility = View.GONE
        onNetwork_view.visibility = View.VISIBLE
        //获取上个页面返回的TaskId再请求一次
        taskDetails(intent.getStringExtra("taskId")!!, intent.getStringExtra("applyLogId"))

    }


    //是否有长任务步骤可以提交
    private fun isCanSubmit() {
        val vos = adapterLVerify.getItems()
        for (i in vos.indices) {
            if (vos[i].auditStatus == 1) {
                //首个待提交的步骤
                putLongActionStep(i)
                break
            }
        }
    }

    fun longTaskReSubmit(step: Int) {
        if (step != -1) {
            putLongActionStep(step)
            //长单任务
            homeViewModel.apiTaskSubmit(
                getTask().applyLogId,
                adapterLVerify.getTaskUploadVerifyList(step),
                isLongTask,
                adapterLVerify.getItems()[step].completeAuditId
            )
        } else {
//                                showToast("所有步骤已经提交！")
        }
    }

    fun putLongActionStep(i: Int) {
        longActionStep = i
    }

    private fun getLongActionStep(): Int {
        return longActionStep
    }
}