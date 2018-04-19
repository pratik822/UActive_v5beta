package com.uactiv.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.adapter.ImageSliderDetailAdapter;
import com.uactiv.adapter.Social_Feed_Adapter;
import com.uactiv.controller.ResponseListener;
import com.uactiv.interfaces.Customgetlike;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Image_Detail_activity extends AppCompatActivity implements AppConstants.urlConstants, AppConstants.SharedConstants,ResponseListener,Customgetlike {
    ViewPager pager;
    ImageSliderDetailAdapter adapter;
    int index;
    ArrayList<String> img;
    ImageView iv_like, iv_share;
    String description;
    String mylikeflag,feedId;
    static int  mylikeCount;
    CustomTextView tv_description, tv_like_count;
    String share="";
    int indexs=0; int likeindex=0,pagerindex=0;
    HashMap<String, String> param;
    public  static int hack=0;
    int ctr;
    public static int pagerpos=0;
    public static Customgetlike customlike;
    int myctr=0;

    public static int yo=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image__detail_activity);
        customlike=(Customgetlike)this;

        pager = (ViewPager) findViewById(R.id.pager);
        param = new HashMap<>();

        tv_description = (CustomTextView) findViewById(R.id.tv_description);
        iv_like = (ImageView) findViewById(R.id.iv_like);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        tv_like_count = (CustomTextView) findViewById(R.id.tv_like_count);
