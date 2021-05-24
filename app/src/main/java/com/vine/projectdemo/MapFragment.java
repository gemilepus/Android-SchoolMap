package com.vine.projectdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerLayout;
import com.vine.projectdemo.VineMapView.SampleCallout;

import com.vine.projectdemo.VineReValues.GPS_Dot;
import com.vine.projectdemo.VineReValues.GlobalVariable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;
import static com.vine.projectdemo.VinePHPMySQL.Constants.BASE_URL;

public class MapFragment extends Fragment implements SensorEventListener, LocationListener {

    public static final String TITLE = "校園地圖";
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  TileView  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    LinearLayout mLinearLayout_P;
    TileView tileView;
    int tileView_Run = 1;
    public static final double SOUTH_EAST_LONGITUDE = 120.8167;
    public static final double SOUTH_EAST_LATITUDE = 24.533648; //南 東
    public static final double NORTH_WEST_LONGITUDE = 120.7832;//經度     //0.012904468412943
    public static final double NORTH_WEST_LATITUDE = 24.547866; //北  西 緯度   //0.0117471872931833
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ GPS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private LocationManager locationManager;
    private static final int MinTime = 1000;//更新時間
    private static final float MinDistance = 1;//移動多少 M 才會監聽
    int GPS_Run = 0;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Dijkstra ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static final int INF = Integer.MAX_VALUE;   // 最大值
    int[] mVexs;// 純標記
    int mMatrix[][];
    int vs;//起始點
    int[] prev;
    int[] dist;
    int[] parent;

    String[] AfterSplitStartString;
    String[] AfterSplitEndString;

    int Doflag = 0, Plusflag = 0; //  跨區旗標
    int StartPointMin = 0, EndPointMin = 0;  // 最近短距離的 起點 終點
    int ListStFlag = 0;

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Label  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    TextView[] marker_Text_array = new TextView[60]; // Test Array
    String ValueString, TextString;
    String[] ValueStringArray;
    List<DataObject> list;
    int List_Length;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  dot  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ImageView[] marker_Movedot_array = new ImageView[25];
    // ImageView[] marker_Movedot_array[] ;
    int marker_Move_dot_num = 0;
    ImageView marker_MoveHere;//作為全域使用  removeMarker
    ArrayList<double[]> DrawPointsList = new ArrayList<>();
    
    // ~~~~~~~~~~~~~~~  ElectronicCompass  ~~~~~~~~~~~~~~~
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    String vector;
    float[] mGravity;
    float[] mGeomagnetic;
    float Rotation[] = new float[9];
    float[] degree = new float[3];
    Button ElectronicCompassBtn;
    Timer timer = new Timer(true);
    Timer timerAnima = new Timer(true);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_content_main, container, false);

        // 電子羅盤
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        // 電子羅盤

        //String  NN;
        //NN = String.valueOf(v.getTag());

        ElectronicCompassBtn = (Button) v.findViewById(R.id.BTNN);
        ElectronicCompassBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSensorManager.unregisterListener(MapFragment.this);
            }
        });

        tileView = new TileView(this.getActivity());

        // let the image explode
        tileView.setScaleLimits(0, 2);//放大大小

        // size of original image at 100% mScale

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
        // 陰影效果  (耗能)
//        paint.setShadowLayer(
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics),
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
//                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics),
//                0x66000000
//        );

        paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics));  //  線寬
