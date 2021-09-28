package com.vine.projectdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vine.projectdemo.VineReValues.GlobalVariable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class HomeFragment extends Fragment {

    public static final String TITLE = "查詢路線";
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    // Fragment Communicating
    SendMessage SM;

    Button buttonTest;
    RecyclerView recyclerView;
    HomeFragment.RecyclerviewAdapter myRecAdapter;
    List<DataObject> list;
    int ListOpenFlag = 0;

    private EditText searchTextStart , searchTextEnd;
    TextWatcher textWatcher;
    Button btnStart;
    int StartTextFlag = 0 , EndTextFlag = 0; // 起點 終點 的以選擇標記
    String searchString = "", StartPoints, EndPoints;
    short SelectStatus = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_content_main, container, false);
        LinearLayout LI_Home = (LinearLayout) v.findViewById(R.id.LIhome);// 介面上放Button的位置
        LI_Home.setVisibility(View.GONE);
        //recipient = (EditText) v.findViewById(R.id.recipient);
        //setContentView(R.layout.home_content_main);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//    EditText 取得焦點 但不會立刻彈出鍵盤

        buttonTest = (Button)v.findViewById(R.id.button2);
        buttonTest.setOnClickListener(new View.OnClickListener() { // 展開清單
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                activity.animateFAB();

                if(ListOpenFlag == 1){
                    ListOpenFlag = 0;
                    buttonTest.setText("+"); // button 要於全域宣告
                }else{
                    ListOpenFlag = 1;
                    buttonTest.setText("-");
                }

                final List<DataObject> filteredModelList = filter(list, "");//清單重置
                // RecyclerView 主體
                recyclerView.getLayoutManager().scrollToPosition(10);// 移動到對 X 個

//                ViewGroup.LayoutParams laParams =  recyclerView.getLayoutParams();
//                laParams.
                //myRecAdapter.notifyItemRemoved(10);
            }});

        btnStart = (Button) v.findViewById(R.id.Btn);
        btnStart.setEnabled(false);
        btnStart.setBackgroundColor(0xFF2F8FBB);
        btnStart.setOnClickListener(new View.OnClickListener() { // 展開清單
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setClass(HomeFragment.this.getActivity(),MapMainActivity.class);
                intent.putExtra("startstring",StartPoints);
                intent.putExtra("endstring",EndPoints);
                startActivity(intent);
            }});

        list = new ArrayList<DataObject>();
        list.add(0,new DataObject("二坪校區正門","二坪校區","126"));
        list.add(1,new DataObject("J1活動中心","二坪校區","139-142"));
        list.add(2,new DataObject("H1行政大樓","二坪校區","144-157-170"));
        list.add(3,new DataObject("F1第一研究大樓","二坪校區","147-172"));
        list.add(4,new DataObject("F2建築系館&設計學院","二坪校區","150-160"));
        list.add(5,new DataObject("K2教學大樓","二坪校區","153-178"));
        list.add(6,new DataObject("K3教學大樓","二坪校區","154-182"));
        list.add(7,new DataObject("景觀餐廳","二坪校區","163"));
        list.add(8,new DataObject("C1管理學院","二坪校區","168"));
        list.add(9,new DataObject("F3工業設計系館","二坪校區","175"));
        list.add(10,new DataObject("M2男一舍","二坪校區","184"));
        list.add(11,new DataObject("M8男二舍","二坪校區","180"));
        list.add(12,new DataObject("M4男三舍-影山樓","二坪校區","208-211"));
        list.add(13,new DataObject("M3女一舍&學生餐廳","二坪校區","199"));
        list.add(14,new DataObject("M7女二舍","二坪校區","173-196"));
        list.add(15,new DataObject("M6女三舍","二坪校區","193"));
        list.add(16,new DataObject("M1女五舍","二坪校區","203"));
        list.add(17,new DataObject("P1教職員工宿舍","二坪校區","215-217"));
        list.add(18,new DataObject("操場&籃球場","二坪校區","140"));
        list.add(19,new DataObject("蓮荷水世界","二坪校區","214"));
        list.add(20,new DataObject("網球場","二坪校區","218"));
        list.add(21,new DataObject("第一機車停車場","二坪校區","134-135"));
        list.add(22,new DataObject("第二機車停車場","二坪校區","190"));
        list.add(23,new DataObject("汽車停車場","二坪校區","225"));
        list.add(24,new DataObject("蓮荷文藝空間","二坪校區","188"));
        list.add(25,new DataObject("淑女坡","二坪校區","137"));
        list.add(26,new DataObject("好漢坡","二坪校區","219"));
        list.add(27,new DataObject("好客坡","二坪校區","186"));
        list.add(28,new DataObject("電機系館","八甲校區","73"));
        list.add(29,new DataObject("電子系館","八甲校區","40-74"));
        list.add(30,new DataObject("光電系館","八甲校區","52"));
        list.add(31,new DataObject("資訊系館","八甲校區","51-50"));
        list.add(32,new DataObject("國鼎圖書館","八甲校區","75"));
        list.add(33,new DataObject("資訊處","八甲校區","76"));
        list.add(34,new DataObject("理工學院一館","八甲校區","122-123-47-125"));
        list.add(35,new DataObject("理工學院二館","八甲校區","118-119"));
        list.add(36,new DataObject("共同教育委員會","八甲校區","80-81-82-83-84-85-86-94"));
        list.add(37,new DataObject("人文社會學院","八甲校區","88-89-90-91-92-93-96-97-98-99-100-101"));
        list.add(38,new DataObject("客家學院","八甲校區","105-106-107-108-109-112-113-114-115"));
        list.add(39,new DataObject("藝文教學中心","八甲校區","110-111-116"));
        list.add(40,new DataObject("學生餐廳","八甲校區","120"));
        list.add(41,new DataObject("第五男生宿舍","八甲校區","121"));
        list.add(42,new DataObject("風雨球場","八甲校區","59"));

        // Recyclerview
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        // Adapter
        myRecAdapter = new HomeFragment.RecyclerviewAdapter(list,HomeFragment.this.getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity()));
        recyclerView.setAdapter(myRecAdapter);
        // Item Spacing
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_cardview);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        //Edittext
        searchTextStart = (EditText) v.findViewById(R.id.editTextStart);
        searchTextEnd = (EditText) v.findViewById(R.id.editTextEnd);

        //  目前位置搜尋輸入
        searchTextStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before , int count) {
                // textView.setText(searchTextStart.getText());
                //SelectStatus=1;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Toast.makeText(ShowAddressActivity.this,"beforeTextChanged ",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void afterTextChanged(Editable s) {
                searchTextStart.setTextColor(Color.BLACK);
                String text = searchTextStart.getText().toString().toLowerCase(Locale.getDefault());
                final List<DataObject> filteredModelList = filter(list, text);
                if (filteredModelList.size() > 0) {
                    myRecAdapter.setFilter(filteredModelList);
                    //return true;
                } else {
                    // If not matching search filter data
                    //Toast.makeText(HomeFragment.this.getActivity(), "Not Found", Toast.LENGTH_SHORT).show();
                    //return false;
                }
            }
        });
        searchTextStart.setOnFocusChangeListener(new View.OnFocusChangeListener() { //FocusChange

            public void onFocusChange(View v, boolean hasFocus) {
                SelectStatus=1;
            }
        });
        searchTextEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {//FocusChange
            public void onFocusChange(View v, boolean hasFocus) {
                SelectStatus=2;
            }
        });

        // 目的位置搜尋輸入
        searchTextEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before , int count) {
                // textView1.setText(searchTextEnd.getText());
                //  SelectStatus=2;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Toast.makeText(ShowAddressActivity.this,"beforeTextChanged ",Toast.LENGTH_SHORT).show();

            }
            @Override
            public void afterTextChanged(Editable s) {

                searchTextEnd.setTextColor(Color.BLACK);


                String text = searchTextEnd.getText().toString().toLowerCase(Locale.getDefault());
                final List<DataObject> filteredModelList = filter(list, text);

                if (filteredModelList.size() > 0) {
                    myRecAdapter.setFilter(filteredModelList);
                    //return true;
                } else {
                    // If not matching search filter data
                    //Toast.makeText(HomeFragment.this.getActivity(), "Not Found", Toast.LENGTH_SHORT).show();
                    //return false;
                }
            }
        });

        final List<DataObject> filteredModelList = filter(list, "");//清單重置

        int[] location = new int[2];
        searchTextStart.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Adapter
    public class RecyclerviewAdapter extends RecyclerView.Adapter< HomeFragment.RecyclerviewAdapter.VH> {

        public List<DataObject> DataList;
        public Context context;
        ArrayList<DataObject> mModel;

        public RecyclerviewAdapter(List<DataObject> parkingList, Context context) {
            this.DataList = parkingList;
            this.context = context;
        }

        @Override
        public HomeFragment.RecyclerviewAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HomeFragment.RecyclerviewAdapter.VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false));
        }

        @Override
        public void onBindViewHolder(HomeFragment.RecyclerviewAdapter.VH holder, int position) { // 這裡產生 清單
            holder.htxt.setText(DataList.get(position).getHeading());
            holder.dtxt.setText(DataList.get(position).getDescription());
            holder.vtxt.setText(DataList.get(position).getValue());
            //HomeFragment.RecyclerviewAdapter.this.notifyItemRemoved(1);
            if( DataList.get(position).getDescription() == "Hello"){
                holder.itemView.setBackgroundColor(Color.parseColor("#4ba1fc")); // 改變顏色
                // holder.itemView.setBackground(R.drawable.back_blue);
                // holder.itemView.setBackground();
                // holder.itemView.getLayoutParams().resolveLayoutDirection(10);

                // HomeFragment.RecyclerviewAdapter.this.
                ViewGroup.LayoutParams laParams =holder.itemView.getLayoutParams();

                holder.itemView.getWidth();
                laParams.height = 150;
                laParams.width = 800; //800
                //holder.itemView.setLeft(800);//NO
                //myRecAdapter.
                holder.itemView.scrollTo(200,0);//OK (-200)
                //holder.itemView.requestLayout(); // RE
                holder.itemView.setLayoutParams(laParams);
//                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
//                params.rightMargin = 30;
//                view.setLayoutParams(params);
                //  ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams( );
                // layoutParams.setMargins(30, 20, 30, 0);
            }else{
                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff")); // 改變顏色
                ViewGroup.LayoutParams laParams =holder.itemView.getLayoutParams();
                holder.itemView.getWidth();
                laParams.height = 150;
                laParams.width = MATCH_PARENT;
                holder.itemView.scrollTo(0,0);
                //holder.itemView.setLeft(800);//NO
                //myRecAdapter.
                //holder.itemView.scrollTo(+200,0);
                holder.itemView.setLayoutParams(laParams);
            }

            DataObject txt = DataList.get(position);
            String startstring = txt.getHeading().toLowerCase(Locale.getDefault());
            // logic of highlighted text
            if (startstring.contains(searchString)) {
                int startPos = startstring.indexOf(searchString);
                int endPos = startPos + searchString.length();
                Spannable spanString = Spannable.Factory.getInstance().newSpannable(holder.htxt.getText());
                spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // red color of matching text
                holder.htxt.setText(spanString);
                // Toast.makeText(getApplicationContext(), String.valueOf(position) ,Toast.LENGTH_SHORT).show();// position 為 顯示的清單 第1個~最後
            }
        }

        @Override
        public int getItemCount() {
            return DataList.size();
        }

        public class VH extends RecyclerView.ViewHolder {
            TextView htxt,dtxt,vtxt;
            public VH(View itemView) {
                super(itemView);
                htxt = (TextView) itemView.findViewById(R.id.textViewMain);
                dtxt = (TextView) itemView.findViewById(R.id.textViewString);
                vtxt = (TextView) itemView.findViewById(R.id.textViewTemp);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String startstring = htxt.getText().toString(); // 讀取清單的文字
                        String point = vtxt.getText().toString();
                        if (SelectStatus==1) {
                            StartPoints = point;
                            SM.sendData("START".trim());  // PassingDataBetweenFragments  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            GlobalVariable globalVariable = (GlobalVariable)getActivity().getApplicationContext();
                            globalVariable.Start =  StartPoints;
                            searchTextStart.setText("(" + startstring + ")");
                            searchTextStart.setTextColor(Color.parseColor("#136388"));
                            //  textView.setText(StartPoints);
                            final List<DataObject> filteredModelList = filter(list, "");//清單重置
                            searchTextEnd.requestFocus();//  Focus 下一個 edittext
                            StartTextFlag = 1; // 起點已選擇
                        }
                        else {
                            EndPoints = point;
                            SM.sendData("END".trim());  // PassingDataBetweenFragments  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            GlobalVariable globalVariable = (GlobalVariable)getActivity().getApplicationContext();
                            globalVariable.End =  EndPoints;
                            searchTextEnd.setText("(" + startstring + ")");
                            searchTextEnd.setTextColor(Color.parseColor("#136388"));
                            //  textView1.setText(EndPoints);
                            final List<DataObject> filteredModelList = filter(list, "");//清單重置
                            btnStart.setEnabled(true);//  解除鎖定 Button
                            btnStart.setBackgroundColor(0xFF47C5FF);
                            EndTextFlag = 1; // 終點已選擇
                        }
                        Toast.makeText(HomeFragment.this.getActivity(), startstring, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public void setFilter(List<DataObject> countryModels) {
            mModel = new ArrayList<>();
            mModel.addAll(countryModels);
            notifyDataSetChanged();
        }

    }

    // Search Method
    private List<DataObject> filter(List<DataObject> models, String query) { // 產生  Search 後的清單
        query = query.toLowerCase();
        this.searchString = query;

        final List<DataObject> filteredModelList = new ArrayList<>();
        for (DataObject model : models) {
            final String text = model.getHeading().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model); // 加入 搜尋到的清單
                // Toast.makeText(getApplicationContext(), String.valueOf(model) ,Toast.LENGTH_SHORT).show();// position 為 顯示的清單 第1個~最後
                if( ListOpenFlag == 1){

                    filteredModelList.add(new DataObject("子清單","Hello","YEEEE")); // 加入子清單
                    //filteredModelList.removeAll();
                }else{
                    filteredModelList.remove("YEEEE");
                }
            }
        }
        myRecAdapter = new HomeFragment.RecyclerviewAdapter(filteredModelList, HomeFragment.this.getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity()));
        recyclerView.setAdapter(myRecAdapter);
        myRecAdapter.notifyDataSetChanged();

        return filteredModelList;
    }

    //  Fragment Communicating........................
    interface SendMessage {
        void sendData(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    //region #########################################################  test   ####################################################
    public void SetCommend() { // 測試

    }
    //endregion #########################################################  test   ##################################################
}