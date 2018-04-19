package com.uactiv.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import com.uactiv.R;


public class CustomEditTextDone extends EditText {

    private boolean underline = false;

    public CustomEditTextDone(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
        	init(context, attrs);
    }


    public CustomEditTextDone(Context context, AttributeSet attrs) {
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

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection connection = super.onCreateInputConnection(outAttrs);
        int imeActions = outAttrs.imeOptions&EditorInfo.IME_MASK_ACTION;
        if ((imeActions&EditorInfo.IME_ACTION_DONE) != 0) {
            // clear the existing action
            outAttrs.imeOptions ^= imeActions;
            // set the DONE action
            outAttrs.imeOptions |= EditorInfo.IME_ACTION_DONE;
        }
        if ((outAttrs.imeOptions&EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
            outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        return connection;
    }
}