package com.dolphkon.me.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by langstone on 2018/7/31.
 */
public class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener {

    private int mLastDownX,mLastDownY;
    //该值记录了最小滑动距离
    private int touchSlop ;
    private OnItemClickListener mListener;
    //是否是单击事件
    private boolean isSingleTapUp = false;
    //是否是长按事件
    private boolean isLongPressUp = false;
    private boolean isMove = false;
    private long mDownTime;

    //内部接口，定义点击方法以及长按方法
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public RecyclerViewClickListener(Context context, OnItemClickListener listener){
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListener = listener;
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (e.getAction()){
            /**
             *  如果是ACTION_DOWN事件，那么记录当前按下的位置，
             *  记录当前的系统时间。
             */
            case MotionEvent.ACTION_DOWN:
                mLastDownX = x;
                mLastDownY = y;
                mDownTime = System.currentTimeMillis();
                isMove = false;
                break;
            /**
             *  如果是ACTION_MOVE事件，此时根据TouchSlop判断用户在按下的时候是否滑动了，
             *  如果滑动了，那么接下来将不处理点击事件
             */
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(x - mLastDownX)>touchSlop || Math.abs(y - mLastDownY)>touchSlop){
                    isMove = true;
                }
                break;
            /**
             *  如果是ACTION_UP事件，那么根据isMove标志位来判断是否需要处理点击事件；
             *  根据系统时间的差值来判断是哪种事件，如果按下事件超过1s，则认为是长按事件，
             *  否则是单击事件。
             */
            case MotionEvent.ACTION_UP:
                if(isMove){
                    break;
                }
                if(System.currentTimeMillis()-mDownTime > 1000){
                    isLongPressUp = true;
                }else {
                    isSingleTapUp = true;
                }
                break;
        }
        if(isSingleTapUp ){
            //根据触摸坐标来获取childView
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            isSingleTapUp = false;
            if(childView != null){
                //回调mListener#onItemClick方法
                mListener.onItemClick(childView,rv.getChildLayoutPosition(childView));
                return true;
            }
            return false;
        }
        if (isLongPressUp ){
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            isLongPressUp = false;
            if(childView != null){
                mListener.onItemLongClick(childView, rv.getChildLayoutPosition(childView));
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

