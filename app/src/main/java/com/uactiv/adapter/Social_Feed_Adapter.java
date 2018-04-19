package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.share.widget.ShareDialog;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uactiv.R;
import com.uactiv.activity.BusinessDetailsActivity;
import com.uactiv.activity.Exp;
import com.uactiv.activity.Image_Detail_activity;
import com.uactiv.activity.In_App_Browser;
import com.uactiv.activity.MyImageview;
import com.uactiv.activity.PickUpEventPage;
import com.uactiv.activity.PickupListing;
import com.uactiv.activity.Video_Player;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.DetailsList;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.GlideLoader;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CircleIndicator;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;
import de.hdodenhof.circleimageview.CircleImageView;
import ss.com.bannerslider.banners.Banner;

/**
 * Created by pratikb on 24-10-2017.
 */
public class Social_Feed_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener, ViewPager.OnPageChangeListener {
    byte[] bitmap;
    Context myctx;
    Activity myact;
    String newurl;
    int i = 1;
    int mylikepos;
    Viewclicklistner clik;
    List<DetailsList> mynewsfeedmodellist;
    // List<DetailsList> myattendlingmodellist;
    HashMap<String, String> param;

    final int PICKUP = 0;
    final int SLIDER = 1;
    final int VIDEO_VIEW = 2;
    final int ARTICLE_VIEW = 3;

    Spannable spanText;
    int mypostion;
    String video_image;
    public static boolean completed = true;
    View view;
    private String likeCount = "";
    public static String mylikeflag;
    RecyclerView.ViewHolder holders;
    Boolean iscroll = false;
    CustomArticleHolder customArticleHolder;
    static Boolean sss = false;

    int myAttendies = 0;
    String myarea;
    char myvovels;
    String url = "";
    Spannable spansTexts;
    List<Banner> banners;
    ArrayList<Page> pageViews;

    private int dotsCount;
    private ImageView[] dots;

    public static String hacks;
    ClickableSpan clickableSpan;
    String spanTextnew = " Read More";
    private String articleDiscritpin = "";

    public Social_Feed_Adapter(Context ctx, Activity act, List<DetailsList> newsFeedList) {
        myctx = ctx;
        param = new HashMap<>();
        this.mynewsfeedmodellist = newsFeedList;
        this.myact = act;


    }

