package com.vine.projectdemo.Adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vine.projectdemo.R;

public class PHPViewHolder extends RecyclerView.ViewHolder{

    public TextView tv_name,tv_version,tv_api_level;
    public ImageButton tv_Btn_Del;

    public PHPViewHolder(View view) {
        super(view);

        tv_name = (TextView)view.findViewById(R.id.txt_head);
        tv_version = (TextView)view.findViewById(R.id.txt_type);
        tv_api_level = (TextView)view.findViewById(R.id.txt_text);
        tv_Btn_Del = (ImageButton)view.findViewById(R.id.Btn_Del);
    }

}