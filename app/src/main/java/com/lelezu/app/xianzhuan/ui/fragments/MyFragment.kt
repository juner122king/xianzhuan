package com.lelezu.app.xianzhuan.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.views.AutoOutActivity
import com.lelezu.app.xianzhuan.ui.views.MessageActivity
import com.lelezu.app.xianzhuan.ui.views.MyTaskActivity
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil

class MyFragment : BaseFragment(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        loginViewModel.errMessage.observe(this) {
//            showToast(it)
//        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.iv_op).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_message).setOnClickListener(this)

        view.findViewById<View>(R.id.ll_l1).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l2).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l3).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l4).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l5).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l6).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l7).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l8).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l9).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_my_task).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_my_task1).setOnClickListener(this)
        view.findViewById<View>(R.id.btm_vip).setOnClickListener(this)

        val ivVipPic = view.findViewById<ImageView>(R.id.iv_user_vip)


        //执行获取用户信息接口
        loginViewModel.getUserInfo(ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID))
        //执行获取收徒收益
        loginViewModel.getEarnings()

        loginViewModel.userInfo.observe(requireActivity()) {
            view.findViewById<TextView>(R.id.tv_user_name).text = it!!.nickname
            view.findViewById<TextView>(R.id.tv_user_id).text = it.userId
            view.findViewById<TextView>(R.id.tv_my_text1).text = it.rechargeAmount.toString()
            view.findViewById<TextView>(R.id.tv_my_text2).text = it.balanceAmount.toString()
            ImageViewUtil.load(view.findViewById(R.id.iv_user_pic), it.headImageUrl)


            when (it.vipLevel) {
                0 -> {
                }   //普通
                1 -> {
                    ImageViewUtil.load(ivVipPic, R.drawable.my_icon_vip_lv0)
                }   //白银
                2 -> {
                    ImageViewUtil.load(ivVipPic, R.drawable.my_icon_vip_lv)//黄金
                }

                3 -> {
                }   //忽略
                4 -> {
                    ImageViewUtil.load(ivVipPic, R.drawable.my_icon_vip_lv1)//钻
                }
            }

        }
        loginViewModel.earnings.observe(requireActivity()) {
            view.findViewById<TextView>(R.id.tv_my_text3).text = it.totalEarned.toString()
            view.findViewById<TextView>(R.id.tv_my_text4).text = it.beEarned.toString()
        }

        sysMessageViewModel.msgNum.observe(requireActivity()) {

        }
        //获取系统消息数量
        sysMessageViewModel.getSysMessageNum()
    }

    companion object {

        @JvmStatic
        fun newInstance() = MyFragment()
    }

    override fun onClick(p0: View?) {


        if (p0?.id == R.id.ll_l9) {
            startActivity(Intent(activity, AutoOutActivity::class.java))//关于我们

        } else if (p0?.id == R.id.iv_message) {
            startActivity(Intent(activity, MessageActivity::class.java))//消息
        } else if (p0?.id == R.id.ll_my_task || p0?.id == R.id.ll_my_task1) {
            startActivity(Intent(activity, MyTaskActivity::class.java))//我的任务
        } else {
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            when (p0?.id) {
                R.id.ll_l1 -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link5)
                    intent.putExtra(WebViewSettings.URL_TITLE, "发布任务")
                }

                R.id.ll_l2 -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link4)
                    intent.putExtra(WebViewSettings.URL_TITLE, getString(R.string.btm_zq))
                }

                R.id.ll_l4 -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link8)
                    intent.putExtra(WebViewSettings.URL_TITLE, "充值")
                }

                R.id.ll_l5 -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link9)
                    intent.putExtra(WebViewSettings.URL_TITLE, "提现")
                }

                R.id.ll_l6 -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link7)
                    intent.putExtra(WebViewSettings.URL_TITLE, "流水报表")
                }

                R.id.ll_l7 -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link10)
                    intent.putExtra(WebViewSettings.URL_TITLE, "举报维权")
                }

                R.id.ll_l8 -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link11)
                    intent.putExtra(WebViewSettings.URL_TITLE, "客服与反馈")
                }

                R.id.iv_op -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link12)
                    intent.putExtra(WebViewSettings.URL_TITLE, "设置个人资料")
                }

                R.id.btm_vip -> {
                    intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link13)
                    intent.putExtra(WebViewSettings.URL_TITLE, "开通会员")
                }
            }
            startActivity(intent)
        }
    }

}