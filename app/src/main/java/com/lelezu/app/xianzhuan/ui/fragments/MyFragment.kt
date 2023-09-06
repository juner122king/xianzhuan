package com.lelezu.app.xianzhuan.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.ui.adapters.ComplexViewAdapter
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.views.AutoOutActivity
import com.lelezu.app.xianzhuan.ui.views.BulletinView
import com.lelezu.app.xianzhuan.ui.views.MessageActivity
import com.lelezu.app.xianzhuan.ui.views.MyTaskActivity
import com.lelezu.app.xianzhuan.ui.views.PermissionsActivity
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.ui.views.ZJTaskHistoryActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil

class MyFragment : BaseFragment(), View.OnClickListener {

    private lateinit var bulletinView: BulletinView//公告栏View
    private lateinit var llNotice: View//公告栏区域

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
        view.findViewById<View>(R.id.iv_my_task1).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_my_task2).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_my_task3).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_my_task4).setOnClickListener(this)
        view.findViewById<View>(R.id.btm_vip).setOnClickListener(this)

        view.findViewById<View>(R.id.iv_log).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_cash).setOnClickListener(this)

        view.findViewById<View>(R.id.tv_my_text2).setOnClickListener(this)
        view.findViewById<View>(R.id.tv_my_text4).setOnClickListener(this)

        bulletinView = view.findViewById(R.id.bv)
        llNotice = view.findViewById(R.id.ll_notice)

        val ivVipPic = view.findViewById<ImageView>(R.id.iv_user_vip)

        loginViewModel.userInfo.observe(requireActivity()) {
            view.findViewById<TextView>(R.id.tv_user_name).text = it!!.nickname
            view.findViewById<TextView>(R.id.tv_user_id).text = "UID:${it.userId}"
            view.findViewById<TextView>(R.id.tv_my_text2).text = it.balanceAmount.toString()
            view.findViewById<TextView>(R.id.tv_my_text4).text = it.rechargeAmount.toString()
            ImageViewUtil.loadCircleCrop(view.findViewById(R.id.iv_user_pic), it.headImageUrl)

            when (it.vipLevel) {
                0 -> {
                    view.findViewById<ImageView>(R.id.btm_vip)
                        .setImageResource(R.drawable.my_icon_get_vip)
                }   //普通
                1 -> {
                    ImageViewUtil.load(ivVipPic, R.drawable.my_icon_vip_lv0)
                    view.findViewById<ImageView>(R.id.btm_vip)
                        .setImageResource(R.drawable.my_icon_get_vip2)
                }   //白银
                2 -> {
                    ImageViewUtil.load(ivVipPic, R.drawable.my_icon_vip_lv)//
                    view.findViewById<ImageView>(R.id.btm_vip)
                        .setImageResource(R.drawable.my_icon_get_vip2)
                }
                3 -> {
                }   //忽略
                4 -> {
                    ImageViewUtil.load(ivVipPic, R.drawable.my_icon_vip_lv1)//钻
                    view.findViewById<ImageView>(R.id.btm_vip)
                        .setImageResource(R.drawable.my_icon_get_vip2)

                }
            }

        }

        loginViewModel.related.observe(requireActivity()) {
            view.findViewById<TextView>(R.id.tv_my_text3).text = it.apprenticeTribute.toString()
            view.findViewById<TextView>(R.id.tv_my_text5).text = it.rewardTaskCount.toString()
            view.findViewById<TextView>(R.id.tv_my_text1).text = it.taskEstimatedAmount.toString()
        }

        val messageIv = view.findViewById<ImageView>(R.id.iv_message)

        //消息数量
        sysMessageViewModel.msgNum.observe(requireActivity()) {
            if (it > 0) messageIv.setImageResource(R.drawable.icon_message)
            else messageIv.setImageResource(R.drawable.icon_message2)
        }



        sysMessageViewModel.announce.observe(requireActivity()) {
            //处理公告滚动

            if (it.isNotEmpty()) {
                llNotice.visibility = View.VISIBLE
                bulletinView.setAdapter(ComplexViewAdapter(it))
                bulletinView.setOnItemClickListener { itemData, _, _ ->
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra(
                        WebViewSettings.LINK_KEY, (itemData as Announce).announceContent
                    )
                    intent.putExtra(WebViewSettings.URL_TITLE, itemData.announceTitle)
                    intent.putExtra(WebViewSettings.isProcessing, false)
                    startActivity(intent)
                }
            }else
            {
                llNotice.visibility = View.GONE
            }


        }

    }

    override fun onResume() {
        super.onResume()
        //获取系统消息数量
        sysMessageViewModel.getSysMessageNum()
        //执行获取用户信息接口
        loginViewModel.getUserInfo(ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID))

        //个人中心任务相关的数据
        loginViewModel.getRelated()

        //获取公告
        sysMessageViewModel.getAnnounce()

    }


    companion object {

        @JvmStatic
        fun newInstance() = MyFragment()
    }

    override fun onClick(p0: View?) {
        //我的任务
        if (p0?.tag == getString(R.string.tag_my_task)) {
            val intent = Intent(requireContext(), MyTaskActivity::class.java)
            when (p0.id) {
                R.id.iv_my_task1, R.id.ll_my_task1 -> {
                    intent.putExtra("selectedTab", 1)
                }

                R.id.iv_my_task2 -> {
                    intent.putExtra("selectedTab", 2)
                }

                R.id.iv_my_task3 -> {
                    intent.putExtra("selectedTab", 3)
                }

                R.id.iv_my_task4 -> {
                    intent.putExtra("selectedTab", 4)
                }
            }
            startActivity(intent)//我的任务


        } else {
            when (p0?.id) {
                R.id.ll_l9 -> {
                    startActivity(Intent(activity, AutoOutActivity::class.java))//关于我们
                }

                R.id.iv_message -> {
                    startActivity(Intent(activity, MessageActivity::class.java))//消息
//                    startActivity(Intent(activity, PermissionsActivity::class.java))//消息
                }

                R.id.iv_log -> {
                    startActivity(Intent(activity, ZJTaskHistoryActivity::class.java))//任务墙领奖记录
                }


                else -> {
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

                        R.id.ll_l3 -> {
                            intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link6)
                            intent.putExtra(WebViewSettings.URL_TITLE, "我的店铺")
                        }

                        R.id.ll_l4, R.id.tv_my_text4 -> {
                            intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link8)
                            intent.putExtra(WebViewSettings.URL_TITLE, "充值")
                        }

                        R.id.ll_l5, R.id.tv_my_text2, R.id.iv_cash -> {
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
    }

}