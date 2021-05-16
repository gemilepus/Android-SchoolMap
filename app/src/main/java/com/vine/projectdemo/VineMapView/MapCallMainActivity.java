package com.vine.projectdemo.VineMapView;

import android.app.ActionBar.LayoutParams;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vine.projectdemo.R;
import com.vine.projectdemo.VineJsonParsing.JSONMainActivity;

import java.io.IOException;
import java.io.InputStream;

public class MapCallMainActivity extends AppCompatActivity {
    Bitmap bmp;
    ImageView imgPhoto;
    //int[] imgId = { R.mipmap.img01, R.mipmap.img02, R.mipmap.img03,
           // R.mipmap.img04, R.mipmap.img05, R.mipmap.img06};
    //int count = imgId.length; // 共有多少張圖片
   //int btn_number=0;

    String[] Assfile = null;
    AssetManager assets = null;
    int AssfileSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_call_activity_main);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbarJSON);
        //toolbar.setLogo(R.drawable.ic_menu_back);
        toolbar.setTitle("室內地圖");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setOnClickListener(new View.OnClickListener() {  // 整個ToolBar的  onClick
            @Override
            public void onClick(View v) {
                //Toast.makeText( JSONMainActivity.this, "返回", Toast.LENGTH_SHORT).show();
                MapCallMainActivity.this.finish();
            }
        });
        setSupportActionBar(toolbar);  // 建立 toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearMain);
        imgPhoto = (ImageView) findViewById(R.id.imageView);

        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        try {
            //獲取assets的物件
            assets = getAssets();
            Assfile = assets.list("map");
            AssfileSize = Assfile.length; //檔案數

            Toast.makeText(this, String.valueOf(AssfileSize) , Toast.LENGTH_SHORT).show();//2

        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int j=1;j<=AssfileSize;j++)
        {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            // Create TextView
            //TextView product = new TextView(this);
            //product.setText(" Product"+j+"  ");
            //ll.addView(product);

            // Create Button
            final Button btn = new Button(this);

            // Give button an ID
            btn.setId(j+1);
            btn.setText("第"+j+"樓");
            btn.setBackgroundResource(R.drawable.new_icon);
            // set the layoutParams on the button
            btn.setLayoutParams(params);

            final int index = j;

            // Set click listener for button
            btn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.i("TAG", "index :" + index);
                    Toast.makeText(getApplicationContext(),
                            "Clicked Button Index :" + index,
                            Toast.LENGTH_LONG).show();
                    switch (index) {
                        case 1:
                             bmp = getImageFromAssetsFile("/assets/map/1F.jpg");
                            break;
                        case 2:
                            bmp = getImageFromAssetsFile("/assets/map/2F.jpg");
                            break;
                        case 3:
                            bmp = getImageFromAssetsFile("/assets/map/3F.jpg");
                            break;
                        case 4:
                            bmp = getImageFromAssetsFile("/assets/map/4F.jpg");
                            break;
                        case 5:
                            bmp = getImageFromAssetsFile("/assets/map/5F.jpg");
                            break;
                        case 6:
                            bmp = getImageFromAssetsFile("/assets/map/6F.jpg");
                            break;
                        case 7:
                            bmp = getImageFromAssetsFile("/assets/map/7F.jpg");
                            break;
                        //case .........
                    }
                    imgPhoto.setImageBitmap(bmp);


                }

            });
            //Add button to LinearLayout
            ll.addView(btn);
            //Add button to LinearLayout defined in XML
            lm.addView(ll);
        }
    }

    private Bitmap getImageFromAssetsFile(String fileName)
    {
        Bitmap image = null;
        try
        {
            InputStream is = getClass().getResourceAsStream(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return image;
    }

    //讀取Assets資料夾圖片，型態為Bitmap
    /*private Bitmap getBitmapFromAssets(String file)
    {
        try
        {
            AssetManager am = getAssets();
            InputStream is = am.open(file);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // Toolbar 上的  onClick
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {  // Toolbar 上的返回圖示 按下

            MapCallMainActivity.this.finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
