package com.vine.projectdemo.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vine.projectdemo.Model.JSONStructure;
import com.vine.projectdemo.R;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<JSONStructure> android;

    public DataAdapter(ArrayList<JSONStructure> android) {
        this.android = android;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.jsonparsing_card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tv_name.setText(android.get(i).getHead());
        viewHolder.tv_version.setText(android.get(i).getType());
        viewHolder.tv_api_level.setText(android.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name,tv_version,tv_api_level;
        public ViewHolder(View view) {
            super(view);

            tv_name = (TextView)view.findViewById(R.id.txt_head);
            tv_version = (TextView)view.findViewById(R.id.txt_type);
            tv_api_level = (TextView)view.findViewById(R.id.txt_text);

        }
    }
}