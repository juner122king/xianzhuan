package com.lelezu.app.xianzhuan.ui.views

import android.app.Activity
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
import com.lelezu.app.xianzhuan.ui.adapters.ChatAdapter
import com.lelezu.app.xianzhuan.utils.Base64Utils
import com.lelezu.app.xianzhuan.utils.ImageViewUtil

class ChatActivity : BaseActivity() {
    //vip头像等级框图片
    private var pic = mapOf(
        1 to R.drawable.icon_head1, 2 to R.drawable.icon_head, 4 to R.drawable.icon_head2
        // 可以继续添加其他映射关系
    )
    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private lateinit var enter: TextView
    private lateinit var ll: View
    private lateinit var editText: EditText
    private lateinit var enterPic: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        userId = intent.getStringExtra("userId").toString()

        initView()
        initObserve()

    }

    private fun initView() {



        findViewById<View>(R.id.tv_user_vip).visibility = View.GONE//隐藏vip等级
        editText = findViewById(R.id.et_s)
        ll = findViewById(R.id.ll)
        enterPic = findViewById(R.id.icon_pic)
        enter = findViewById(R.id.enter)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = ChatAdapter(emptyList(), ivDialog,this)
        recyclerView.adapter = adapter

        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
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
                loginViewModel.apiSend(userId, textContent, false)
                // 清空EditText内容

            }
        }
        enterPic.setOnClickListener {
            showLoading()
            //上传图片，打开相册
            pickImageContract.launch(Unit)

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


        loginViewModel.apiRecord(userId)
        loginViewModel.chatMessage.observe(this) {

            adapter.updateData(it)

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
            loginViewModel.apiSend(userId, link, true)
        }

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
        return getString(R.string.chat_title)
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
                val imageData = Base64Utils.zipPic(it)
                if (imageData == null) {
                    // 如果 imageData 为 null，执行处理空值的操作
                    // 例如，显示一个提示消息或采取其他适当的操作
                    showToast("图片不支持，请重新选择！")
                } else if (imageData.length > 1000 * 2000) {
                    // 如果 imageData 不为 null 且其长度大于2MB（2 * 1000 * 2000字节），则执行以下操作：
                    showToast("图片不能超过2MB，请重新选择！")
                } else {
                    // 否则，执行以下操作：
                    // 执行上传动作，传递参数 it
                    homeViewModel.apiUpload(it)
                }
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