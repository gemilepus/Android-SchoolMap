package com.vine.projectdemo.AccountView;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.vine.projectdemo.Constants;
import com.vine.projectdemo.R;

public class PHPMainActivity extends AppCompatActivity {
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.php_activity_main);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbarJSON);
        //toolbar.setLogo(R.drawable.ic_menu_back);
        toolbar.setTitle("校園公告管理系統");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PHPMainActivity.this.finish();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = getPreferences(0);
        initFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // Toolbar
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            PHPMainActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFragment(){
        Fragment fragment;
        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            fragment = new ProfileFragment();
        }else {
            fragment = new LoginFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,fragment);
        ft.commit();
    }

}
