package com.lelezu.app.xianzhuan.ui.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.view.setPadding
import com.lelezu.app.xianzhuan.R
import com.lelezu.app.xianzhuan.data.model.TaskType

/**
 * @author:Administrator
 * @date:2023/7/20 0020
 * @description:任务类型列表适配器
 *
 */
class TaskCondAdapter(var context: Context, var datas: List<TaskType>) : BaseAdapter() {

    private var selectposition: Int = 0//保存当前选择位置

    inner class MyHolder() {
        lateinit var textView: TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = null
        var myHolder: MyHolder? = null
        if (convertView == null) {
            myHolder = MyHolder()



            view = LayoutInflater.from(context).inflate(R.layout.item_grid_task_cond, null)
            myHolder.textView = view.findViewById(R.id.tv_task_type)

            view.tag = myHolder
        } else {
            view = convertView
            myHolder = view.tag as MyHolder
        }

        myHolder.textView.text = datas[position].typeTitle



        if (selectposition == position) {

            myHolder.textView.setTextColor(context.getColor(R.color.colorControlActivated))
            myHolder.textView.setBackgroundResource(R.drawable.radius_border_stroke_selected)
            myHolder.textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            myHolder.textView.setPadding(26, 10, 26, 10)
            myHolder.textView.gravity = Gravity.CENTER_HORIZONTAL

        } else {


            myHolder.textView.setTextColor(context.getColor(R.color.text_999))
            myHolder.textView.setBackgroundResource(R.drawable.radius_border_stroke)
            myHolder.textView.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            myHolder.textView.setPadding(26, 10, 26, 10)
            myHolder.textView.gravity = Gravity.CENTER_HORIZONTAL
        }




        view?.setOnClickListener {
            selectposition = position
            notifyDataSetChanged();
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        //获取指定位置(position)上的item对象，通常不需要修改
        return datas[position]
    }

    override fun getItemId(position: Int): Long {
        // 获取指定位置(position)上的item的id，通常不需要修改
        return position.toLong()
    }

    override fun getCount(): Int {
        //返回一个整数,就是要在listview中现实的数据的条数
        return datas.size
    }

    //获取选中的item对象
    public fun getSelectItem(): TaskType {

        return datas[selectposition]
    }


}

