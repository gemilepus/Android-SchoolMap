package com.vine.projectdemo.AccountView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerLayout;
import com.vine.projectdemo.API.RequestInterface;
import com.vine.projectdemo.API.RequestInterfaceByID;
import com.vine.projectdemo.Adapter.PHPDataAdapter;
import com.vine.projectdemo.Constants;
import com.vine.projectdemo.HomeFragment;
import com.vine.projectdemo.Model.JSONResponse;
import com.vine.projectdemo.Model.JSONStructure;
import com.vine.projectdemo.R;
import com.vine.projectdemo.AccountView.models.ServerRequest;
import com.vine.projectdemo.AccountView.models.ServerResponse;
import com.vine.projectdemo.AccountView.models.User;
import com.vine.projectdemo.Values.GPS_Point;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences pref;

    private RecyclerView recyclerView;
    private ArrayList<JSONStructure> data;
    private PHPDataAdapter adapter;

    private TextView tv_name,tv_email,tv_message;
    private AppCompatButton btn_change_password,btn_logout,btn_chg_info;
    private EditText et_old_password,et_new_password;
    private EditText et_head,et_type,et_text,ckpass;
    private Button BtnLocal;

    private AlertDialog dialog;
    private AlertDialog dialog_info;
    private AlertDialog MapDialog;

    private ProgressBar progress;
    private ProgressBar progress_info;
    // spinner
    final String[] SpinnerItem = {"重要", "國際交流","活動", "演講", "公告" ,"社團活動"};

    // 存取中的清單編號紀錄
    private int data_position;

    private TileView tileView;
    TextView[] marker_Text_array = new TextView[60];

    String ValueString , TextString;
    String[] ValueStringArray;

    private double[] Select_Point =  new double[2];
    int List_MAX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.php_fragment_profile,container,false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pref = getActivity().getPreferences(0);
        tv_name.setText(getResources().getString(R.string.User) + " : "+pref.getString(Constants.NAME,""));
        tv_email.setText(pref.getString(Constants.EMAIL,""));

        loadJSON();
    }

    private void initViews(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        tv_name = (TextView)view.findViewById(R.id.txt_head);
        tv_email = (TextView)view.findViewById(R.id.tv_email);
        btn_change_password = (AppCompatButton)view.findViewById(R.id.btn_chg_password);
        btn_logout = (AppCompatButton)view.findViewById(R.id.btn_logout);
        btn_chg_info =  (AppCompatButton)view.findViewById(R.id.btn_chg_info);

        btn_change_password.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_chg_info.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_chg_password:
                showDialog();
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.btn_chg_info:
                showDialogInfo();
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

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.php_dialog_change_password, null);
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

    private void showDialogInfo(){
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

        Spinner spinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<String>  typestrlist = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, SpinnerItem);
        spinner.setAdapter(typestrlist);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                String new_head = et_head.getText().toString();
                String new_type = spinner.getSelectedItem().toString();
                String new_text = et_text.getText().toString();
                String ck_pass = ckpass.getText().toString();

                String longitude = String.valueOf(Select_Point[0]);
                String latitude  = String.valueOf(Select_Point[1]);

                // txt_head.setText("Welcome : "+pref.getString(Constants.NAME,""));
                // tv_email.setText(pref.getString(Constants.EMAIL,""));
                if(!new_head.isEmpty() && !new_type.isEmpty()){
                    progress_info.setVisibility(View.VISIBLE);
                    NewInfoProcess( pref.getString(Constants.EMAIL,"") ,ck_pass,new_head,new_type,new_text,pref.getString(Constants.UNIQUE_ID,"") ,longitude,latitude ,pref.getString(Constants.TOKEN,""));
                    //NewInfoProcess(pref.getString(Constants.EMAIL,""),pref.getString(Constants.EMAIL,""),new_head,new_type);

                }else {
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Fields are empty");
                }
            }
        });
    }

    private void changePasswordProcess(String email, String old_password, String new_password){ // 改密碼
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
        request.setToken(pref.getString(Constants.TOKEN,""));

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

    private void NewInfoProcess(String email, String old_password, String head, String type, String texts , String unique ,String longitude , String latitude , String token){
        // Log
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // set log level
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
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
        request.setToken(token);
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
        RequestInterfaceByID request = retrofit.create(RequestInterfaceByID.class);

        ServerRequest mServerRequest = new ServerRequest();
        mServerRequest.setOperation("getdatabyid");

        Call<JSONResponse> call = request.operation(pref.getString(Constants.UNIQUE_ID,""),mServerRequest);
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getData()));
                adapter = new PHPDataAdapter(data);
                adapter.setOnImgClickListener(new  PHPDataAdapter.OnImgClickListener() {
                    @Override
                    public void onImgClick(View view, int position) {
                        data_position = position;
                        deleteCheck();
                        // data.get((int)view.getTag()).getHead()
                    }
                });
                adapter.setOnItemClickListener(new  PHPDataAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //view.getTag();
                    }
                });
                adapter.setOnItemLongClickListener(new  PHPDataAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                        data_position = position;
                        alertCheck();
                    }
                });
                 recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
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
        request.setOperation(Constants.REMOVE_INFO_OPERATION);
        request.setUser(user);
        request.setToken(pref.getString(Constants.TOKEN,""));

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
                Snackbar.make(getView(), "failed", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void alertCheck() {
        //String[] alert_menu = {"修改", "刪除", "取消"};
        String[] alert_menu = {"刪除", "取消"};
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        alert.setTitle( "訊息 : " + data.get(data_position).getHead() );
        alert.setItems(alert_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int idx) {
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
        alertDialogBuilder.setTitle("刪除");
        alertDialogBuilder.setMessage("確定刪除這筆？");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveInfoProcess( data.get( data_position).getHead() , data.get( data_position ).getSno() , pref.getString(Constants.UNIQUE_ID,"") );
                    }
                });
        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void MapCheck() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
        alertDialogBuilder.setTitle("");
        alertDialogBuilder.setMessage("確定選擇這裡？");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // MapDialog.dismiss();
            }
        });
        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Select_Point[0] = 0;
                Select_Point[1] = 0;
            }
        });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void MapDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.php_dialog_map, null);

        tileView = new TileView(this.getActivity());
        tileView.setScaleLimits(0, 2);
        tileView.setSize(25960, 12088);
        tileView.addDetailLevel(0.0125f, "tiles/04/img%d_%d.png");
        tileView.addDetailLevel(0.2500f, "tiles/03/img%d_%d.png");
        tileView.addDetailLevel(0.5000f, "tiles/02/img%d_%d.png");
        tileView.addDetailLevel(1.0000f, "tiles/01/img%d_%d.png");

        tileView.setMarkerAnchorPoints(-0.5f, -1.0f);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Paint paint = tileView.getDefaultPathPaint();
        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics));
        paint.setPathEffect(new DashPathEffect(new float[] {20, 10}, 0));
        tileView.setScale(0);
        tileView.setShouldRenderWhilePanning(true);
        tileView.setShouldLoopScale(false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        ((LinearLayout) view.findViewById(R.id.LinearLayout_Map)).addView(tileView,lp);

        ArrayList<double[]> map_point = new ArrayList<>();
        for (int r = 0; r < GPS_Point.Xys_List.length; r++) {
            map_point.add(new double[]{GPS_Point.Xys_List[r][0] , GPS_Point.Xys_List[r][1]});
        }
        for (double[] point : map_point) {  //建立標記圖示
            ImageView marker = new ImageView(this.getActivity());
            marker.setTag(point);
            marker.setImageResource(R.drawable.map_path_marker);
            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            // add it to the view tree
            tileView.addMarker(marker, point[0], point[1], null, null);
        }
        
        List_MAX = (int)HomeFragment.list.size();
        double x , y;
        for (int Nend = 0; Nend <  List_MAX ; Nend++) {
            x = 0;
            y = 0;
            TextString =  HomeFragment.list.get(Nend).getHeading();
            ValueString = HomeFragment.list.get(Nend).getValue();
            ValueStringArray = ValueString.split("-");

            // ValueStringArray.length
            for (int N = 0; N < ValueStringArray.length  ; N++) {
                //Integer.valueOf( ValueStringArray[N])
                // ValueStringArray[]
                x = x + GPS_Point.Xys_List[Integer.valueOf( ValueStringArray[N])-1][0];
                y = y + GPS_Point.Xys_List[Integer.valueOf( ValueStringArray[N])-1][1];

            }
            x = x / ValueStringArray.length;
            y = y / ValueStringArray.length;
            ArrayList<double[]> pointsText = new ArrayList<>();
            marker_Text_array[Nend] = new TextView(this.getActivity());
            marker_Text_array[Nend].setText( TextString );
            marker_Text_array[Nend].setTag( pointsText);
            marker_Text_array[Nend].setTextColor(Color.parseColor("#ff32abfc"));
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

    private MarkerLayout.MarkerTapListener markerTapListener = new MarkerLayout.MarkerTapListener() {
        @Override
        public void onMarkerTap(View view, int x, int y) {
            // get reference to the TileView
            // tileView = this.getActivity().getTileView();
            if( !(view instanceof TextView) ){
                // we saved the coordinate in the marker's tag
                double[] position = (double[]) view.getTag();
                // lets center the screen to that coordinate
                tileView.slideToAndCenter(position[0], position[1]);
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
                progress_info.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(t.getLocalizedMessage());
            }
        });
    }
}