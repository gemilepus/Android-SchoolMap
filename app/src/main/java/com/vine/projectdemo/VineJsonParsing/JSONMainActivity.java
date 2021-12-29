package com.vine.projectdemo.VineJsonParsing;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vine.projectdemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vine.projectdemo.VinePHPMySQL.Constants.BASE_URL;

public class JSONMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<AndroidVersion> data;
    private DataAdapter adapter;
    private TextView textViewJSON; // 連線狀態Text
    private int dataSize = 0;
    private Timer timer = new Timer(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jsonparsing_activity_main);
        
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbarJSON);
        toolbar.setTitle("聯大校園公告");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setOnClickListener(new View.OnClickListener() {  // 整個ToolBar的  onClick
            @Override
            public void onClick(View v) {
                JSONMainActivity.this.finish(); // 結束
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        timer.schedule(new MyTimerTask(), 1000, 1000);  //long delay, long period
    }

    private void initViews(){
        ImageView mImageView = (ImageView)findViewById(R.id.imageViewBar);
        mImageView.setImageResource(R.drawable.bar);
        mImageView.setAlpha(100); // 透明度

        textViewJSON = (TextView) this.findViewById(R.id.textViewJSON);
        textViewJSON.setBackgroundColor(0xFFffa830);
        textViewJSON.setText("連線中 ...");

        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        loadJSON();
    }

    public class MyTimerTask extends TimerTask{
        public void run(){
            JSONMainActivity .this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(JSONMainActivity .this, String.valueOf(aa), Toast.LENGTH_LONG).show();
                    if(dataSize > 0){
                        //   已連線 BAR
                        textViewJSON.setBackgroundColor(0xFF79ff05);  // 綠色
                        textViewJSON.setText("已連線");
                        //  timer 停止
                        timer.cancel();
                    }else{
                        textViewJSON.setBackgroundColor(0xFFff2323);  // 紅色
                        textViewJSON.setText("未連線");
                    }
                }
            });
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // Toolbar onClick
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            JSONMainActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call = request.getJSON();
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getAndroid()));
                adapter = new DataAdapter(data);
                recyclerView.setAdapter(adapter);
                dataSize = data.size(); // 清單大小
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
    
    @Override
    public void onDestroy() { //  Activity 銷毀
        super.onDestroy();
    }
}