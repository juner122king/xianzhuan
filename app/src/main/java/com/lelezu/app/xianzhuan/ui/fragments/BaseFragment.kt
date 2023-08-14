package com.lelezu.app.xianzhuan.ui.fragments

import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.SysMessageViewModel
import com.lelezu.app.xianzhuan.utils.ToastUtils


/**
 * @author:Administrator
 * @date:2023/7/24 0024
 * @description:
 *
 */
open class BaseFragment : Fragment() {
    protected val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.ViewFactory((activity?.application as MyApplication).taskRepository)
    }

    protected val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewFactory(((activity?.application as MyApplication).userRepository))
    }

    protected val sysMessageViewModel: SysMessageViewModel by viewModels {
        SysMessageViewModel.ViewFactory((activity?.application  as MyApplication).sysInformRepository)
    }

    protected open fun showToast(message: String?) {
        ToastUtils.showToast(requireContext(), message, Toast.LENGTH_SHORT)
    }
}