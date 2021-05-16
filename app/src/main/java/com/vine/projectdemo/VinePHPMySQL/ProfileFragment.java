package com.vine.projectdemo.VinePHPMySQL;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerLayout;
import com.vine.projectdemo.DataObject;
import com.vine.projectdemo.R;

import com.vine.projectdemo.VinePHPMySQL.models.ServerRequest;
import com.vine.projectdemo.VinePHPMySQL.models.ServerResponse;
import com.vine.projectdemo.VinePHPMySQL.models.User;
import com.vine.projectdemo.VineReValues.GPS_Dot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences pref;  //  儲存索引鍵值組

    private RecyclerView recyclerView;
    private ArrayList<AndroidVersion> data;
    private PHPDataAdapter adapter; //  VinePHPMySQL

    private TextView tv_name,tv_email,tv_message; // message 在 Dialog 上
    private AppCompatButton btn_change_password,btn_logout,btn_chg_info;  //plus 按鈕
    private EditText et_old_password,et_new_password;
    private EditText et_head,et_type,et_text,ckpass;
    private Button BtnLocal;

    private AlertDialog dialog;   // 彈出畫面
    private AlertDialog dialog_info;
    private AlertDialog MapDialog;

    private ProgressBar progress;
    private ProgressBar progress_info;
    //~~~~~~~~~~~~~~~~~~~~~  spinner  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    final String[] typestr = {"重要", "國際交流","活動", "演講", "公告" ,"社團活動"};
    String selecttypestr; //所選擇的 STR
    //END~~~~~~~~~~~~~~~~~~  spinner  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // 存取中的清單編號紀錄
    private int data_position;

    // 畫 TEXT
    TextView[] marker_Text_array = new TextView[60]; // Test Array

    Timer timer = new Timer(true);
    Timer timerAnima = new Timer(true);

    String ValueString , TextString;
    String[] ValueStringArray;

    List<DataObject> list;

    private TileView tileView;
    public static final double SOUTH_EAST_LONGITUDE =   120.8167;
    public static final double SOUTH_EAST_LATITUDE = 24.533648; //南 東
    public static final double NORTH_WEST_LONGITUDE =  120.7832;//經度     //0.012904468412943
    public static final double NORTH_WEST_LATITUDE =24.547866; //北  西 緯度   //0.0117471872931833
    private double[] Select_Point =  new double[2];
    int List_MAX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    // 建立 fragment
        View view = inflater.inflate(R.layout.php_fragment_profile,container,false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {                                      // 取得儲存的資訊 (API)
        pref = getActivity().getPreferences(0);
        tv_name.setText("使用者 : "+pref.getString(Constants.NAME,""));
        tv_email.setText(pref.getString(Constants.EMAIL,""));

        loadJSON();

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
    }

    private void initViews(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        tv_name = (TextView)view.findViewById(R.id.tv_name);
        tv_email = (TextView)view.findViewById(R.id.tv_email);
        btn_change_password = (AppCompatButton)view.findViewById(R.id.btn_chg_password);
        btn_logout = (AppCompatButton)view.findViewById(R.id.btn_logout);
        btn_chg_info =  (AppCompatButton)view.findViewById(R.id.btn_chg_info);

        btn_change_password.setOnClickListener(this); // 按鈕監聽
        btn_logout.setOnClickListener(this); // 按鈕監聽
        btn_chg_info.setOnClickListener(this); // 按鈕監聽
    }

    @Override
    public void onClick(View v) { // 監聽後的動作
        switch (v.getId()){
            case R.id.btn_chg_password:
                showDialog();  // 開啟修改介面
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.btn_chg_info:
                showDialogInfo();  // 開啟修改介面
                break;
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN,false);
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.NAME,"");
        editor.putString(Constants.UNIQUE_ID,"");
        editor.apply();
        goToLogin();
    }

    private void goToLogin(){
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,login);
        ft.commit();
    }

    private void showDialog(){  // 開啟修改介面
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.php_dialog_change_password, null);   // 開啟 XML
        et_old_password = (EditText)view.findViewById(R.id.et_old_password);
        et_new_password = (EditText)view.findViewById(R.id.et_new_password);
        tv_message = (TextView)view.findViewById(R.id.tv_message);
        progress = (ProgressBar)view.findViewById(R.id.progress);

        builder.setView(view);
        builder.setTitle("修改密碼");
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }

        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String old_password = et_old_password.getText().toString();
                String new_password = et_new_password.getText().toString();
                if(!old_password.isEmpty() && !new_password.isEmpty()){

                    progress.setVisibility(View.VISIBLE);
                    changePasswordProcess(pref.getString(Constants.EMAIL,""),old_password,new_password); // 對PHP做修改的函數

                }else {

                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Fields are empty");
                }
            }
        });
    }

    private void showDialogInfo(){  // 開啟修改介面
        AlertDialog.Builder builderin = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater_in = getActivity().getLayoutInflater();
        View view = inflater_in.inflate(R.layout.php_dialog_change_info, null);
        et_head = (EditText)view.findViewById(R.id.et_head);
        //et_type = (EditText)view.findViewById(R.id.et_type);
        et_text = (EditText)view.findViewById(R.id.et_text);
        ckpass  = (EditText)view.findViewById(R.id.ck_pass);
        BtnLocal  = (Button)view.findViewById(R.id.btnLocal);
        BtnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapDialog();
            }
        });
         //~~~~~~~~~~~~~~~~~~~~~  下拉清單  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Spinner spinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<String>  typestrlist = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, typestr); // Dialog View
        spinner.setAdapter(typestrlist);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   //  spinner 事件  事件裡使用的變數要是全域
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {  // 在 onItemSelected 事件中所代表的是當使用者按下 Spinner 的某個 item
                selecttypestr = typestr[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //~~~~~~~~~~~~~~~~~~~~~  下拉清單  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        tv_message = (TextView)view.findViewById(R.id.tv_message_in);
        progress_info = (ProgressBar)view.findViewById(R.id.progres_in);

        builderin.setView(view);
        builderin.setTitle("新增訊息");
        builderin.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
        builderin.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog_info.dismiss();
            }
        });
        dialog_info = builderin.create();
        dialog_info.show();
        dialog_info.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String new_head = et_head.getText().toString(); // 輸入字串
                String new_type = selecttypestr;
                String new_text = et_text.getText().toString(); // 輸入字串
                String ck_pass = ckpass.getText().toString(); // 輸入字串

                // 地點 GPS值
                String longitude = String.valueOf(Select_Point[0]);
                String latitude  = String.valueOf(Select_Point[1]);

                // tv_name.setText("Welcome : "+pref.getString(Constants.NAME,""));
                // tv_email.setText(pref.getString(Constants.EMAIL,""));
                if(!new_head.isEmpty() && !new_type.isEmpty()){
                    progress_info.setVisibility(View.VISIBLE);
                    NewInfoProcess( pref.getString(Constants.EMAIL,"") ,ck_pass,new_head,new_type,new_text,pref.getString(Constants.UNIQUE_ID,"") ,longitude,latitude ); // 對PHP做修改的函數
                    //NewInfoProcess(pref.getString(Constants.EMAIL,""),pref.getString(Constants.EMAIL,""),new_head,new_type); // 對PHP做修改的函數
                   // unigetJSON(pref.getString(Constants.EMAIL,""),"5457"); // @@@@@@@@@@@@

                }else {
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Fields are empty");//@@@#@
                }
            }
        });
    }

    private void changePasswordProcess(String email, String old_password, String new_password){ //  改密碼
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants. CHANGE_PASSWORD_OPERATION);

        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    progress.setVisibility(View.GONE);
                    tv_message.setVisibility(View.GONE);
                    dialog.dismiss();
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }else {
                    progress.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(resp.getMessage());
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                progress.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(t.getLocalizedMessage());
            }
        });
    }

    private void NewInfoProcess(String email, String old_password, String head, String type, String texts , String unique ,String longitude , String latitude ){
        //  NewInfoProcess(pref.getString(Constants.EMAIL,""),ck_pass,new_head,new_type,new_text); // 對PHP做修改的函數
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setHead(head);
        user.setType(type);
        user.setTexts(texts);
        user.setUnique_id(unique);
        user.setLongitude(longitude);
        user.setLatitude(latitude);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.NEW_INFO_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    progress_info.setVisibility(View.GONE);
                    tv_message.setVisibility(View.GONE);
                    dialog_info.dismiss();
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                    loadJSON();
                }else {
                    progress_info.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(resp.getMessage());

                    loadJSON();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                //deleteCheck();
                progress_info.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(t.getLocalizedMessage());
            }
        });
    }

    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceID request = retrofit.create(RequestInterfaceID.class);
       // String uni = pref.getString(Constants.UNIQUE_ID,"");
        Call<JSONResponse> call = request.getJSON(pref.getString(Constants.UNIQUE_ID,""));// test
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) { //沒連上時 不會進入迴圈
                JSONResponse jsonResponse = response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getAndroid()));
                adapter = new PHPDataAdapter(data);
                // ClickListener
                adapter.setOnImgClickListener(new  PHPDataAdapter.OnImgClickListener() {
                    @Override
                    public void onImgClick(View view, int position) {
                        data_position = position; // 設定清單編號
                        deleteCheck();
                        // data.get((int)view.getTag()).getHead()
                        //RemoveInfoProcess(   data.get( position).getHead()  ,   data.get( position).getSno() ,  pref.getString(Constants.UNIQUE_ID,"") );
                        Toast.makeText(ProfileFragment.this.getActivity(), "click img " +   String.valueOf( position) , Toast.LENGTH_SHORT).show();
                    }

                });
                // ClickListener
                adapter.setOnItemClickListener(new  PHPDataAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                       // view.getTag();
                       // Toast.makeText(ProfileFragmentTry.this.getActivity(), "click " +   String.valueOf( view.getTag()) , Toast.LENGTH_SHORT).show();
                        Toast.makeText(ProfileFragment.this.getActivity(), "click " +   String.valueOf( position) , Toast.LENGTH_SHORT).show();
                    }

                });
                // ClickListener
                adapter.setOnItemLongClickListener(new  PHPDataAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {

                        data_position = position; // 設定清單編號

                        alertCheck(); // 開啟清單

                        Toast.makeText(ProfileFragment.this.getActivity(), "Long click " +   String.valueOf( position) , Toast.LENGTH_SHORT).show();
                  }

                });
                 recyclerView.setAdapter(adapter);
                //adapter.
                //recyclerView.setOnClickListener(getActivity()); // 按鈕監聽
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) { //沒連上時 不會進入迴圈
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void RemoveInfoProcess(String head, String sno, String uni){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setHead(head);
        user.setSno(sno);
        user.setUnique_id(uni);

        ServerRequest request = new ServerRequest();
        // request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setOperation(Constants.REMOVE_INFO_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    loadJSON();
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                //tv_message.setVisibility(View.VISIBLE);
                //tv_message.setText(t.getLocalizedMessage());
                Snackbar.make(getView(), "BOOM !!!!!!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // 影藏 MapDialog(未完成)
    private void alertCheck() {
        //String[] alert_menu = {"修改", "刪除", "取消"};
        String[] alert_menu = {"刪除", "取消"};
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        alert.setTitle( "訊息 : " + data.get(data_position).getHead() );
        alert.setItems(alert_menu, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int idx) {
                // リストアイテムを選択したときの処理
//                if (idx == 0) {
//                    MapDialog(); // test
//                }
//                else if (idx == 1) {
//                    deleteCheck();
//                }
//                // cancel
//                else {
//                    // nothing to do
//                }
                if (idx == 0) {
                    deleteCheck();
                }
                else if (idx == 1) {

                }
            }

        });
        alert.show();
    }

    private void deleteCheck() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
        // AlertDialogのタイトル設定します
        alertDialogBuilder.setTitle("刪除");
        // AlertDialogのメッセージ設定
        alertDialogBuilder.setMessage("確定刪除這筆？");
        // AlertDialogのYesボタンのコールバックリスナーを登録
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveInfoProcess( data.get( data_position).getHead() , data.get( data_position ).getSno() , pref.getString(Constants.UNIQUE_ID,"") );
                    }
                });
        // AlertDialogのNoボタンのコールバックリスナーを登録
        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do
                    }
                });
        // AlertDialogのキャンセルができるように設定
        alertDialogBuilder.setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();
        // AlertDialogの表示
        alertDialog.show();
    }

    private void deleteCheck_V() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
        // AlertDialogのタイトル設定します
        alertDialogBuilder.setTitle("刪除");
        // AlertDialogのメッセージ設定
        alertDialogBuilder.setMessage("確定刪除這筆？");
        // AlertDialogのYesボタンのコールバックリスナーを登録
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  RemoveInfoProcess( data.get( data_position).getHead() , data.get( data_position ).getSno() , pref.getString(Constants.UNIQUE_ID,"") );
            }

        });
        // AlertDialogのNoボタンのコールバックリスナーを登録
        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing to do
                dialog.dismiss();
            }

        });

        // 讓  dialog BUTTON 按下後  dialog 不會消失
        dialog_info =  alertDialogBuilder.create();
        dialog_info.show();
        dialog_info.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog_info.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_info.dismiss();
            }

        });
        // AlertDialogのキャンセルができるように設定
        alertDialogBuilder.setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();
        // AlertDialogの表示
        // alertDialog.show();
    }

    private void MapCheck() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
        // AlertDialogのタイトル設定します
        alertDialogBuilder.setTitle("");
        // AlertDialogのメッセージ設定
        alertDialogBuilder.setMessage("確定選擇這裡？");
        // AlertDialogのYesボタンのコールバックリスナーを登録
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               // MapDialog.dismiss();
            }
        });
        // AlertDialogのNoボタンのコールバックリスナーを登録
        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Select_Point[0] = 0;
                Select_Point[1] = 0;
                // nothing to do
            }
        });

        // AlertDialogのキャンセルができるように設定
        alertDialogBuilder.setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();
        // AlertDialogの表示
        alertDialog.show();
    }

    private void MapDialog(){  // 開啟修改介面
        // LinearLayout_Map
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.php_dialog_map, null);   // 開啟 XML

        tileView = new TileView(this.getActivity());
        tileView.setScaleLimits(0, 2);//放大大小
        tileView.setSize(25960, 12088);
        tileView.addDetailLevel(0.0125f, "tiles/04/img%d_%d.png");
        tileView.addDetailLevel(0.2500f, "tiles/03/img%d_%d.png");
        tileView.addDetailLevel(0.5000f, "tiles/02/img%d_%d.png");
        tileView.addDetailLevel(1.0000f, "tiles/01/img%d_%d.png");
        //tileView.addDetailLevel(1.0000f, "tiles/map/phi-500000-%d_%d.jpg");

        tileView.setMarkerAnchorPoints(-0.5f, -1.0f);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Paint paint = tileView.getDefaultPathPaint();
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics));  //  線寬
        paint.setPathEffect(new DashPathEffect(new float[] {20, 10}, 0)); // 畫虛線
        tileView.setScale(0);
        tileView.setShouldRenderWhilePanning(true);
        tileView.setShouldLoopScale(false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1); // 框框 View
        ((LinearLayout) view.findViewById(R.id.LinearLayout_Map)).addView(tileView,lp); // addView


        ArrayList<double[]> map_point = new ArrayList<>();//測試 所有點
        {
        }
        for (int r = 0;r < GPS_Dot.Xys_List.length; r++) {
            map_point.add(new double[]{GPS_Dot.Xys_List[r][0] , GPS_Dot.Xys_List[r][1]});
        }
        for (double[] point : map_point) {  //建立標記圖示
            ImageView marker = new ImageView(this.getActivity());
            marker.setTag(point);
            marker.setImageResource(R.drawable.map_marker_123);
            //marker.getMarkerLayout().setMarkerTapListener(markerTapListener);
            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            // add it to the view tree
            tileView.addMarker(marker, point[0], point[1], null, null);
        }
        
        List_MAX = (int)list.size();
        double x , y;
        for (int Nend = 0; Nend <  List_MAX ; Nend++) {
            x = 0;
            y = 0;
            TextString =  list.get(Nend).getHeading();
            ValueString = list.get(Nend).getValue();
            ValueStringArray = ValueString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");

            // ValueStringArray.length
            for (int N = 0; N < ValueStringArray.length  ; N++) {
                //Integer.valueOf( ValueStringArray[N])
                // ValueStringArray[]
                x = x + GPS_Dot.Xys_List[Integer.valueOf( ValueStringArray[N])-1][0];
                y = y + GPS_Dot.Xys_List[Integer.valueOf( ValueStringArray[N])-1][1];

            }
            x = x / ValueStringArray.length;
            y = y / ValueStringArray.length;
            ArrayList<double[]> pointsText = new ArrayList<>();
            // pointsText.add(new double[]{datalist[Integer.valueOf( ValueStringArray[N])-1][0] ,datalist[Integer.valueOf( ValueStringArray[N])-1][1]});
            marker_Text_array[Nend] = new TextView(this.getActivity());
            marker_Text_array[Nend].setText( TextString );
            marker_Text_array[Nend].setTag( pointsText);
            marker_Text_array[Nend].setTextColor(Color.parseColor("#ff32abfc")); // 藍色
            marker_Text_array[Nend].setBackgroundColor(Color.parseColor("#4032abfc"));
            // tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);

            tileView.addMarker(marker_Text_array[Nend], x , y , null, null);
        }

        builder.setView(view);
        builder.setTitle("MAP");
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });
    }

    private MarkerLayout.MarkerTapListener markerTapListener = new MarkerLayout.MarkerTapListener() {  // Marker 監聽
        @Override
        public void onMarkerTap(View view, int x, int y) {
            // get reference to the TileView
            // tileView = this.getActivity().getTileView();
            if( !(view instanceof TextView) ){
                // we saved the coordinate in the marker's tag
                double[] position = (double[]) view.getTag();
                // lets center the screen to that coordinate
                tileView.slideToAndCenter(position[0], position[1]);//移動
                // create a simple callout
                Select_Point[0] = position[0] ;
                Select_Point[1] = position[1] ;
                MapCheck();
                // slideToAndCenterWithScale( double x, double y, float scale )
            }
        }};

    private void UpdateInfoProcess(String email, String old_password, String head, String type, String texts , String unique){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setHead(head);
        user.setType(type);
        user.setTexts(texts);
        user.setUnique_id(unique);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.UPDATE_INFO_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){  //php 主機回傳
                    progress_info.setVisibility(View.GONE);
                    tv_message.setVisibility(View.GONE);
                    dialog_info.dismiss();
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    
                    loadJSON();
                }else {
                    progress_info.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(resp.getMessage());

                    loadJSON();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                progress_info.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(t.getLocalizedMessage());
            }
        });
    }
}