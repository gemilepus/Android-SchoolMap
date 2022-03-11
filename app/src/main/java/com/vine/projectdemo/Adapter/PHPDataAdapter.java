package com.vine.projectdemo.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vine.projectdemo.Adapter.PHPViewHolder;
import com.vine.projectdemo.Model.JSONStructure;
import com.vine.projectdemo.R;
import java.util.ArrayList;

public class PHPDataAdapter extends RecyclerView.Adapter<PHPViewHolder> {

    private ArrayList<JSONStructure> android;

    public PHPDataAdapter(ArrayList<JSONStructure> android) {
        this.android = android;
    }

    @Override
    public PHPViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.php_card_row, viewGroup, false);
        return new PHPViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PHPViewHolder viewHolder, int i) {
        viewHolder.tv_name.setText(android.get(i).getHead());
        viewHolder.tv_version.setText(android.get(i).getType());
        viewHolder.tv_api_level.setText(android.get(i).getText());
        viewHolder.itemView.setTag(i);   // 標記  position
        viewHolder.tv_Btn_Del.setTag(i); // 標記  position
        viewHolder.tv_Btn_Del.setAlpha(200 );
        viewHolder.tv_Btn_Del.setScaleY((float)7/10.f);
        viewHolder.tv_Btn_Del.setScaleX((float)7/10.f);
        viewHolder.tv_Btn_Del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                mOnImgClickListener.onImgClick( v , position );
            }
        });
        if(mOnItemClickListener != null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)v.getTag();
                    mOnItemClickListener.onItemClick( v , position );
                }

            });

        }
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int position =  (int)v.getTag();
                mOnItemLongClickListener.onItemLongClick( v , position );
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public interface OnItemClickListener{
      void onItemClick(View view,int position);
       
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;

    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener){
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public interface OnImgClickListener{
        void onImgClick(View view,int position);
    }

    private OnImgClickListener mOnImgClickListener;

    public void setOnImgClickListener(OnImgClickListener mOnImgClickListener){
        this.mOnImgClickListener = mOnImgClickListener;

    }
}