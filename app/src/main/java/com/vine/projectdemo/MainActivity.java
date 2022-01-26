package com.vine.projectdemo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.tabs.TabLayoutMediator;
import com.vine.projectdemo.Adapter.ViewPagerAdapter;
import com.vine.projectdemo.DataView.JSONMainActivity;
import com.vine.projectdemo.AccountView.PHPMainActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , HomeFragment.SendMessage{

    // Fragment
    private ViewPager2 mViewPager;
    public HomeFragment homeFragment;
    public MapFragment mapFragment;

    private BottomNavigationView mBottomNavigationView;
    private FloatingActionButton fab_Go,fab_help;
    private Animation fab_open,fab_close, fab_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupNavigationView();
        setupBottomNavigation();
        setViewPager(); // add Tab
        setupFab();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    //  Fragment Communicating........................
    int MapStart = 0 , MapEnd = 0 ;
    @Override
    public void sendData(String message) {
        if(message.equals("START")){
            MapStart = 1;
        }
        if(message.equals("END")){
            MapEnd = 1;
        }
        if(  MapStart == 1 &&  MapEnd == 1 ){
            fab_Go.setVisibility(View.VISIBLE); 
            fab_Go.startAnimation(fab_open);
            fab_Go.setClickable(true);
        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    ViewPagerAdapter mViewPagerAdapter;
    private void setViewPager() {
        mViewPager = (ViewPager2) findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
        mViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setUserInputEnabled(false);
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab);
        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText(getResources().getString(R.string.plan_route));
                    } else if (position == 1) {
                        tab.setText(getResources().getString(R.string.campus_map));
                    }
                }
        ).attach();

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
                    fab_Go.startAnimation(fab_close);
                    fab_Go.setClickable(false);
                    fab_Go.setVisibility(View.GONE);
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
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager manager = getSupportFragmentManager();
                mapFragment = (MapFragment) manager.getFragments().get(1);
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
        Log.i("MainActivity", "CurrentItem : " + String.valueOf(mViewPager.getCurrentItem()));

        fab_open        = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close       = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_right       = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_right);

        fab_Go = (FloatingActionButton)findViewById(R.id.fab1);
        fab_Go.setY(200);
        fab_Go.setVisibility(View.GONE);
        fab_Go.setClickable(false);
        fab_Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                //mapFragment = (MapFragment) manager.findFragmentById(R.id.pager);
                mapFragment = (MapFragment) manager.getFragments().get(1);

                mapFragment.Draw_Dijkstra();
                mViewPager.setCurrentItem(1);

                animateFAB();
                hideSoftKeyboard();

            }
        });

        fab_help = (FloatingActionButton) findViewById(R.id.fab_help);
        fab_help.setOnClickListener(new View.OnClickListener() {
            //
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "輸入完現在及目的位置後，按下開始導航即會進入地圖畫面", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        });
    }

    public void animateFAB(){
        fab_Go.startAnimation(fab_right);
        fab_Go.setVisibility(View.GONE);
        fab_Go.setClickable(false);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "onPause()");

        if(mViewPager.getCurrentItem() != 0){
            fab_Go.clearAnimation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "onDestroy()");
    }
}