//        paint.setPathEffect(
//                new CornerPathEffect(
//                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics)
//                )
//        ); // 線的彎曲幅度
        //paint.setPathEffect(null);  // 不做任何效果

        paint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0)); // 畫虛線
        //paint.setColor();

        //tileView.drawPath(points.subList(2, 5), null);//畫線

        // set mScale to 0, but keep scaleToFit true, so it'll be as small as possible but still match the container
        tileView.setScale(0);

        // frame to center
        // frameTo(0.5, 0.5);

        // render while panning
        tileView.setShouldRenderWhilePanning(true);

        // disallow going back to minimum scale while double-taping at maximum scale (for demo purpose)
        tileView.setShouldLoopScale(false);

        // tileView.setSaveEnabled( true );
        // setContentView(tileView);

        // View v =  tileView;
        //LinearLayout lp =  (LinearLayout) v.findViewById(R.id.LI);
        //lp.addView(tileView,lp);
        //((LinearLayout)getActivity().findViewById(R.id.LI).addView(tileView,lp);

        // 用 LinearLayout 當框框 放入 tileView ， tileView 超出的部分會裁掉
        mLinearLayout_P = (LinearLayout) v.findViewById(R.id.LI);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1); // 框框 View
        ((LinearLayout) v.findViewById(R.id.LI)).addView(tileView, lp); // addView
        tileView.setScaleY((float) 16.5 / 10.f); // 放大
        tileView.setScaleX((float) 16.5 / 10.f); // 放大

        list = new ArrayList<DataObject>();
        list.add(0, new DataObject("二坪校區正門", "二坪校區", "126"));
        list.add(1, new DataObject("J1活動中心", "二坪校區", "139-142"));
        list.add(2, new DataObject("H1行政大樓", "二坪校區", "144-157-170"));
        list.add(3, new DataObject("F1第一研究大樓", "二坪校區", "147-172"));


        list.add(4, new DataObject("F2建築系館&設計學院", "二坪校區", "150-160"));
        list.add(5, new DataObject("K2教學大樓", "二坪校區", "153-178"));
        list.add(6, new DataObject("K3教學大樓", "二坪校區", "154-182"));
        list.add(7, new DataObject("景觀餐廳", "二坪校區", "163"));
        list.add(8, new DataObject("C1管理學院", "二坪校區", "168"));
        list.add(9, new DataObject("F3工業設計系館", "二坪校區", "175"));
        list.add(10, new DataObject("M2男一舍", "二坪校區", "184"));
        list.add(11, new DataObject("M8男二舍", "二坪校區", "180"));
        list.add(12, new DataObject("M4男三舍-影山樓", "二坪校區", "208-211"));
        list.add(13, new DataObject("M3女一舍&學生餐廳", "二坪校區", "199"));
        list.add(14, new DataObject("M7女二舍", "二坪校區", "173-196"));
        list.add(15, new DataObject("M6女三舍", "二坪校區", "193"));
        list.add(16, new DataObject("M1女五舍", "二坪校區", "203"));
        list.add(17, new DataObject("P1教職員工宿舍", "二坪校區", "215-217"));
        list.add(18, new DataObject("操場&籃球場", "二坪校區", "140"));
        list.add(19, new DataObject("蓮荷水世界", "二坪校區", "214"));
        list.add(20, new DataObject("網球場", "二坪校區", "218"));
        list.add(21, new DataObject("第一機車停車場", "二坪校區", "134-135"));
        list.add(22, new DataObject("第二機車停車場", "二坪校區", "190"));
        list.add(23, new DataObject("汽車停車場", "二坪校區", "225"));
        list.add(24, new DataObject("蓮荷文藝空間", "二坪校區", "188"));
        list.add(25, new DataObject("淑女坡", "二坪校區", "137"));
        list.add(26, new DataObject("好漢坡", "二坪校區", "219"));
        list.add(27, new DataObject("好客坡", "二坪校區", "186"));
        list.add(28, new DataObject("電機系館", "八甲校區", "73"));
        list.add(29, new DataObject("電子系館", "八甲校區", "40-74"));
        list.add(30, new DataObject("光電系館", "八甲校區", "52"));
        list.add(31, new DataObject("資訊系館", "八甲校區", "51-50"));
        list.add(32, new DataObject("國鼎圖書館", "八甲校區", "75"));
        list.add(33, new DataObject("資訊處", "八甲校區", "76"));
        list.add(34, new DataObject("理工學院一館", "八甲校區", "122-123-47-125"));
        list.add(35, new DataObject("理工學院二館", "八甲校區", "118-119"));
        list.add(36, new DataObject("共同教育委員會", "八甲校區", "80-81-82-83-84-85-86-94"));
        list.add(37, new DataObject("人文社會學院", "八甲校區", "88-89-90-91-92-93-96-97-98-99-100-101"));
        list.add(38, new DataObject("客家學院", "八甲校區", "105-106-107-108-109-112-113-114-115"));
        list.add(39, new DataObject("藝文教學中心", "八甲校區", "110-111-116"));
        list.add(40, new DataObject("學生餐廳", "八甲校區", "120"));
        list.add(41, new DataObject("第五男生宿舍", "八甲校區", "121"));
        list.add(42, new DataObject("風雨球場", "八甲校區", "59"));

        //GPS 服務檢查
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            //請求權限的對話框
        } else {
            locationStart();
            GPS_Run = 1;
        }

        DrawTextMarker();

        //loadJSON(); // not use

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public TileView getTileView(){
        return tileView;
    }

    public void frameTo( final double x, final double y ) {
        getTileView().post( new Runnable() {
            @Override
            public void run() {
               // getTileView().moveToMarker();
            }
        });
    }

    public void DrawTextMarker() {

        //region 標記所有點
//        ArrayList<double[]> map_point = new ArrayList<>();{}
//        for (int r = 0; r < 227; r++) {
//            map_point.add(new double[]{GPS_Dot.Xys_List[r][0] ,GPS_Dot.Xys_List[r][1]});
//        }
//        for (double[] point : map_point) {  //建立標記圖示
//            // any view will do...
//            //marker = new ImageView(this);
//            ImageView marker = new ImageView(this.getActivity());
//            // save the coordinate for centering and callout positioning
//            marker.setTag(point);
//            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors
//            //marker.setImageResource(Math.random() < 0.75 ? R.drawable.map_marker_normal : R.drawable.map_marker_featured);//random 隨機
//            //random 隨機
//            marker.setImageResource(R.drawable.map_marker_123);
//            // on tap show further information about the area indicated
//            // this could be done using a OnClickListener, which is a little more "snappy", since
//            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
//            // confirm it's not the start of a double-tap. But this would consume the touch event and
//            // interrupt dragging
//            // tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
//            // add it to the view tree
//
//            tileView.addMarker(marker, point[0], point[1], null, null);
//        }
        //endregion

        List_Length = (int) list.size();
        double x, y;
        for (int N = 0; N < List_Length; N++) {

            x = 0;
            y = 0;
            TextString = list.get(N).getHeading();
            ValueString = list.get(N).getValue();
            ValueStringArray = ValueString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");
            for (int i = 0; i < ValueStringArray.length; i++) {
                //Integer.valueOf( ValueStringArray[N])
                // ValueStringArray[]
                x = x + GPS_Dot.Xys_List[Integer.valueOf(ValueStringArray[i]) - 1][0];
                y = y + GPS_Dot.Xys_List[Integer.valueOf(ValueStringArray[i]) - 1][1];
            }
            x = x / ValueStringArray.length;
            y = y / ValueStringArray.length;

            ArrayList<double[]> pointsText = new ArrayList<>();
            // pointsText.add(new double[]{GPS_Dot.Xys_List[Integer.valueOf( ValueStringArray[N])-1][0] ,GPS_Dot.Xys_List[Integer.valueOf( ValueStringArray[N])-1][1]});
            marker_Text_array[N] = new TextView(this.getActivity());
            //TextView marker = new TextView(this.getActivity());
            marker_Text_array[N].setText(TextString);
            marker_Text_array[N].setTag(pointsText);
            marker_Text_array[N].setTextColor(Color.parseColor("#ff007acc"));
            marker_Text_array[N].setBackgroundColor(Color.parseColor("#4032abfc"));

            // tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            // tileView.addMarker(marker,GPS_Dot.Xys_List[Integer.valueOf( ValueStringArray[N])-1][0] ,GPS_Dot.Xys_List[Integer.valueOf( ValueStringArray[N])-1][1], null, null);
            tileView.addMarker(marker_Text_array[N], x, y, null, null);
        }
        // 轉變為GPS座標
        tileView.defineBounds(NORTH_WEST_LONGITUDE, NORTH_WEST_LATITUDE, SOUTH_EAST_LONGITUDE, SOUTH_EAST_LATITUDE);
    }

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

    //region ###################################################  Timer  ####################################################

    int RotationFlag = 0;

    public class MyTimerTask extends TimerTask {
        public void run() {
            MapFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MapFragment.this.getActivity(), " R ", Toast.LENGTH_LONG).show();
                    tileView.setRotation(RotationFlag);//地圖選轉
                    //marker.
                    for (int N = 0; N < List_Length; N++) { // 先只取一點
                        marker_Text_array[N].setRotation(-RotationFlag);//地圖選轉
                    }
                    RotationFlag = RotationFlag + 10;
                    if (RotationFlag == 360) {
                        RotationFlag = 0;
                    }
                }
            });
            // Toast.makeText(this,  String.valueOf(ra) , Toast.LENGTH_SHORT).show(); // 距離
        }
    }

    int MyTimerTaskATime = 0;
    float raAnima;
    int raAnimaLo;

    public class MyTimerTaskA extends TimerTask {
        public void run() {
            // do something (cut)

            // and at the end show info
            MapFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentDegree > -degree[0]) {
                        tileView.setRotation(currentDegree - MyTimerTaskATime * raAnima);
                    } else {
                        tileView.setRotation(currentDegree + MyTimerTaskATime * raAnima);
                    }
                    MyTimerTaskATime++;
                    if (MyTimerTaskATime == 30) {
                        MyTimerTaskATime = 0;
                        timerAnima.cancel();//  timer 停止
                    }
                }
            });
            // Toast.makeText(this,  String.valueOf(ra) , Toast.LENGTH_SHORT).show(); // 距離
        }
    }

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    // and at the end show info
                    MapFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentDegree > -degree[0]) {
                                tileView.setRotation(currentDegree - MyTimerTaskATime * raAnima);
                            } else {
                                tileView.setRotation(currentDegree + MyTimerTaskATime * raAnima);
                            }
                            MyTimerTaskATime++;
                            if (MyTimerTaskATime == 30) {
                                MyTimerTaskATime = 0;
                                stopTimer();
                            }
                        }
                    });
                }
            };
        }
        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 50, 50);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
    //endregion ###################################################  Timer  ####################################################

    //region ###################################################  Sensor  ####################################################
    int Animation_Run = 0;
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }
        if (mGravity != null && mGeomagnetic != null) {

            SensorManager.getRotationMatrix(Rotation, null, mGravity,
                    mGeomagnetic);
            SensorManager.getOrientation(Rotation, degree);

            degree[0] = (float) Math.toDegrees(degree[0]);

            //取方位
//            if ((int)degree[0]<30 && (int)degree[0]>-30) {
//                vector="北";}
//            else if ((int)degree[0]>=30 && (int)degree[0]<=60){
//                vector="東北";
//            }
//            else if ((int)degree[0]>60 && (int)degree[0]<120){
//                vector="東";
//            }
//            else if ((int)degree[0]>=120 && (int)degree[0]<=150){
//                vector="東南";
//            }
//            else if ((int)degree[0]>=-60 && (int)degree[0]<=-30){
//                vector="西北";
//            }
//            else if ((int)degree[0]>=-150 && (int)degree[0]<=-120){
//                vector="西南";
//            }
//            else if ((int)degree[0]>-120 && (int)degree[0]<-60){
//                vector="西";
//            }
//            else {
//                vector="南";
//            }
            //if( Animation_Run == 0) {
                if (currentDegree - (-degree[0]) > 5 || currentDegree - (-degree[0]) < -5 && MyTimerTaskATime == 0) {

                    //  Toast.makeText( MapFragment.this.getActivity(),  String.valueOf( -degree[0]) , Toast.LENGTH_LONG).show();

                    // tvHeading.setText("Heading: " + (int) degree[0] + " degrees  " + vector);

                    // currentDegree-初始角度,-degree逆時針旋轉結束角度
                    RotateAnimation ra = new RotateAnimation(currentDegree, -degree[0], Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // y座標 // x座標

                    // 轉動時間
                    ra.setDuration(200);

                    // 預設狀態結束後的動作設定
                    ra.setFillAfter(true);

                    // 將動作放入圖片
                   // tileView.startAnimation(ra);


                    // tileView.onAnimationEnd();


                    ra.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            Animation_Run = 1;
                        }

                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            //Functionality here


                            tileView.setRotation(-degree[0]);
                            //tileView.clearAnimation();
                            Animation_Run = 0;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


//                tileView.getAnimation().setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationEnd(Animation arg0) {
//                        //Functionality here
//                    }
//                });


                    // onAnimationStart()
                    //   onAnimationEnd()


                    // 自定的旋轉動畫
//                float raAnima ;
//                int   raAnimaLo;
//                raAnimaLo = (int)( Math.abs(currentDegree -(-degree[0])))*30;
//                raAnima = Math.abs(currentDegree -(-degree[0]))/raAnimaLo ;
//
//                if(currentDegree > -degree[0]){
//                    for (int r = 0; r < raAnimaLo ; r++) {
//                        tileView.setRotation(currentDegree - r * raAnima);
//                    }
//                }else{
//                    for (int r = 0; r < raAnimaLo ; r++) {
//                        tileView.setRotation(currentDegree + r * raAnima);
//                    }
//                }

                    //raAnimaLo = (int)( Math.abs(currentDegree -(-degree[0])))*30;
                    //raAnima = Math.abs(currentDegree -(-degree[0]))/30 ;


                    //timerAnima.schedule(new MyTimerTaskA(), 50, 50);  //long delay, long period  設 Timer


                    //startTimer();


                    //  Animation 不轉動實際VIEW 手勢方向會異常
                    tileView.setRotation(-degree[0]);

                    RotateAnimation ra_U = new RotateAnimation(-currentDegree, degree[0],
                            Animation.RELATIVE_TO_SELF, 0.5f, // x座標
                            Animation.RELATIVE_TO_SELF, 0.5f); // y座標

                    // 轉動時間
                    ra_U.setDuration(210);

                    // 預設狀態結束後的動作設定
                    ra_U.setFillAfter(true);


                    //marker.
                    for (int N = 0; N < List_Length; N++) { // 先只取一點
                        marker_Text_array[N].startAnimation(ra_U);

                    }

                    //  image.startAnimation(ra);
                    currentDegree = -degree[0];

                }
            //}
        }
    }

    /* 改變經確度 */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
    //endregion ################################################  Sensor  ####################################################

    //region #################################################   GPS   ####################################################

    private void locationStart() {
        Log.d("debug", "locationStart()");
        // LocationManager インスタンス生成
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);  // TabFragment 改

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MinTime, MinDistance, this);// 對GPS做一些設定

            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(MapFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);//重複的動作
    }

    //locationStart_Old
