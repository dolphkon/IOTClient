package com.dolphkon.me.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolphkon.me.R;
import com.dolphkon.me.applications.IOTApplication;
import com.dolphkon.me.bean.DoorEventMsg;
import com.dolphkon.me.utils.LogUtil;

import java.util.List;

/**
 * Created by dolphkon on 2018/7/30.
 */

public class DoorSwitchAdapter extends RecyclerView.Adapter<DoorSwitchAdapter.DoorSwitchViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<DoorEventMsg> mdoorswitchList;
    private Context context;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public DoorSwitchAdapter(Context context, List<DoorEventMsg> mdoorswitchList){
        this.context=context;
        this.mdoorswitchList=mdoorswitchList;
    }
    @Override
    public DoorSwitchAdapter.DoorSwitchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DoorSwitchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycler_doorswitch, parent, false));
    }
    @Override
    public void onBindViewHolder(final DoorSwitchViewHolder holder, final int position, List<Object> payloads) {
   if (payloads.isEmpty()){
       onBindViewHolder(holder, position);
   }else {
       if (mdoorswitchList.size() != 0&&mdoorswitchList!=null) {
           DoorEventMsg doorEventMsg=mdoorswitchList.get(position);
           holder.tv_device.setText(doorEventMsg.getDevice());
           holder.tv_daddress.setText(doorEventMsg.getDaddress());
           holder.tv_dtime.setText(doorEventMsg.getDtime());
           holder.tv_delectric.setText(String.valueOf(doorEventMsg.getDbattery()));
           holder.tv_dstrength.setText(String.valueOf(doorEventMsg.getDstrength()));
          if (doorEventMsg.getDisopen()==1){
              holder.img_disopen.setBackground(ContextCompat.getDrawable(IOTApplication.getContext(),R.drawable.door_off));
          }else {
              holder.img_disopen.setBackground(ContextCompat.getDrawable(IOTApplication.getContext(),R.drawable.door_on));
          }
       }
   }

    }
    @Override
    public void onBindViewHolder(final DoorSwitchAdapter.DoorSwitchViewHolder holder, final int position) {
           DoorEventMsg doorEventMsg=mdoorswitchList.get(position);
         holder.tv_device.setText(doorEventMsg.getDevice());
         holder.tv_daddress.setText(doorEventMsg.getDaddress());
         holder.tv_dtime.setText(doorEventMsg.getDtime());
         holder.tv_delectric.setText(String.valueOf(doorEventMsg.getDbattery()));
         holder.tv_dstrength.setText(String.valueOf(doorEventMsg.getDstrength()));
        if (doorEventMsg.getDisopen()==1){
            holder.img_disopen.setBackgroundResource(R.drawable.door_off);
        }else {
            holder.img_disopen.setBackgroundResource(R.drawable.door_on);
        }
        if(onItemClickListener!=null){
            holder.recydoor_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }

    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mdoorswitchList.size();
    }

    static class DoorSwitchViewHolder extends RecyclerView.ViewHolder {
        TextView tv_daddress;
        TextView tv_device;
        TextView tv_dtime;
        TextView tv_dstrength;
        TextView tv_delectric;
        ImageView img_disopen;
        RelativeLayout recydoor_item_layout;

        public DoorSwitchViewHolder(View itemView) {
            super(itemView);
            tv_daddress=(TextView)itemView.findViewById(R.id.tv_daddress);
            tv_device=(TextView)itemView.findViewById(R.id.tv_device);
            tv_dtime=(TextView)itemView.findViewById(R.id.tv_dtime);
            tv_dstrength=(TextView)itemView.findViewById(R.id.tv_dstrength);
            tv_delectric=(TextView)itemView.findViewById(R.id.tv_delectric);
            img_disopen=(ImageView)itemView.findViewById(R.id.img_disopen);
            recydoor_item_layout=itemView.findViewById(R.id.recydoor_item_layout);
        }
    }
}
