package com.uactiv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.uactiv.R;
import com.uactiv.activity.Image_Detail_activity;
import com.uactiv.activity.MyImageview;
import com.uactiv.model.Detail_;
import com.uactiv.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pratikb on 16-01-2018.
 */

public class ImageSliderAdapter extends PagerAdapter {
    Context myctx;
    List<Detail_> myimg;
    MyImageview gifImageView;
    ImageView iv_video_play;
    int curpos = 0;
    int mypos;
    String mydescription,likeCount,mylikeflag,share,idfeed;

    public ImageSliderAdapter(Context ctx, List<Detail_> img, String description, String likeCount, String mylikeflag, String share, String idfeed, int position) {
        myctx = ctx;
        myimg = img;
        this.mydescription=description;
        this.likeCount=likeCount;
        this.mylikeflag=mylikeflag;
        this.share=share;
        this.idfeed=idfeed;
        this.mypos=position;
        if(myimg.size()==0){
            myimg=new ArrayList<>();
            Detail_ d=new Detail_();
            d.setImage("");
            d.setLink("");
            myimg.add(d);
        }

    }


    @Override
    public int getCount() {
        return myimg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(myctx, R.layout.pickupplisting_pager_inflator, null);
        gifImageView = (MyImageview) view.findViewById(R.id.gifImageView);
        container.addView(view);
        ImageLoader imageLoader = ImageLoader.getInstance();
       // imageLoader.displayImage(myimg.get(position).getImage(), gifImageView);

       // Glide.with(myctx).load(myimg.get(position).getImage().trim()).into(gifImageView);
     //   Picasso.with(myctx).load(myimg.get(position).getImage()).error(R.drawable.uactv).into(gifImageView);
        gifImageView.setImageResource(R.drawable.static_img);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.static_img);
        requestOptions.error(R.drawable.static_img);
        Glide.with(myctx).setDefaultRequestOptions(requestOptions).load(myimg.get(position).getImage().trim()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                gifImageView.setImageResource(R.drawable.static_img);

                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(gifImageView);
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                curpos = position;
                Intent sliderpage = new Intent(myctx, Image_Detail_activity.class);
                ArrayList<String> images = new ArrayList<>();
                for (int i = 0; i < myimg.size(); i++) {
                    try {
                        images.add( "http" + myimg.get(i).getImage().substring(4));
                    }catch (StringIndexOutOfBoundsException ex){
                        ex.printStackTrace();
                         images.add( "http" + myimg.get(i).getImage());
                    }


                }
                sliderpage.putExtra("index", curpos);
                sliderpage.putExtra("likeindex", mypos);
                sliderpage.putExtra("description",mydescription);
                sliderpage.putExtra("mylikeCount",likeCount);
                sliderpage.putExtra("mylikeflag",mylikeflag);
                sliderpage.putExtra("share",share);
                sliderpage.putExtra("feedId",idfeed);
                sliderpage.putStringArrayListExtra("image", images);
                myctx.startActivity(sliderpage);

            }
        });
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
