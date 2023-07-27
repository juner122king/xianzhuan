package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Task
import com.lelezu.app.xianzhuan.ui.adapters.MessageItemAdapter
import com.lelezu.app.xianzhuan.ui.adapters.TaskDetailsStepAdapter
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel

class TaskDetailsActivity : BaseActivity(), OnClickListener {


    private lateinit var iv1: ImageView//步骤图1
    private lateinit var iv2: ImageView//完成图1
    private lateinit var iv3: ImageView//上传图1
    private lateinit var ivDialog: Dialog


    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((application as MyApplication).taskRepository)
    }


    private lateinit var adapter: TaskDetailsStepAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_task_step)

        // 创建适配器，并将其绑定到 RecyclerView 上
        adapter = TaskDetailsStepAdapter(emptyList())
        recyclerView.adapter = adapter
        // 可以在这里设置 RecyclerView 的布局管理器，例如：
        recyclerView.layoutManager = LinearLayoutManager(this)


        //选择图片按钮
        findViewById<View>(R.id.tv_up_pic).setOnClickListener(this)
        //开始--示例图打开功能
        ivDialog = Dialog(this, R.style.FullActivity)
        val attributes = window.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT
        ivDialog.window?.attributes = attributes


        iv1 = findViewById(R.id.iv_illustration)

        //示例图
        iv1.setOnClickListener {
            ivDialog.setContentView(getImageView())
            ivDialog.show()
        }
        //结束--示例图打开功能


        //开始--上传图片功能
        iv2 = findViewById(R.id.iv_done1_pic)
        //示例图
        iv2.setOnClickListener {
            ivDialog.setContentView(getImageView())
            ivDialog.show()
        }

        iv3 = findViewById(R.id.iv_up_pic)

        //示例图
        iv3.setOnClickListener {
            ivDialog.setContentView(getImageView())
            ivDialog.show()
        }


        //监听任务信息变化
        homeViewModel.task.observe(this) {
            //初始化页面数据
            setData(it)

        }

        val string = intent.getStringExtra("taskId")
        homeViewModel.getTaskDetails(string!!)


    }

    @SuppressLint("SetTextI18n")
    private fun setData(task: Task) {

        findViewById<TextView>(R.id.tv_task_title).text = task.taskTitle //任务标题
        findViewById<TextView>(R.id.tv_task_des_c).text = task.taskDesc //任务说明
        findViewById<TextView>(R.id.tv_time).text = "限时${task.operateTime}小时完成" //
        findViewById<TextView>(R.id.tv_nub).text = "剩余${task.rest}单" //

        val string = "${task.earnedCount}人已完成任务，任务可报名${task.limitTimes}次"
        findViewById<TextView>(R.id.tv_info).text = string


        adapter.updateData(task.taskStepList)
    }


    //获取一个ImageView对象放到Dialog上
    private fun getImageView(): ImageView {
        val imageView = ImageView(this)
        imageView.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageView.load("https://t7.baidu.com/it/u=4036010509,3445021118&fm=193&f=GIF") {
            crossfade(true)
            transformations(RoundedCornersTransformation(10f, 10f, 10f, 10f))
        }

        imageView.setOnClickListener {
            ivDialog.dismiss()
        }

        //1.注册菜单
        registerForContextMenu(imageView)
        imageView.setOnLongClickListener {
            //显示选项  保存图
            //2.打开菜单
            openContextMenu(imageView);
            true
        }
        return imageView

    }


    override fun onCreateContextMenu(
        menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(0, 1, 0, "保存")
        menu?.add(0, 2, 1, "取消")

        menu!!.getItem(0).setOnMenuItemClickListener {
            Toast.makeText(this, "保存", Toast.LENGTH_SHORT).show()
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
            R.id.tv_up_pic -> selectPic()
        }


    }

    private fun selectPic() {
        //打开系统图片
        // 打开相机相册
        // 在Activity Action里面有一个“ACTION_GET_CONTENT”字符串常量，
        // 该常量让用户选择特定类型的数据，并返回该数据的URI.我们利用该常量，
        // 然后设置类型为“image/*”，就可获得Android手机内的所有image。*/
        // 打开相机相册
        // 在Activity Action里面有一个“ACTION_GET_CONTENT”字符串常量，
        // 该常量让用户选择特定类型的数据，并返回该数据的URI.我们利用该常量，
        // 然后设置类型为“image/*”，就可获得Android手机内的所有image。*/
        val intent = Intent()/* 开启Pictures画面Type设定为image *//* 开启Pictures画面Type设定为image */
        intent.type =
            "image/*"/* 使用Intent.ACTION_GET_CONTENT这个Action *//* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.action = Intent.ACTION_GET_CONTENT/* 取得相片后返回本画面 *//* 取得相片后返回本画面 */
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            iv3.load(uri) {
                crossfade(true)
                transformations(RoundedCornersTransformation(10f, 10f, 10f, 10f))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

}