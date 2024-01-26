package com.lelezu.app.xianzhuan.ui.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.Announce
import com.lelezu.app.xianzhuan.ui.h5.WebViewSettings
import com.lelezu.app.xianzhuan.ui.views.WebViewActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * @author:Administrator
 * @date:2023/7/25 0020
 * @description:平台公告列表适配器
 *
 */
class MessageSysItemAdapter(
    private var items: List<Announce>, var context: Context,
) : RecyclerView.Adapter<MessageSysItemAdapter.ItemViewHolder>() {

    // 更新数据方法
    fun updateData(newItems: List<Announce>) {
        items = newItems
        notifyDataSetChanged()
    }

    // 创建 ItemViewHolder，用于展示每个列表项的视图
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val content: TextView = itemView.findViewById(R.id.tv_content)
        val time: TextView = itemView.findViewById(R.id.tv_message_time)
        val don: View = itemView.findViewById(R.id.v_tip) //红点

    }

    // 创建视图，并返回 ItemViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sys_message2_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    // 绑定数据到 ItemViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.announceTitle


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.content.text = Html.fromHtml(item.getShortContent(), Html.FROM_HTML_MODE_COMPACT)
        } else {
            holder.content.text = Html.fromHtml(item.getShortContent())
        }



        if (item.isRead) holder.don.visibility = View.INVISIBLE
        else holder.don.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.time.text = calculateTimeDifference(item.createdDt) //时间处理
        } else holder.time.text = item.createdDt


        holder.itemView.setOnClickListener {

            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(
                WebViewSettings.LINK_KEY, WebViewSettings.link103
            )
            intent.putExtra(WebViewSettings.URL_TITLE, item.announceTitle)
            intent.putExtra(WebViewSettings.ANNOUNCEID, item.announceId)
            intent.putExtra(WebViewSettings.isDataUrl, true)
            context.startActivity(intent)
        }


    }

    // 返回数据项数量
    override fun getItemCount(): Int {
        return items.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateTimeDifference(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateString)

        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - date.time
        // 计算时间差对应的单位
        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 1 -> {
                // 使用 Calendar 获取月份和日期
                val calendar = Calendar.getInstance()
                calendar.time = date
                // 获取月份和日期
                val month = calendar.get(Calendar.MONTH) + 1 // 注意：Calendar.MONTH 是从 0 开始计数的
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                // 大于1天，返回"X天前"
                "${month}月${day}日"
            }

            days == 1L -> {
                // 昨天，返回"昨天"
                "昨天"
            }

            hours > 0 -> {
                // 小于1天但大于0小时，返回"X小时前"
                "$hours 小时前"
            }

            minutes > 0 -> {
                // 小于1小时但大于0分钟，返回"X分钟前"
                "$minutes 分钟前"
            }

            else -> {
                // 小于1分钟，返回"刚刚"
                "刚刚"
            }
        }
    }
}
