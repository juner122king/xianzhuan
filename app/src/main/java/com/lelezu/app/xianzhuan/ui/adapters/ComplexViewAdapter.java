package com.lelezu.app.xianzhuan.ui.adapters;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lelezu.app.xianzhuan.R;
import com.lelezu.app.xianzhuan.data.model.Announce;
import com.lelezu.app.xianzhuan.ui.views.BulletinView;

import java.util.List;

/**
 * @author:Administrator
 * @date:2023/8/17 0017
 * @description:公告TextView，支持滚动
 */
public class ComplexViewAdapter extends BulletinView.BulletinViewadapter<String> {


    public ComplexViewAdapter(List<String> data) {
        super(data);
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    @Override
    public View onBindViewHolder(View itemView, int position, String itemData) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((TextView) itemView.findViewById(R.id.title)).setText(Html.fromHtml(itemData, Html.FROM_HTML_MODE_COMPACT));
        } else {
            ((TextView) itemView.findViewById(R.id.title)).setText(Html.fromHtml(itemData));
        }

        return itemView;
    }

    @Override
    protected View onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {

        return inflater.inflate(R.layout.item_comples_view, parent);
    }

}