package com.lelezu.app.xianzhuan.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hjq.toast.ToastUtils
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.data.model.ErrResponse
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.SysMessageViewModel
import com.lelezu.app.xianzhuan.ui.views.LoginActivity
import com.lelezu.app.xianzhuan.utils.LogUtils


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

    protected val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModel.LoginViewFactory(((activity?.application as MyApplication).userRepository))
    }

    protected val sysMessageViewModel: SysMessageViewModel by viewModels {
        SysMessageViewModel.ViewFactory((activity?.application as MyApplication).sysInformRepository)
    }

    protected open fun showToast(message: String?) {
        ToastUtils.show(message)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.limit.removeObservers(requireActivity())
        loginViewModel.errMessage.removeObservers(requireActivity())
        homeViewModel.errMessage.removeObservers(requireActivity())
    }

    private fun initViewModel() {

        loginViewModel.errMessage.observe(requireActivity()) {

            onErrMessage(it)
        }
        homeViewModel.errMessage.observe(requireActivity()) {
            onErrMessage(it)
        }

//        //监听发布任务前的验证接口
//        homeViewModel.limit.observe(requireActivity()) {
//            if (it.isLimit) {
//                val intent = Intent(requireActivity(), WebViewActivity::class.java)
//                intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link5)
//                intent.putExtra(WebViewSettings.URL_TITLE, "选择任务分类")
//                startActivity(intent)
//            } else {
//                (requireActivity() as BaseActivity).showPostDialog(it.endTime)
//            }
//        }
    }


    private fun onErrMessage(it: ErrResponse) {

        showToast(it.message)
        if (it.isTokenLose()) goToLoginView()    //重新打开登录页面
    }


    //重新打开登录页面
    private fun goToLoginView() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    protected fun copyText(text: String) {
        LogUtils.i("复制文字到剪切板", "复制文字到剪切板")

        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val mClipData = ClipData.newPlainText("Label", text)

        clipboard.setPrimaryClip(mClipData)

    }

}