//  locationStart_Old
//    private void locationStart_Old(){
//        Log.d("debug","locationStart()");
//        // LocationManager インスタンス生成
//        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);  // TabFragment 改
//
//        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (!gpsEnabled) {
//            // GPSを設定するように促す
//            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(settingsIntent);
//
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MinTime, MinDistance, this);// 對GPS做一些設定
//
//            Log.d("debug", "gpsEnable, startActivity");
//        } else {
//            Log.d("debug", "gpsEnabled");
//        }
//
//        if (ActivityCompat.checkSelfPermission(MapFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MapFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
//
//            Log.d("debug", "checkSelfPermission false");
//            return;
//        }
//
//        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);//重複的動作
//    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");
                //Toast toast1 = Toast.makeText(this, "GPS 啟動", Toast.LENGTH_SHORT);
                locationStart();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(MapFragment.this.getActivity(), "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");

                Toast toast = Toast.makeText(MapFragment.this.getActivity(), "LocationProvider.AVAILABLE", Toast.LENGTH_SHORT);
                toast.show();

                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");

                Toast toast1 = Toast.makeText(MapFragment.this.getActivity(), "LocationProvider.OUT_OF_SERVICE", Toast.LENGTH_SHORT);
                toast1.show();

                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");

                Toast toast2 = Toast.makeText(MapFragment.this.getActivity(), "LocationProvider.TEMPORARILY_UNAVAILABLE", Toast.LENGTH_SHORT);
                toast2.show();
                break;
        }

    }

    double  lasttLatitude,lastLongitude;  // 紀錄
    @Override
    public void onLocationChanged(Location location) {  //GPS更新監聽
        if(tileView_Run == 1){ // 檢查 tileview 啟動
            //~~~~~~~~~~~~~~~~~~ path marker
            double[]  point3 = {location.getLongitude() ,location.getLatitude()};
            marker_Movedot_array[marker_Move_dot_num] = new ImageView(this.getActivity());
            marker_Movedot_array[marker_Move_dot_num].setTag(point3);
            marker_Movedot_array[marker_Move_dot_num].setImageResource(R.drawable.dot);
            //tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            tileView.addMarker(marker_Movedot_array[marker_Move_dot_num], point3[0], point3[1], null, null);  // 使GPS座標設定Marker的位置
            //marker_Movedot_array[marker_Move_dot_num].setScrollY(-95);
            marker_Movedot_array[marker_Move_dot_num].setScaleY((float) 0.6); // 縮小
            marker_Movedot_array[marker_Move_dot_num].setScaleX((float) 0.6); // 縮小
            marker_Move_dot_num = marker_Move_dot_num ++;

            if( marker_Move_dot_num == 26){
                marker_Move_dot_num = 0;
            }
            //~~~~~~~~~~~~~~~~~~~~~~ here marker
            double[] point2 = {location.getLongitude() ,location.getLatitude()};
            tileView.removeMarker(marker_MoveHere);
            marker_MoveHere = new ImageView(this.getActivity());
            // save the coordinate for centering and callout positioning
            marker_MoveHere.setTag(point2);
            //~~~~~~~~~~~~~~~~~~~~ rotation
            //marker_MoveHere.setImageResource(R.drawable.map_marker_green_f);
            marker_MoveHere.setImageResource(R.drawable.map_min_m);
            // add it to the view tree
            //tileView.addMarker(marker2, 120.816 ,24.536, null, null);
            tileView.addMarker(marker_MoveHere, point2[0], point2[1], null, null);// 使GPS座標設定Marker的位置
            marker_MoveHere.setRotation( (int)GetAngle( lasttLatitude,lastLongitude,location.getLatitude(),location.getLongitude() ) ); //旋轉 TEST
            // moveToMarker
            getTileView().moveToMarker( marker_MoveHere,false);
            //Toast.makeText(this, "GPS : " +  String.valueOf( location.getLongitude()) +" , "+  String.valueOf(location.getLatitude())  , Toast.LENGTH_SHORT).show();

            lasttLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
        }
    }

    public double GetAngle(double ax, double ay,double bx,double by) // OK
    {
        // 這邊需要過濾掉位置相同的問題
        if ( ax == bx && ay >= by ) return 0;
        bx -= ax;
        by -= ay;
        //double angle = Math.cos(-by / bx) * (180 / Math.PI);
        double DRoation = Math.atan2(by,bx);
        double WRotation = DRoation/Math.PI*180;

        //return (bx < ax ? -angle : angle);
        //Toast.makeText( MapFragment.this.getActivity(),  "方向" + String.valueOf(WRotation) , Toast.LENGTH_LONG).show();
        return (WRotation);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //endregion #######################################################  GPS   ####################################################

    //region #################################################  Dijkstra   ####################################################


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   Dijkstra  & F ile I/O~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /*
     * Dijkstra最短路徑。
     * 即，統計圖中"頂點vs"到其它各個頂點的最短路徑。
     *
     * 參數說明：
     *       vs -- 起始頂點(start vertex)。即計算"頂點vs"到其它頂點的最短路徑。
     *     prev -- 前驅頂點陣列。即，prev[i]的值是"頂點vs"到"頂點i"的最短路徑所經歷的全部頂點中，位於"頂點i"之前的那個頂點。
     *     dist -- 長度陣列。即，dist[i]是"頂點vs"到"頂點i"的最短路徑的長度。
     */
    // dijkstraT(終點)
    private void  dijkstraT(){

//        for (int i = 0; i < mVexs.length; i++) { // 9999 換成 INF = 無限大
//            mVexs[i]= i ;
//        }


        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~已做過~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//        for (int i = 0; i < mVexs.length; i++) { // 9999 換成 INF = 無限大
//            for (int j = 0; j < mVexs.length; j++) {
//                mMatrix[i][j]= Loaddata[i][j];
//                if(Loaddata[i][j]==9999){
//                    mMatrix[i][j]=INF;
//                }
//            }
//        }
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~已做過~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //vs=3;
        // vs = Integer.valueOf(txtScreen.getText().toString());
        // flag[i]=true表示"頂點vs"到"頂點i"的最短路徑已成功獲取
        boolean[] flag = new boolean[mVexs.length];
        // 初始化
        for (int i = 0; i < mVexs.length; i++) {
            parent[i] = -1;//~~~
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
                    parent[j] = k;//~~~
                }
            }
        }
