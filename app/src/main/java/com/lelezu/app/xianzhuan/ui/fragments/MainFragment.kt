package com.lelezu.app.xianzhuan.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
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
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.zj.zjsdk.ad.ZjAdError
import com.zj.zjsdk.ad.ZjTaskAd
import com.zj.zjsdk.ad.ZjTaskAdListener


class MainFragment : BaseFragment(), OnClickListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: MyPagerAdapter
    private lateinit var tabLayout: TabLayout

    private lateinit var zjTask: ZjTaskAd

    // 定义一个包含Tab文字的List
    private var tabTextList = arrayOf<String>()
    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        // 添加其他需要的权限
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main2, container, false)
        viewPager = view.findViewById(R.id.task_vp)
        pagerAdapter = MyPagerAdapter(childFragmentManager, lifecycle)
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

        initZjTask()//执行广告sdk
        addLocalTaskFragment()//加载本地任务列表


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
        // 检查是否已经有权限
        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (!hasPermissions) {
            try {
                // 请求权限
                requestPermissionLauncher.launch(permissions)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        } else {
            zjTask = ZjTaskAd(requireActivity(),
                ZJ_BUSINESS_POS_ID,
                ShareUtil.getString(ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID),
                object : ZjTaskAdListener {
                    override fun onZjAdLoaded() {
                        addZjTaskFragment()
                    }

                    override fun onZjAdError(zjAdError: ZjAdError) {
//                        showToast("任务墙加载错误:" + zjAdError.errorCode + "-" + zjAdError.errorMsg)
                    }
                })
        }

    }

    //添加本地任务列表
    private fun addLocalTaskFragment() {


        // 创建一个新的Tab对象
        val newTab = tabLayout.newTab()
        // 设置Tab的文本内容
        newTab.text = tabTextList[0]
        tabLayout.addTab(newTab)

        val mainTaskFragment = MainTaskFragment.newInstance()  //创建一个本地任务列表fragment
        pagerAdapter.addFragment(mainTaskFragment)
        pagerAdapter.notifyDataSetChanged()
    }

    //添加SDK任务列表
    fun addZjTaskFragment() {


        // 创建一个新的Tab对象
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

        pagerAdapter.addFragment(zjTask.loadCPAFragmentAd())
        pagerAdapter.addFragment(zjTask.loadCPLFragmentAd())
        pagerAdapter.notifyDataSetChanged()
    }

    class MyPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val fragments = mutableListOf<Fragment>()

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 在这里处理权限请求结果
        if (permissions.all { it.value }) {
            // 所有权限被授予
            initZjTask()
        } else {
            // 至少一个权限未被授予
//            Toast.makeText(
//                requireActivity(), "没有相关权限加载任务墙，请退出重试!", Toast.LENGTH_SHORT
//            ).show()
        }
    }

}