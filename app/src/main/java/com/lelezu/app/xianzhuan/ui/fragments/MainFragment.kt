package com.lelezu.app.xianzhuan.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.ApiConstants.ZJ_BUSINESS_POS_ID
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link1
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link2
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link3
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.zj.zjsdk.ad.ZjAdError
import com.zj.zjsdk.ad.ZjTaskAd
import com.zj.zjsdk.ad.ZjTaskAdListener


class MainFragment : BaseFragment(), OnClickListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: MyPagerAdapter
    private lateinit var tabLayout: TabLayout

    private lateinit var zjTask: ZjTaskAd

    private var isZjTaskLoadDone: Boolean = false//任务墙是否加载完成
    private var isZjTaskLoading: Boolean = false//任务墙是否加载中

    // 定义一个包含Tab文字的List
    private var tabTextList = arrayOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main2, container, false)
        viewPager = view.findViewById(R.id.task_vp)
        pagerAdapter = MyPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        tabLayout = view.findViewById(R.id.tab_task_list)

        // 定义一个包含Tab文字的List
        tabTextList = arrayOf(
            getString(R.string.l_task), getString(R.string.app_task), getString(R.string.game_task)
        )
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.ll_top_btm1).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_top_btm2).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_top_btm3).setOnClickListener(this)
        //Banner图初始化
        val viewFlipper = view.findViewById<ViewFlipper>(R.id.vp_banner)
//        viewFlipper.startFlipping()

        initTaskTabLayout()//初始化TabLayout


    }

    override fun onResume() {
        super.onResume()

        if (havePermission) {
            if (!isZjTaskLoadDone && !isZjTaskLoading) initZjTask()//执行广告sdk
        } else {
            checkPermission()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    override fun onClick(p0: View?) {

        // 用户已登录， 跳转到主页登录页面
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        when (p0?.id) {
            R.id.ll_top_btm1 -> {
                intent.putExtra(LINK_KEY, link1)
                intent.putExtra(URL_TITLE, getString(R.string.btm_mrjl))
            }

            R.id.ll_top_btm2 -> {
                intent.putExtra(LINK_KEY, link2)
                intent.putExtra(URL_TITLE, getString(R.string.btm_xrjl))
            }

            R.id.ll_top_btm3 -> {
                intent.putExtra(LINK_KEY, link3)
                intent.putExtra(URL_TITLE, getString(R.string.btm_zq))
            }
        }
        startActivity(intent)

    }

    private fun initZjTask() {

        isZjTaskLoading = true
        LogUtils.d("任务墙加载中")
        zjTask = ZjTaskAd(requireActivity(),
            ZJ_BUSINESS_POS_ID,
            ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID),
            object : ZjTaskAdListener {
                override fun onZjAdLoaded() {
                    LogUtils.d("任务墙加载完成")
                    isZjTaskLoadDone = true
                    isZjTaskLoading = false
                    addZjTaskFragment()
                }

                override fun onZjAdError(zjAdError: ZjAdError) {
                    showToast("任务墙加载错误:" + zjAdError.errorCode + "-" + zjAdError.errorMsg)
                }
            })
    }


    private fun initTaskTabLayout() {
        // 创建一个新的Tab对象
        val newTab = tabLayout.newTab()
        // 设置Tab的文本内容
        newTab.text = tabTextList[0]
        tabLayout.addTab(newTab)

        // 创建一个新的Tab对象 DK任务列表
        val newTab1 = tabLayout.newTab()
        // 创建一个新的Tab对象
        val newTab2 = tabLayout.newTab()

        // 将新的Tab添加到TabLayout中
        tabLayout.addTab(newTab1)
        tabLayout.addTab(newTab2)

        // 将TabLayout与ViewPager2关联起来
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()


    }


    //添加SDK任务列表
    fun addZjTaskFragment() {
        pagerAdapter.showZjTask(zjTask)

    }


    /**
     *
     * @property isShowZjTask Boolean
     * @property zjTask ZjTaskAd
     * @property fid1 Long
     * @property fid2 Long
     * @property fid3 Long
     * @property fid4 Long
     * @property fid5 Long
     * @property ids ArrayList<Long>
     * @property createdIds HashSet<Long>
     * @constructor 参考：https://zhuanlan.zhihu.com/p/105700960
     */
    class MyPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        private var isShowZjTask: Boolean = false
        private lateinit var zjTask: ZjTaskAd
        fun showZjTask(zj: ZjTaskAd) {
            isShowZjTask = true
            zjTask = zj
            notifyDataSetChanged()
        }

        private val fid1 = 111.toLong()
        private val fid2 = 222.toLong()
        private val fid3 = 333.toLong()
        private val fid4 = 444.toLong()
        private val fid5 = 555.toLong()


        private val ids: ArrayList<Long>
            get() = if (isShowZjTask) {
                arrayListOf(fid1, fid4, fid5)
            } else {
                arrayListOf(fid1, fid2, fid3)
            }

        private val createdIds = hashSetOf<Long>()

        override fun getItemCount(): Int {
            return if (true) 3 else 4
        }

        override fun getItemId(position: Int): Long {
            return ids[position]
        }

        override fun containsItem(itemId: Long): Boolean {
            return createdIds.contains(itemId)
        }

        override fun createFragment(position: Int): Fragment {
            val id = ids[position]
            createdIds.add(id)
            return when (id) {
                fid2 -> EmptyFragment()
                fid3 -> EmptyFragment()
                fid4 -> zjTask.loadCPAFragmentAd()
                fid5 -> zjTask.loadCPLFragmentAd()
                else -> MainTaskFragment.newInstance()
            }
        }
    }

    class EmptyFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragent_empty, container, false)
        }
    }

}