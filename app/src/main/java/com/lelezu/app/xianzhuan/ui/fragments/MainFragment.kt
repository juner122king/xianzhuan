package com.lelezu.app.xianzhuan.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.ApiConstants.HOST
import com.lelezu.app.xianzhuan.data.ApiConstants.ZJ_BUSINESS_POS_ID
import com.lelezu.app.xianzhuan.data.model.ConfValue
import com.lelezu.app.xianzhuan.data.model.DBanner
import com.lelezu.app.xianzhuan.data.model.TaskQuery
import com.lelezu.app.xianzhuan.data.repository.TaskRepository
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.LINK_KEY
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.URL_TITLE
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link102
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link2
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings.link3
import com.lelezu.app.xianzhuan.ui.views.TaskDetailsActivity
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import com.lelezu.app.xianzhuan.utils.ImageViewUtil
import com.lelezu.app.xianzhuan.utils.LogUtils
import com.lelezu.app.xianzhuan.utils.MyPermissionUtil
import com.lelezu.app.xianzhuan.utils.ShareUtil
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import com.zj.zjsdk.ad.ZjAdError
import com.zj.zjsdk.ad.ZjTaskAd
import com.zj.zjsdk.ad.ZjTaskAdListener

class MainFragment : BaseFragment(), OnClickListener {


    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: MyPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var banner: Banner<DBanner, BannerImageAdapter<DBanner>>
    private lateinit var banner_iv: ImageView

    private lateinit var pics: List<DBanner>

    private lateinit var zjTask: ZjTaskAd

    private lateinit var ll_top_view: View//需要隐藏的View

    private var isZjTaskLoadDone: Boolean = false//任务墙是否加载完成
    private var isZjTaskLoading: Boolean = false//任务墙是否加载中

    // 定义一个包含Tab文字的List
    private var tabTextList = arrayOf<String>()
    private var isBannerSet = false // 添加标志变量
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        LogUtils.i("MainFragment", "onCreateView()")
        val view = inflater.inflate(R.layout.fragment_main2, container, false)

        viewPager = view.findViewById(R.id.task_vp)
        ll_top_view = view.findViewById(R.id.ll_top_view)
        pagerAdapter = MyPagerAdapter(this)

        viewPager.adapter = pagerAdapter


        tabLayout = view.findViewById(R.id.tab_task_list)


        //添加隐藏条件
        //1去掉新人奖励、收徒赚钱、招募合伙
        //2去掉热门任务，游戏试玩
        //3置顶任务改为  推荐，仅显示，问卷调查分类的数据，其他分类的数据不显示
        //4底部菜单去掉  悬赏大厅  、收徒赚钱

