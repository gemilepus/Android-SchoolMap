package com.vine.projectdemo.VinePHPMySQL;


import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.vine.projectdemo.R;
import java.util.ArrayList;


public class PHPDataAdapter extends RecyclerView.Adapter<PHPDataAdapter.ViewHolder> {
    private ArrayList<AndroidVersion> android;

    public PHPDataAdapter(ArrayList<AndroidVersion> android) {
        this.android = android;
    }

    @Override
    public PHPDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.php_card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PHPDataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tv_name.setText(android.get(i).getHead());
        viewHolder.tv_version.setText(android.get(i).getType());
        viewHolder.tv_api_level.setText(android.get(i).getText());
        viewHolder.itemView.setTag(i); // 標記  position
        // viewHolder.tv_Btn_Del.setTag( Integer.parseInt( android.get(i).getSno() ) ); // Tag
        viewHolder.tv_Btn_Del.setTag(i); // 標記  position
        viewHolder.tv_Btn_Del.setAlpha(200 ); // 透明度
        viewHolder.tv_Btn_Del.setScaleY((float)7/10.f); // 放大
        viewHolder.tv_Btn_Del.setScaleX((float)7/10.f); // 放大
        viewHolder.tv_Btn_Del.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int position =   (int) v.getTag(); // 1

                mOnImgClickListener.onImgClick( v , position ); // 2
            }

        });
        if(mOnItemClickListener != null){ //为ItemView设置监听器
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                  //  int position =   viewHolder2.getLayoutPosition(); // 1
//                    int position =   (int)viewHolder2.itemView.getTag(); // 1
//                    //int position =  viewHolder.getLayoutPosition();
//                    mOnItemClickListener.onItemClick( viewHolder2.itemView , position ); // 2

                    int position =   (int)v.getTag(); // 1
                    //int position =  viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick( v , position ); // 2
                }

            });

        }
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int position =   (int)v.getTag(); // 1
                mOnItemLongClickListener.onItemLongClick( v , position ); // 2
                return true;
            }


        });

    }

    @Override
    public int getItemCount() {
        return android.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name,tv_version,tv_api_level;
        private ImageButton tv_Btn_Del;

        public ViewHolder(View view) {
            super(view);
            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_version = (TextView)view.findViewById(R.id.tv_version);
            tv_api_level = (TextView)view.findViewById(R.id.tv_api_level);

            tv_Btn_Del = (ImageButton)view.findViewById(R.id.Btn_Del);
        }

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

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    public interface OnImgClickListener{
        void onImgClick(View view,int position);

    }

    private OnImgClickListener mOnImgClickListener;

    public void setOnImgClickListener(OnImgClickListener mOnImgClickListener){
        this.mOnImgClickListener = mOnImgClickListener;

    }
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
}