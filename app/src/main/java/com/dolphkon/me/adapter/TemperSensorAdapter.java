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
 * Created by dolphkon on 2018/7/19.
 */
public class TemperSensorAdapter extends RecyclerView.Adapter<TemperSensorAdapter.TemperSensorViewHolder> {
    private List<TemperEventMsg> mtemperayureList = null;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public TemperSensorAdapter(Context context, List<TemperEventMsg> temperEventMsgsList) {
        mtemperayureList = temperEventMsgsList;
    }

    //    返回一个 ViewHolder
    @Override
    public TemperSensorAdapter.TemperSensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将创建的View注册点击事件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycle_temper, parent, false);

        return new TemperSensorViewHolder(view);
    }

    //   在这里将View 里面的控件与ViewHolder 进行绑定(也就是给控件赋值)
    @Override
    public void onBindViewHolder(final TemperSensorAdapter.TemperSensorViewHolder holder, final int position) {
        holder.tv_type.setText("温度传感器");
        TemperEventMsg temperEventMsg = mtemperayureList.get(position);
        holder.tv_tdevice.setText(temperEventMsg.getTdevice());
        holder.tv_time.setText(temperEventMsg.getTime());
        holder.tv_temperature.setText(String.valueOf(temperEventMsg.getTemperature()));
        holder.tv_tstrength.setText(String.valueOf(temperEventMsg.getTstrength()));
        holder.tv_telectric.setText(String.valueOf(temperEventMsg.getTbattery()));
        holder.tv_taddress.setText(temperEventMsg.getTaddress());
        if (onItemClickListener != null) {
            holder.recytemper_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        Object tag = holder.recytemper_item_layout.getTag();
        if(tag==null || !tag.equals(holder.getItemId())){
            holder.recytemper_item_layout.setTag(holder.getItemId());
            holder.tv_type.setText("温度传感器");
            temperEventMsg = mtemperayureList.get(position);
            holder.tv_tdevice.setText(temperEventMsg.getTdevice());
            holder.tv_time.setText(temperEventMsg.getTime());
            holder.tv_temperature.setText(String.valueOf(temperEventMsg.getTemperature()));
            holder.tv_tstrength.setText(String.valueOf(temperEventMsg.getTstrength()));
            holder.tv_telectric.setText(String.valueOf(temperEventMsg.getTbattery()));
            holder.tv_taddress.setText(temperEventMsg.getTaddress());
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return mtemperayureList == null ? 0 : mtemperayureList.size();
    }

    static class TemperSensorViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type;
        TextView tv_tdevice;
        TextView tv_time;
        TextView tv_temperature;
        TextView tv_tstrength;
        TextView tv_telectric;
        TextView tv_taddress;
        RelativeLayout recytemper_item_layout;

        public TemperSensorViewHolder(View view) {
            super(view);
            tv_type = (TextView) view.findViewById(R.id.tv_type);
            tv_tdevice = (TextView) view.findViewById(R.id.tv_tdevice);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_temperature = (TextView) view.findViewById(R.id.tv_temperature);
            tv_tstrength = (TextView) view.findViewById(R.id.tv_strength);
            tv_telectric = (TextView) view.findViewById(R.id.tv_telectric);
            tv_taddress = (TextView) view.findViewById(R.id.tv_taddress);
            recytemper_item_layout = (RelativeLayout) view.findViewById(R.id.recytemper_item_layout);
        }
    }
}