//        for (int i = 0; i < mVexs.length; i++) {
//            displaytext +=  mVexs[vs] + " to " + mVexs[i] + "=" + dist[i];
//            displaytext += " ( " + vs;
//            printPath(parent, i);
//             displaytext +=  " ) ";
//             displaytext += "\n";
//
//        }
        // txtScreen.setText(displaytext);//顯示結果
//        int EndPointTemp = Integer.parseInt(EndString)-1;//CCCCCCCCCCC
//        int StartPointTemp Integer.parseInt(StartString)-1;

        int StartPointTemp = StartPointMin;
        int EndPointTemp = EndPointMin;
//        DrawPointsList.add(new double[]{GPS_Dot.Xys_List[StartPointTemp][0] ,GPS_Dot.Xys_List[StartPointTemp][1]});//加入起點座標
//        printPath(parent, EndPointTemp);//加入路徑座標
//        DrawPointsList.add(new double[]{GPS_Dot.Xys_List[EndPointTemp][0] ,GPS_Dot.Xys_List[EndPointTemp][1]});//加入終點座標
        if(Plusflag == 1){ // 二坪用
            DrawPointsList.add(new double[]{GPS_Dot.Xys_List[StartPointTemp+125][0] ,GPS_Dot.Xys_List[StartPointTemp+125][1]});//加入起點座標
            printPath(parent, EndPointTemp);//加入路徑座標
//            DrawPointsList.add(new double[]{24.538329, 120.792896});
//            DrawPointsList.add(new double[]{24.539129, 120.795621});
            DrawPointsList.add(new double[]{GPS_Dot.Xys_List[EndPointTemp+125+1][0] ,GPS_Dot.Xys_List[EndPointTemp+125][1]});//加入終點座標
        }
        else{

            DrawPointsList.add(new double[]{GPS_Dot.Xys_List[StartPointTemp][0] ,GPS_Dot.Xys_List[StartPointTemp][1]});//加入起點座標
            printPath(parent, EndPointTemp);//加入路徑座標
            DrawPointsList.add(new double[]{GPS_Dot.Xys_List[EndPointTemp][0] ,GPS_Dot.Xys_List[EndPointTemp][1]});//加入終點座標

        }
        Toast.makeText(this.getActivity(),  String.valueOf(dist[EndPointTemp]) , Toast.LENGTH_SHORT).show(); // 距離

