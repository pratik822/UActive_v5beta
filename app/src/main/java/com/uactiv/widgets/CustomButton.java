package com.uactiv.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

import com.uactiv.R;


public class CustomButton extends Button {

    private boolean underline = false;

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
        	init(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
        	init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        underline = a.getBoolean(R.styleable.CustomTextView_underline, underline);
        CustomFont.setFont(context, this, a.getInt(R.styleable.CustomTextView_typeface, -1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(underline) {
            setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}