package com.dolphkon.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolphkon.me.R;
import com.dolphkon.me.bean.SensirEventMsg;
import java.util.List;

/**
 * Created by dolphkon on 2018/7/30.
 */

public class SensirionAdapter extends RecyclerView.Adapter<SensirionAdapter.SensirionViewHolder> {
    List<SensirEventMsg>msensirEventMsgList=null;
    private  Context context;
    private SensirionAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(SensirionAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public SensirionAdapter(Context context, List<SensirEventMsg>msensirEventMsgList){
        this.context=context;
        this.msensirEventMsgList=msensirEventMsgList;

    }
    @Override
    public SensirionAdapter.SensirionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new SensirionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycle_sensirion, parent, false));
    }
    @Override
    public void onBindViewHolder(final SensirionAdapter.SensirionViewHolder holder, final int position) {
       holder.tv_stype.setText("温湿度传感器");
        SensirEventMsg sensirEventMsg=msensirEventMsgList.get(position);
        holder.tv_sdevice.setText(sensirEventMsg.getSdevice());
        holder.tv_selectric.setText(String.valueOf(sensirEventMsg.getSbattery()));
        holder.tv_strength.setText(String.valueOf(sensirEventMsg.getStrength()));
        holder.tv_shumidity.setText(String.valueOf(sensirEventMsg.getShumidity()));
        holder.tv_stemperature.setText(String.valueOf(sensirEventMsg.getStemperature()));
        holder.tv_stime.setText(sensirEventMsg.getStime());
        holder.tv_saddress.setText(sensirEventMsg.getSaddress());
        if(onItemClickListener!=null){
            holder.recy_sensirion_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return msensirEventMsgList.size();
    }


    static class SensirionViewHolder extends RecyclerView.ViewHolder {
        TextView tv_stype;
        TextView tv_sdevice;
        TextView tv_stime;
        TextView tv_stemperature;
        TextView tv_shumidity;
        TextView tv_strength;
        TextView tv_selectric;
        TextView tv_saddress;
        RelativeLayout recy_sensirion_item_layout;
        public  SensirionViewHolder(View view ){
            super(view);
           tv_stype=view.findViewById(R.id.tv_stype);
            tv_sdevice=view.findViewById(R.id.tv_sdevice);
            tv_stime=view.findViewById(R.id.tv_stime);
            tv_stemperature=view.findViewById(R.id.tv_stemperature);
            tv_shumidity=view.findViewById(R.id.tv_shumidity);
            tv_strength=view.findViewById(R.id.tv_strength);
            tv_selectric=view.findViewById(R.id.tv_selectric);
            tv_saddress=view.findViewById(R.id.tv_saddress);
            recy_sensirion_item_layout=view.findViewById(R.id.recy_sensirion_item_layout);
        }
    }
}
