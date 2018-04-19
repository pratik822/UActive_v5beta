package com.uactiv.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.uactiv.R;

import java.util.HashMap;



public class CustomAutoCompleteTextView extends AutoCompleteTextView {
    private boolean underline = false;
	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
        if(!isInEditMode())
            init(context, attrs);
	}
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        underline = a.getBoolean(R.styleable.CustomTextView_underline, underline);
        CustomFont.setFont(context, this, a.getInt(R.styleable.CustomTextView_typeface, -1));
    }
	/** Returns the country name corresponding to the selected item */
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		/** Each item in the autocompetetextview suggestion list is a hashmap object */
		HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
		return hm.get("txt");
	}
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(underline) {
            setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}

