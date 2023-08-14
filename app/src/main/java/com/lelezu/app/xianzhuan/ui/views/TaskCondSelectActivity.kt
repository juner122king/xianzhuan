package com.lelezu.app.xianzhuan.ui.views

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.GridView
import android.widget.TextView
import androidx.core.view.setPadding
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.ui.adapters.TaskCondAdapter

/**
 * @author:Administrator
 * @date:2023/7/24 0024
 * @description:任务条件选择页面
 *
 */
class TaskCondSelectActivity : BaseActivity(), OnClickListener {

    private var selectPrice: Int = 0//喜好当前选中位置

    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    private lateinit var tv3: TextView
    private lateinit var tv4: TextView


    private val adapter = TaskCondAdapter(this, emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tv1 = findViewById(R.id.btu_price1)
        tv2 = findViewById(R.id.btu_price2)
        tv3 = findViewById(R.id.btu_price3)
        tv4 = findViewById(R.id.btu_price4)

        val gridView = findViewById<GridView>(R.id.gv_cond)
        val sButton = findViewById<View>(R.id.ll_ss)//搜索按钮
        sButton.setOnClickListener {
            Log.i(
                "条件选择",
                "id:" + adapter.getSelectItem().taskTypeId + "lp:" + getLowPrice() + "hp:" + getHighPrice()
            )

            val bundle = Bundle()
            bundle.putString("taskTypeId", adapter.getSelectItem().taskTypeId)
            bundle.putFloat("lowPrice", getLowPrice())
            bundle.putFloat("highPrice", getHighPrice())
            val intent = Intent(this, TaskSearchResultActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        homeViewModel.taskTypeList.observe(this) { itemList ->
            adapter.datas = itemList
            adapter.notifyDataSetChanged()
        }
        // 数据变化时更新数据
        gridView.adapter = adapter
        homeViewModel.getTaskTypeList()


        tv1.setOnClickListener(this)
        tv2.setOnClickListener(this)
        tv3.setOnClickListener(this)
        tv4.setOnClickListener(this)

        tv1.setPadding(18)
        tv2.setPadding(18)
        tv3.setPadding(18)
        tv4.setPadding(18)
    }


    private fun initView() {

        tv1.setTextColor(resources.getColor(R.color.text_999))
        tv1.setBackgroundResource(R.drawable.radius_border_stroke)
        tv1.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        tv1.setPadding(18)


        tv2.setTextColor(resources.getColor(R.color.text_999))
        tv2.setBackgroundResource(R.drawable.radius_border_stroke)
        tv2.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        tv2.setPadding(18)

        tv3.setTextColor(resources.getColor(R.color.text_999))
        tv3.setBackgroundResource(R.drawable.radius_border_stroke)
        tv3.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        tv3.setPadding(18)

        tv4.setTextColor(resources.getColor(R.color.text_999))
        tv4.setBackgroundResource(R.drawable.radius_border_stroke)
        tv4.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        tv4.setPadding(18)
    }

    override fun onClick(p0: View?) {

        initView()

        (p0 as TextView).setTextColor(resources.getColor(R.color.colorControlActivated))
        p0.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        p0.setBackgroundResource(R.drawable.radius_border_stroke_selected)
        p0.setPadding(18)


        when (p0?.id) {
            R.id.btu_price1 -> selectPrice = 0
            R.id.btu_price2 -> selectPrice = 1
            R.id.btu_price3 -> selectPrice = 2
            R.id.btu_price4 -> selectPrice = 3

        }
    }

    private fun getLowPrice(): Float {


        when (selectPrice) {
            0 -> return 0f
            1 -> return 1f
            2 -> return 3.01f
            3 -> return 10f
        }
        return 0f
    }

    private fun getHighPrice(): Float {
        when (selectPrice) {
            0 -> return 1f
            1 -> return 3f
            2 -> return 10f
            3 -> return 1000f
        }
        return 0f
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_task_cond_select
    }

    override fun getContentTitle(): String? {
        return getString(R.string.title_task_cs)
    }

    override fun isShowBack(): Boolean {
        return true
    }


}