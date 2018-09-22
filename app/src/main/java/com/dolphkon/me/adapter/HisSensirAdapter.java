package com.dolphkon.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dolphkon.me.R;
import com.dolphkon.me.bean.SensirEventMsg;
import com.dolphkon.me.bean.TemperEventMsg;

import java.util.List;
/**
 * Created by dolphkon on 2018/8/3.
 */

public class HisSensirAdapter extends RecyclerView.Adapter<HisSensirAdapter.HisSensirViewHolder>{
    private List<SensirEventMsg> mhisSenList=null;
    public HisSensirAdapter(Context context , List<SensirEventMsg> sensirEventMsgsList) {
        mhisSenList = sensirEventMsgsList;
    }

    @Override
    public HisSensirViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycler_history_snesirion, parent, false);
        return new HisSensirAdapter.HisSensirViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HisSensirViewHolder holder, int position) {
    SensirEventMsg sensirEventMsg=mhisSenList.get(position);
    holder.hisensir_device.setText(sensirEventMsg.getSdevice());
   holder.hisensir_time.setText(sensirEventMsg.getStime());
   holder.hisensir_battery.setText(String.valueOf(sensirEventMsg.getSbattery()));
   holder.hisensir_strength.setText(String.valueOf(sensirEventMsg.getStrength()));
   holder.hisensir_temperature.setText(String.valueOf(sensirEventMsg.getStemperature()));
   holder.hisensir_shumidity.setText(String.valueOf(sensirEventMsg.getShumidity()));
   holder.hisensir_address.setText(sensirEventMsg.getSaddress());
    }
    @Override
    public int getItemCount() {
        return mhisSenList.size();
    }
    static class HisSensirViewHolder extends RecyclerView.ViewHolder {
        TextView hisensir_type;
        TextView hisensir_device;
        TextView hisensir_time;
        TextView hisensir_temperature;
        TextView hisensir_strength;
        TextView hisensir_battery;
        TextView hisensir_address;
        TextView hisensir_shumidity;
        public HisSensirViewHolder(View view) {
            super(view);
            hisensir_type=(TextView)view.findViewById(R.id.hisensir_type);
            hisensir_device = (TextView) view.findViewById(R.id.hisensir_device);
            hisensir_time = (TextView) view.findViewById(R.id.hisensir_time);
            hisensir_temperature = (TextView) view.findViewById(R.id.hisensir_temperature);
            hisensir_strength = (TextView) view.findViewById(R.id.hisensir_strength);
            hisensir_battery = (TextView) view.findViewById(R.id.hisensir_battery);
            hisensir_address = (TextView) view.findViewById(R.id.hisensir_address);
            hisensir_shumidity=(TextView)view.findViewById(R.id.hisensir_shumidity);
        }
    }
}