package com.vine.projectdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.vine.projectdemo.VineJsonParsing.JSONMainActivity;
import com.vine.projectdemo.VinePHPMySQL.PHPMainActivity;
import com.vine.projectdemo.VineReValues.GlobalVariable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , HomeFragment.SendMessage{

    //  ~~~~~~~~~~~~~~~~~~~~~ Fragment ~~~~~~~~~~~~~~~~~~~~~~~~~~
    private ViewPager mViewPager;
    public HomeFragment homeFragment;
    public MapFragment mapFragment;
    //  ~~~~~~~~~~~~~~~~~~~~~ Fragment ~~~~~~~~~~~~~~~~~~~~~~~~~~

    private BottomNavigationView mBottomNavigationView;
    private FloatingActionButton fab_Go,fab_help;
    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close, fab_right;
    private Animation rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupNavigationView();
        setupBottomNavigation();
        setViewPager(); // add Tab
        setupFab();
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// EditText 取得焦點 但不會立刻彈出鍵盤
    }

    //  Fragment Communicating........................
    int MapStart = 0 , MapEnd = 0 ;
    @Override
    public void sendData(String message) {
        if( message == "START"){
            MapStart = 1;
        }
        if( message == "END"){
            MapEnd = 1;
        }
        if(  MapStart == 1 &&  MapEnd == 1 ){
            fab_Go.setVisibility(View.VISIBLE); 
            fab_Go.startAnimation(fab_open);
            fab_Go.setClickable(true);
        }
        //【FragmentPagerAdapter】
        String tag = "android:switcher:" + R.id.pager + ":" + 1;
        MapFragment f = (MapFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.displayReceivedData(message);
        //【FragmentStatePagerAdapter】
        //FragmentManager manager = getSupportFragmentManager();
        //mapFragment = (MapFragment) manager.findFragmentById(R.id.pager);
        //mapFragment.displayReceivedData(message);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    fab_help.setVisibility(View.VISIBLE);
                    mBottomNavigationView.setVisibility(View.GONE); 
                    if(MapStart == 1 &&  MapEnd == 1) {
                        fab_Go.setVisibility(View.VISIBLE); 
                        fab_Go.startAnimation(fab_open);
                        fab_Go.setClickable(true);
                    }
                }else{
                    fab_help.setVisibility(View.GONE); 
                    mBottomNavigationView.setVisibility(View.VISIBLE);

                    fab_Go.setVisibility(View.GONE); 
                    fab_Go.startAnimation(fab_close);
                    fab_Go.setClickable(false);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.ic_key);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupBottomNavigation() {
        mBottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager manager = getSupportFragmentManager();
                mapFragment = (MapFragment) manager.findFragmentById(R.id.pager);
                switch (item.getItemId()) {
                    case R.id.action_a:
                        mapFragment.MoveToA();
                        return true;
                    case R.id.action_b:
                        mapFragment.MoveToB();
                        return true;
                }
                return false;
            }

        });
        mBottomNavigationView.setVisibility(View.GONE); 
    }

    private void setupFab(){
        fab_open        = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close       = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward  = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_right);

//        mViewPager.getCurrentItem();
//        mViewPagerAdapter.getItem(1).getTag().toString();

        fab_Go = (FloatingActionButton)findViewById(R.id.fab1);
        fab_Go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                FragmentManager manager = getSupportFragmentManager();
//                homeFragment = (HomeFragment) manager.findFragmentById(R.id.pager);
//                homeFragment.SetCommend();

                FragmentManager manager = getSupportFragmentManager();
                mapFragment = (MapFragment) manager.findFragmentById(R.id.pager);
                mapFragment.Draw_Dijkstra();   // Activity 可以直接命令 Fragment (Fragment 的函數要為 public)
                mViewPager.setCurrentItem(1); // 跳到 index 1 的 Page

                //  SM.sendData("32".trim());  // PassingDataBetweenFragments

                fab_Go.startAnimation(fab_right);
                //animateFAB();
                fab_Go.setVisibility(View.GONE);
                fab_Go.setClickable(false);

                hideSoftKeyboard();
            }
        });
        fab_Go.setY(200);
        fab_Go.setVisibility(View.GONE); 
        fab_Go.setClickable(false);

        fab_help = (FloatingActionButton) findViewById(R.id.fab_help);
        fab_help.setOnClickListener(new View.OnClickListener() {
            //
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "輸入完現在及目的位置後，按下開始導航即會進入地圖畫面", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                // fab_Go.layout(fab_help.getLeft(),fab_help.getRight(),fab_help.getTop(),fab_help.getBottom() );
                // fab_Go.getLocationInWindow();
                // fab_Go.setX(300);
                // fab_Go.topMargin  = 200;
            }

        });
    }

    public void animateFAB(){
//        if(isFabOpen){
//         //   fab_Go.startAnimation(rotate_backward);
//           // fab_Go.startAnimation(fab_close);
//           // fab2.startAnimation(fab_close);
//            //fab_Go.setClickable(false);
//           // fab2.setClickable(false);
//            isFabOpen = false;
//            Log.d("Raj", "close");
//        } else {
//          //  fab_Go.startAnimation(rotate_forward);
//           fab_Go.startAnimation(fab_open);
//           // fab2.startAnimation(fab_open);
//            fab_Go.setClickable(true);
//           // fab2.setClickable(true);
//            isFabOpen = true;
//            Log.d("Raj","open");
//        }
        fab_Go.startAnimation(fab_open);
    }

    //region #########################################################  NavigationView   ####################################################

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) { // Map
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,JSONMainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) { // PHPMainActivity
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,PHPMainActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
    //endregion #########################################################  NavigationView   ####################################################

    //region #########################################################  test   ####################################################

    //RelativeLayout relativeLayout = findViewById(R.id.your_relative_layout);
    //mVideo = new VideoView(this);
    //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); // or wrap_content
    //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    //relativeLayout.addView(mVideo , layoutParams);
    
    private List<WeakReference<Fragment>> mFragList = new ArrayList<WeakReference<Fragment>>();

    @Override
    public void onAttachFragment (Fragment fragment) {
        mFragList.add(new WeakReference(fragment));
    }
    public List<Fragment> getActiveFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for(WeakReference<Fragment> ref : mFragList) {
            Fragment f = ref.get();
            if(f != null) {
                if(f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

//    public Fragment findFragement(String tClass) {
//
//        List<Fragment> fragments = getActiveFragments();
//        for (Fragment fragment : fragments) {
//            if (tClass.equalsIgnoreCase("Home")) {
//                if (fragment instanceof MapFragment) {
//                    return fragment;
//
//                }
//            } else if (tClass.equalsIgnoreCase("Contacts")) {
//                if (fragment instanceof ContactFragment) {
//                    return fragment;
//                }
//            }
//        }
//        return null;
//    }
    //endregion #########################################################  test   ##################################################
}