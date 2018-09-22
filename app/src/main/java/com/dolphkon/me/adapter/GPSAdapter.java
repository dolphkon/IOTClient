package com.dolphkon.me.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dolphkon.me.R;
import com.dolphkon.me.bean.GPSEventMsp;

import java.util.List;

/**
 * Created by dolphkon on 2018/8/28.
 */

public class GPSAdapter  extends RecyclerView.Adapter<GPSAdapter.GPSViewHolder> {
    private List<GPSEventMsp> mgpsList=null;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(GPSAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public GPSAdapter(Context context, List<GPSEventMsp> gpsList) {
        mgpsList = gpsList;
    }

    @Override
    public GPSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecycler_history_gps,parent, false);
        return new GPSAdapter.GPSViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final GPSViewHolder holder, final int position) {
        GPSEventMsp gpsEventMsp=mgpsList.get(position);
        holder.tv_gtdevice.setText(gpsEventMsp.getGdevice());
        holder.tv_gtime.setText(gpsEventMsp.getGtime());
        holder.tv_gip.setText(String.valueOf(gpsEventMsp.getGip()));
        holder.tv_gfrag.setText(gpsEventMsp.getGfrag());
        holder.tv_gtemperature.setText(gpsEventMsp.getGtemperature());
        holder.tv_glongitude.setText(String.valueOf(gpsEventMsp.getGlongitud()));
        holder.tv_dimension.setText(String.valueOf(gpsEventMsp.getDimension()));
        if(onItemClickListener!=null){
            holder.layout_gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mgpsList.size();
    }

     class GPSViewHolder extends RecyclerView.ViewHolder {
         TextView tv_gtdevice;
        TextView tv_gtime;
        TextView tv_gip;
        TextView tv_gfrag;
        TextView tv_gtemperature;
        TextView tv_glongitude;
        TextView tv_dimension;
         RelativeLayout layout_gps;
        public GPSViewHolder(View itemView) {
            super(itemView);
            tv_gtdevice=(TextView)itemView.findViewById(R.id.tv_gtdevice);
            tv_gtime = (TextView) itemView.findViewById(R.id.tv_gtime);
            tv_gip = (TextView) itemView.findViewById(R.id.tv_gip);
            tv_gfrag = (TextView) itemView.findViewById(R.id.tv_gfrag);
            tv_gtemperature = (TextView) itemView.findViewById(R.id.tv_gtemperature);
            tv_glongitude = (TextView) itemView.findViewById(R.id.tv_glongitude);
            tv_dimension = (TextView) itemView.findViewById(R.id.tv_dimension);
            layout_gps=(RelativeLayout)itemView.findViewById(R.id.layout_gps);
        }
    }
}