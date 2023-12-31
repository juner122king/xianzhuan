package com.lelezu.app.xianzhuan.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lelezu.app.xianzhuan.R


/**
 * @author:Administrator
 * @date:2023/8/1 0001
 * @description:
 *
 */
class RefreshRecycleView @JvmOverloads constructor(
    context: Context?, @Nullable attrs: AttributeSet? = null, defStyle: Int = 0
) : RecyclerView(context!!, attrs, defStyle), OnTouchListener {
    private var isDecoration //显示分隔线
            : Boolean = true


    private var isLoadMore //加载更多标志
            : Boolean = false
    private var isLoadEnd //加载到最后的标志
            : Boolean = false
    private var isLoadStart //顶部的标志
            : Boolean = false
    private var isRefresh //下拉刷新标志
            : Boolean = false
    private var listener //事件监听
            : IOnScrollListener? = null
    private var mLastY //监听移动的位置
            = 0f
    private val mEFRESHLoad = 0 //下拉刷新
    private val mORELoad = 1 //加载更多

    private var nowAction = -1//当前动作

    init {
        init()
    }

    /**
     * 初始化
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init() {

        val decoration = DividerItemDecoration(
            context, DividerItemDecoration.VERTICAL
        )
        context.getDrawable(R.drawable.divider)?.let { decoration.setDrawable(it) }


        if (isDecoration) addItemDecoration(decoration)


        isLoadEnd = false
        isLoadStart = true
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //SCROLL_STATE_DRAGGING 拖动时  和   SCROLL_STATE_IDLE 当屏幕停止滚动时
                // SCROLL_STATE_SETTLING 要移动到最后位置时
                if (newState == SCROLL_STATE_IDLE) {
                    loadData()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //上滑
                if (dy > 0) {

                    //是否滑到底部
                    isLoadEnd = !recyclerView.canScrollVertically(1)
                } else if (dy < 0) {

                    //是否滑到顶部
                    isLoadStart = !recyclerView.canScrollVertically(-1)
                }
            }
        })
        setOnTouchListener(this)
        layoutManager = LinearLayoutManager(context)
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        if (isLoadEnd) {
            // 判断是否已加载所有数据
            if (isLoadMore) { //未加载完所有数据，加载数据，并且还原isLoadEnd值为false，重新定位列表底部
                if (getListener() != null) {
                    getListener()!!.onLoadMore()
                    nowAction = mORELoad
                }
            } else { //加载完了所有的数据
                if (getListener() != null) {
                    getListener()!!.onLoaded()
                    nowAction = mORELoad
                }
            }
            isLoadEnd = false
        } else if (isLoadStart) {
            if (isRefresh) {
                if (getListener() != null) {
                    getListener()!!.onRefresh()
                    nowAction = mEFRESHLoad
                }
                isLoadStart = false
            }
        }
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = motionEvent.rawY
        }
        when (motionEvent.action) {
            MotionEvent.ACTION_MOVE -> {
                val deltaY = motionEvent.rawY - mLastY
                mLastY = motionEvent.rawY

                //向上移动
                if (deltaY < 0) {

                    //是否滑到底部
                    isLoadEnd = !canScrollVertically(1)
                } else if (deltaY > 0) {

                    //是否滑到顶部
                    isLoadStart = !canScrollVertically(-1)
                }
            }

            MotionEvent.ACTION_DOWN -> mLastY = motionEvent.rawY
            else -> mLastY = -1f
        }
        return false
    }

    //事件监听
    interface IOnScrollListener {
        fun onRefresh()
        fun onLoadMore()
        fun onLoaded()
    }

    private fun getListener(): IOnScrollListener? {
        return listener
    }

    //设置事件监听
    fun setListener(listener: IOnScrollListener?) {
        this.listener = listener
    }

    //设置是否支持加载更多
    fun setLoadMoreEnable(loadMore: Boolean) {
        isLoadMore = loadMore
    }

    //设置是否支持下拉刷新
    fun setRefreshEnable(refresh: Boolean) {
        isRefresh = refresh
    }
    fun isRefresh(): Boolean {//是否是刷新动作
        return nowAction == mEFRESHLoad
    }
}