//        for (int r = 0;r< 227; r++) {   //  畫地圖上的所有點
//            map_point.add(new double[]{GPS_Dot.Xys_List[r][0] ,GPS_Dot.Xys_List[r][1]});
//        }
//        for (double[] point : map_point) {  //建立標記圖示
//            // any view will do...
//            //marker = new ImageView(this);
//
//            ImageView marker = new ImageView(this.getActivity());
//            // save the coordinate for centering and callout positioning
//            marker.setTag(point);
//            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors
//
//            //marker.setImageResource(Math.random() < 0.75 ? R.drawable.map_marker_normal : R.drawable.map_marker_featured);//random 隨機
//            //random 隨機
//
//            marker.setImageResource(R.drawable.map_marker_123);
//
//
//            marker.setScaleY((float) 0.5); // 放大
//            marker.setScaleX((float) 0.5); // 放大
//
//            // on tap show further information about the area indicated
//            // this could be done using a OnClickListener, which is a little more "snappy", since
//            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
//            // confirm it's not the start of a double-tap. But this would consume the touch event and
//            // interrupt dragging
//            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
//            // add it to the view tree
//
//            tileView.addMarker(marker, point[0], point[1], null, null);
//        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    void printPath(int parent[], int j) { // 路徑
        // Base Case : If j is source
        if (parent[j]==-1) {
            //  displaytext +=" > "+ j  ;
            // Toast.makeText(this,  String.valueOf(j) , Toast.LENGTH_SHORT).show();
            // DrawPointsList.add(new double[]{GPS_Dot.Xys_List[j][0] ,GPS_Dot.Xys_List[j][1]});
            if(Plusflag == 1){ // 二坪用
                DrawPointsList.add(new double[]{GPS_Dot.Xys_List[j+125][0] ,GPS_Dot.Xys_List[j+125][1]});
            }
            else{
                DrawPointsList.add(new double[]{GPS_Dot.Xys_List[j][0] ,GPS_Dot.Xys_List[j][1]});
            }
            return;
        }
        printPath(parent, parent[j]);

        //  displaytext +=" > "+ j  ;
        if(Plusflag == 1){ // 二坪用
            DrawPointsList.add(new double[]{GPS_Dot.Xys_List[j+125][0] ,GPS_Dot.Xys_List[j+125][1]});
        }
        else{
            DrawPointsList.add(new double[]{GPS_Dot.Xys_List[j][0] ,GPS_Dot.Xys_List[j][1]});
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~loadFile~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public String loadfiletoArray(String fStartString){
        String result=null;
        try
        {
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
            result = new String(buff,"UTF-8");//AN
            String[] AfterSplit =  result.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");// C
            int  ArraySum =  (int)Math.sqrt(AfterSplit.length);  //  開根號 及 型別轉換
            // ~~~~~~~~~~~~~~~~~~設定各種大小~~~~~~~~~~~~~~~~~~~~
            mVexs = new int[ArraySum];// 純標記

            mMatrix = new int[mVexs.length][mVexs.length];
            prev = new int[mVexs.length];
            dist = new int[mVexs.length];
            parent = new int[mVexs.length];
            //~~~~~~~~~~~~~~~~~~ 一維轉二維陣列 ~~~~~~~~~~~~~~~~~~~~
            int NumSplit = 0;
            for (int i = 0; i< ArraySum ; i++){
                for (int j = 0 ; j < ArraySum ; j++){
                    int temp= Integer.parseInt(AfterSplit[NumSplit]);
                    if(temp == 9999){
                        mMatrix[i][j] = INF;
                    }
                    else{
                        mMatrix[i][j] = temp;
                    }
                    NumSplit++;
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

    //END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   Dijkstra  & F ile I/O~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void dijkstraMin(){
        int[][] PointNumTemp = new int[ AfterSplitStartString.length ][ AfterSplitEndString.length ]; // 個路徑的距離紀錄
        int vsTemp; //  起點
//        for (int i = 0; i < mVexs.length; i++) { // 9999 換成 INF = 無限大
//            for (int j = 0; j < mVexs.length; j++) {
//                mMatrix[i][j]= Loaddata[i][j];
//                if(Loaddata[i][j]==9999){
//                    mMatrix[i][j]=INF;
//                }
//            }
//        }

        for (int PointNum = 0; PointNum <  AfterSplitStartString.length ; PointNum++) {
            vsTemp =  Integer.parseInt(AfterSplitStartString[PointNum]) -1;  //  設定起點  點的起始為1
            if(Plusflag == 1){ // 二坪用
                vsTemp = vsTemp - 125;
            }

            // flag[i]=true表示"頂點vsTemp"到"頂點i"的最短路徑已成功獲取
            boolean[] flag = new boolean[mVexs.length];
            // 初始化
            for (int i = 0; i < mVexs.length; i++) {
                parent[i] = -1;//~~~
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
                        parent[j] = k;//~~~
                    }
                }
            }

            //  存入各點之間的距離大小
            for (int N = 0; N <  AfterSplitEndString.length ; N++) {
                //dist[N] 距離
                if(Plusflag == 1){ // 二坪用 -125  (125)
                    PointNumTemp[PointNum][N] = dist[Integer.parseInt(AfterSplitEndString[N])-125];
                }
                else{
                    PointNumTemp[PointNum][N] = dist[Integer.parseInt(AfterSplitEndString[N]) -1]; // 給終點 取出最短路徑
                }

            }

        }

        // 最小位置
        int MinxPoint=0 ,  MinyPoint=0;
        for (int qx = 0;qx < AfterSplitStartString.length ; qx++) {
            for (int qy = 0; qy < AfterSplitEndString.length ; qy++) {
                if (PointNumTemp[MinxPoint][MinyPoint] > PointNumTemp[qx][qy]) {
                    MinxPoint = qx;
                    MinyPoint = qy;
                }

            }
        }

        // 取出對應的點
//        StartPointMin = Integer.parseInt(AfterSplitStartString[MinxPoint]) -1; // 最近短距離的 起點
//        EndPointMin   = Integer.parseInt(AfterSplitEndString[MinyPoint])   -1;   // 最近短距離的 終點

        if(Plusflag == 1){ // 二坪用
            // 設定最短的起點+終點   從別的Activity取得原始的值
            StartPointMin = Integer.parseInt(AfterSplitStartString[MinxPoint]) -1 -125; // 最近短距離的 起點
            EndPointMin   = Integer.parseInt(AfterSplitEndString[MinyPoint])   -1 -125;   // 最近短距離的 終點

        }
        else{
            StartPointMin = Integer.parseInt(AfterSplitStartString[MinxPoint]) -1; // 最近短距離的 起點
            EndPointMin   = Integer.parseInt(AfterSplitEndString[MinyPoint])   -1;   // 最近短距離的 終點

        }
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
//        Intent intent = this.getActivity().getIntent();
//        StartString = intent.getStringExtra("startstring");
//        EndString = intent.getStringExtra("endstring");

        StartString =  StartStr;
        EndString   = EndStr;

        //   String   切割  -
        AfterSplitStartString = StartString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");
        AfterSplitEndString = EndString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");

        //    跨校區 測試 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (Integer.parseInt(AfterSplitStartString[0]) < 125 && Integer.parseInt(AfterSplitEndString[0]) < 125) { //只有八甲

            String contentStr = ("map_b.txt");//讀ASSETS~~~~~~~
            loadfiletoArray(contentStr);

            Plusflag = 0;
            dijkstraMin();   //  取得最短的組合
            vs = StartPointMin;
            dijkstraT();
        } else if (Integer.parseInt(AfterSplitStartString[0]) > 125 && Integer.parseInt(AfterSplitEndString[0]) > 125) {  //只有二坪

            String contentStr = ("map_a.txt");//讀ASSETS~~~~~~~!! 用二坪的檔案
            loadfiletoArray(contentStr);

            Plusflag = 1;
            dijkstraMin();   //  取得最短的組合
            vs = StartPointMin;
            dijkstraT();
        } else { // 跨校區
            if (Integer.parseInt(AfterSplitStartString[0]) < 125) { // 八甲開始

                Doflag = 1; //  跨區旗標 = 1 終點圖示改變    (目前未使用
                Plusflag = 0;

                String contentStr = ("map_b.txt");
                loadfiletoArray(contentStr);

                AfterSplitEndString = new String[0]; // 清空陣列
                AfterSplitEndString = new String[1];
                AfterSplitEndString[0] = String.valueOf(123); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitEndString = EndString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復

                Doflag = 2; //  跨區旗標 = 2 起點圖示改變
                Plusflag = 1;

//            DrawPointsList.add(new double[]{ 120.79289624,24.538329});
//            DrawPointsList.add(new double[]{120.795621, 24.539129});
//            DrawPointsList.add(new double[]{ 120.797069,24.541462});
//            DrawPointsList.add(new double[]{120.799379, 24.543649});


                ListStFlag = DrawPointsList.size(); //   八甲校區已經畫了幾點


                contentStr = ("map_a.txt");
                loadfiletoArray(contentStr);
                AfterSplitStartString = new String[0]; // 清空陣列
                AfterSplitStartString = new String[1];
                AfterSplitStartString[0] = String.valueOf(200); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitStartString = StartString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復


            } else {  // 二坪開始

                Doflag = 2; //  跨區旗標 = 2 起點圖示改變
                Plusflag = 1;

                String contentStr = ("map_a.txt");
                loadfiletoArray(contentStr);

                AfterSplitEndString = new String[0]; // 清空陣列
                AfterSplitEndString = new String[1];
                AfterSplitEndString[0] = String.valueOf(200); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitEndString = EndString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復


                Doflag = 1; //  跨區旗標 = 1 終點圖示改變
                Plusflag = 0;

                contentStr = ("map_b.txt");
                loadfiletoArray(contentStr);
                AfterSplitStartString = new String[0]; // 清空陣列
                AfterSplitStartString = new String[1];
                AfterSplitStartString[0] = String.valueOf(123); //校門口
                dijkstraMin();   //  取得最短的組合
                vs = StartPointMin;
                dijkstraT();
                AfterSplitStartString = StartString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split("-");//恢復
            }
        }
        //   跨校區 測試 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~畫線 & 標記~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        int pointnum = 0;// 迴圈計次 (路徑點數)
        for (double[] point : DrawPointsList) {  //建立標記圖示
            //marker = new ImageView(this);
            ImageView marker = new ImageView(this.getActivity());
            // save the coordinate for centering and callout positioning
            marker.setTag(point);
            // give it a standard marker icon - this indicator points down and is centered, so we'll use appropriate anchors

            if (pointnum == 0) {  // 標記起點
                marker.setImageResource(R.drawable.map_marker_green_f);

            } else if (pointnum == DrawPointsList.size() - 1) {  // 標記終點
                marker.setImageResource(R.drawable.map_marker_red_f);

            } else {
                marker.setImageResource(R.drawable.map_marker_123);
            }

            marker.setScaleY((float) 0.5); // 縮小
            marker.setScaleX((float) 0.5); // 縮小
            marker.setScrollY(-100);

//            Drawable d = getResources().getDrawable(R.drawable.map_marker_green_f);
//            int h = d.getIntrinsicHeight();
//            int w = d.getIntrinsicWidth();

            // marker.getHeight(); //TTTTTTTTTTTTT
            // Toast.makeText(this,  " 高 " + String.valueOf(h) , Toast.LENGTH_SHORT).show(); // 距離


            // on tap show further information about the area indicated
            // this could be done using a OnClickListener, which is a little more "snappy", since
            // MarkerTapListener uses GestureDetector.onSingleTapConfirmed, which has a delay of 300ms to
            // confirm it's not the start of a double-tap. But this would consume the touch event and
            // interrupt dragging
            tileView.getMarkerLayout().setMarkerTapListener(markerTapListener);
            // add it to the view tree

            tileView.addMarker(marker, point[0], point[1], null, null);

            if(pointnum==0){
                // tileView move to start
                tileView.moveToMarker(marker,false);
            }

            pointnum++; // (路徑點數)
        }

        if (ListStFlag != 0) { // 跨區時 分開價點測試
            tileView.drawPath(DrawPointsList.subList(0, pointnum), null);//  點數  畫線
        } else {
            tileView.drawPath(DrawPointsList.subList(0, pointnum), null);//  點數  畫線
        }

        mMatrix = new int[0][0];
        tileView.defineBounds(NORTH_WEST_LONGITUDE, NORTH_WEST_LATITUDE, SOUTH_EAST_LONGITUDE, SOUTH_EAST_LATITUDE); // to GPS座標
    }
    //endregion #########################################################  Dijkstra   ####################################################

    //region #########################################################  test   ####################################################
    // Fragment Communicating........................
    String StartStr , EndStr;
    protected void displayReceivedData(String message)
    {
//        GlobalVariable globalVariable = (GlobalVariable)getActivity().getApplicationContext();
//        StartStr = globalVariable.Start;
//
//        EndStr   = globalVariable.End;
//        txtData.setText("Data received: "+message);
//        Toast.makeText( MapFragment.this.getActivity(),  message , Toast.LENGTH_LONG).show();
//
//        tileView.defineBounds(0, 0, 25960, 12088);
//        Draw_Dijkstra();
//        tileView.defineBounds(NORTH_WEST_LONGITUDE, NORTH_WEST_LATITUDE, SOUTH_EAST_LONGITUDE, SOUTH_EAST_LATITUDE);//變成GPS座標
    }

    private MarkerLayout.MarkerTapListener markerTapListener = new MarkerLayout.MarkerTapListener() {

        @Override
        public void onMarkerTap(View view, int x, int y) {
            // get reference to the TileView
            // tileView = this.getActivity().getTileView();
            Toast.makeText( MapFragment.this.getActivity(),
                    "X = " + String.valueOf(x)
                            + " Y = " + String.valueOf(y),
                    Toast.LENGTH_LONG).show();

            if( !(view instanceof TextView) ){ // View != TextView
                // we saved the coordinate in the marker's tag
                double[] position = (double[]) view.getTag();
                // lets center the screen to that coordinate
                //tileView.slideToAndCenter(position[0], position[1]);//移動
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

    private void callout_dialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
        alertDialogBuilder.setTitle("Info");
        alertDialogBuilder.setMessage("");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing to do
            }
        });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private ArrayList<JSONStructure> data;
    private DataAdapter adapter;
    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<com.vine.projectdemo.JSONResponse> call = request.getJSON();
        call.enqueue(new Callback<com.vine.projectdemo.JSONResponse>() {
            @Override
            public void onResponse(Call<com.vine.projectdemo.JSONResponse> call, Response<com.vine.projectdemo.JSONResponse> response) { //沒連上時 不會進入迴圈
                com.vine.projectdemo.JSONResponse jsonResponse = response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getAndroid()));
            }
            @Override
            public void onFailure(Call<com.vine.projectdemo.JSONResponse> call, Throwable t) { //沒連上時 不會進入迴圈
                Log.d("Error",t.getMessage());
            }
        });
    }
    //endregion #########################################################  test   ##################################################

    @Override
    public void onResume() {
        super.onResume();

        tileView.resume();
        tileView_Run = 1;

        // 註冊感應監聽器
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tileView_Run = 0;
        tileView.destroy();
        tileView = null;
    }

    public void onPause() {
        super.onPause();
        tileView.pause();
        tileView_Run = 0;

        // 停止GPS更新
        //stopLocationUpdates();

        // 停止感應監聽器
        mSensorManager.unregisterListener(this);
    }
}