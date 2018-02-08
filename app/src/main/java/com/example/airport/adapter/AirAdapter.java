package com.example.airport.adapter;

import android.content.Context;

import com.example.airport.R;
import com.example.airport.base.adapter.BaseAdapter;
import com.example.airport.base.adapter.BaseRecyclerViewHolder;
import com.example.airport.modle.AirQuery;

import java.util.List;

/**
 * Created by dell on 2018/1/22.
 */

public class AirAdapter extends BaseAdapter<AirQuery> {


    public AirAdapter(Context context, List<AirQuery> airListQuery) {
        super(context, R.layout.layout_air_query, airListQuery);
    }


    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param viewHolder A fully initialized helper.
     * @param item       The item that needs to be displayed.
     * @param pos
     */
    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, AirQuery item, int pos) {
        viewHolder.getTextView(R.id.tv_airName).setText(item.getAirName());
        viewHolder.getTextView(R.id.tv_airPlanTime).setText(item.getAirPlanTime());
        viewHolder.getTextView(R.id.tv_airActualTime).setText(item.getAirActualTime());
        viewHolder.getTextView(R.id.tv_airStart).setText(item.getAirStart());
        viewHolder.getTextView(R.id.tv_airPlanArriveTime).setText(item.getAirPlanArriveTime());
        viewHolder.getTextView(R.id.tv_airActualArriveTime).setText(item.getAirActualArriveTime());
        viewHolder.getTextView(R.id.tv_airArrive).setText(item.getAirArrive());
        viewHolder.getTextView(R.id.tv_airOnTime).setText(item.getAirOnTime());
        if ("0".equals(item.getAirStatus())) {
            viewHolder.getTextView(R.id.tv_airStatus).setText(item.getAirStatus());
            viewHolder.getTextView(R.id.tv_airStatus).setTextColor(context.getResources().getColor(R.color.color_air_status));
        } else {
            viewHolder.getTextView(R.id.tv_airStatus).setText(item.getAirStatus());
            viewHolder.getTextView(R.id.tv_airStatus).setTextColor(context.getResources().getColor(R.color.color_red));
        }
    }
}
