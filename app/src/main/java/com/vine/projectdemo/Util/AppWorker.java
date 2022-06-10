package com.vine.projectdemo.Util;

import static android.content.Context.ALARM_SERVICE;
import static com.vine.projectdemo.Constants.BASE_URL;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.vine.projectdemo.API.RequestInterfaceAll;
import com.vine.projectdemo.DataView.JSONMainActivity;
import com.vine.projectdemo.Model.JSONResponse;
import com.vine.projectdemo.Model.JSONStructure;
import com.vine.projectdemo.Model.ServerRequest;
import com.vine.projectdemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppWorker extends Worker {
    Context ctx;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public AppWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        Log.d("AppWorker", "I am working");
        // task results
        ctx = getApplicationContext();

        initNotification();

        mOnAlarmListener = new AlarmManager.OnAlarmListener()
        {
            @Override
            public void onAlarm() {
                Log.d("AlarmManager", "OnAlarm");
                if(!IsCall){
                    load();
                    Log.d("AppWorker","load");
                }

                SetAlarm();
            }
        };
        //Toast.makeText(ctx, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job

        //timer.schedule(new mainTask(), 0, TimerPeriod);
        alarmManager = (AlarmManager)ctx.getSystemService(ALARM_SERVICE);
        SetAlarm();
        // Successfully returns the result of the task. The actual result is contained in the data object. If there is no need to return the result, return: Result.success()
        // Failed return: Result.failure()
        // Need to re-execute the return: Result.retry()
        return Result.success();
    }

    private AlarmManager alarmManager;

    private long TimerPeriod = 10000;
    AlarmManager.OnAlarmListener mOnAlarmListener;
    private void SetAlarm() {
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+TimerPeriod,
                "alarm", mOnAlarmListener,null);
    }

    String channelId;
    NotificationManager nm;
    private void initNotification() {

        channelId = "SchoolMap Notification";

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = channelId;
            String description = channelId + " Desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    Notification getNotification(String Title,String Content) {
        Intent resultIntent = new Intent(ctx, JSONMainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(ctx, channelId)
                .setContentTitle(Title)
                .setContentText(Content)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.baseline_my_location)
                .setAutoCancel(true)
                .build();
    }


    private boolean IsCall = false;
    private void load(){
        IsCall = true;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceAll requestInterface = retrofit.create(RequestInterfaceAll.class);

        ServerRequest request = new ServerRequest();
        request.setOperation("getdata");

        Call<JSONResponse> call = requestInterface.operation(request);
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                ArrayList<JSONStructure> data = new ArrayList<>(Arrays.asList(jsonResponse.getData()));;

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if( !pref.getString("No","").equals(data.get(data.size()-1).getSno())){
                    if(pref.getString("No","").length() > 1){
                        for(int i= 0;i < data.size();i++){
                            if(Integer.parseInt(data.get(i).getSno()) > Integer.parseInt(pref.getString("No",""))){
                                int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE)+i;
                                nm.notify(id, getNotification(data.get(i).getHead(),data.get(i).getText()));
                            }
                        }
                    }
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("No",data.get(data.size()-1).getSno());
                    editor.apply();
                }

                IsCall = false;
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());

                IsCall = false;
            }
        });
    }
}