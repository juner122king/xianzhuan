package com.lelezu.app.xianzhuan.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeActivityAdapter(FragmentManager: FragmentManager, lifecycle: Lifecycle,private val fragmentList: ArrayList<Fragment>) :
    FragmentStateAdapter(FragmentManager,lifecycle) {

    override fun getItemCount(): Int {
        // 返回 Fragment 的数量
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        // 根据位置创建对应的 Fragment
        return fragmentList[position]
    }
}

