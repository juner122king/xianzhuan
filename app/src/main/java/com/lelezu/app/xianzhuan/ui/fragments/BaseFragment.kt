package com.lelezu.app.xianzhuan.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.SysMessageViewModel
import com.lelezu.app.xianzhuan.ui.views.LoginActivity
import com.lelezu.app.xianzhuan.utils.ToastUtils


/**
 * @author:Administrator
 * @date:2023/7/24 0024
 * @description:
 *
 */
open class BaseFragment : Fragment() {


    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        // 添加其他需要的权限
    )
    private val permissions_READ_PHONE_STATE = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        // 添加其他需要的权限
    )

    private var swiper: SwipeRefreshLayout? = null

    protected fun setSwipeRefreshLayout(s: SwipeRefreshLayout) {

        swiper = s
    }

    protected val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((activity?.application as MyApplication).taskRepository)
    }

    protected val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewFactory(((activity?.application as MyApplication).userRepository))
    }

    protected val sysMessageViewModel: SysMessageViewModel by viewModels {
        SysMessageViewModel.ViewFactory((activity?.application as MyApplication).sysInformRepository)
    }

    protected open fun showToast(message: String?) {
        ToastUtils.showToast(requireContext(), message, Toast.LENGTH_SHORT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }


    private fun initViewModel() {

        loginViewModel.errMessage.observe(requireActivity()) {

            onErrMessage(it)
        }
        homeViewModel.errMessage.observe(requireActivity()) {
            onErrMessage(it)
        }

        homeViewModel.emptyListMessage.observe(viewLifecycleOwner) {
            // 如果列表为空 停止刷新动画
            if (it) onStopSwiperRefreshing()
        }

    }


    private fun onErrMessage(it: ErrResponse) {
        onStopSwiperRefreshing()
        showToast(it.message)
        if (it.isTokenLose()) goToLoginView()    //重新打开登录页面
    }


    //重新打开登录页面
    private fun goToLoginView() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun onStopSwiperRefreshing() {
        if (swiper != null) swiper!!.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        onStopSwiperRefreshing()
    }


    protected fun checkAndRequestPermissions(registerForActivityResult: ActivityResultLauncher<Array<String>>) {

        if (!isHasPermissions()) {
            // 请求权限
            registerForActivityResult.launch(permissions)
        }
    }

    protected fun isHasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", requireContext().packageName, null)
        startActivity(intent)
    }

    protected fun showPermissionAlertDialog(message: String, negaString: String) {
        val alertDialog =
            AlertDialog.Builder(requireContext()).setTitle("权限请求").setMessage(message)
                .setPositiveButton("前往设置开启权限") { _, _ ->
                    openAppSettings()
                }.setNegativeButton("取消") { dialog, _ ->
                    showToast(negaString)
                    dialog.dismiss()
                }.create()
        alertDialog.show()
    }


    private var dialog: AlertDialog? = null

    protected var havePermission = false

    private var REQUEST_EXTERNAL_STORAGE = 1

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    protected fun checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                if (dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
                dialog = AlertDialog.Builder(requireContext()).setTitle("提示") //设置标题
                    .setMessage("请开启文件访问权限，否则无法正常使用本应用！").setNegativeButton(
                        "取消"
                    ) { dialog, i -> dialog.dismiss() }.setPositiveButton(
                        "确定"
                    ) { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        startActivity(intent)
                    }.create()
                dialog!!.show()
            } else {
                havePermission = true
                ActivityCompat.requestPermissions(
                    requireActivity(), permissions_READ_PHONE_STATE, 0
                )

                Log.i("swyLog", "Android 11以上，当前已有权限")
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //申请权限
                    if (dialog != null) {
                        dialog!!.dismiss()
                        dialog = null
                    }
                    dialog = AlertDialog.Builder(requireContext()).setTitle("提示") //设置标题
                        .setMessage("请开启文件访问权限，否则无法正常使用本应用！").setPositiveButton(
                            "确定"
                        ) { dialog, _ ->
                            dialog.dismiss()
                            ActivityCompat.requestPermissions(
                                requireActivity(), PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE
                            )
                        }.create()
                    dialog!!.show()
                } else {
                    havePermission = true
                    ActivityCompat.requestPermissions(
                        requireActivity(), permissions_READ_PHONE_STATE, 0
                    )
                    Log.i("swyLog", "Android 6.0以上，11以下，当前已有权限")
                }
            } else {
                havePermission = true
                ActivityCompat.requestPermissions(
                    requireActivity(), permissions_READ_PHONE_STATE, 0
                )
                Log.i("swyLog", "Android 6.0以下，已获取权限")
            }
        }
    }
}