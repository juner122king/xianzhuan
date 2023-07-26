package com.lelezu.app.xianzhuan.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.Optional


/**
 * @author:Administrator
 * @date:2023/7/24 0024
 * @description:
 *
 */
open class BaseFragment : Fragment() {


    open fun optActivity(): Optional<FragmentActivity> {
        return Optional.ofNullable(activity)
    }
}