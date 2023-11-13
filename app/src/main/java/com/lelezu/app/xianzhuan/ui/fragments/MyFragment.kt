package com.lelezu.app.xianzhuan.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.ui.adapters.ComplexViewAdapter
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.ANNOUNCEID
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link103
import com.lelezu.app.xianzhuan.ui.views.AutoOutActivity
import com.lelezu.app.xianzhuan.ui.views.BulletinView
import com.lelezu.app.xianzhuan.ui.views.ChatListActivity
import com.lelezu.app.xianzhuan.ui.views.MessageActivity
import com.lelezu.app.xianzhuan.ui.views.MyMaterActivity
import com.lelezu.app.xianzhuan.ui.views.MyTaskActivity
import com.lelezu.app.xianzhuan.ui.views.PartnerCenterActivity
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.ui.views.ZJTaskHistoryActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.wxapi.WxLogin


class MyFragment : BaseFragment(), View.OnClickListener {

    private lateinit var dialog: Dialog //师傅昵称窗口


    private lateinit var ll_gac: View //游戏记录，收益提现入口

    //vip等级图片
    private var vippic = mapOf(
        "V0" to R.drawable.icon_vip0,
        "V1" to R.drawable.icon_vip1,
        "V2" to R.drawable.icon_vip2,
        "V3" to R.drawable.icon_vip3,
        "V4" to R.drawable.icon_vip4,
        "V5" to R.drawable.icon_vip5,
        "V6" to R.drawable.icon_vip6,
        "V7" to R.drawable.icon_vip7,
        "V8" to R.drawable.icon_vip8,
        "V9" to R.drawable.icon_vip9,
    )

    //vip等级经验值区间最大值
    private var vipLevel_max_l = mapOf(
        "V0" to 500,
        "V1" to 1500,
        "V2" to 3000,
        "V3" to 5000,
        "V4" to 8000,
        "V5" to 12000,
        "V6" to 17000,
        "V7" to 30000,
        "V8" to 40000,
        "V9" to 50000,
    )


    private lateinit var bulletinView: BulletinView//公告栏View
    private lateinit var llNotice: View//公告栏区域
    private lateinit var swiper: SwipeRefreshLayout//下拉刷新控件
    private lateinit var vipLevel: ImageView//vip等级icon


    private lateinit var pb: ProgressBar//经验进度条
    private lateinit var tv_pb: TextView//经验值显示

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vipLevel = view.findViewById(R.id.iv_vip_level)

        pb = view.findViewById(R.id.pb)
        tv_pb = view.findViewById(R.id.tv_pb)

