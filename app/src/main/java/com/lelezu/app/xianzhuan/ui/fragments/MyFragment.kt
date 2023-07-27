package com.lelezu.app.xianzhuan.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.lelezu.app.xianzhuan.MyApplication
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.viewmodels.HomeViewModel
import com.lelezu.app.xianzhuan.ui.viewmodels.LoginViewModel2
import com.lelezu.app.xianzhuan.ui.viewmodels.SysMessageViewModel
import com.lelezu.app.xianzhuan.ui.views.AutoOutActivity
import com.lelezu.app.xianzhuan.ui.views.HomeActivity
import com.lelezu.app.xianzhuan.ui.views.MessageActivity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyFragment : Fragment(), View.OnClickListener {


    private val loginViewModel2: LoginViewModel2 by viewModels {
        LoginViewModel2.LoginViewFactory(((activity?.application as MyApplication).userRepository))
    }


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.iv_op).setOnClickListener(this)
        view.findViewById<View>(R.id.iv_message).setOnClickListener(this)

        view.findViewById<View>(R.id.ll_l1).setOnClickListener(this)
        view.findViewById<View>(R.id.ll_l9).setOnClickListener(this)


        //执行获取用户信息接口
//        loginViewModel2.getUserInfo()
//
//        loginViewModel2.userInfo.observe(requireActivity()) {
//            view.findViewById<TextView>(R.id.tv_user_name).text = it!!.nickname
//            view.findViewById<TextView>(R.id.tv_user_id).text = it!!.userId
//            view.findViewById<TextView>(R.id.tv_my_text1).text = it!!.rechargeAmount.toString()
//            view.findViewById<TextView>(R.id.tv_my_text2).text = it!!.balanceAmount.toString()
//            imageViewUserPic = view.findViewById(R.id.iv_user_pic)
//            imageViewUserPic.load(it.headImageUrl) { crossfade(true) }
//
//
//        }
        val imageViewUserPic = view.findViewById<ImageView>(R.id.iv_user_pic)
        imageViewUserPic.load("https://profile-avatar.csdnimg.cn/26922ef5d403474f917c8e68afeeca42_qq_25749749.jpg!1") {
            crossfade(1000)
            transformations(CircleCropTransformation())
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = MyFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ll_l1 -> startActivity(Intent(requireContext(), MessageActivity::class.java))
            R.id.ll_l9 -> startActivity(Intent(requireContext(), AutoOutActivity::class.java))

//            R.id.iv_op -> startActivity(Intent(requireContext(), MessageActivity::class.java))
            R.id.iv_message -> startActivity(Intent(requireContext(), MessageActivity::class.java))
        }
    }

}