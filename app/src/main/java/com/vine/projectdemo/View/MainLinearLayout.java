package com.vine.projectdemo.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class MainLinearLayout extends LinearLayout {

    public MainLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        Log.d("MainLinearLayout", " onInterceptTouchEvent");
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                Log.d("MainLinearLayout", " ACTION_OUTSIDE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("MainLinearLayout", " ACTION_POINTER_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("MainLinearLayout", " ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("MainLinearLayout", " ACTION_UP");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("MainLinearLayout", " ACTION_POINTER_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("MainLinearLayout", " ACTION_CANCEL");
                break;
        }
        onInterceptTouchListener.onLITouchEvent(event);


        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
        // scroll this container).
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent
        Log.d("MainLinearLayout", " onTouchEvent");

        return false;
    }

    private OnInterceptTouchListener onInterceptTouchListener;
    public interface OnInterceptTouchListener {
         void onLITouchEvent(MotionEvent event);
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        onInterceptTouchListener = listener;
    }

};