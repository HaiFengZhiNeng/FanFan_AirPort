package com.example.airport.base.adapter;

import android.content.Context;

import java.util.List;

public abstract class BaseAdapter<T> extends BaseRecyclerAdapter<T, BaseRecyclerViewHolder>  {

    public BaseAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public BaseAdapter(Context context, int layoutResId, List<T> datas) {
        super(context, layoutResId, datas);
    }

}
