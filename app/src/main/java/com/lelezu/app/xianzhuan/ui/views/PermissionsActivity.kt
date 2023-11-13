package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lelezu.app.xianzhuan.R

class PermissionsActivity : BaseActivity(), OnClickListener {


    private lateinit var permissionStatus1: TextView
    private lateinit var permissionStatus2: TextView
    private lateinit var permissionStatus3: TextView


    private lateinit var permissions: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        permissions = arrayOf(
            Permission.READ_MEDIA_IMAGES,
            Permission.WRITE_EXTERNAL_STORAGE,
            Permission.READ_PHONE_STATE
        )
    }

    override fun onResume() {
        super.onResume()
        //监听权限是否授权变化
        checkPermissionsStatus()

    }

    private fun initView() {
        permissionStatus1 = findViewById(R.id.tv_status11)
        permissionStatus2 = findViewById(R.id.tv_status21)
        permissionStatus3 = findViewById(R.id.tv_status31)
        findViewById<View>(R.id.cl1).setOnClickListener(this)
        findViewById<View>(R.id.cl2).setOnClickListener(this)
        findViewById<View>(R.id.cl3).setOnClickListener(this)
    }


    //检查权限授权变化
    private fun checkPermissionsStatus() {

        //1.是否有打开相册权限
        if (XXPermissions.isGranted(
                this, Permission.READ_MEDIA_IMAGES
            )
        ) {//是否有手机状态读取权限
            permissionStatus1.text = getString(R.string.opened)
        } else {
            permissionStatus1.text = getString(R.string.closed)
        }


        //2.是否有存储权限
        if (XXPermissions.isGranted(
                this, arrayOf(
                    Permission.READ_MEDIA_IMAGES, Permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) {
            permissionStatus2.text = getString(R.string.opened)
        } else {
            permissionStatus2.text = getString(R.string.closed)
        }


        //3.是否有手机状态读取权限
        if (XXPermissions.isGranted(this, Permission.READ_PHONE_STATE)) {
            permissionStatus3.text = getString(R.string.opened)
        } else {
            permissionStatus3.text = getString(R.string.closed)
        }


    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.cl1 -> {
                openSysPermissionsSetting()
            }

            R.id.cl2 -> {
                openSysPermissionsSetting()
            }

            R.id.cl3 -> {
                openSysPermissionsSetting()
            }

        }
    }


    //打开系统权限设置页面
    private fun openSysPermissionsSetting() {
        // 跳转到应用权限设置页
        XXPermissions.startPermissionActivity(this)

    }


    override fun getLayoutId(): Int {
        return R.layout.activity_permissions
    }

    override fun getContentTitle(): String? {
        return "权限管理"
    }

    override fun isShowBack(): Boolean {
        return true
    }


}