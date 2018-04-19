package com.uactiv.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.widgets.CustomTextView;

public class In_App_Browser extends AppCompatActivity {
    private WebView webView;
    private String url,title;
    private ImageView imageView1;
    GifImageView progressWheel = null;
    CustomTextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in__app__browser);
        webView = (WebView) findViewById(R.id.webView);
        progressWheel = (GifImageView)findViewById(R.id.gifLoader);
        tv_title=(CustomTextView)findViewById(R.id.title);
        imageView1=(ImageView)findViewById(R.id.imageView1);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        if (this.getIntent().getStringExtra("url") != null) {
            url = this.getIntent().getStringExtra("url");
            try {
                title=this.getIntent().getStringExtra("title");
                tv_title.setText(title);
            }catch (NullPointerException ex){
                ex.printStackTrace();
            }


            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressWheel.setVisibility(View.GONE);
                    progressWheel.stopAnimation();
                }


            });
            webView.loadUrl(url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
