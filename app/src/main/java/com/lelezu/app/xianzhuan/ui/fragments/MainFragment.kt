package com.lelezu.app.xianzhuan.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link1
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link2
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link3
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.lelezu.app.xianzhuan.utils.ToastUtils
import com.zj.zjsdk.ad.ZjAdError
import com.zj.zjsdk.ad.ZjTaskAd
import com.zj.zjsdk.ad.ZjTaskAdListener


class MainFragment : Fragment(), OnClickListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: MyPagerAdapter

    private lateinit var zjTask: ZjTaskAd


    private val posID = "J1517087581"

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        // 添加其他需要的权限
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        viewPager = view.findViewById(R.id.task_vp)
        pagerAdapter = MyPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter


        addLocalTaskFragment()//加载本地任务列表

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<View>(R.id.ll_top_btm1).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_top_btm2).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_top_btm3).setOnClickListener(this)
        //Banner图初始化
        val viewFlipper = view.findViewById<ViewFlipper>(R.id.vp_banner)
        viewFlipper.startFlipping()


        initZjTask()//执行广告sdk

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
            // 请求权限
            requestPermissionLauncher.launch(permissions)
        } else {
            zjTask = ZjTaskAd(requireActivity(), posID, ShareUtil.APP_SHARED_PREFERENCES_LOGIN_ID, object : ZjTaskAdListener {
                override fun onZjAdLoaded() {
                    ToastUtils.showToast(requireActivity(), "任务墙加载成功", 0)
                    addZjTaskFragment()
                }
                override fun onZjAdError(zjAdError: ZjAdError) {
                    ToastUtils.showToast(
                        requireActivity(),
                        "任务墙加载错误:" + zjAdError.errorCode + "-" + zjAdError.errorMsg,
                        0
                    )
                }
            })
        }

    }

    //添加本地任务列表
    private fun addLocalTaskFragment() {
        val mainTaskFragment = MainTaskFragment.newInstance()  //创建一个本地任务列表fragment
        pagerAdapter.addFragment(mainTaskFragment)
        pagerAdapter.notifyDataSetChanged()
    }

    //添加SDK任务列表
    fun addZjTaskFragment() {
        pagerAdapter.addFragment(zjTask.loadCPLFragmentAd())
        pagerAdapter.addFragment(zjTask.loadCPAFragmentAd())
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
            Toast.makeText(requireActivity(), "已授权", Toast.LENGTH_SHORT).show()
            initZjTask()
        } else {
            // 至少一个权限未被授予
            Toast.makeText(
                requireActivity(), "没有相关权限加载任务墙，请退出重试!", Toast.LENGTH_SHORT
            ).show()
        }
    }

}