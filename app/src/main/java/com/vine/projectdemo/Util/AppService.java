package com.vine.projectdemo.Util;

import static com.vine.projectdemo.Constants.BASE_URL;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.vine.projectdemo.API.RequestInterfaceAll;
import com.vine.projectdemo.Model.JSONResponse;
import com.vine.projectdemo.Model.JSONStructure;
import com.vine.projectdemo.Model.ServerRequest;
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

public class AppService extends Service {

    public AppService() { }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        initNotification();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel serviceChannel = new NotificationChannel(
                "SchoolMapServiceNotificationChannelId",
                "SchoolMap Alive",
                NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(serviceChannel);

        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, serviceChannel.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new Notification.Builder(this, serviceChannel.getId())
                .setContentTitle("SchoolMap Alive")
                .setContentText(getResources().getString(R.string.To_Hide_Me))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.baseline_my_location)
                .getNotification();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job

        timer.scheduleAtFixedRate(new mainTask(), 0, 20000);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private static Timer timer = new Timer();
    private class mainTask extends TimerTask
    {
        public void run()
        {
            if(call!=null){
                if(call.isExecuted()){
                    //load();
                }
            }
        }
    }

    Context ctx;
    String channelId;
    NotificationManager nm;
    private void initNotification() {
        ctx = getApplicationContext();
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
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    Notification getNotification(String Title,String Content) {
        return new NotificationCompat.Builder(ctx, channelId)
                .setContentTitle(Title)
                .setContentText(Content)
                .setSmallIcon(R.drawable.baseline_my_location)
                .build();
    }

    private Call<JSONResponse> call;
    private void load(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterfaceAll requestInterface = retrofit.create(RequestInterfaceAll.class);

        ServerRequest request = new ServerRequest();
        request.setOperation("getdata");

        call = requestInterface.operation(request);
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                ArrayList<JSONStructure> data = new ArrayList<>(Arrays.asList(jsonResponse.getData()));;

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if( !pref.getString("No","").equals(data.get(data.size()-1).getSno())){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("No",data.get(data.size()-1).getSno());
                    editor.apply();
                    nm.notify(1, getNotification(data.get(data.size()-1).getHead(),data.get(data.size()-1).getText()));
                }
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}