    //recycleview view holder items
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == PICKUP) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pickupcustom_layout, null);
            holders = new CustomPickupViewholder(view);
        } else if (viewType == ARTICLE_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_article_layout, null);
            holders = new CustomArticleHolder(view);
        } else if (viewType == SLIDER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslidercustom_layout, null);
            holders = new CustomSliderHolder(view);

        } else if (viewType == VIDEO_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videocustom_layout, null);
            holders = new CustomVideoHolder(view);
        }
        return holders;

    }


    private void updateLike(RecyclerView.ViewHolder holder, int position) {
        final CustomPickupViewholder holders = (CustomPickupViewholder) holder;

    }

    // like button animation
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

    //bindview holder without having payload
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case PICKUP:
                //pickup feed  bind view
                final CustomPickupViewholder holders = (CustomPickupViewholder) holder;

                if (mynewsfeedmodellist.get(position).getFeedtype().equalsIgnoreCase("pick_up")) {
                    holders.tv_title.setText(mynewsfeedmodellist.get(position).getFirstname());
                    Glide.with(myctx).load(mynewsfeedmodellist.get(position).getImage().trim()).into(holders.iv_pic);
                    holders.gifView.setImageResource(R.drawable.uactv);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.uactv);
                    requestOptions.error(R.drawable.uactv);

                    Glide.with(myctx).setDefaultRequestOptions(requestOptions).load(mynewsfeedmodellist.get(position).getActivityimage().trim()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                            holders.gifView.setImageResource(R.drawable.uactv);

                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    }).into(holders.gifView);

                    ((CustomPickupViewholder) holder).tv_date.setText(mynewsfeedmodellist.get(position).getStart_date());
                    ((CustomPickupViewholder) holder).tv_pickup_time.setText(Utility.timeFormatChanage(mynewsfeedmodellist.get(position).getStart_time()));
                    holders.tv_time.setText(mynewsfeedmodellist.get(position).getCreated());

                    try {
                        if (mynewsfeedmodellist.get(position).getAttendiees().size() > 0) {
                            if (mynewsfeedmodellist.get(position).getAttendiees().size() > 3) {
                                holders.textView11.setText("+ " + (mynewsfeedmodellist.get(position).getAttendiees().size() - 3) + "");
                            }
                        } else {
                            holders.textView11.setText(" ");
                        }
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        holders.textView11.setText(" ");
                    }


                    if (mynewsfeedmodellist.get(position).getArea().isEmpty() || mynewsfeedmodellist.get(position).getArea().contains("(null)")) {
                        holders.tv_paid.setText("");
                    } else {
                        if (mynewsfeedmodellist.get(position).getArea().length() > 13) {
                            myarea = mynewsfeedmodellist.get(position).getArea().substring(0, Math.min(mynewsfeedmodellist.get(position).getArea().length(), 12));
                            holders.tv_paid.setText(myarea + "...");
                        } else {
                            holders.tv_paid.setText(mynewsfeedmodellist.get(position).getArea());
                        }

                    }

                    myAttendies = Integer.parseInt(mynewsfeedmodellist.get(position).getAttending());
                    holders.tv_like_count.setText(mynewsfeedmodellist.get(position).getLiked());
                    if (mynewsfeedmodellist.get(position).getAttendiees() != null && mynewsfeedmodellist.get(position).getAttendiees().size() > 0) {


                    }


                    try {

                        if (myAttendies == 3) {
                            ((CustomPickupViewholder) holder).textView11.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).attendi_three_layout.setVisibility(View.VISIBLE);
                            ((CustomPickupViewholder) holder).attendi_more_layout.setVisibility(View.GONE);

                            if (mynewsfeedmodellist.get(position).getAttendiees().size() > 0) {
                                try {

                                    for (int i = 0; i < mynewsfeedmodellist.get(position).getAttendiees().size(); i++) {
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(0).getImage(), holders.iv_pic_one1);
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(1).getImage(), holders.iv_pic_two2);
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(2).getImage(), holders.iv_pic_three3);

                                    }
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } else if (myAttendies > 3) {
                            ((CustomPickupViewholder) holder).textView11.setVisibility(View.VISIBLE);
                            ((CustomPickupViewholder) holder).attendi_more_layout.setVisibility(View.VISIBLE);
                            ((CustomPickupViewholder) holder).attendi_three_layout.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).iv_count.setVisibility(View.VISIBLE);
                            holders.iv_pic_one.setVisibility(View.VISIBLE);
                            holders.iv_pic_two.setVisibility(View.VISIBLE);
                            holders.iv_pic_three.setVisibility(View.VISIBLE);

                            if (mynewsfeedmodellist.get(position).getAttendiees().size() > 0) {
                                try {
                                    for (int i = 0; i < mynewsfeedmodellist.get(position).getAttendiees().size(); i++) {
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(0).getImage(), holders.iv_pic_one);
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(1).getImage(), holders.iv_pic_two);
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(2).getImage(), holders.iv_pic_three);
                                    }
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } else if (myAttendies == 1) {
                            ((CustomPickupViewholder) holder).textView11.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).attendi_more_layout.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).attendi_three_layout.setVisibility(View.VISIBLE);
                            ((CustomPickupViewholder) holder).iv_pic_one1.setVisibility(View.INVISIBLE);
                            ((CustomPickupViewholder) holder).iv_pic_three3.setVisibility(View.INVISIBLE);
                            ((CustomPickupViewholder) holder).iv_pic_two2.setVisibility(View.VISIBLE);
                            holders.iv_pic_two2.setVisibility(View.VISIBLE);
                            if (mynewsfeedmodellist.get(position).getAttendiees().size() > 0) {
                                try {
                                    for (int i = 0; i < mynewsfeedmodellist.get(position).getAttendiees().size(); i++) {
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(0).getImage(), ((CustomPickupViewholder) holder).iv_pic_two2);
                                    }
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } else if (myAttendies == 2) {
                            ((CustomPickupViewholder) holder).textView11.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).attendi_more_layout.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).attendi_three_layout.setVisibility(View.VISIBLE);
                            ((CustomPickupViewholder) holder).iv_pic_two2.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).iv_pic_one1.setVisibility(View.VISIBLE);
                            ((CustomPickupViewholder) holder).iv_pic_three3.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            textViewLayoutParams.setMargins(0, 0, 35, 0);
                            holders.iv_pic_one1.setVisibility(View.VISIBLE);
                            holders.iv_pic_three3.setVisibility(View.VISIBLE);

                            ((CustomPickupViewholder) holder).attendi_three_layout.setLayoutParams(textViewLayoutParams);

                            if (mynewsfeedmodellist.get(position).getAttendiees().size() > 0) {
                                try {
                                    for (int i = 0; i < mynewsfeedmodellist.get(position).getAttendiees().size(); i++) {
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(0).getImage(), holders.iv_pic_one1);
                                        Utility.setcircularImageUniversalLoader(myctx, mynewsfeedmodellist.get(position).getAttendiees().get(1).getImage(), holders.iv_pic_three3);
                                    }
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        } else if (myAttendies == 0) {
                            ((CustomPickupViewholder) holder).textView11.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).attendi_more_layout.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).attendi_three_layout.setVisibility(View.GONE);
                            ((CustomPickupViewholder) holder).iv_pic_two2.setVisibility(View.GONE);

                        }

                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                    if (mynewsfeedmodellist.get(position).getSpot().equalsIgnoreCase("unlimited")) {
                        holders.tv_spots.setText(mynewsfeedmodellist.get(position).getSpot());
                    } else {
                        holders.tv_spots.setText(mynewsfeedmodellist.get(position).getSpot());
                    }

                    if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                        holders.iv_like.setImageResource(R.drawable.redlike);
                        holders.iv_like.setTag("like");
                    } else {
                        holders.iv_like.setImageResource(R.drawable.ic_like);
                        holders.iv_like.setTag("unlike");
                    }

                    if (mynewsfeedmodellist.get(position).getIs_admin().equalsIgnoreCase("1")) {
                        if (checkvovels(mynewsfeedmodellist.get(position).getActivity())) {
                            Spannable spanTexts = new SpannableString("Shared an " + mynewsfeedmodellist.get(position).getActivity() + " Pick Up");
                            spanTexts.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holders.tv_pickupname.setText(spanTexts);
                        } else {
                            Spannable spanTexts = new SpannableString("Shared a " + mynewsfeedmodellist.get(position).getActivity() + " Pick Up");
                            spanTexts.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holders.tv_pickupname.setText(spanTexts);
                        }
                    } else {
                        if (checkvovels(mynewsfeedmodellist.get(position).getActivity())) {
                            Spannable spanTexts = new SpannableString("Created an " + mynewsfeedmodellist.get(position).getActivity() + " Pick Up");
                            spanTexts.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holders.tv_pickupname.setText(spanTexts);
                        } else {
                            Spannable spanTexts = new SpannableString("Created a " + mynewsfeedmodellist.get(position).getActivity() + " Pick Up");
                            spanTexts.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holders.tv_pickupname.setText(spanTexts);
                        }

                    }


                    holders.btn_knowmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(myctx, PickUpEventPage.class);
                            intent.putExtra("SheduleId", mynewsfeedmodellist.get(position).getIdschedule());
                            intent.putExtra("frompickupPopup", "frompickupPopup");
                            myctx.startActivity(intent);
                        }
                    });

                    holders.rel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utility.setScreenTracking(myctx, "Social Feed");
                            Utility.setEventTracking(myctx, "Social Feed-Pickup", mynewsfeedmodellist.get(position).getTitle()+" "+mynewsfeedmodellist.get(position).getActivity() + "" + "");
                            Intent intent = new Intent(myctx, PickUpEventPage.class);
                            intent.putExtra("SheduleId", mynewsfeedmodellist.get(position).getIdschedule());
                            intent.putExtra("frompickupPopup", "frompickupPopup");
                            myctx.startActivity(intent);
                        }
                    });
                    holders.pickuplayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utility.setScreenTracking(myctx, "Social Feed");
                            Utility.setEventTracking(myctx, "Social Feed-Pickup", mynewsfeedmodellist.get(position).getTitle()+" "+mynewsfeedmodellist.get(position).getActivity() + "");
                            Intent intent = new Intent(myctx, PickUpEventPage.class);
                            intent.putExtra("SheduleId", mynewsfeedmodellist.get(position).getIdschedule());
                            intent.putExtra("frompickupPopup", "frompickupPopup");
                            myctx.startActivity(intent);
                        }
                    });


                    holders.iv_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utility.setEventTracking(myctx, "Social Feed-Pickup shared", mynewsfeedmodellist.get(position).getActivity());
                            ShareDialog dialog = new ShareDialog(myact);
                            AppConstants.showShareAlert(myctx, "Check out this exciting Pick Up on UACTIV!", mynewsfeedmodellist.get(position).getShare(), "Share Pick Up", dialog);
                        }
                    });

                }
                break;
            case SLIDER:
                //image sliding feed  bind view
                final CustomSliderHolder customesliderholder = (CustomSliderHolder) holder;
                String descri = mynewsfeedmodellist.get(customesliderholder.getAdapterPosition()).getDescription().trim();
                customesliderholder.tv_title.setText(mynewsfeedmodellist.get(position).getFirstname());
                Glide.with(myctx).load(mynewsfeedmodellist.get(position).getImage().trim()).into(customesliderholder.profilepic);
                likeCount = mynewsfeedmodellist.get(position).getLiked();
                Social_Feed_Adapter.mylikeflag = mynewsfeedmodellist.get(position).getLikecount();
                mylikepos = position;
                Log.d("curruntpos", position + "");
                Log.d("curruntpos2", position + "");
                customesliderholder.tv_time.setText(mynewsfeedmodellist.get(position).getCreated());
                customesliderholder.tv_pickupname.setText(spanText);
                if (descri.isEmpty()) {
                    customesliderholder.tv_pickup_description.setVisibility(View.GONE);
                } else {
                    customesliderholder.tv_pickup_description.setVisibility(View.VISIBLE);
                }

                customesliderholder.tv_pickup_description.setText(descri);
                Log.d("mysize", mynewsfeedmodellist.get(position).getDetails().size() + "");


                //array size check
                if (mynewsfeedmodellist.get(position).getDetails().size() > 0) {
                    ImageSliderAdapter adapter = new ImageSliderAdapter(myctx, mynewsfeedmodellist.get(position).getDetails(), mynewsfeedmodellist.get(position).getDescription(), likeCount, Social_Feed_Adapter.mylikeflag, mynewsfeedmodellist.get(position).getShare(), mynewsfeedmodellist.get(position).getIdfeed(), position);
                    customesliderholder.pager.setAdapter(adapter);
                    try {
                        customesliderholder.indicator_custom.setViewPager(customesliderholder.pager);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                }


                if (mynewsfeedmodellist.get(position).getDetails().size() == 0) {
                  /*  pageViews = new ArrayList<>();
                    pageViews.add(new Page("https" + "ff"));
                    customesliderholder.mAnimCircleIndicator.notifyDataChange(pageViews);*/
                }

                // customesliderholder.pager_indicator.removeAllViews();
            /*    if(mynewsfeedmodellist.get(position).getDetails().size()>0){
                    dots = new ImageView[mynewsfeedmodellist.get(position).getDetails().size()];
                *//*    pageViews = new ArrayList<>();
                    pageViews = new ArrayList<>();*//*
                    Log.d("currpos",position+"");
                    for (int x = 0; x <mynewsfeedmodellist.get(position).getDetails().size(); x++) {
                        Log.d("myimage", mynewsfeedmodellist.get(position).getDetails().size()+" "+mynewsfeedmodellist.get(position).getFirstname()+" "+"https" + mynewsfeedmodellist.get(position).getDetails().get(x).getImage().substring(4));
                        pageViews.add(new Page("https" + mynewsfeedmodellist.get(position).getDetails().get(x).getImage().substring(4)));

                        dots[x] = new ImageView(myctx);
                        dots[x].setImageDrawable(myctx.getResources().getDrawable(R.drawable.normal));


                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );


                        params.setMargins(4, 0, 4, 0);
                        customesliderholder.pager_indicator.addView(dots[x], params);

                    }


                    try {
                        dots[0].setImageDrawable(myctx.getResources().getDrawable(R.drawable.selected_img));
                    }catch (ArrayIndexOutOfBoundsException ex){
                        ex.printStackTrace();
                    }
                }*/

                //  customesliderholder.mAnimCircleIndicator.notifyDataChange(pageViews);
                customesliderholder.tv_like_count.setText(mynewsfeedmodellist.get(position).getLiked());
                likeCount = mynewsfeedmodellist.get(position).getLiked();
                mylikeflag = mynewsfeedmodellist.get(position).getLikecount();


                if (mynewsfeedmodellist.get(position).getDetails().size() == 0) {
                    ImageSliderAdapter adapter = new ImageSliderAdapter(myctx, mynewsfeedmodellist.get(position).getDetails(), mynewsfeedmodellist.get(position).getDescription(), likeCount, mylikeflag, mynewsfeedmodellist.get(position).getShare(), mynewsfeedmodellist.get(position).getIdfeed(), position);
                    customesliderholder.pager.setAdapter(adapter);
                }

                if (mynewsfeedmodellist.get(position).getDetails().size() > 1) {
                    try {
                        customesliderholder.indicator_custom.setVisibility(View.VISIBLE);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                    spanText = new SpannableString("Added photos");
                    spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    customesliderholder.tv_pickupname.setText(spanText);
                    //customesliderholder.pager_indicator.setVisibility(View.VISIBLE);

                } else {
                    spanText = new SpannableString("Added a photo");
                    spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    customesliderholder.tv_pickupname.setText(spanText);
                    try {
                        customesliderholder.indicator_custom.setVisibility(View.INVISIBLE);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                    //  customesliderholder.pager_indicator.setVisibility(View.INVISIBLE);
                }

                if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                    customesliderholder.iv_like.setImageResource(R.drawable.redlike);
                    customesliderholder.iv_like.setTag("like");
                } else {
                    customesliderholder.iv_like.setImageResource(R.drawable.ic_like);
                    customesliderholder.iv_like.setTag("unlike");
                }
                System.out.println("getcharlength" + customesliderholder.tv_pickup_description);
                customesliderholder.tv_pickup_description.setText(descri + " ");
                customesliderholder.tv_pickup_description.makeExpandable(3);

             /*   if (customesliderholder.tv_pickup_description.getText().length() > 200) {
                //    makeTextViewResizables(customesliderholder.tv_pickup_description, 3, "Read More", true);
                    customesliderholder.tv_pickup_description.setText(descri);
                }*/
                // makeTextViewResizables(customesliderholder.tv_pickup_description, 3, "Read More", true);

          /*      new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("myline", customesliderholder.tv_pickup_description.getLineCount() + "");
                        if (customesliderholder.tv_pickup_description.getLineCount() > 3) {
                            makeTextViewResizables(customesliderholder.tv_pickup_description, 3, "Read More", true);
                        }
                    }
                });*/

                customesliderholder.iv_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShareDialog dialog = new ShareDialog(myact);
                        AppConstants.showShareAlert(myctx, "Check out UACTIV, a cool app to find buddies and organize sports & fitness activities. ", "https://www.uactiv.com/inviteDownload.html", "Share the App!", dialog);
                    }
                });


                break;
            case ARTICLE_VIEW:

                //article feed  bind view
                final CustomArticleHolder customArticleHolder = (CustomArticleHolder) holder;
                customArticleHolder.tv_article_header.setText(mynewsfeedmodellist.get(position).getTitle().trim());

                customArticleHolder.tv_title.setText(mynewsfeedmodellist.get(position).getCredit());
                customArticleHolder.tv_time.setText(mynewsfeedmodellist.get(position).getCreated());
                //customArticleHolder.tv_article_description.setText(mynewsfeedmodellist.get(position).getDescription().trim());
                articleDiscritpin = mynewsfeedmodellist.get(position).getDescription().trim().replaceAll("\n*>", "") + " ";

                if (articleDiscritpin.isEmpty()) {
                    customArticleHolder.tv_article_description.setVisibility(View.GONE);
                } else {
                    customArticleHolder.tv_article_description.setVisibility(View.VISIBLE);
                }

               if (articleDiscritpin.length() > 200) {
                    articleDiscritpin = articleDiscritpin.substring(0, 200) + "...";
                    articleDiscritpin += spanTextnew;
                    SpannableString ss = new SpannableString(articleDiscritpin);
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {

                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(Color.parseColor("#3268BF"));
                            ds.setTextSize(28f);
                            ds.setUnderlineText(false);
                        }
                    };
                    if (articleDiscritpin.contains(spanTextnew)) {
                        ss.setSpan(clickableSpan, articleDiscritpin.indexOf(spanTextnew), articleDiscritpin.indexOf(spanTextnew) + spanTextnew.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }

                    customArticleHolder.tv_article_description.setText(ss);
                    customArticleHolder.tv_article_description.setMovementMethod(LinkMovementMethod.getInstance());
                    customArticleHolder.tv_article_description.setHighlightColor(Color.BLUE);


                } else {
                    articleDiscritpin = mynewsfeedmodellist.get(position).getDescription().trim();
                    customArticleHolder.tv_article_description.setText(articleDiscritpin);

                }

            /*    new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("myline",  customArticleHolder.tv_article_description.getLineCount() + "");
                        if(customArticleHolder.tv_article_description.getText().length()>200){
                            makeTextViewResizables(customArticleHolder.tv_article_description, 3, "Read More", true);
                        }


                    }
                });*/

                spansTexts = new SpannableString("Shared an article");
                spansTexts.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                //  Glide.with(myctx).load(mynewsfeedmodellist.get(position).getImage().trim()).into(customArticleHolder.profilepic);
                customArticleHolder.video_view.setImageResource(R.drawable.static_article);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.static_article);
                requestOptions.error(R.drawable.static_article);

                //process for image loading using glide
                Glide.with(myctx).setDefaultRequestOptions(requestOptions).load(mynewsfeedmodellist.get(position).getActivityimage().trim()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        customArticleHolder.video_view.setImageResource(R.drawable.static_article);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(customArticleHolder.video_view);

                customArticleHolder.video_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utility.setScreenTracking(myctx, "Social Feed");
                        Utility.setEventTracking(myctx, "Social Feed-Article", mynewsfeedmodellist.get(position).getTitle());
                        Intent url = new Intent(myctx, In_App_Browser.class);
                        url.putExtra("url", mynewsfeedmodellist.get(position).getLink());
                        url.putExtra("title", mynewsfeedmodellist.get(position).getCredit());
                        myctx.startActivity(url);
                    }
                });

                customArticleHolder.tv_article_description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utility.setScreenTracking(myctx, "Social Feed");
                        Utility.setEventTracking(myctx, "Social Feed-Article", mynewsfeedmodellist.get(position).getTitle());
                        Intent url = new Intent(myctx, In_App_Browser.class);
                        url.putExtra("url", mynewsfeedmodellist.get(position).getLink());
                        url.putExtra("title", mynewsfeedmodellist.get(position).getCredit());
                        myctx.startActivity(url);
                    }
                });
                customArticleHolder.tv_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utility.setScreenTracking(myctx, "Social Feed");
                        Utility.setEventTracking(myctx, "Social Feed-Article", mynewsfeedmodellist.get(position).getTitle());
                        Intent url = new Intent(myctx, In_App_Browser.class);
                        url.putExtra("url", mynewsfeedmodellist.get(position).getLink());
                        url.putExtra("title", mynewsfeedmodellist.get(position).getCredit());
                        myctx.startActivity(url);
                    }
                });

                customArticleHolder.tv_article_header.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utility.setScreenTracking(myctx, "Social Feed");
                        Utility.setEventTracking(myctx, "Social Feed-Article", mynewsfeedmodellist.get(position).getTitle());
                        Intent url = new Intent(myctx, In_App_Browser.class);
                        url.putExtra("url", mynewsfeedmodellist.get(position).getLink());
                        url.putExtra("title", mynewsfeedmodellist.get(position).getCredit());
                        myctx.startActivity(url);
                    }
                });

                customArticleHolder.tv_articlename.setText(spansTexts);
                customArticleHolder.tv_like_count.setText(mynewsfeedmodellist.get(position).getLiked());

                if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                    customArticleHolder.iv_like.setImageResource(R.drawable.redlike);
                    customArticleHolder.iv_like.setTag("like");
                } else {
                    customArticleHolder.iv_like.setImageResource(R.drawable.ic_like);
                    customArticleHolder.iv_like.setTag("unlike");
                }


            /*    if (!mynewsfeedmodellist.get(position).getDescription().trim().isEmpty()) {
                    customArticleHolder.tv_article_description.setVisibility(View.VISIBLE);
                   *//* customArticleHolder.tv_article_description.setColorClickableText(Color.parseColor("#3268BF"));
                    customArticleHolder.tv_article_description.setTrimExpandedText("Read Less");
                    customArticleHolder.tv_article_description.setTrimCollapsedText("Read More");*//*
                    customArticleHolder.tv_article_description.setText(mynewsfeedmodellist.get(position).getDescription());
                    Typeface faces = Typeface.createFromAsset(myctx.getAssets(), "fonts/Brandon_reg.otf");
                    customArticleHolder.tv_article_description.setTypeface(faces);
                    makeTextViewResizables(customArticleHolder.tv_article_description, 3, "Read More", true);

                } else {
                    customArticleHolder.tv_article_description.setVisibility(View.GONE);
                }*/
                break;
            case VIDEO_VIEW:
                final CustomVideoHolder videoHolder = (CustomVideoHolder) holder;
                videoHolder.tv_time.setText(mynewsfeedmodellist.get(position).getCreated());
                videoHolder.tv_title.setText(mynewsfeedmodellist.get(position).getFirstname());

                videoHolder.profilepic.setImageResource(R.drawable.static_video);

                //process for video loading using glide
                Glide.with(myctx).load(mynewsfeedmodellist.get(position).getImage().trim()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                        videoHolder.profilepic.setImageResource(R.drawable.static_video);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(videoHolder.profilepic);


                Spannable spansText = new SpannableString("Added a video");
                spansText.setSpan(new ForegroundColorSpan(Color.parseColor("#888E9E")), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                videoHolder.tv_pickupname.setText(spansText);
                //  url = "http://d3534htadowgzl.cloudfront.net/videos/" + mynewsfeedmodellist.get(position).getDetails().get(0).getLink();

                video_image = "http://d3534htadowgzl.cloudfront.net/thumbs/" + mynewsfeedmodellist.get(position).getDetails().get(0).getImage();
                Log.d("video_image", video_image);
                Picasso.with(myctx).load(video_image).placeholder(R.drawable.static_video).error(R.drawable.static_video).into(videoHolder.video_view);
                videoHolder.tv_video_description.setText(mynewsfeedmodellist.get(position).getDescription());
                videoHolder.tv_like_count.setText(mynewsfeedmodellist.get(position).getLiked());
                videoHolder.tv_video_description.makeExpandable(3);


                if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                    videoHolder.iv_like.setImageResource(R.drawable.redlike);
                    videoHolder.iv_like.setTag("like");

                } else {
                    videoHolder.iv_like.setImageResource(R.drawable.ic_like);
                    videoHolder.iv_like.setTag("unlike");
                }

             /*   if (videoHolder.tv_video_description.getText().length() > 200) {
                    makeTextViewResizables(videoHolder.tv_video_description, 3, "Read More", true);
                }*/

            /*    new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("myline", videoHolder.tv_video_description.getLineCount() + "");
                        if (videoHolder.tv_video_description.getLineCount() > 3) {
                            makeTextViewResizables(videoHolder.tv_video_description, 3, "Read More", true);
                        }
                    }
                });*/


                break;
        }


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payload) {
        if (!payload.isEmpty()) {
            if (payload.contains("lola")) {
                Log.d("insideplayload", "true");
                switch (holder.getItemViewType()) {

                    case PICKUP:
                        final CustomPickupViewholder holders = (CustomPickupViewholder) holder;
                        if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                            holders.iv_like.setImageResource(R.drawable.redlike);
                            holders.iv_like.setTag("like");
                        } else {
                            holders.iv_like.setImageResource(R.drawable.ic_like);
                            holders.iv_like.setTag("unlike");
                        }
                        break;

                    case SLIDER:
                        final CustomSliderHolder customesliderholder = (CustomSliderHolder) holder;
                        customesliderholder.tv_like_count.setText(mynewsfeedmodellist.get(position).getLiked());
                        likeCount = mynewsfeedmodellist.get(position).getLiked();
                        mylikeflag = mynewsfeedmodellist.get(position).getLikecount();

                        if (mynewsfeedmodellist.get(position).getDetails().size() > 0) {
                            // ImageSliderAdapter adapter = new ImageSliderAdapter(myctx, mynewsfeedmodellist.get(position).getDetails(), mynewsfeedmodellist.get(position).getDescription(), likeCount, mylikeflag, mynewsfeedmodellist.get(position).getShare(), mynewsfeedmodellist.get(position).getIdfeed(), position);
                            // customesliderholder.pager.setAdapter(adapter);
                            //  adapter.notifyDataSetChanged();


                            // customesliderholder.indicator_custom.setViewPager(customesliderholder.pager);
                        }

                        if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                            customesliderholder.iv_like.setImageResource(R.drawable.redlike);
                            customesliderholder.iv_like.setTag("like");
                        } else {
                            customesliderholder.iv_like.setImageResource(R.drawable.ic_like);
                            customesliderholder.iv_like.setTag("unlike");
                        }


                        break;

                    case ARTICLE_VIEW:
                        final CustomArticleHolder customArticleHolder = (CustomArticleHolder) holder;
                        customArticleHolder.tv_like_count.setText(mynewsfeedmodellist.get(position).getLiked());
                        if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                            customArticleHolder.iv_like.setImageResource(R.drawable.redlike);
                            customArticleHolder.iv_like.setTag("like");
                        } else {
                            customArticleHolder.iv_like.setImageResource(R.drawable.ic_like);
                            customArticleHolder.iv_like.setTag("unlike");
                        }
                        break;

                    case VIDEO_VIEW:
                        CustomVideoHolder videoHolder = (CustomVideoHolder) holder;
                        videoHolder.tv_like_count.setText(mynewsfeedmodellist.get(position).getLiked());

                        if (mynewsfeedmodellist.get(position).getLikecount().equalsIgnoreCase("1")) {
                            videoHolder.iv_like.setImageResource(R.drawable.redlike);
                            videoHolder.iv_like.setTag("like");

                        } else {
                            videoHolder.iv_like.setImageResource(R.drawable.ic_like);
                            videoHolder.iv_like.setTag("unlike");
                        }
                        break;


                }
            }

        } else {
            super.onBindViewHolder(holder, position, payload);

        }


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

   /* @Override
    public void onPageClick(int pos, Page page) {
        Intent sliderpage = new Intent(myctx, Image_Detail_activity.class);
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < mynewsfeedmodellist.get(mylikepos).getDetails().size(); i++) {
            images.add(mynewsfeedmodellist.get(mylikepos).getDetails().get(i).getImage());
        }
        Log.d("currpos",mylikepos+"");
        sliderpage.putExtra("index", pos);
        sliderpage.putExtra("likeindex", mylikepos);
        sliderpage.putExtra("description",mynewsfeedmodellist.get(mylikepos).getDescription());
        sliderpage.putExtra("mylikeCount",likeCount);
        sliderpage.putExtra("mylikeflag",mylikeflag);
        sliderpage.putExtra("share","");
        sliderpage.putExtra("feedId",mynewsfeedmodellist.get(mylikepos).getIdfeed());
        sliderpage.putStringArrayListExtra("image", images);
        myctx.startActivity(sliderpage);


    }*/

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // Your Code here
            return true;
        }
    }

    //check vovels for pickup
    public Boolean checkvovels(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(0) == 'A' || str.charAt(0) == 'E' || str.charAt(0) == 'I' || str.charAt(0) == 'O' || str.charAt(0) == 'U') {
                return true;
            } else if (str.charAt(0) != 'A' || str.charAt(0) != 'E' || str.charAt(0) != 'I' || str.charAt(0) != 'O' || str.charAt(0) != 'U') {
                System.out.println("The String contains no Vowels");
                return false;
            }
        }
        return false;
    }

    //spannable text for readmore
    public static class MySpannable extends ClickableSpan {

        private boolean isUnderline = false;

        /**
         * Constructor
         */
        public MySpannable(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {

            ds.setUnderlineText(isUnderline);
            ds.setColor(Color.parseColor("#343434"));

        }

        @Override
        public void onClick(View widget) {

        }
    }

    //spannable text for readmore
    private static SpannableStringBuilder addClickablePartTextViewResizable1(final Spanned strSpanned, final TextView tv,
                                                                             final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);


        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizables(tv, -1, "Read Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizables(tv, 3, "Read More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
            ForegroundColorSpan foregroundSpans = new ForegroundColorSpan(Color.parseColor("#3268BF"));
            ssb.setSpan(new RelativeSizeSpan(0.8f), str.indexOf(spanableText), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(foregroundSpans, str.indexOf(spanableText), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return ssb;

    }

    public static void makeTextViewResizables(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable1(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
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
                    try {
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable1(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    } catch (StringIndexOutOfBoundsException ex) {
                        ex.printStackTrace();
                    }

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


    @Override
    public int getItemCount() {
        return mynewsfeedmodellist.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (mynewsfeedmodellist.get(position).getFeedtype().equalsIgnoreCase("pick_up")) {
            return PICKUP;
        }
        if (mynewsfeedmodellist.get(position).getFeedtype().equalsIgnoreCase("article")) {
            return ARTICLE_VIEW;
        }
        if (mynewsfeedmodellist.get(position).getFeedtype().equalsIgnoreCase("photo")) {
            return SLIDER;
        }
        if (mynewsfeedmodellist.get(position).getFeedtype().equalsIgnoreCase("video")) {
            return VIDEO_VIEW;
        }


        return PICKUP;
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {

        JSONObject obj = new JSONObject(successResponse);
        String msg = obj.getString("details");
        Log.d("getresplike", msg);
        if (msg.equalsIgnoreCase("like")) {
            mynewsfeedmodellist.get(mypostion).setLikecount("1");
        } else {
            mynewsfeedmodellist.get(mypostion).setLikecount("0");
        }
        completed = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(mypostion, "lola");
                    }
                });


            }
        });


        sss = true;


    }

    private void bind(final RecyclerView.ViewHolder holder) {
        // holder.textView.setText("item " + holder.getAdapterPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Log.d("butt", "click " + position);

            }
        });
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

    class loader extends AsyncTask<Void, Void, Void> {
        String myimage;
        GifImageView mygif;

        loader(GifImageView gif, String image) {
            this.mygif = gif;
            this.myimage = image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = PickupListing.getBitmapFromURL(myimage);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mygif.setBytes(bitmap);
            mygif.startAnimation();
        }
    }

    class CustomSliderHolder extends RecyclerView.ViewHolder implements OnPageClickListener, ViewPager.OnPageChangeListener {
        private CustomTextView tv_title, tv_pickupname, tv_time, tv_like_count;
        CircleImageView profilepic;
        CircleIndicator indicator_custom;
        private LinearLayout pager_indicator;
        ViewPager pager;
        // InfiniteIndicator mAnimCircleIndicator;
        private Exp tv_pickup_description;
        ImageView iv_like, iv_share;
        IndicatorConfiguration configuration = new IndicatorConfiguration.Builder()
                .imageLoader(new GlideLoader())
                .isStopWhileTouch(true)
                .isAutoScroll(false).isLoop(false)
                .onPageChangeListener(this).isDrawIndicator(false)
                .onPageClickListener(this).direction(IndicatorConfiguration.LEFT)
                .build();


        public CustomSliderHolder(View itemView) {
            super(itemView);
            tv_title = (CustomTextView) itemView.findViewById(R.id.tv_title);
            tv_pickupname = (CustomTextView) itemView.findViewById(R.id.tv_pickupname);
            tv_time = (CustomTextView) itemView.findViewById(R.id.tv_time);
            indicator_custom = (CircleIndicator) itemView.findViewById(R.id.indicator_custom);
            tv_pickup_description = (Exp) itemView.findViewById(R.id.tv_pickup_description);
            profilepic = (CircleImageView) itemView.findViewById(R.id.iv_pic);
            //  pager_indicator = (LinearLayout) itemView.findViewById(R.id.viewPagerCountDots);
            pager = (ViewPager) itemView.findViewById(R.id.pager);

            //  mAnimCircleIndicator.init(configuration);
            // mAnimCircleIndicator.notifyDataChange(pageViews);


            // customesliderholder.pager.setBanners(banners);


            //  customesliderholder.pager.set(R.drawable.uactv);

            //customesliderholder.pager.setAdapter(adapter);

            //  customesliderholder.pager.setCurrentItem(Image_Detail_activity.hack)


            iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
            tv_like_count = (CustomTextView) itemView.findViewById(R.id.tv_like_count);
            iv_share = (ImageView) itemView.findViewById(R.id.iv_share);

            iv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("mylikeclickpos", mynewsfeedmodellist.get(getAdapterPosition()).getLiked());
                            Log.d("mylikeclickpos", getAdapterPosition() + "");
                            mypostion = getAdapterPosition();
                            if (completed == true) {
                                if (String.valueOf(iv_like.getTag()) == "unlike") {

                                    Social_Feed_Adapter.hacks = "1";
                                    Utility.setEventTracking(myctx, "Social Feed-Images unlike", mynewsfeedmodellist.get(getAdapterPosition()).getTitle());
                                    iv_like.setImageResource(R.drawable.redlike);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    Social_Feed_Adapter.mylikeflag = "1";
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "1");
                                    // mynewsfeedmodellist.get(position).getLiked() = "1";

                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);

                                } else {
                                    Utility.setEventTracking(myctx, "Social Feed-Images like", mynewsfeedmodellist.get(getAdapterPosition()).getTitle());
                                    //   Image_Detail_activity.customlike.getlike(0);
                                    Social_Feed_Adapter.hacks = "0";
                                    iv_like.setImageResource(R.drawable.ic_like);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");
                                    Social_Feed_Adapter.mylikeflag = "0";
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "0");


                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);
                                }
                            }


                            animateHeart(iv_like);
                            completed = false;
                        }
                    }, 60);


                }
            });

            profilepic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Intent intent_buddydetails = new Intent(myctx, BusinessDetailsActivity.class);
                    intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent_buddydetails.putExtra("view", true);
                    intent_buddydetails.putExtra("userid", mynewsfeedmodellist.get(getAdapterPosition()).getIduser());
                    myctx.startActivity(intent_buddydetails);
                    return false;
                }
            });
            profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profilepic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                       /*     Intent intent_buddydetails = new Intent(myctx, BusinessDetailsActivity.class);
                            intent_buddydetails.putExtra("view", true);
                            intent_buddydetails.putExtra("userid", mynewsfeedmodellist.get(getAdapterPosition()).getIduser());
                            intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            myctx.startActivity(intent_buddydetails);*/

                        }
                    });
                }
            });
        }

        private void addBottomDots(int currentPage) {
            dots = new ImageView[mynewsfeedmodellist.get(getAdapterPosition()).getDetails().size()];
            pager_indicator.removeAllViews();
            for (int i = 0; i < mynewsfeedmodellist.get(getAdapterPosition()).getDetails().size(); i++) {
                dots[i] = new ImageView(myctx);
                dots[i].setImageDrawable(myctx.getResources().getDrawable(R.drawable.normal));


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


                params.setMargins(4, 0, 4, 0);
                pager_indicator.addView(dots[i], params);

            }
            try {
                dots[currentPage].setImageDrawable(myctx.getResources().getDrawable(R.drawable.selected_img));
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }

        }

        @Override
        public void onPageClick(int position, Page page) {
            System.out.println("adaaas" + getAdapterPosition());
            likeCount = mynewsfeedmodellist.get(getAdapterPosition()).getLiked();
            mylikeflag = mynewsfeedmodellist.get(getAdapterPosition()).getLikecount();
            Intent sliderpage = new Intent(myctx, Image_Detail_activity.class);
            ArrayList<String> images = new ArrayList<>();
            for (int i = 0; i < mynewsfeedmodellist.get(getAdapterPosition()).getDetails().size(); i++) {
                images.add("https" + mynewsfeedmodellist.get(getAdapterPosition()).getDetails().get(i).getImage().substring(4));
            }
            Log.d("currpos", getAdapterPosition() + "");
            sliderpage.putExtra("index", position);
            sliderpage.putExtra("likeindex", getAdapterPosition());
            sliderpage.putExtra("description", mynewsfeedmodellist.get(getAdapterPosition()).getDescription());
            sliderpage.putExtra("mylikeCount", likeCount);
            sliderpage.putExtra("mylikeflag", mylikeflag);
            sliderpage.putExtra("share", "");
            sliderpage.putExtra("feedId", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
            sliderpage.putStringArrayListExtra("image", images);
            Log.d("mypage", getAdapterPosition() + new Gson().toJson(images));
            myctx.startActivity(sliderpage);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
      /*      if(mynewsfeedmodellist.get(getAdapterPosition()).getDetails().size()>0){
                for (int i = 0; i < mynewsfeedmodellist.get(getAdapterPosition()).getDetails().size(); i++) {
                    Log.d("adapterpos",i+"");
                    dots[i].setImageDrawable(myctx.getResources().getDrawable(R.drawable.normal));
                }
            }
            Log.d("adaptese",position+"");*/
            // dots[position].setImageDrawable(myctx.getResources().getDrawable(R.drawable.selected_img));

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class CustomVideoHolder extends RecyclerView.ViewHolder {
        private CustomTextView tv_title, tv_pickupname, tv_time, tv_like_count;
        MyImageview video_view;
        CircleImageView profilepic;
        private Exp tv_video_description;
        ImageView iv_like, iv_share;

        public CustomVideoHolder(View itemView) {
            super(itemView);
            tv_title = (CustomTextView) itemView.findViewById(R.id.tv_title);
            tv_pickupname = (CustomTextView) itemView.findViewById(R.id.tv_pickupname);
            tv_video_description = (Exp) itemView.findViewById(R.id.tv_video_description);
            tv_time = (CustomTextView) itemView.findViewById(R.id.tv_time);
            video_view = (MyImageview) itemView.findViewById(R.id.video_view);
            profilepic = (CircleImageView) itemView.findViewById(R.id.iv_pic);

            iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
            tv_like_count = (CustomTextView) itemView.findViewById(R.id.tv_like_count);
            iv_share = (ImageView) itemView.findViewById(R.id.iv_share);


            iv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("mylikeclickpos", mynewsfeedmodellist.get(getAdapterPosition()).getLiked());
                            Log.d("mylikeclickpos", getAdapterPosition() + "");
                            mypostion = getAdapterPosition();
                            if (completed == true) {
                                if (String.valueOf(iv_like.getTag()) == "unlike") {
                                    Utility.setEventTracking(myctx, "Social Feed-Video unlike ", url);
                                    iv_like.setImageResource(R.drawable.redlike);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "1");
                                    // mynewsfeedmodellist.get(position).getLiked() = "1";

                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);

                                } else {
                                    Utility.setEventTracking(myctx, "Social Feed-Video like", url);
                                    iv_like.setImageResource(R.drawable.ic_like);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");

                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "0");


                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);
                                }
                            }


                            animateHeart(iv_like);
                            completed = false;
                        }
                    }, 60);


                }
            });

            video_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.setScreenTracking(myctx, "Social Feed");
                    Utility.setEventTracking(myctx, "Social Feed-Video", mynewsfeedmodellist.get(getAdapterPosition()).getTitle() + " Video Link " + url);
                    Intent videoplayer = new Intent(myctx, Video_Player.class);
                    videoplayer.putExtra("url", "http://d3534htadowgzl.cloudfront.net/videos/" + mynewsfeedmodellist.get(getAdapterPosition()).getDetails().get(0).getLink());
                    Log.d("myvdo", mynewsfeedmodellist.get(getAdapterPosition()).getDetails().get(0).getLink());
                    videoplayer.putExtra("video_image", video_image);
                    myctx.startActivity(videoplayer);
                }
            });


            profilepic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Intent intent_buddydetails = new Intent(myctx, BusinessDetailsActivity.class);
                    intent_buddydetails.putExtra("view", true);
                    intent_buddydetails.putExtra("userid", mynewsfeedmodellist.get(getAdapterPosition()).getIduser());
                    intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myctx.startActivity(intent_buddydetails);
                    return false;
                }
            });

            iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareDialog dialog = new ShareDialog(myact);
                    AppConstants.showShareAlert(myctx, "Check out UACTIV, a cool app to find buddies and organize sports & fitness activities. ", "https://www.uactiv.com/inviteDownload.html", "Share the App!", dialog);
                }
            });


        }
    }

    class CustomArticleHolder extends RecyclerView.ViewHolder {
        private CustomTextView tv_article_header, tv_title, tv_articlename, tv_time, tv_like_count;
        private MyImageview video_view;
        private CustomTextView tv_article_description;
        ImageView iv_like, iv_share;


        public CustomArticleHolder(View itemView) {
            super(itemView);

            tv_article_header = (CustomTextView) itemView.findViewById(R.id.tv_article_header);
            tv_article_description = (CustomTextView) itemView.findViewById(R.id.tv_article_description);
            video_view = (MyImageview) itemView.findViewById(R.id.video_view);
            tv_time = (CustomTextView) itemView.findViewById(R.id.tv_time);
            tv_title = (CustomTextView) itemView.findViewById(R.id.tv_title);
            tv_articlename = (CustomTextView) itemView.findViewById(R.id.tv_articlename);
            iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
            tv_like_count = (CustomTextView) itemView.findViewById(R.id.tv_like_count);
            iv_share = (ImageView) itemView.findViewById(R.id.iv_share);


            iv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mypostion = getAdapterPosition();
                            if (completed == true) {

                                if (String.valueOf(iv_like.getTag()) == "unlike") {
                                    Utility.setEventTracking(myctx, "Social Feed-Article unlike", mynewsfeedmodellist.get(getAdapterPosition()).getTitle());

                                    iv_like.setImageResource(R.drawable.redlike);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "1");
                                    // mynewsfeedmodellist.get(position).getLiked() = "1";

                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);

                                } else {
                                    Utility.setEventTracking(myctx, "Social Feed-Article like", mynewsfeedmodellist.get(getAdapterPosition()).getTitle());
                                    iv_like.setImageResource(R.drawable.ic_like);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");

                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "0");


                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);
                                }

                            }
                            animateHeart(iv_like);
                            completed = false;
                        }
                    }, 60);


                }
            });


            iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareDialog dialog = new ShareDialog(myact);
                    AppConstants.showShareAlert(myctx, "", mynewsfeedmodellist.get(getAdapterPosition()).getLink(), "Share article", dialog);
                }
            });

        }
    }


    class CustomPickupViewholder extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        MyImageview gifView;
        ImageView iv_like, iv_share;
        View.OnClickListener viewclicklistner;
        LinearLayout attendi_three_layout, attendi_more_layout;
        RelativeLayout rel, pickuplayout;


        private CustomTextView tv_title, tv_pickupname, tv_time, tv_date, tv_pickup_time, tv_location, tv_spots, tv_paid, tv_attending_count, tv_like_count, tv_video_description, tv_article_header, tv_article_description, tv_articlename, textView11;
        private CustomButton btn_knowmore;
        private CircleImageView iv_pic_one, iv_pic_two, iv_pic_three, iv_count, iv_pic_one1, iv_pic_two2, iv_pic_three3;
        private de.hdodenhof.circleimageview.CircleImageView profilepic;


        public CustomPickupViewholder(View itemView) {
            super(itemView);

            gifView = (MyImageview) itemView.findViewById(R.id.gifImageView);
            tv_title = (CustomTextView) itemView.findViewById(R.id.tv_title);
            tv_pickupname = (CustomTextView) itemView.findViewById(R.id.tv_pickupname);
            tv_time = (CustomTextView) itemView.findViewById(R.id.tv_time);
            tv_date = (CustomTextView) itemView.findViewById(R.id.tv_date);
            tv_pickup_time = (CustomTextView) itemView.findViewById(R.id.tv_pickup_time);
            tv_spots = (CustomTextView) itemView.findViewById(R.id.tv_spots);
            tv_paid = (CustomTextView) itemView.findViewById(R.id.tv_paid);
            tv_attending_count = (CustomTextView) itemView.findViewById(R.id.tv_attending_count);
            iv_like = (ImageView) itemView.findViewById(R.id.iv_like);
            iv_share = (ImageView) itemView.findViewById(R.id.iv_share);
            iv_pic_one = (CircleImageView) itemView.findViewById(R.id.iv_pic_one);
            iv_pic_two = (CircleImageView) itemView.findViewById(R.id.iv_pic_two);
            iv_pic_three = (CircleImageView) itemView.findViewById(R.id.iv_pic_three);
            tv_like_count = (CustomTextView) itemView.findViewById(R.id.tv_like_count);
            profilepic = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.iv_pic);
            btn_knowmore = (CustomButton) itemView.findViewById(R.id.btn_knowmore);
            iv_pic = (CircleImageView) itemView.findViewById(R.id.iv_pic);
            iv_count = (CircleImageView) itemView.findViewById(R.id.iv_count);
            rel = (RelativeLayout) itemView.findViewById(R.id.rel);

            attendi_three_layout = (LinearLayout) itemView.findViewById(R.id.attendi_three_layout);
            attendi_more_layout = (LinearLayout) itemView.findViewById(R.id.attendi_more_layout);

            iv_pic_one1 = (CircleImageView) itemView.findViewById(R.id.iv_pic_one1);
            iv_pic_two2 = (CircleImageView) itemView.findViewById(R.id.iv_pic_two2);
            iv_pic_three3 = (CircleImageView) itemView.findViewById(R.id.iv_pic_three3);
            pickuplayout = (RelativeLayout) itemView.findViewById(R.id.pickuplayout);

            textView11 = (CustomTextView) itemView.findViewById(R.id.textView11);


            iv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("mylikeclickpos", mynewsfeedmodellist.get(getAdapterPosition()).getLiked());
                            Log.d("mylikeclickpos", getAdapterPosition() + "");
                            mypostion = getAdapterPosition();

                            if (completed == true) {

                                if (String.valueOf(iv_like.getTag()) == "unlike") {

                                    iv_like.setImageResource(R.drawable.redlike);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) + 1 + "");
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "1");

                                    // mynewsfeedmodellist.get(position).getLiked() = "1";


                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);

                                } else {
                                    iv_like.setImageResource(R.drawable.ic_like);
                                    tv_like_count.setText(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");
                                    mynewsfeedmodellist.get(getAdapterPosition()).setLiked(Integer.parseInt(mynewsfeedmodellist.get(getAdapterPosition()).getLiked()) - 1 + "");

                                    param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, userId));
                                    param.put("idfeed", mynewsfeedmodellist.get(getAdapterPosition()).getIdfeed());
                                    param.put("like", "0");


                                    RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants
                                                    .getBaseUrl(SharedPref.getInstance()
                                                            .getBooleanValue(myctx, isStaging)) + like_feed,
                                            param, Social_Feed_Adapter.this, 0);
                                }


                                animateHeart(iv_like);
                                completed = false;
                            }


                        }
                    }, 60);

                }
            });

            profilepic.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Intent intent_buddydetails = new Intent(myctx, BusinessDetailsActivity.class);
                    intent_buddydetails.putExtra("view", true);
                    intent_buddydetails.putExtra("userid", mynewsfeedmodellist.get(getAdapterPosition()).getIduser());
                    intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myctx.startActivity(intent_buddydetails);
                    return false;
                }
            });

        }
    }


}
