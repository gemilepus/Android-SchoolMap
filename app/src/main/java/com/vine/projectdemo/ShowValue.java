package com.vine.projectdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowValue extends Activity {

    private TextView text1,text2;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_showvalue);
        findView();
        Intent intent = this.getIntent();//取得傳遞過來的資料
        String name = intent.getStringExtra("name");
        String abc = intent.getStringExtra("abc");
        text1.setText(name);
        text2.setText(abc);
    }

    public void Touch(View v) {
        finish();
    }

    public void findView() {
        text1= (TextView) this.findViewById(R.id.textView7);
        text2= (TextView) this.findViewById(R.id.textView8);
        btn2 = (Button) this.findViewById(R.id.button);
    }
}
