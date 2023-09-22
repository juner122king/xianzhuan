package com.lelezu.app.xianzhuan.ui.views

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Partner

private const val ARG_PARAM = "Partner"

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
        1 to R.color.line2,
        2 to R.color.colorControlActivated,
    )
    private lateinit var partnerItem: Partner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {

        val bundle = intent.getParcelableExtra<Bundle>(ARG_PARAM)
        bundle?.let {
            partnerItem = when {
                Build.VERSION.SDK_INT >= 33 -> it.getParcelable(ARG_PARAM, Partner::class.java)!!
                else -> it.getParcelable(ARG_PARAM)!!
            }
            // 设置文本颜色
            val statusColor =
                colorMap[partnerItem.settlementStatus] ?: R.color.colorControlActivated
            val resolvedColor = ContextCompat.getColor(this, statusColor)
            findViewById<TextView>(R.id.tv1).setTextColor(resolvedColor)

            findViewById<TextView>(R.id.tv1).text =
                statusMap[partnerItem.settlementStatus] ?: "未知状态"
            findViewById<TextView>(R.id.tv2).text = "￥${partnerItem.earning}"
            findViewById<TextView>(R.id.tv3).text = "${partnerItem.taskCount}个"
            findViewById<TextView>(R.id.tv4).text = "${partnerItem.teamCount}个"
            findViewById<TextView>(R.id.tv5).text = "${partnerItem.teamLevel}"
            findViewById<TextView>(R.id.tv6).text = "${partnerItem.taskCount}个"
            findViewById<TextView>(R.id.tv7).text = "${partnerItem.taskCount}个"
            findViewById<TextView>(R.id.tv8).text = "${partnerItem.taskCount}个"
        }


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_partner_center_item
    }

    override fun getContentTitle(): String? {
        return "结算明细"
    }

    override fun isShowBack(): Boolean {
        return true
    }
}