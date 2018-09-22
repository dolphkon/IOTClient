package com.dolphkon.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolphkon.me.R;
import com.dolphkon.me.bean.DoorEventMsg;

import java.lang.annotation.ElementType;
import java.util.List;
/**
 * Created by dolphkon on 2018/8/3.
 */
public class HisdoorAdapter extends RecyclerView.Adapter<HisdoorAdapter.HisdoorViewHolder> {
    private List<DoorEventMsg> mhisdoorList=null;
    public HisdoorAdapter(Context context , List<DoorEventMsg> doorEventMsgsList) {
        mhisdoorList = doorEventMsgsList;
    }
    @Override
    public HisdoorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将创建的View注册点击事件
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycle_history_door, parent, false);
        return new HisdoorAdapter.HisdoorViewHolder(view);
    }
    @Override
    public void onBindViewHolder(HisdoorViewHolder holder, int position) {
        final DoorEventMsg doorEventMsg = mhisdoorList.get(position);
        holder.hisdoor_device.setText(doorEventMsg.getDevice());
        holder.hisdoor_dtime.setText(doorEventMsg.getDtime());
        holder.hisdoor_dstrength.setText(String.valueOf(doorEventMsg.getDstrength()));
        holder.hisdoor_dbattery.setText(String.valueOf(doorEventMsg.getDbattery()));
        holder.hisdoor_daddress.setText(doorEventMsg.getDaddress());
    }

    @Override
    public int getItemCount() {
        return mhisdoorList.size();
    }

    static class HisdoorViewHolder extends RecyclerView.ViewHolder {
        TextView hisdoor_device;
        TextView hisdoor_dtime;
        TextView hisdoor_disopen;
        TextView hisdoor_dstrength;
        TextView hisdoor_dbattery;
        TextView hisdoor_daddress;
        RelativeLayout recytemper_item_layout;
        public HisdoorViewHolder(View view) {
            super(view);
            hisdoor_device = (TextView) view.findViewById(R.id.hisdoor_device);
            hisdoor_dtime = (TextView) view.findViewById(R.id.hisdoor_dtime);
            hisdoor_disopen = (TextView) view.findViewById(R.id.hisdoor_disopen);
            hisdoor_dstrength = (TextView) view.findViewById(R.id.hisdoor_dstrength);
            hisdoor_dbattery = (TextView) view.findViewById(R.id.hisdoor_dbattery);
            hisdoor_daddress = (TextView) view.findViewById(R.id.hisdoor_daddress);
        }
    }
}