        if (MyApplication.isMarketVersion) {
            ll_top_view.visibility = View.GONE

            // 定义一个包含Tab文字的List
            tabTextList = arrayOf(
                getString(R.string.l_task2)
            )

        } else {
            ll_top_view.visibility = View.VISIBLE
            // 定义一个包含Tab文字的List
            tabTextList = arrayOf(
                getString(R.string.l_task),
                getString(R.string.app_task),
                getString(R.string.game_task)
            )
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.i("MainFragment", "onViewCreated()")

        view.findViewById<View>(R.id.ll_top_btm1).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_top_btm2).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_top_btm3).setOnClickListener(this)

        banner = view.findViewById(R.id.banner)
        banner_iv = view.findViewById(R.id.banner_iv)

        initTaskTabLayout()//初始化TabLayout


        initZjTask()
        initBanner()

    }

    private fun initBanner() {
        if (!MyApplication.isMarketVersion) {
            banner.visibility = View.VISIBLE
            // 添加条件来执行 setBanner 仅一次
            if (!isBannerSet) {
                //获取首页轮播图
                sysMessageViewModel.apiCarouselConfig()
                sysMessageViewModel.bannerconfig.observe(requireActivity()) {
                    pics = it  //需要添加一个对象持久保存图片url
                    setBanner()
                    isBannerSet = true // 标志设置为 true，以后不再执行 setBanner
                }
            } else {
                setBanner()
            }
        } else {
            banner_iv.visibility = View.VISIBLE
            ImageViewUtil.loadWH(banner_iv, R.drawable.icon_h_banner)//加载广告图
        }
    }

    private fun setBanner() {
        LogUtils.i("MainFragment", "加载setBanner")

        banner.apply {
            addBannerLifecycleObserver(requireActivity())
            setBannerRound(20f)
            indicator = CircleIndicator(requireActivity())
            setAdapter(object : BannerImageAdapter<DBanner>(pics) {
                override fun onBindView(
                    holder: BannerImageHolder, data: DBanner, position: Int, size: Int
                ) {
                    ImageViewUtil.loadWH(holder.imageView, data.bannerImg)//加载广告图

                    if (data.jumpUrl != "") {//如果URL不为空，则设置点击跳转到WebView
                        holder.imageView.setOnClickListener {
                            //判断data.jumpUrl内容去作不同的跳转
                            val intent = Intent(requireContext(), WebViewActivity::class.java)
                            intent.putExtra(LINK_KEY, HOST + data.jumpUrl)
                            intent.putExtra(URL_TITLE, data.bannerName)
                            startActivity(intent)
                        }
                    }




                    if (data.jumpUrl.isNotBlank()) {
                        holder.imageView.setOnClickListener {

                            if (data.jumpUrl.startsWith("http")) { //外部H5页面
                                val intent = Intent(requireContext(), WebViewActivity::class.java)
                                intent.putExtra(LINK_KEY, data.jumpUrl)
                                intent.putExtra(URL_TITLE, data.bannerName)
                                startActivity(intent)
                            } else if (data.jumpUrl.startsWith("activity://TaskDetails")) {//任务详情页面
                                val taskId = data.jumpUrl.substringAfterLast("=")
                                val intent = Intent(context, TaskDetailsActivity::class.java)
                                intent.putExtra("taskId", taskId)
                                startActivity(intent)
                            }
                            else if (data.jumpUrl.startsWith("activity://home")) {
                                val page = data.jumpUrl.substringAfterLast("=")
                                showToast("打开个人中心")
                            }
                            else { //内部H5页面
                                //判断data.jumpUrl内容去作不同的跳转
                                val intent = Intent(requireContext(), WebViewActivity::class.java)
                                intent.putExtra(LINK_KEY, HOST + data.jumpUrl)
                                intent.putExtra(URL_TITLE, data.bannerName)
                                startActivity(intent)

                            }
                        }
                    }


                }
            })

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
                intent.putExtra(LINK_KEY, link102)
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
        if (MyApplication.isMarketVersion) return

        if (XXPermissions.isGranted(
                requireActivity(), Permission.READ_PHONE_STATE, Permission.MANAGE_EXTERNAL_STORAGE
            )
        ) {
            onInitZjTask()
        } else {
            MyPermissionUtil.readPhoneStateApply(requireActivity(), object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (all) onInitZjTask()
                    else {
                        showToast("您授权的权限不全，热门任务和游戏试玩将不能加载！")
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    showToast("您已拒绝授权，热门任务和游戏试玩将不能加载！")
                }
            })
        }
    }

    private fun onInitZjTask() {
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
//                    showToast("任务墙加载错误:" + zjAdError.errorCode + "-" + zjAdError.errorMsg)
                    showToast("游戏任务列表数据加载缓慢")
                }
            })
    }


    private fun initTaskTabLayout() {
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
            return if (MyApplication.isMarketVersion) 1
            else 3
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
                else -> TaskListFragment.newInstance(
                    TaskQuery(
                        if (MyApplication.isMarketVersion) TaskRepository.queryCondCOMBO
                        else TaskRepository.queryCondTOP
                    )
                )
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