List<String>a=new ArrayList<>();
a.add("popo");
        a.add("topo");

        Observable<List<String>> observable=Observable.from(a).map(new Func1<String, List<String>>() {
            @Override
            public List<String> call(String s) {
                List<String>d=new ArrayList<>();
                d.add(s);

                return d;
            }
        });


        Observer<List<String>> obr=new Observer<List<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<String> strings) {
                System.out.println("yo boy "+strings);
            }
        };
     /*   Observer<String>ob=new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("yo boy");

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("yo boy "+s);
            }
        };*/


        Subscription subscription = observable
                .subscribeOn(Schedulers.io())       //observable will run on IO thread.
                .observeOn(AndroidSchedulers.mainThread())      //Observer will run on main thread.
                .subscribe(obr);

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
            tv_like_count.setText(mylikeCount+"");

            Log.d("testlike",mylikeflag);
            if (mylikeflag.equalsIgnoreCase("1")) {
                iv_like.setImageResource(R.drawable.ilike);
                iv_like.setTag("like");
            } else {
                iv_like.setImageResource(R.drawable.unlikess);
                iv_like.setTag("unlike");
            }
            tv_description.setText(description);

            iv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (String.valueOf(iv_like.getTag()) == "unlike") {
                                iv_like.setImageResource(R.drawable.ilike);

                                PickupListing.detailArraylist.get(likeindex).setLiked(String.valueOf((mylikeCount + 1)));
                                PickupListing.detailArraylist.get(likeindex).setLikecount("1");
                                mylikeCount=mylikeCount + 1;

                                param.put("iduser", SharedPref.getInstance().getStringVlue(Image_Detail_activity.this, userId));
                                param.put("idfeed",feedId);
                                param.put("like", "1");
                                // mynewsfeedmodellist.get(position).getLiked() = "1";

                                RequestHandler.getInstance().stringRequestVolley(Image_Detail_activity.this, AppConstants
                                                .getBaseUrl(SharedPref.getInstance()
                                                        .getBooleanValue(Image_Detail_activity.this, isStaging)) + like_feed,
                                        param, Image_Detail_activity.this, 0);


                            } else {
                                iv_like.setImageResource(R.drawable.unlikess);
                                PickupListing.detailArraylist.get(likeindex).setLiked(String.valueOf((mylikeCount - 1))+ "");
                                PickupListing.detailArraylist.get(likeindex).setLikecount("0");
                                mylikeCount=mylikeCount - 1;

                                param.put("iduser", SharedPref.getInstance().getStringVlue(Image_Detail_activity.this, userId));
                                param.put("idfeed", feedId);
                                param.put("like", "0");


                                RequestHandler.getInstance().stringRequestVolley(Image_Detail_activity.this, AppConstants
                                                .getBaseUrl(SharedPref.getInstance()
                                                        .getBooleanValue(Image_Detail_activity.this, isStaging)) + like_feed,
                                        param, Image_Detail_activity.this, 0);
                            }
                            tv_like_count.setText(mylikeCount+"");
                            Log.d("getlike", PickupListing.detailArraylist.get(likeindex).getLiked()+"");
                            animateHeart(iv_like);
                        }
                    }, 300);


                }
            });
            tv_like_count.setText(PickupListing.detailArraylist.get(likeindex).getLiked()+"");

            tv_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myctr++;
                    if(myctr%2==0){
                        ImageSliderDetailAdapter.alpha.setalpha(false);
                    }else{
                        ImageSliderDetailAdapter.alpha.setalpha(true);
                        yo=1;

                    }

                     adapter.notifyDataSetChanged();


                }
            });
            tv_description.post(new Runnable() {
                @Override
                public void run() {
                    int lineCnt = tv_description.getLineCount();
                    if(lineCnt>2){
                        if(!tv_description.getText().toString().trim().isEmpty()){
                            makeTextViewResizable(tv_description, 3, "Read More", true);
                        }
                    }

                    // Perform any actions you want based on the line count here.
                }
            });

        }

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareDialog dialog = new ShareDialog(Image_Detail_activity.this);

                AppConstants.showShareAlert(Image_Detail_activity.this, "Check out UACTIV, a cool app to find buddies and organize sports & fitness activities. ","https://www.uactiv.com/inviteDownload.html", "Share the App!", dialog);

               // AppConstants.showShareAlert2(Image_Detail_activity.this, "Check out this exciting Pick Up on UACTIV! ","Share the App!","https://www.uactiv.com/inviteDownload.html", dialog);
            }
        });
        ArrayList<String> filelist = (ArrayList<String>) getIntent().getSerializableExtra("image");
        String[] array = new String[filelist.size()];

        for (int i = 0; i < filelist.size(); i++) {
            array[i] = filelist.get(i);
        }
        Log.d("myarray",new Gson().toJson(array));
        adapter = new ImageSliderDetailAdapter(Image_Detail_activity.this, array);
        pager.setAdapter(adapter);


        if (this.getIntent().getIntExtra("index", -1) > -1) {
            index = this.getIntent().getIntExtra("index", 0);
            pager.setCurrentItem(index);
        }
    }


    /**
     *
     * @param view  like animation android
     */
    public void animateHeart(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(300);
        animation.setFillAfter(true);
        view.startAnimation(animation);

    }

    /**
     *
     * @param tv
     * @param maxLine
     * @param expandText
     * @param viewMore
     *
     * read more functinality
     */
    public  void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable1(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable1(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable1(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }

            }
        });
    }

    /**
     *
     * @param strSpanned
     * @param tv
     * @param maxLine
     * @param spanableText
     * @param viewMore
     *
     * read more function
     * @return
     */
    private  SpannableStringBuilder addClickablePartTextViewResizable1(final Spanned strSpanned, final TextView tv,
                                                                             final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);


        if (str.contains(spanableText)) {


            ssb.setSpan(new Social_Feed_Adapter.MySpannable(false) {
                @Override
                public void onClick(View widget) {


                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "Read Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "Read More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
            ForegroundColorSpan foregroundSpans = new ForegroundColorSpan(Color.parseColor("#1C3A6B"));
            ssb.setSpan(new RelativeSizeSpan(0.8f), str.indexOf(spanableText), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(foregroundSpans, str.indexOf(spanableText), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return ssb;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PickupListing.customRefresh.doRefresh(likeindex);
        Social_Feed_Adapter.completed=true;
        Social_Feed_Adapter.hacks=null;
        //PickupListing.adapter.notifyDataSetChanged();
        finish();

    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {
        JSONObject obj = new JSONObject(successResponse);
        String msg = obj.getString("details");
        Log.d("getresplike", msg);
        if (msg.equalsIgnoreCase("like")) {
            mylikeflag="1";
          //  PickupListing.detailArraylist.get(likeindex).setLikecount("1");
        } else {
            mylikeflag="0";
          //  PickupListing.detailArraylist.get(likeindex).setLikecount("0");
        }

        if (mylikeflag.equalsIgnoreCase("1")) {
            iv_like.setImageResource(R.drawable.ilike);
            iv_like.setTag("like");
        } else {
            iv_like.setImageResource(R.drawable.unlikess);
            iv_like.setTag("unlike");
        }

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    @Override
    public void getlike(int pos) {
        Log.d("interfacelike",pos+"");
    }
}
