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
    private ArrayList<JSONStructure> date;

    public DataAdapter(ArrayList<JSONStructure> date) {
        this.date = date;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.jsonparsing_card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tv_head.setText(date.get(i).getHead());
        viewHolder.tv_type.setText(date.get(i).getType());
        viewHolder.tv_text.setText(date.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return date.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_head,tv_type,tv_text;
        public ViewHolder(View view) {
            super(view);

            tv_head = (TextView)view.findViewById(R.id.txt_head);
            tv_type = (TextView)view.findViewById(R.id.txt_type);
            tv_text = (TextView)view.findViewById(R.id.txt_text);

        }
    }
}