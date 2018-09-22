package com.dolphkon.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolphkon.me.R;
import com.dolphkon.me.bean.TemperEventMsg;

import java.util.List;

/**
 * Created by dolphkon on 2018/8/3.
 */

public class HistempAdapter extends RecyclerView.Adapter<HistempAdapter.HistemViewHolder>{
    private List<TemperEventMsg> mtemperayureList=null;
    public HistempAdapter(Context context , List<TemperEventMsg> temperEventMsgsList) {
        mtemperayureList = temperEventMsgsList;
    }
    //    返回一个 ViewHolder
    @Override
    public HistempAdapter.HistemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将创建的View注册点击事件
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycle_history_temp, parent, false);

        return new HistempAdapter.HistemViewHolder(view);
    }

    //   在这里将View 里面的控件与ViewHolder 进行绑定(也就是给控件赋值)
    @Override
    public void onBindViewHolder(final HistempAdapter.HistemViewHolder holder, final int position) {
        final TemperEventMsg temperEventMsg = mtemperayureList.get(position);
        holder.histemp_tdevice.setText(temperEventMsg.getTdevice());
        holder.histemp_time.setText(temperEventMsg.getTime());
        holder.histemp_temperature.setText(String.valueOf(temperEventMsg.getTemperature()));
        holder.histemp_strength.setText(String.valueOf(temperEventMsg.getTstrength()));
        holder.histemp_battery.setText(String.valueOf(temperEventMsg.getTbattery()));
        holder.histemp_address.setText(temperEventMsg.getTaddress());
    }
    @Override
    public int getItemCount() {
        return mtemperayureList==null ? 0 : mtemperayureList.size();
    }
    static class HistemViewHolder extends RecyclerView.ViewHolder {
        TextView histemp_tdevice;
        TextView histemp_time;
        TextView histemp_temperature;
        TextView histemp_strength;
        TextView histemp_battery;
        TextView histemp_address;
        public HistemViewHolder(View view) {
            super(view);
            histemp_tdevice = (TextView) view.findViewById(R.id.histemp_tdevice);
            histemp_time = (TextView) view.findViewById(R.id.histemp_time);
            histemp_temperature = (TextView) view.findViewById(R.id.histemp_temperature);
            histemp_strength = (TextView) view.findViewById(R.id.histemp_strength);
            histemp_battery = (TextView) view.findViewById(R.id.histemp_battery);
            histemp_address = (TextView) view.findViewById(R.id.histemp_address);
        }
    }
}