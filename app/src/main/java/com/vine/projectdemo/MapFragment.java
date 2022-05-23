package com.vine.projectdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerLayout;
import com.vine.projectdemo.View.MainLinearLayout;
import com.vine.projectdemo.Util.RotationGestureDetector;
import com.vine.projectdemo.View.SampleCallout;

import com.vine.projectdemo.Values.GPS_Point;
import com.vine.projectdemo.Values.GlobalVariable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class MapFragment extends Fragment implements SensorEventListener, LocationListener ,
        RotationGestureDetector.OnRotationGestureListener {

    public static final String TITLE = "校園地圖";
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    // TileView Setting
    private TileView tileView;
    private boolean tileView_Run = true;

    // TileView Label
    TextView[] LabelMarker = new TextView[60];
    int List_Length;
    // Marker
    private LinearLayout[] PathMarker = new LinearLayout[25];
    int PathMarker_Num = 0;
    private LinearLayout NowMarker;

    // GPS
    private LocationManager locationManager;
    private static final int MinTime = 1000;
    private static final float MinDistance = 1;

    // Dijkstra
    private static final int INF = Integer.MAX_VALUE;
    int[] mVexs;// 純標記
    int mMatrix[][];
    int vs;//起始點
    int[] prev;
    int[] dist;
    int[] parent;

    String[] StartPointArr;
    String[] EndPointArr;

    ArrayList<double[]> DrawPointsList = new ArrayList<>();

    int IsUsedMapA = 0;
    int StartPointMin = 0, EndPointMin = 0;  // 最近短距離的 起點 終點
    
    // ElectronicCompass
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private ImageButton ElectronicCompassBtn;
    String vector;
    float[] mGravity;
    float[] mGeomagnetic;
    float Rotation[] = new float[9];
    float[] degree = new float[3];

    private Timer timer;
    private mTimerTask timerTask;
    private RotationGestureDetector mRotationDetector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_content_main, container, false);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        /*
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
         */

        // ElectronicCompass
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        ElectronicCompassBtn = (ImageButton) v.findViewById(R.id.imageButton);
        ElectronicCompassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElectronicCompassON = true;
                StartElectronicCompass();
            }
        });

        // check Permission
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }

        SetTileView(v);
        DrawLabel();

        startTimer();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void SetTileView(View v){
        tileView = new TileView(this.getActivity());

        // let the image explode
        tileView.setScaleLimits(0, 2);
        // size and geolocation
        tileView.setSize(25960, 12088);

        // we won't use a downsample here, so color it similarly to tiles
        //tileView.setBackgroundColor( 0xFFe7e7e7 );

        tileView.addDetailLevel(0.0125f, "tiles/04/img%d_%d.png");
        tileView.addDetailLevel(0.2500f, "tiles/03/img%d_%d.png");
        tileView.addDetailLevel(0.5000f, "tiles/02/img%d_%d.png");
        tileView.addDetailLevel(1.0000f, "tiles/01/img%d_%d.png");

        // markers should align to the coordinate along the horizontal center and vertical bottom
        tileView.setMarkerAnchorPoints(-0.5f, -1.0f);

        // get metrics for programmatic DP
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // get the default paint and style it.  the same effect could be achieved by passing a custom Paint instnace
        Paint paint = tileView.getDefaultPathPaint();

        // dress up the path effects and draw it between some points

        /*
        paint.setShadowLayer(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
                0x66000000
        );
         */

        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics));
        paint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
        //paint.setColor();
        //tileView.drawPath(points.subList(2, 5), null);

        // set mScale to 0, but keep scaleToFit true, so it'll be as small as possible but still match the container
        tileView.setScale(0);

        // frame to center
        //frameTo(0.5, 0.5);

        // render while panning
        tileView.setShouldRenderWhilePanning(true);

        // disallow going back to minimum scale while double-taping at maximum scale (for demo purpose)
        tileView.setShouldLoopScale(false);
        //tileView.setSaveEnabled(true);

        // Map Layout
        MainLinearLayout mMainLinearLayout = (MainLinearLayout) v.findViewById(R.id.LI);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        ((LinearLayout) v.findViewById(R.id.LI)).addView(tileView, lp);

        Point size = new Point();
        MapFragment.this.getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        //mScale= Math.sqrt((Math.pow(size.x, 2)+Math.pow(size.y, 2)))/size.x;
        float mScale= (float)size.y/size.x;
        tileView.setScaleY(mScale);
        tileView.setScaleX(mScale);

        mRotationDetector = new RotationGestureDetector(this,mMainLinearLayout);
        mMainLinearLayout.setOnInterceptTouchListener(new MainLinearLayout.OnInterceptTouchListener() {
            @Override
            public void onLITouchEvent(MotionEvent event) {
                mRotationDetector.onTouchEvent(event);

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_OUTSIDE:
                        Log.d(String.valueOf(this), "ACTION_OUTSIDE");
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d(String.valueOf(this), "ACTION_POINTER_DOWN");
                        Log.d(String.valueOf(this), "lastDegree " + String.valueOf(lastDegree));
                        TempDegree = lastDegree;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d(String.valueOf(this), "ACTION_POINTER_UP");
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private float TempDegree;

    @Override
    public void onRotation(RotationGestureDetector rotationDetector) {
        float angle = rotationDetector.getAngle();
        Log.d("RotationGestureDetector", "Rotation: " + Float.toString((TempDegree + angle)));
        //RotateMap((TempDegree + angle));

        stopTimer();
        tileView.setRotation(-(TempDegree + angle));
        lastDegree = TempDegree + angle;
        for (int i = 0; i < List_Length; i++) {
            LabelMarker[i].setRotation(TempDegree + angle);
        }
        startTimer();
    }

    private void DrawAllPoint() {
        ArrayList<double[]> map_point = new ArrayList<>();{}
        for (int i = 0; i < 227; i++) {
            map_point.add(new double[]{GPS_Point.Xys_List[i][0] , GPS_Point.Xys_List[i][1]});
        }

        for (double[] point : map_point) {
            //marker = new ImageView(this);
            ImageView marker = new ImageView(this.getActivity());
            // save the coordinate for centering and callout positioning
            marker.setTag(point);
            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors
            marker.setImageResource(R.drawable.map_path_marker);

            tileView.addMarker(marker, point[0], point[1], null, null);
        }
    }
    
    private void DrawLabel() {
        List_Length = (int) HomeFragment.list.size();
        double x, y;
        for (int i = 0; i < List_Length; i++) {
            x = 0;
            y = 0;
            String TextString = HomeFragment.list.get(i).getHeading();
            String ValueString = HomeFragment.list.get(i).getValue();
            String[] ValueStringArray = ValueString.split("-");
            
            for (String s : ValueStringArray) {
                x = x + GPS_Point.Xys_List[Integer.parseInt(s) - 1][0];
                y = y + GPS_Point.Xys_List[Integer.parseInt(s) - 1][1];
            }
            x = x / ValueStringArray.length;
            y = y / ValueStringArray.length;

            // create marker
            LabelMarker[i] = new TextView(this.getActivity());
            LabelMarker[i].setText(TextString);
            LabelMarker[i].setTextColor(Color.parseColor("#ff007acc"));
            LabelMarker[i].setBackgroundColor(Color.parseColor("#4032abfc"));
            LabelMarker[i].setScaleY((float) 0.75);
            LabelMarker[i].setScaleX((float) 0.75);
            //tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            tileView.addMarker(LabelMarker[i], x, y, null, null);
        }
        tileView.defineBounds(Constants.NORTH_WEST_LONGITUDE, Constants.NORTH_WEST_LATITUDE, Constants.SOUTH_EAST_LONGITUDE, Constants.SOUTH_EAST_LATITUDE);
    }

    private MarkerLayout.MarkerTapListener markerTapListener = new MarkerLayout.MarkerTapListener() {
        @Override
        public void onMarkerTap(View view, int x, int y) {
            // get reference to the TileView
            // tileView = this.getActivity().getTileView();
            Toast.makeText( MapFragment.this.getActivity(), "X = " + String.valueOf(x) + " Y = " + String.valueOf(y), Toast.LENGTH_LONG).show();

            if( !(view instanceof TextView) ){ // View != TextView
                // we saved the coordinate in the marker's tag
                double[] position = (double[]) view.getTag();
                // lets center the screen to that coordinate
                //tileView.slideToAndCenter(position[0], position[1]);
                // create a simple callout
                SampleCallout callout = new SampleCallout(view.getContext());
                // add it to the view tree at the same position and offset as the marker that invoked it
                tileView.addCallout(callout, position[0], position[1], -0.5f, -1.0f);
                // a little sugar
                callout.transitionIn();
                // stub out some text
                callout.setTitle("Info");
                callout.setSubtitle("位置 : " + position[1] + ", " + position[0]);
            }
        }
    };

    public void MoveToA(){ // 二坪校區
        tileView.moveToMarker(tileView.getMarkerLayout().getChildAt(0),false);
    }

    public void MoveToB(){ // 八甲校區
        int num=0;
        for(int i=0;i<tileView.getMarkerLayout().getChildCount() ;i++){
            if( tileView.getMarkerLayout().getChildAt(i) instanceof TextView ){
                num++;
            }
        }
        tileView.moveToMarker(tileView.getMarkerLayout().getChildAt(num-1),false);
    }

    public TileView getTileView(){
        return tileView;
    }

    private ArrayList<Float> DegreeList = new ArrayList<>();

    class mTimerTask extends TimerTask {
        @Override
        public void run() {
            if(DegreeList.size() > 0){
                if(DegreeList.get(0)!= null){
                    tileView.setRotation(-DegreeList.get(0));
                }
                DegreeList.remove(0);
            }
        }
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new mTimerTask();
        timer.schedule(timerTask, 0, 35);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private float lastDegree = 0;
    private void RotateMap(float mDegree){

        stopTimer();
        if(DegreeList.size()>60){
            DegreeList.clear();
        }

        float mSpacing = Math.abs((mDegree - lastDegree)%360)/20;
        for (int i=0;i<20;i++){
            if(mDegree > lastDegree){
                DegreeList.add(lastDegree+mSpacing*i);
            }else{
                DegreeList.add(lastDegree-mSpacing*i);
            }
        }
        DegreeList.add(mDegree);
        lastDegree = mDegree;
        Log.d("debug", "fromDegree: " + tileView.getRotation()
                +" Spacing: " + mSpacing
                + " toDegree: "+ mDegree);
        startTimer();

        RotateAnimation ra_U = new RotateAnimation(lastDegree,mDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra_U.setDuration(1000);
        ra_U.setInterpolator(new LinearInterpolator());
        ra_U.setFillAfter(true);
        ra_U.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation arg0) {


            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        for (int i = 0; i < List_Length; i++) {
            LabelMarker[i].setRotation(mDegree);
           // LabelMarker[i].startAnimation(ra_U);
        }

    }

    //region ###################################################  Sensor  ####################################################
    private boolean ElectronicCompassON = true;
    private void StartElectronicCompass(){
        // 註冊感應監聽器
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    private void StopElectronicCompass(){
        // 停止感應監聽器
        mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }
        if (mGravity != null && mGeomagnetic != null) {

            SensorManager.getRotationMatrix(Rotation, null, mGravity, mGeomagnetic);
            SensorManager.getOrientation(Rotation, degree);

            degree[0] = (float) Math.toDegrees(degree[0]);
            //Log.d("debug", "degree: " + String.valueOf( -degree[0]));

            // 取方位
            if((int)degree[0]<30 && (int)degree[0]>-30) { vector="北";}
            else if((int)degree[0]>=30  && (int)degree[0]<=60){ vector="東北"; }
            else if((int)degree[0]>60   && (int)degree[0]<120){ vector="東"; }
            else if((int)degree[0]>=120 && (int)degree[0]<=150){ vector="東南"; }
            else if((int)degree[0]>=-60 && (int)degree[0]<=-30){ vector="西北"; }
            else if((int)degree[0]>=-150&& (int)degree[0]<=-120){ vector="西南"; }
            else if((int)degree[0]>-120 && (int)degree[0]<-60){ vector="西"; }
            else{ vector="南"; }

            if (Math.abs(currentDegree - degree[0]) > 5) {

                RotateMap((int)degree[0]);
                currentDegree = -degree[0];

                // Stop ElectronicCompass
                ElectronicCompassON = false;
                StopElectronicCompass();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //endregion ################################################  Sensor  ####################################################

    //region #################################################   GPS   ####################################################

    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);

            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MinTime, MinDistance, this);
            Log.d("debug", "gpsEnable, startActivity");
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MinTime, MinDistance, this);
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(MapFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();
                Toast.makeText(MapFragment.this.getActivity(), "GPS 啟動", Toast.LENGTH_SHORT).show();
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(MapFragment.this.getActivity(), "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 0x01:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        locationStart();

                        break;
                    case Activity.RESULT_CANCELED:

                        break;
                }
                break;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");

                Toast.makeText(MapFragment.this.getActivity(), "LocationProvider.AVAILABLE", Toast.LENGTH_SHORT).show();

                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");

                Toast.makeText(MapFragment.this.getActivity(), "LocationProvider.OUT_OF_SERVICE", Toast.LENGTH_SHORT).show();

                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");

                Toast.makeText(MapFragment.this.getActivity(), "LocationProvider.TEMPORARILY_UNAVAILABLE", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(tileView_Run){
            // path marker
            tileView.removeMarker(PathMarker[PathMarker_Num % 10]);
            double[] PathMarker_Point = {location.getLongitude() ,location.getLatitude()};
            PathMarker[PathMarker_Num] = new LinearLayout(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(6, 6, 1);
            PathMarker[PathMarker_Num].setLayoutParams(layoutParams);
            PathMarker[PathMarker_Num].setTag(PathMarker_Point);
            // set ICON
            ImageView mDot = new ImageView( PathMarker[PathMarker_Num].getContext());
            mDot.setImageResource(R.drawable.dot);
            PathMarker[PathMarker_Num].addView(mDot,layoutParams);
            mDot.setY(6);
            // add it to the view tree
            tileView.addMarker(PathMarker[PathMarker_Num], PathMarker_Point[0], PathMarker_Point[1], null, null);
            PathMarker_Num ++;
            if(PathMarker_Num == 10){
                PathMarker_Num = 0;
            }

            // Now marker
            double[] NowMarker_Point = {location.getLongitude() ,location.getLatitude()};
            tileView.removeMarker(NowMarker);
            NowMarker = new LinearLayout(getActivity());
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(44, 44, 1);
            NowMarker.setLayoutParams(mLayoutParams);
            // save the coordinate for centering and callout positioning
            NowMarker.setTag(NowMarker_Point);
            // set ICON
            ImageView mICON = new ImageView(NowMarker.getContext());
            mICON.setImageResource(R.drawable.map_min);
            NowMarker.addView(mICON,mLayoutParams);
            mICON.setY(22);

            // if in National United University
            if(NowMarker_Point[0] > Constants.NORTH_WEST_LONGITUDE
                    && NowMarker_Point[0] < Constants.SOUTH_EAST_LONGITUDE
                    && NowMarker_Point[1] > Constants.SOUTH_EAST_LATITUDE
                    && NowMarker_Point[1] < Constants.NORTH_WEST_LATITUDE){
                // add it to the view tree
                tileView.addMarker(NowMarker, NowMarker_Point[0], NowMarker_Point[1], null, null);
                // moveToMarker
                getTileView().moveToMarker( NowMarker,false);
            }

            // rotation
            if(lasttLatitude != 0){
                float RotateValue = (float)GetAngle(lasttLatitude,lastLongitude,location.getLatitude(),location.getLongitude());
                mICON.setRotation(RotateValue);
                if(distanceInmBetweenEarthCoordinates(lasttLatitude,lastLongitude,location.getLatitude(),location.getLongitude())>1.5){
                    RotateMap(RotateValue);
                }
            }

            //Toast.makeText(MapFragment.this.getActivity(), "Lon: " +  String.valueOf( location.getLongitude()) + " Lat: " + String.valueOf(location.getLatitude()),Toast.LENGTH_SHORT).show();
            Log.d("debug", "Lon: " +  String.valueOf( location.getLongitude()) + " Lat: " + String.valueOf(location.getLatitude()));
            Log.d("debug", "Distance: " + distanceInmBetweenEarthCoordinates(lasttLatitude,lastLongitude,location.getLatitude(),location.getLongitude()));

            lasttLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    double  lasttLatitude = 0,lastLongitude = 0; // 紀錄
    public double GetAngle(double ax, double ay,double bx,double by){
        // 這邊需要過濾掉位置相同的問題
        if ( ax == bx && ay >= by ) return 0;
        bx -= ax;
        by -= ay;
        //double angle = Math.cos(-by / bx) * (180 / Math.PI);
        double DRoation = Math.atan2(by,bx);
        double WRotation = DRoation/Math.PI*180;

        //return (bx < ax ? -angle : angle);
        //Toast.makeText( MapFragment.this.getActivity(),  "角度" + String.valueOf(WRotation) , Toast.LENGTH_LONG).show();
        return (WRotation);
    }

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    private double distanceInmBetweenEarthCoordinates(double lat1,double lon1,double lat2,double lon2) {
        double earthRadiusKm = 6371;
        double dLat = degreesToRadians(lat2-lat1);
        double dLon = degreesToRadians(lon2-lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadiusKm * c * 1000;
    }

    //endregion #######################################################  GPS   ####################################################

    //region #################################################  Dijkstra   ####################################################

    private void GetMIN(){

        int[][] PointNumTemp = new int[ StartPointArr.length ][ EndPointArr.length ]; // 各路徑的距離紀錄
        int vsTemp; //  起點

        for (int PointNum = 0; PointNum <  StartPointArr.length ; PointNum++) {
            vsTemp =  Integer.parseInt(StartPointArr[PointNum]) -1;  //  設定起點  點的起始為1
            if(IsUsedMapA == 1){ // 二坪用
                vsTemp = vsTemp - 125;
            }

            // flag[i]=true表示"頂點vsTemp"到"頂點i"的最短路徑已成功獲取
            boolean[] flag = new boolean[mVexs.length];
            // 初始化
            for (int i = 0; i < mVexs.length; i++) {
                parent[i] = -1;//
                flag[i] = false;          // 頂點i的最短路徑還沒獲取到。
                //prev[i] = 0;              // 頂點i的前驅頂點為0。
                dist[i] = mMatrix[vsTemp][i];  // 頂點i的最短路徑為"頂點vsTemp"到"頂點i"的權。
            }
            // 對"頂點vsTemp"自身進行初始化
            flag[vsTemp] = true;
            dist[vsTemp] = 0;
            // 遍歷mVexs.length-1次；每次找出一個頂點的最短路徑。
            int k = 0;
            for (int i = 1; i < mVexs.length; i++) {
                // 尋找當前最小的路徑；
                // 即，在未獲取最短路徑的頂點中，找到離vsTemp最近的頂點(k)。
                int min = INF;
                for (int j = 0; j < mVexs.length; j++) {
                    if (flag[j] == false && dist[j] < min) {
                        min = dist[j];
                        k = j;
                    }
                }
                // 標記"頂點k"為已經獲取到最短路徑
                flag[k] = true;
                // 修正當前最短路徑和前驅頂點
                // 即，當已經"頂點k的最短路徑"之後，更新"未獲取最短路徑的頂點的最短路徑和前驅頂點"。
                for (int j = 0; j < mVexs.length; j++) {
                    int tmp = (mMatrix[k][j] == INF ? INF : (min + mMatrix[k][j]));
                    if (flag[j] == false && (tmp < dist[j])) {
                        dist[j] = tmp;
                        //prev[j] = k;
                        parent[j] = k;//
                    }
                }
            }

            //  存入各點之間的距離大小
            for (int N = 0; N <  EndPointArr.length ; N++) {
                //dist[N] 距離
                if(IsUsedMapA == 1){ // 二坪用 -125  (125)
                    PointNumTemp[PointNum][N] = dist[Integer.parseInt(EndPointArr[N])-125];
                }
                else{
                    PointNumTemp[PointNum][N] = dist[Integer.parseInt(EndPointArr[N]) -1]; // 給終點 取出最短路徑
                }
            }

        }

        // 最小位置
        int MinxPoint=0 ,  MinyPoint=0;
        for (int qx = 0;qx < StartPointArr.length ; qx++) {
            for (int qy = 0; qy < EndPointArr.length ; qy++) {
                if (PointNumTemp[MinxPoint][MinyPoint] > PointNumTemp[qx][qy]) {
                    MinxPoint = qx;
                    MinyPoint = qy;
                }
            }
        }

        // 取出對應的點
        if(IsUsedMapA == 1){ // 二坪用
            // 設定最短的起點+終點   從別的Activity取得原始的值
            StartPointMin = Integer.parseInt(StartPointArr[MinxPoint]) -1 -125; // 最近短距離的 起點
            EndPointMin   = Integer.parseInt(EndPointArr[MinyPoint])   -1 -125; // 最近短距離的 終點
        }
        else{
            StartPointMin = Integer.parseInt(StartPointArr[MinxPoint]) -1; // 最近短距離的 起點
            EndPointMin   = Integer.parseInt(EndPointArr[MinyPoint])   -1; // 最近短距離的 終點
        }
    }

    private void GetDijkstra(){

        /*
         * Dijkstra最短路徑。
         * 即，統計圖中"頂點vs"到其它各個頂點的最短路徑。
         *
         * 參數說明：
         *       vs -- 起始頂點(start vertex)。即計算"頂點vs"到其它頂點的最短路徑。
         *     prev -- 前驅頂點陣列。即，prev[i]的值是"頂點vs"到"頂點i"的最短路徑所經歷的全部頂點中，位於"頂點i"之前的那個頂點。
         *     dist -- 長度陣列。即，dist[i]是"頂點vs"到"頂點i"的最短路徑的長度。
         */

         /*
        for (int i = 0; i < mVexs.length; i++) { // 9999 換成 INF = 無限大
            for (int j = 0; j < mVexs.length; j++) {
                mMatrix[i][j]= Loaddata[i][j];
                if(Loaddata[i][j]==9999){
                    mMatrix[i][j]=INF;
                }
            }
        }
       */

        //vs=3;
        //vs = Integer.valueOf(txtScreen.getText().toString());
        // flag[i]=true表示"頂點vs"到"頂點i"的最短路徑已成功獲取
        boolean[] flag = new boolean[mVexs.length];
        // 初始化
        for (int i = 0; i < mVexs.length; i++) {
            parent[i] = -1;//
            flag[i] = false;          // 頂點i的最短路徑還沒獲取到。
            //prev[i] = 0;              // 頂點i的前驅頂點為0。
            dist[i] = mMatrix[vs][i];  // 頂點i的最短路徑為"頂點vs"到"頂點i"的權。
        }
        // 對"頂點vs"自身進行初始化
        flag[vs] = true;
        dist[vs] = 0;
        // 遍歷mVexs.length-1次；每次找出一個頂點的最短路徑。
        int k=0;
        for (int i = 1; i < mVexs.length; i++) {
            // 尋找當前最小的路徑；
            // 即，在未獲取最短路徑的頂點中，找到離vs最近的頂點(k)。
            int min = INF;
            for (int j = 0; j < mVexs.length; j++) {
                if (flag[j]==false && dist[j]<min) {
                    min = dist[j];
                    k = j;
                }
            }
            // 標記"頂點k"為已經獲取到最短路徑
            flag[k] = true;
            // 修正當前最短路徑和前驅頂點
            // 即，當已經"頂點k的最短路徑"之後，更新"未獲取最短路徑的頂點的最短路徑和前驅頂點"。
            for (int j = 0; j < mVexs.length; j++) {
                int tmp = (mMatrix[k][j]==INF ? INF : (min + mMatrix[k][j]));
                if (flag[j]==false && (tmp<dist[j]) ) {
                    dist[j] = tmp;
                    //prev[j] = k;
                    parent[j] = k;//
                }
            }
        }

        /*
        for (int i = 0; i < mVexs.length; i++) {
            displaytext +=  mVexs[vs] + " to " + mVexs[i] + "=" + dist[i];
            displaytext += " ( " + vs;
            PrintPath(parent, i);
             displaytext +=  " ) ";
             displaytext += "\n";
        }
         */
        //txtScreen.setText(displaytext);

        //int EndPointTemp = Integer.parseInt(EndString)-1;
        //int StartPointTemp Integer.parseInt(StartString)-1;

        int StartPointTemp = StartPointMin;
        int EndPointTemp = EndPointMin;
        if(IsUsedMapA == 1){ // 二坪用
            DrawPointsList.add(new double[]{GPS_Point.Xys_List[StartPointTemp+125][0] , GPS_Point.Xys_List[StartPointTemp+125][1]});//加入起點座標
            PrintPath(parent, EndPointTemp);//加入路徑座標
            DrawPointsList.add(new double[]{GPS_Point.Xys_List[EndPointTemp+125+1][0] , GPS_Point.Xys_List[EndPointTemp+125][1]});//加入終點座標
        }
        else{
            DrawPointsList.add(new double[]{GPS_Point.Xys_List[StartPointTemp][0] , GPS_Point.Xys_List[StartPointTemp][1]});//加入起點座標
            PrintPath(parent, EndPointTemp);//加入路徑座標
            DrawPointsList.add(new double[]{GPS_Point.Xys_List[EndPointTemp][0] , GPS_Point.Xys_List[EndPointTemp][1]});//加入終點座標
        }
        Toast.makeText(this.getActivity(),  String.valueOf(dist[EndPointTemp]) , Toast.LENGTH_SHORT).show(); // 距離
    }

    public void Draw_Dijkstra(){
        String StartString,EndString;

        // remove marker
        while(true) {
            int num = 0;
            for(int i=0;i<tileView.getMarkerLayout().getChildCount() ;i++){
                if( tileView.getMarkerLayout().getChildAt(i) instanceof TextView ){
                }else{
                    tileView.removeMarker(tileView.getMarkerLayout().getChildAt(i));
                    num++;
                }
            }
            if(num == 0){
                break;
            }
        }
        // remove path
        tileView.getCompositePathView().clear();
        // remove point list
        DrawPointsList = new ArrayList<>();

        tileView.defineBounds(0, 0, 25960, 12088);
        GlobalVariable globalVariable = (GlobalVariable)getActivity().getApplicationContext();
        StartStr = globalVariable.Start;
        EndStr   = globalVariable.End;
        //~~~~~~~~~~~~~~取得傳遞過來的資料~~~~~~~~~~~~~~
        //Intent intent = this.getActivity().getIntent();
        //StartString = intent.getStringExtra("startstring");
        //EndString = intent.getStringExtra("endstring");

        StartString = StartStr;
        EndString   = EndStr;

        StartPointArr = StartString.split("-");
        EndPointArr   = EndString.split("-");
        String MapFileName;

        if ((Integer.parseInt(StartPointArr[0]) < 125 && Integer.parseInt(EndPointArr[0]) < 125) ||
                (Integer.parseInt(StartPointArr[0]) > 125 && Integer.parseInt(EndPointArr[0]) > 125)) { //只有 八甲(b) OR 二坪(a)


            if(Integer.parseInt(StartPointArr[0]) < 125){
                MapFileName =  ("map_b.txt");
                IsUsedMapA = 0;
            }else{
                MapFileName =  ("map_a.txt");
                IsUsedMapA = 1;
            }
            LoadFileToMatrix(MapFileName);

            GetMIN();
            vs = StartPointMin;
            GetDijkstra();
        } else { // 跨校區
            if (Integer.parseInt(StartPointArr[0]) < 125) {
                IsUsedMapA = 0;
                MapFileName = ("map_b.txt");
                EndPointArr = new String[]{"67"};
            }else{
                IsUsedMapA = 1;
                MapFileName = ("map_a.txt");
                EndPointArr = new String[]{"126"};
            }
            LoadFileToMatrix(MapFileName);
            GetMIN();
            vs = StartPointMin;
            GetDijkstra();
            EndPointArr = EndString.split("-");//恢復

            // TODO 畫八甲跟二坪之間的路徑
            //DrawPointsList.add(new double[]{120.799379, 24.543649});

            if (Integer.parseInt(StartPointArr[0]) < 125) {
                IsUsedMapA = 1;
                MapFileName = ("map_a.txt");
                StartPointArr = new String[]{"126"};
               
            }else{
                IsUsedMapA = 0;
                MapFileName = ("map_b.txt");
                StartPointArr = new String[]{"67"};
            }
            LoadFileToMatrix(MapFileName);
            GetMIN();
            vs = StartPointMin;
            GetDijkstra();
            StartPointArr = StartString.split("-");//恢復
        }

        // 畫線 & 標記
        int PointNo = 0;
        for (double[] point : DrawPointsList) {
            //marker = new ImageView(this);
            ImageView marker = new ImageView(this.getActivity());
            // save the coordinate for centering and callout positioning
            marker.setTag(point);
            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors

            if (PointNo == 0) { 
                // 標記起點
                marker.setImageResource(R.drawable.map_marker_green_f);
            } else if (PointNo == DrawPointsList.size() - 1) { 
                // 標記終點
                marker.setImageResource(R.drawable.map_marker_red_f);
            } else {
                marker.setImageResource(R.drawable.map_path_marker);
            }

            marker.setScaleY((float) 0.5);
            marker.setScaleX((float) 0.5);
            marker.setScrollY(-100);
            
            // on tap show further information about the area indicated
            // this could be done using a OnClickListener, which is a little more "snappy", since
            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
            // confirm it's not the start of a double-tap. But this would consume the touch event and
            // interrupt dragging
            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            // add it to the view tree
            tileView.addMarker(marker, point[0], point[1], null, null);

            if(PointNo==0){
                tileView.moveToMarker(marker,false);
            }

            PointNo++;
        }

        // drawPath
        tileView.drawPath(DrawPointsList.subList(0, PointNo), null);//  點數  畫線
        tileView.defineBounds(Constants.NORTH_WEST_LONGITUDE, Constants.NORTH_WEST_LATITUDE, Constants.SOUTH_EAST_LONGITUDE, Constants.SOUTH_EAST_LATITUDE);
        mMatrix = new int[0][0];
    }

    private void PrintPath(int parent[], int j) {
        // Base Case : If j is source
        if (parent[j]==-1) {
            if(IsUsedMapA == 1){ // 二坪(A)
                DrawPointsList.add(new double[]{GPS_Point.Xys_List[j+125][0] , GPS_Point.Xys_List[j+125][1]});
            }
            else{
                DrawPointsList.add(new double[]{GPS_Point.Xys_List[j][0] , GPS_Point.Xys_List[j][1]});
            }
            return;
        }
        PrintPath(parent, parent[j]);

        if(IsUsedMapA == 1){ // 二坪(A)
            DrawPointsList.add(new double[]{GPS_Point.Xys_List[j+125][0] , GPS_Point.Xys_List[j+125][1]});
        }
        else{
            DrawPointsList.add(new double[]{GPS_Point.Xys_List[j][0] , GPS_Point.Xys_List[j][1]});
        }
    }

    public String LoadFileToMatrix(String fStartString){
        String result = null;

        try {
            InputStream in=this.getResources().getAssets().open(fStartString);
            int ch=0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((ch=in.read())!=-1)
            {
                baos.write(ch);
            }
            byte[] buff=baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, StandardCharsets.UTF_8);
            String[] AfterSplit =  result.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            int ArraySum = (int)Math.sqrt(AfterSplit.length);  //  開根號 及 型別轉換

            // ~~~~~~~~~~~~~~~~~~設定各種大小~~~~~~~~~~~~~~~~~~~~
            mVexs = new int[ArraySum];// 純標記
            mMatrix = new int[mVexs.length][mVexs.length];
            prev = new int[mVexs.length];
            dist = new int[mVexs.length];
            parent = new int[mVexs.length];

            //~~~~~~~~~~~~~~~~~~ 一維轉二維陣列 ~~~~~~~~~~~~~~~~~~~~
            int k = 0;
            for (int i = 0; i< ArraySum ; i++){
                for (int j = 0 ; j < ArraySum ; j++){
                    int temp= Integer.parseInt(AfterSplit[k]);
                    if(temp == 9999){
                        mMatrix[i][j] = INF;
                    }
                    else{
                        mMatrix[i][j] = temp;
                    }
                    k++;
                }
            }
            result=result.replaceAll("\\r\\n","\n");
        }
        catch(Exception e)
        {
            Toast.makeText(this.getActivity(), "你已經GG了！", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    //endregion #########################################################  Dijkstra   ####################################################

    // Fragment Communicating
    String StartStr , EndStr;
    protected void displayReceivedData(String message) {
        //txtData.setText("Data received: "+message);
    }

    @Override
    public void onResume() {
        super.onResume();

        tileView.resume();
        tileView_Run = true;

        locationStart();

        if (ElectronicCompassON){
            StartElectronicCompass();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tileView_Run = false;
        tileView.destroy();
        tileView = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        tileView_Run = false;
        tileView.pause();

        // 停止GPS更新
        if (locationManager != null) {
            Log.d("LocationActivity", "locationManager.removeUpdates");
            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.removeUpdates(this);
        }

        if (ElectronicCompassON){
            StopElectronicCompass();
        }
    }
}