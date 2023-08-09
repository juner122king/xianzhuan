package com.lelezu.app.xianzhuan.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R

/**
 * @author:Administrator
 * @date:2023/8/3 0003
 * @description:
 *
 */
abstract class EmptyAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private var emptyView: View? = null
    private var recyclerView: RecyclerView? = null

    private val observer: RecyclerView.AdapterDataObserver =
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                checkIfEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                checkIfEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                checkIfEmpty()
            }
        }

    fun setEmptyView(view: View) {


        this.emptyView = view.findViewById(R.id.empty_layout)
        this.recyclerView = view.findViewById(R.id.recyclerView)
        checkIfEmpty()
    }

    private fun checkIfEmpty() {
        if (emptyView != null && recyclerView != null) {
            val isEmpty = itemCount == 0
            emptyView?.visibility = if (isEmpty) View.VISIBLE else View.GONE
            recyclerView?.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
//
//        emptyView?.visibility =  View.VISIBLE
//        recyclerView?.visibility = View.GONE
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        registerAdapterDataObserver(observer)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        unregisterAdapterDataObserver(observer)
    }
}