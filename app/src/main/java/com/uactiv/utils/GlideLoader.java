package com.uactiv.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.uactiv.R;

import cn.lightsky.infiniteindicator.ImageLoader;

/**
 * Created by lightsky on 16/1/31.
 */
public class GlideLoader implements ImageLoader {

    public void initLoader(Context context) {

    }

    @Override
    public void load(Context context, final ImageView targetView, Object res) {

        if (res instanceof String){
            Glide.with(context).load((String) res).into(targetView);
            targetView.setAdjustViewBounds(true);
            targetView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.d("myglide",(String) res);

            Glide.with(context).load((String) res).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {
                    targetView.setImageResource(R.drawable.uactv);
                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(targetView);
        }
    }
}
