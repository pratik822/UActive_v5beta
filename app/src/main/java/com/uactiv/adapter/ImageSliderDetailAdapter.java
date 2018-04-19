package com.uactiv.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.uactiv.R;
import com.uactiv.activity.Image_Detail_activity;
import com.uactiv.utils.MyAlpha;

/**
 * Created by pratikb on 16-01-2018.
 */

public class ImageSliderDetailAdapter extends PagerAdapter implements MyAlpha {
    Context myctx;
    String[] myimg;
    ImageView gifImageView;
    int curpos = 0;
    public static MyAlpha alpha;
    View view;
    public ImageSliderDetailAdapter(Context ctx, String[] img) {
        myctx = ctx;
        myimg = img;
        alpha=(MyAlpha)this;

    }

    @Override
    public int getCount() {
        return myimg.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(myctx, R.layout.pickupplistingdetail_pager_inflator, null);
        gifImageView = (ImageView) view.findViewById(R.id.gifImageView);

        //gifImageView.getBackground().setAlpha(0.5f);
       // Glide.with(myctx).load(myimg[position]).into(gifImageView);


        container.addView(view);
        Log.d("myimgsss",myimg[position]);
       // Picasso.with(myctx).load(myimg[position]).error(R.drawable.uactv).into(gifImageView);
     //   Glide.with(myctx).load(myimg[position]).into(gifImageView);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.static_img);
        requestOptions.error(R.drawable.static_img);

       Glide.with(myctx).setDefaultRequestOptions(requestOptions).load(myimg[position]).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                gifImageView.setImageResource(R.drawable.uactv);

                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).  into(gifImageView);


        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void setalpha(Boolean alpha) {
        Log.d("myalpha",alpha+"");
      /*  if(alpha==true){
            gifImageView.setAlpha(80);
        }else{
            gifImageView.setAlpha(250);

        }*/
    }
}