        view.findViewById<View>(R.id.iv_op).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_message).setOnClickListener(this)

        view.findViewById<View>(R.id.ll_l1).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l2).setOnClickListener(this)//收徒赚钱
        view.findViewById<View>(R.id.ll_l3).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l4).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l5).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l6).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l7).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l8).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l9).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l13).setOnClickListener(this)

        view.findViewById<View>(R.id.ll_l11).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l12).setOnClickListener(this)

        view.findViewById<View>(R.id.customer).setOnClickListener(this)


        view.findViewById<View>(R.id.ll_my_task).setOnClickListener(this)
        view.findViewById<View>(R.id.btm_vip).setOnClickListener(this)

        view.findViewById<View>(R.id.iv_log).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_cash).setOnClickListener(this)

        view.findViewById<View>(R.id.tv_my_text2).setOnClickListener(this)
        view.findViewById<View>(R.id.tv_my_text4).setOnClickListener(this)



        bulletinView = view.findViewById(R.id.bv)
        llNotice = view.findViewById(R.id.ll_notice)

        ll_gac = view.findViewById(R.id.ll_gac)

        swiper = view.findViewById(R.id.swiper)
        swiper.setColorSchemeResources(R.color.colorControlActivated)
        swiper.setOnRefreshListener {
            // 执行刷新操作
            loadData()
        }
        val messageIv = view.findViewById<ImageView>(R.id.iv_message)

        loginViewModel.userInfo.observe(requireActivity()) {
            // 停止刷新动画
            swiper.isRefreshing = false

            val ivVipPic = view.findViewById<ImageView>(R.id.iv_user_vip)

            view.findViewById<TextView>(R.id.tv_user_name).text = it!!.nickname
            view.findViewById<TextView>(R.id.tv_user_id).text = "UID:${it.userId}"
            view.findViewById<TextView>(R.id.tv_my_text2).text = it.balanceAmount.toString()
            view.findViewById<TextView>(R.id.tv_my_text4).text = it.rechargeAmount.toString()
            LogUtils.i("头像LINK:", it.headImageUrl)
            ImageViewUtil.loadCircleCrop(
                view.findViewById(R.id.iv_user_pic), it.headImageUrl, false
            )


            val id = it.userId

            view.findViewById<TextView>(R.id.tv_user_id).setOnLongClickListener {
                showToast("ID已复制到剪切板:${id}")
                copyText(id)
                false
            }

            //处理用户vip等级icon
            vippic[it.level]?.let { it1 -> vipLevel.setImageResource(it1) }
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


            //处理用户vip等级经验值进度
            val userAward = it.taskAward//用户当前经验值
            val maxAward = vipLevel_max_l[it.level]//当前用户等级的最大经验值
            val progress = (userAward * 100 / maxAward!!).toInt() // 计算进度百分比
            pb.progress = progress

            tv_pb.text = "${userAward}/${maxAward}"


            val materID = it.recommendUserId
            //判断是否有师傅
            if ("0" == materID) {
                view.findViewById<View>(R.id.ll_l10).setOnClickListener(this)

            } else {

                view.findViewById<View>(R.id.ll_l10).setOnClickListener {
//                    showToast("您师傅ID:$materID")

                    loginViewModel.getMUserInfo(materID.toString())

                }
            }
        }

        loginViewModel.master_userInfo.observe(requireActivity()) {

            showDialog(it.nickname, it.userId, it.headImageUrl)
        }


        loginViewModel.related.observe(requireActivity()) {
            // 停止刷新动画
            swiper.isRefreshing = false
            view.findViewById<TextView>(R.id.tv_my_text3).text = it.apprenticeTribute.toString()
            view.findViewById<TextView>(R.id.tv_my_text5).text = it.rewardTaskCount.toString()
            view.findViewById<TextView>(R.id.tv_my_text1).text = it.taskEstimatedAmount.toString()
        }


        //消息数量
        sysMessageViewModel.msgNum.observe(requireActivity()) {
            // 停止刷新动画
            swiper.isRefreshing = false
            if (it > 0) messageIv.setImageResource(R.drawable.icon_message)
            else messageIv.setImageResource(R.drawable.icon_message2)
        }



        sysMessageViewModel.announce.observe(requireActivity()) {
            //处理公告滚动
            // 停止刷新动画
            swiper.isRefreshing = false
            if (it.isNotEmpty() && !MyApplication.isMarketVersion) {
                llNotice.visibility = View.VISIBLE
                bulletinView.setAdapter(ComplexViewAdapter(it))
                bulletinView.setOnItemClickListener { itemData, _, _ ->
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra(
                        WebViewSettings.LINK_KEY, link103
                    )
                    intent.putExtra(WebViewSettings.URL_TITLE, (itemData as Announce).announceTitle)
                    intent.putExtra(ANNOUNCEID, itemData.announceId)
                    intent.putExtra(WebViewSettings.isDataUrl, true)
                    startActivity(intent)
                }
            } else {
                llNotice.visibility = View.GONE
            }

        }

        loginViewModel.vip.observe(requireActivity()) {
            if (it.curVipExpireDate != null) {

                view.findViewById<TextView>(R.id.tv_vipcur).text = it.curVipExpireDate + "到期"

            }

        }


        if (MyApplication.isMarketVersion) {
            //去掉游戏记录，收益提现入口
            ll_gac.visibility = View.GONE

            //去掉我的店铺
            view.findViewById<View>(R.id.ll_l3).visibility = View.INVISIBLE
            view.findViewById<View>(R.id.ll_l3_t).visibility = View.INVISIBLE

            // 去掉 推广赚 收徒收益 、  合伙人业绩
            view.findViewById<View>(R.id.ll_tgz).visibility = View.GONE

            //悬赚消息，去掉
            view.findViewById<View>(R.id.ll_l12).visibility = View.GONE
            view.findViewById<View>(R.id.ll_l12_t).visibility = View.GONE

            view.findViewById<View>(R.id.ll_l13).visibility = View.VISIBLE
            view.findViewById<View>(R.id.ll_l13_t).visibility = View.VISIBLE

            view.findViewById<View>(R.id.ll_ll_l9).visibility = View.GONE
            view.findViewById<View>(R.id.ll_ll_l9t).visibility = View.GONE


        }

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }


    private fun loadData() {
        //获取系统消息数量
        sysMessageViewModel.getSysMessageNum()
        //执行获取用户信息接口
        loginViewModel.getUserInfo(ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID))

        //个人中心任务相关的数据
        loginViewModel.getRelated()

        //获取公告
        sysMessageViewModel.getAnnounce()

        loginViewModel.apiEarnings()//获取收徒功能页面数据

        loginViewModel.vipRest()//获取vip信息
    }

    companion object {

        @JvmStatic
        fun newInstance() = MyFragment()
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.ll_my_task -> {
                startActivity(Intent(activity, MyTaskActivity::class.java))
            }

            R.id.ll_l9, R.id.ll_l13 -> {
                startActivity(Intent(activity, AutoOutActivity::class.java))//关于我们
            }

            R.id.ll_l10 -> {
                startActivity(Intent(activity, MyMaterActivity::class.java))//我的师傅
            }

            R.id.ll_l11 -> {
                startActivity(Intent(activity, PartnerCenterActivity::class.java))//合伙人后台
            }

            R.id.ll_l12 -> {

                startActivity(Intent(activity, ChatListActivity::class.java))//雇主列表
            }

            R.id.iv_message -> {
                startActivity(Intent(activity, MessageActivity::class.java))//消息

            }

            R.id.customer -> {
                WxLogin.customer(requireActivity().application)
            }

            R.id.iv_log -> {
                startActivity(Intent(activity, ZJTaskHistoryActivity::class.java))//任务墙领奖记录
            }


            else -> {
                val intent = Intent(requireContext(), WebViewActivity::class.java)
                when (p0?.id) {


                    R.id.ll_l1 -> {
                        intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link5)
                        intent.putExtra(WebViewSettings.URL_TITLE, "选择任务分类")
                    }

                    R.id.ll_l2 -> {
                        intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link4)
                        intent.putExtra(WebViewSettings.URL_TITLE, getString(R.string.btm_zq))
                    }

                    R.id.ll_l3 -> {
                        intent.putExtra(WebViewSettings.LINK_KEY, WebViewSettings.link6)
                        intent.putExtra(WebViewSettings.URL_TITLE, "我的店铺")//我的店铺
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

    private fun showDialog(nikeName: String, materID: String, avatar: String) {
        dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_master)

        val cancel: View = dialog.findViewById(R.id.cancel)

        val name: TextView = dialog.findViewById(R.id.tv_name)
        val uid: TextView = dialog.findViewById(R.id.tv_id)
        val iv_head: ImageView = dialog.findViewById(R.id.iv_head)


        name.setOnLongClickListener {

            showToast("师傅昵称已复制到剪切板:${nikeName}")
            copyText(nikeName)
            false
        }

        uid.setOnLongClickListener {
            showToast("师傅ID已复制到剪切板:${materID}")
            copyText(materID)
            false
        }


        name.text = nikeName
        uid.text = "UID:${materID}"
        ImageViewUtil.loadCircleCrop(iv_head, avatar)

        cancel.setOnClickListener {
            //确定
            dialog.dismiss()
        }

        dialog.show()
    }
}