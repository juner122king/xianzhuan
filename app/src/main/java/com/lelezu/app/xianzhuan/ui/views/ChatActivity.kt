package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.adapters.ChatAdapter
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil

class ChatActivity : BaseActivity() {
    //vip头像等级框图片
    private var pic = mapOf(
        1 to R.drawable.icon_head1, 2 to R.drawable.icon_head, 4 to R.drawable.icon_head2
        // 可以继续添加其他映射关系
    )
    private val statusMap = mapOf(
        1 to "每日1次",
        2 to "每人1次",
        3 to "每人3次",
        // 可以继续添加其他映射关系
    )

    private lateinit var dialog: Dialog //风险提示确认窗口

    private lateinit var userId: String
    private var taskId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private lateinit var enter: TextView
    private lateinit var ll: View
    private lateinit var editText: EditText
    private lateinit var enterPic: ImageView

    private lateinit var tv_sub: TextView //关注数
    private lateinit var tv_fan: TextView //粉丝数


    private lateinit var taskView: View //任务浮窗
    private lateinit var taskViewClose: View //任务浮窗关闭按钮
    private lateinit var taskViewSend: View //任务发送按钮


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        userId = intent.getStringExtra("userId").toString()
        taskId = intent.getStringExtra("taskId")

        initView()
        initObserve()


    }

    private fun initView() {
        taskView = findViewById(R.id.click_view)
        taskViewClose = findViewById(R.id.iv_close)
        taskViewSend = findViewById(R.id.tvv)
        taskViewClose.visibility = View.VISIBLE
        taskViewSend.visibility = View.VISIBLE



        findViewById<View>(R.id.tv_user_vip).visibility = View.GONE//隐藏vip等级
        editText = findViewById(R.id.et_s)
        ll = findViewById(R.id.ll)
        enterPic = findViewById(R.id.icon_pic)
        enter = findViewById(R.id.enter)


        tv_sub = findViewById(R.id.tv_sub)
        tv_fan = findViewById(R.id.tv_fan)


        recyclerView = findViewById(R.id.recyclerView)
        adapter = ChatAdapter(emptyList(), ivDialog, this)
        recyclerView.adapter = adapter

        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        //关闭软键盘
        recyclerView.setOnTouchListener { v, _ ->
            close(v)
            false
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    enter.visibility = TextView.GONE
                    enterPic.visibility = TextView.VISIBLE

                } else {
                    enter.visibility = TextView.VISIBLE
                    enterPic.visibility = TextView.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        enter.setOnClickListener {

            val textContent = editText.text.toString().trim()

            if (textContent.isNotEmpty()) {
                showLoading()
                loginViewModel.apiSend(userId, textContent, 0)
                // 清空EditText内容

            }
        }
        enterPic.setOnClickListener {
            showLoading()
            //上传图片，打开相册
            pickImageContract.launch(Unit)

        }

        //弹出风险提示
        if (!ShareUtil.getBoolean(ShareUtil.CHECKED_FXTS)) {
            showDialog()
        }
    }

    private fun initObserve() {
        showLoading()
        loginViewModel.getUserInfo(userId)//获取商家信息
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


        loginViewModel.apiRecord(userId)//获取双方聊天记录
        loginViewModel.chatMessage.observe(this) {

            adapter.updateData(it)
            recyclerView.scrollToPosition(adapter.itemCount - 1)//此句为设置显示最底部
            hideLoading()
            close(recyclerView)
        }

        //发送信息监听
        loginViewModel.sendMessage.observe(this) {

            showLoading()
            loginViewModel.apiRecord(userId)
        }


        homeViewModel.upLink.observe(this) { link ->
//            showToast( "图片上传成功")
            hideLoading()
            showLoading()
            loginViewModel.apiSend(userId, link, 1)
        }

        //获取关注和粉丝数
        loginViewModel.follows(userId)
        loginViewModel.follow.observe(this) {
            hideLoading()

            tv_fan.text = "${it.fanCnt}粉丝"
            tv_sub.text = "${it.concernCnt}关注"

        }
        loginViewModel.isRecord(userId)//标记消息已读


        taskId?.let {
            homeViewModel.getTaskDetails(it)//获取任务信息
        }
        //监听任务信息
        homeViewModel.task.observe(this) {
            //初始化页面数据
            setData(it)
        }

    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setData(task: Task) {

        taskView.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_task_title).text = task.taskTitle //任务标题
        findViewById<TextView>(R.id.tv_taskLabel).text = task.taskTypeTitle //任务标签1
        findViewById<TextView>(R.id.tv_taskLabel2).text = task.taskLabel //任务标签2

        findViewById<TextView>(R.id.tv_shang_ji).text = "${task.unitPrice}元"//任务金额
        findViewById<TextView>(R.id.tv_task_earnedCount).text = "${task.earnedCount}人已赚"//多人已赚
        findViewById<TextView>(R.id.tv_task_rest).text = "剩余${task.rest}个"//剩余数

        taskViewClose.setOnClickListener {
            taskView.visibility = View.INVISIBLE
        }
        taskViewSend.setOnClickListener {
            //发送任务信息
            taskView.visibility = View.INVISIBLE

            loginViewModel.apiSend(userId, taskId.toString(), 2)
        }

    }

    private fun showDialog() {
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_fxts)

        val ok: TextView = dialog.findViewById(R.id.ok)

        ok.setOnClickListener {
            //确定
            ShareUtil.putBoolean(ShareUtil.CHECKED_FXTS, true)
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun close(view: View) {
        editText.text.clear()

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        // 先 设置根视图获取焦点，以确保 EditText 失去焦点
        ll.requestFocus()
        // 再 让EditText 失去焦点 顺序不能乱
        editText.clearFocus()

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

    override fun getContentTitle(): String {
        return getString(R.string.btm_lxgz)
    }

    override fun isShowBack(): Boolean {
        return true
    }

    private val rc: Int = 123
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == rc) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，继续进行文件操作
                //上传图片，打开相册
                pickImageContract.launch(Unit)
            } else {
                // 用户拒绝了权限，处理拒绝权限的情况
            }
        }
    }

    private val pickImageContract = registerForActivityResult(PickImageContract()) {
        if (it != null) {
            // 获取内容URI对应的文件路径
            val thread = Thread {
                homeViewModel.apiUpload(it)
            }
            thread.start()
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
}