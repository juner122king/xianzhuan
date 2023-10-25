package com.lelezu.app.xianzhuan.ui.views

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.TaskItemAdapter
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.zj.task.sdk.b.f

/**
 * 店铺页面
 */

class MasterActivity : BaseActivity() {

    //vip头像等级框图片
    private var pic = mapOf(
        1 to R.drawable.icon_head1, 2 to R.drawable.icon_head, 4 to R.drawable.icon_head2
        // 可以继续添加其他映射关系
    )
    private lateinit var dialog: Dialog //取消关注确认窗口

    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskItemAdapter

    private lateinit var ivSub: ImageView //关注按钮

    private lateinit var tv_sub: TextView //关注数
    private lateinit var tv_fan: TextView //粉丝数

    private var isSub: Boolean = false//是否已经关注


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = intent.getStringExtra("userId").toString()

        initView()
        initObserve()
    }

    private fun initView() {

        recyclerView = findViewById(R.id.recyclerView)
        adapter = TaskItemAdapter(mutableListOf())
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        ivSub = findViewById(R.id.iv_sub)

        tv_sub = findViewById(R.id.tv_sub)
        tv_fan = findViewById(R.id.tv_fan)

        ivSub.setOnClickListener {

            if (isSub) {
                showDialog()
            } else {

                loginViewModel.onFollows(userId)
            }

        }
    }

    private fun initObserve() {
        showLoading()
        loginViewModel.getUserInfo(userId)//获取商家信息
        loginViewModel.userInfo.observe(this) {

            hideLoading()
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


        // 获取雇主发布的任务列表
        homeViewModel.getMasterTaskList(userId)
        homeViewModel.taskList.observe(this) {
            hideLoading()
            adapter.upData(it)
        }

        //获取关注和粉丝数
        loginViewModel.follows(userId)
        loginViewModel.follow.observe(this) {
            hideLoading()
            isSub = it.isConcerned

            if (it.isConcerned) ivSub.setImageResource(R.drawable.icon_sub2)
            else ivSub.setImageResource(R.drawable.icon_sub)


            tv_fan.text = "${it.fanCnt}粉丝"
            tv_sub.text = "${it.concernCnt}关注"

        }
        loginViewModel.isFollow.observe(this) {


            if (it) showToast("关注成功！")
            else showToast("取关成功！")
            loginViewModel.follows(userId)
        }

    }


    private fun showDialog() {
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_sub2)

        val cancel: TextView = dialog.findViewById(R.id.cancel)
        val ok: TextView = dialog.findViewById(R.id.ok)

        ok.setOnClickListener {
            //确定取消
            loginViewModel.onFollows(userId)
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_master
    }

    override fun getContentTitle(): String? {
        return "店铺"
    }

    override fun isShowBack(): Boolean {
        return true
    }
}