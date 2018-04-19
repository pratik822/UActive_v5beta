package com.uactiv.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MediumTextView extends TextView {

    public MediumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediumTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/Brandon_med.otf");
        setTypeface(tf);
    }

}
