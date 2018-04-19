package com.uactiv.activity;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.adapter.ImageSliderDetailAdapter;
import com.uactiv.adapter.Social_Feed_Adapter;
import com.uactiv.interfaces.Customgetlike;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.widgets.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class Detail_Page extends AppCompatActivity {
    ViewPager pager;
    ImageSliderDetailAdapter adapter;
    int index;
    ArrayList<String> img;
    String description;
    String mylikeflag,feedId;
    static int  mylikeCount;
    TextView tv_description;
    String share="";
    int indexs=0; int likeindex=0,pagerindex=0;
    HashMap<String, String> param;
    public  static int hack=0;
    int ctr;
    public static int pagerpos=0;
    public static Customgetlike customlike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__page);
        pager = (ViewPager) findViewById(R.id.pager);
        param = new HashMap<>();
     //   tv_description = (TextView) findViewById(R.id.tv_description);


        if (this.getIntent().getStringExtra("description") != null) {
            description = this.getIntent().getStringExtra("description");
            mylikeCount=Integer.parseInt(this.getIntent().getStringExtra("mylikeCount"));
            if(Social_Feed_Adapter.hacks!=null){
                mylikeflag=Social_Feed_Adapter.hacks;
            }else{
                mylikeflag=this.getIntent().getStringExtra("mylikeflag");

            }

            feedId=this.getIntent().getStringExtra("feedId");
            indexs=this.getIntent().getIntExtra("index",0);
            likeindex=this.getIntent().getIntExtra("likeindex",0);
            //tv_like_count.setText(mylikeCount+"");


        //    tv_description.setText(description);

            ArrayList<String> filelist = (ArrayList<String>) getIntent().getSerializableExtra("image");
            String[] array = new String[filelist.size()];

            for (int i = 0; i < filelist.size(); i++) {
                array[i] = filelist.get(i);
            }
            Log.d("myarray",new Gson().toJson(array));
            adapter = new ImageSliderDetailAdapter(Detail_Page.this, array);
         //   pager.setAdapter(adapter);


            if (this.getIntent().getIntExtra("index", -1) > -1) {
                index = this.getIntent().getIntExtra("index", 0);
            //    pager.setCurrentItem(index);
            }



        }




    }
}
