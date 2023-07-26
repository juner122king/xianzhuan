package com.lelezu.app.xianzhuan.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.lelezu.app.xianzhuan.R


//自定义View  logo与广告词的组合UI
class CustomLogoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val imageView: ImageView
    private val textView1: TextView
    private val textView2: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_logo_view, this, true)
        imageView = findViewById(R.id.imageView)
        textView1 = findViewById(R.id.tv1)
        textView2 = findViewById(R.id.tv2)
    }

    // 提供公共方法来设置ImageView和TextView的内容、样式等

    fun setImageResource(resourceId: Int) {
        imageView.setImageResource(resourceId)
    }

    fun setText(text: CharSequence) {
        textView1.text = text
        textView2.text = text
    }
}
