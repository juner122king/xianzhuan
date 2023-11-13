package com.lelezu.app.xianzhuan.ui.views

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lelezu.app.xianzhuan.R


/**
 *  合伙人业绩-结算明细
 * @property statusMap Map<Int, String>
 * @property colorMap Map<Int, Int>
 * @property partnerItem Partner
 */
class PartnerCenterItemActivity : BaseActivity() {
    private val statusMap = mapOf(
        0 to "待结算",
        1 to "已结算",
        2 to "封号没收",
        // 可以继续添加其他映射关系
    )


    private val colorMap = mapOf(
        0 to R.color.text_check,
        1 to R.color.B3B3B3,
        2 to R.color.colorControlActivated,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {

        val t0 = intent.getIntExtra("T0", -1)
        val t1 = intent.getStringExtra("T1")
        val t2 = intent.getStringExtra("T2")
        val t3 = intent.getStringExtra("T3")
        val t4 = intent.getStringExtra("T4")
        val t5 = intent.getStringExtra("T5")
        val t6 = intent.getStringExtra("T6")
        val t7 = intent.getStringExtra("T7")
        val t8 = intent.getStringExtra("T8")


        // 设置文本颜色
        val statusColor = colorMap[t0] ?: R.color.colorControlActivated
        val resolvedColor = ContextCompat.getColor(this, statusColor)

        findViewById<TextView>(R.id.tv1).setTextColor(resolvedColor)

        findViewById<TextView>(R.id.tv1).text = statusMap[t0] ?: "未知状态"
        findViewById<TextView>(R.id.tv2).text = "￥${t1}"
        findViewById<TextView>(R.id.tv3).text = "${t2}个"
        findViewById<TextView>(R.id.tv4).text = "${t3}人"
        findViewById<TextView>(R.id.tv5).text = "${t4}"
        findViewById<TextView>(R.id.tv6).text = "${t5}"
        findViewById<TextView>(R.id.tv7).text = "￥${t6}"
        findViewById<TextView>(R.id.tv8).text = "￥${t7}"

        val topView = findViewById<View>(R.id.ll_top)
        if (t0 == 0) topView.visibility = View.VISIBLE else topView.visibility = View.GONE


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_partner_center_item
    }

    override fun getContentTitle(): String? {
        return intent.getStringExtra("T8") + "结算明细"
    }

    override fun isShowBack(): Boolean {
        return true
    }
}