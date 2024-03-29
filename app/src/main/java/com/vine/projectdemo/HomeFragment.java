package com.vine.projectdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vine.projectdemo.Adapter.SpacesItemDecoration;
import com.vine.projectdemo.Model.DataObject;
import com.vine.projectdemo.Values.GlobalVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class HomeFragment extends Fragment {

    public static final String TITLE = "Home";
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    // Fragment Communicating
    SendMessage SM;

    Button buttonTest;
    RecyclerView recyclerView;
    HomeFragment.RecyclerviewAdapter myRecAdapter;
    public static List<DataObject> list;

    private EditText searchTextStart , searchTextEnd;
    Button btnStart;
    int StartTextFlag = 0 , EndTextFlag = 0; // 起點 終點 的以選擇標記
    String searchString = "", StartPoints, EndPoints;
    short SelectStatus = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_content_main, container, false);
        LinearLayout LI_Home = (LinearLayout) v.findViewById(R.id.LIhome);
        LI_Home.setVisibility(View.GONE);

        btnStart = (Button) v.findViewById(R.id.Btn);
        btnStart.setEnabled(false);
        btnStart.setBackgroundColor(0xFF2F8FBB);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setClass(HomeFragment.this.getActivity(),MapMainActivity.class);
                intent.putExtra("startstring",StartPoints);
                intent.putExtra("endstring",EndPoints);
                startActivity(intent);
            }});

        list = new ArrayList<DataObject>();
        list.add(0,new DataObject(getResources().getString(R.string.School_Gate),getResources().getString(R.string.Erping_Shan_Campus),"126"));
        list.add(1,new DataObject(getResources().getString(R.string.J1_Activity_Center_Building),getResources().getString(R.string.Erping_Shan_Campus),"139-142"));
        list.add(2,new DataObject(getResources().getString(R.string.H1_Industrial_Research_Innovation_and_Promotion_Building),getResources().getString(R.string.Erping_Shan_Campus),"144-157-170"));
        list.add(3,new DataObject(getResources().getString(R.string.F1_Architecture_Building_1),getResources().getString(R.string.Erping_Shan_Campus),"147-172"));
        list.add(4,new DataObject(getResources().getString(R.string.F2_Architecture_Building_2),getResources().getString(R.string.Erping_Shan_Campus),"150-160"));
        list.add(5,new DataObject(getResources().getString(R.string.K2_Teaching_Building),getResources().getString(R.string.Erping_Shan_Campus),"153-178"));
        list.add(6,new DataObject(getResources().getString(R.string.K3_General_Building),getResources().getString(R.string.Erping_Shan_Campus),"154-182"));
        list.add(7,new DataObject(getResources().getString(R.string.View_Restaurant),getResources().getString(R.string.Erping_Shan_Campus),"163"));
        list.add(8,new DataObject(getResources().getString(R.string.C1_Li_teh_Building),getResources().getString(R.string.Erping_Shan_Campus),"168"));
        list.add(9,new DataObject(getResources().getString(R.string.F3_Kung_tao_Building_Department_of_Industrial_Design),getResources().getString(R.string.Erping_Shan_Campus),"175"));
        list.add(10,new DataObject(getResources().getString(R.string.M2_Mens_Dorm_1),getResources().getString(R.string.Erping_Shan_Campus),"184"));
        list.add(11,new DataObject(getResources().getString(R.string.M8_Mens_Dorm_2),getResources().getString(R.string.Erping_Shan_Campus),"180"));
        list.add(12,new DataObject(getResources().getString(R.string.M4_Mens_Dorm_1),getResources().getString(R.string.Erping_Shan_Campus),"208-211"));
        list.add(13,new DataObject(getResources().getString(R.string.M3_Ladies_Dorm_1),getResources().getString(R.string.Erping_Shan_Campus),"199"));
        list.add(14,new DataObject(getResources().getString(R.string.M7_Ladies_Dorm_2),getResources().getString(R.string.Erping_Shan_Campus),"173-196"));
        list.add(15,new DataObject(getResources().getString(R.string.M6_Ladies_Dorm_3),getResources().getString(R.string.Erping_Shan_Campus),"193"));
        list.add(16,new DataObject(getResources().getString(R.string.M1_Ladies_Dorm_5),getResources().getString(R.string.Erping_Shan_Campus),"203"));
        list.add(17,new DataObject(getResources().getString(R.string.P1_Faculty_and_Staff_Residence_Hall),getResources().getString(R.string.Erping_Shan_Campus),"215-217"));
        list.add(18,new DataObject(getResources().getString(R.string.Sports_Field),getResources().getString(R.string.Erping_Shan_Campus),"140"));
        list.add(19,new DataObject(getResources().getString(R.string.Swimming_Pool),getResources().getString(R.string.Erping_Shan_Campus),"214"));
        list.add(20,new DataObject(getResources().getString(R.string.Tennis_Court),getResources().getString(R.string.Erping_Shan_Campus),"218"));
        list.add(21,new DataObject(getResources().getString(R.string.Parking_Lot_1),getResources().getString(R.string.Erping_Shan_Campus),"134-135"));
        list.add(22,new DataObject(getResources().getString(R.string.Parking_Lot_2),getResources().getString(R.string.Erping_Shan_Campus),"190"));
        list.add(23,new DataObject(getResources().getString(R.string.Parking_Lot),getResources().getString(R.string.Erping_Shan_Campus),"225"));
        list.add(24,new DataObject(getResources().getString(R.string.Art_Center),getResources().getString(R.string.Erping_Shan_Campus),"188"));
        list.add(25,new DataObject(getResources().getString(R.string.Shu_Nyu_Hiking_Trail),getResources().getString(R.string.Erping_Shan_Campus),"137"));
        list.add(26,new DataObject(getResources().getString(R.string.Hao_Han_Hiking_Trail),getResources().getString(R.string.Erping_Shan_Campus),"219"));
        list.add(27,new DataObject(getResources().getString(R.string.Hao_Ke_Hiking_Trail),getResources().getString(R.string.Erping_Shan_Campus),"186"));
        list.add(28,new DataObject(getResources().getString(R.string.Department_of_Electrical_Engineering),getResources().getString(R.string.Bajia_Campus),"73"));
        list.add(29,new DataObject(getResources().getString(R.string.Department_of_Electronic_Engineering),getResources().getString(R.string.Bajia_Campus),"40-74"));
        list.add(30,new DataObject(getResources().getString(R.string.Department_of_Electro_Optical_Engineering),getResources().getString(R.string.Bajia_Campus),"52"));
        list.add(31,new DataObject(getResources().getString(R.string.Department_of_Computer_Science_and_Information_Engineering),getResources().getString(R.string.Bajia_Campus),"51-50"));
        list.add(32,new DataObject(getResources().getString(R.string.Kwon_Thas_Library),getResources().getString(R.string.Bajia_Campus),"75"));
        list.add(33,new DataObject(getResources().getString(R.string.International_Conference_Hail),getResources().getString(R.string.Bajia_Campus),"76"));
        list.add(34,new DataObject(getResources().getString(R.string.College_of_Engineering_Science_Building_1),getResources().getString(R.string.Bajia_Campus),"122-123-47-125"));
        list.add(35,new DataObject(getResources().getString(R.string.College_of_Engineering_Science_Building_2),getResources().getString(R.string.Bajia_Campus),"118-119"));
        list.add(36,new DataObject(getResources().getString(R.string.Osmon_Eaucation_Committee),getResources().getString(R.string.Bajia_Campus),"80-81-82-83-84-85-86-94"));
        list.add(37,new DataObject(getResources().getString(R.string.College_of_Fumanities_and_Sociales),getResources().getString(R.string.Bajia_Campus),"88-89-90-91-92-93-96-97-98-99-100-101"));
        list.add(38,new DataObject(getResources().getString(R.string.College_of_Hakka_Studies),getResources().getString(R.string.Bajia_Campus),"105-106-107-108-109-112-113-114-115"));
        list.add(39,new DataObject(getResources().getString(R.string.Arts_Center_2),getResources().getString(R.string.Bajia_Campus),"110-111-116"));
        list.add(40,new DataObject(getResources().getString(R.string.Round_Canteen),getResources().getString(R.string.Bajia_Campus),"120"));
        list.add(41,new DataObject(getResources().getString(R.string.Student_Dormitory),getResources().getString(R.string.Bajia_Campus),"121"));
        list.add(42,new DataObject(getResources().getString(R.string.Athlete_Field),getResources().getString(R.string.Bajia_Campus),"59"));

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
        // Edittext
        searchTextStart = (EditText) v.findViewById(R.id.editTextStart);
        searchTextEnd = (EditText) v.findViewById(R.id.editTextEnd);

        //  目前位置搜尋輸入
        searchTextStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before , int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("HomeFragment", "beforeTextChanged ");
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
                    Log.i("HomeFragment", "Not Found");
                    //return false;
                }
            }
        });
        searchTextStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                SelectStatus=1;
            }
        });
        searchTextEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                SelectStatus=2;
            }
        });

        // 目的位置搜尋輸入
        searchTextEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before , int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("HomeFragment", "beforeTextChanged ");
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
                    Log.i("HomeFragment", "Not Found");
                    //return false;
                }
            }
        });

        final List<DataObject> filteredModelList = filter(list, "");

        int[] location = new int[2];
        searchTextStart.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        buttonTest = (Button)v.findViewById(R.id.button2);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                activity.animateFAB();

                final List<DataObject> filteredModelList = filter(list, "");
                recyclerView.getLayoutManager().scrollToPosition(10);
                //ViewGroup.LayoutParams laParams =  recyclerView.getLayoutParams();
            }});

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
        public void onBindViewHolder(HomeFragment.RecyclerviewAdapter.VH holder, int position) {
            holder.htxt.setText(DataList.get(position).getHeading());
            holder.dtxt.setText(DataList.get(position).getDescription());
            holder.vtxt.setText(DataList.get(position).getValue());
            //HomeFragment.RecyclerviewAdapter.this.notifyItemRemoved(1);
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            ViewGroup.LayoutParams laParams =holder.itemView.getLayoutParams();
            holder.itemView.getWidth();
            laParams.height = 150;
            laParams.width = MATCH_PARENT;
            holder.itemView.scrollTo(0,0);
            holder.itemView.setLayoutParams(laParams);

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
                            SM.sendData("START".trim());  // PassingDataBetweenFragments
                            GlobalVariable globalVariable = (GlobalVariable)getActivity().getApplicationContext();
                            globalVariable.Start =  StartPoints;
                            searchTextStart.setText("(" + startstring + ")");
                            searchTextStart.setTextColor(Color.parseColor("#136388"));
                            final List<DataObject> filteredModelList = filter(list, ""); //清單重置
                            searchTextEnd.requestFocus();//  Focus 下一個 edittext
                            StartTextFlag = 1; // 起點已選擇
                        }
                        else {
                            EndPoints = point;
                            SM.sendData("END".trim());  // PassingDataBetweenFragments
                            GlobalVariable globalVariable = (GlobalVariable)getActivity().getApplicationContext();
                            globalVariable.End =  EndPoints;
                            searchTextEnd.setText("(" + startstring + ")");
                            searchTextEnd.setTextColor(Color.parseColor("#136388"));
                            final List<DataObject> filteredModelList = filter(list, ""); //清單重置
                            btnStart.setEnabled(true);
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

}