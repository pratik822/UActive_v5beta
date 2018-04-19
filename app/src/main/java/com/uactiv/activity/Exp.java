package com.uactiv.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.widgets.CustomFont;

/**
 * Created by pratikb on 22-02-2018.
 */

@SuppressLint("AppCompatCustomView")
public class Exp extends TextView {
    private static final String TAG = "ExpandableTextView";
    private static final String ELLIPSIZE = "... ";
    private static final String MORE = "Read more";
    private static final String LESS = "Read less";
    Typeface faces;
    private String mFullText;
    private int mMaxLines;

    public Exp(Context context) {
        super(context);
        faces = Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_reg.otf");
    }

    public Exp(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        CustomFont.setFont(context, this, a.getInt(R.styleable.CustomTextView_typeface, -1));
    }

    public Exp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Exp(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void makeExpandable(int maxLines) {
        makeExpandable(getText().toString(), maxLines);
    }

    public void makeExpandable(String fullText, final int maxLines) {
        mFullText = fullText;
        mMaxLines = maxLines;
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                if (getLineCount() <= maxLines) {
                    setText(mFullText);
                } else {
                    setMovementMethod(LinkMovementMethod.getInstance());
                    showLess();
                }
            }
        });
    }

    /**
     * truncate text and append a clickable {@link #MORE}
     */
    private void showLess() {
        int lineEndIndex = getLayout().getLineEnd(mMaxLines - 1);
        String newText = mFullText.substring(0, lineEndIndex - (ELLIPSIZE.length() + MORE.length() + 1))
                + ELLIPSIZE + MORE;
        SpannableStringBuilder builder = new SpannableStringBuilder(newText);
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showMore();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#3268BF"));
                ds.setTextSize(28f);
                ds.setUnderlineText(false);
                ds.setTypeface(faces);
            }
        }, newText.length() - MORE.length(), newText.length(), 0);
        setText(builder,BufferType.SPANNABLE);
    }

    /**
     * show full text and append a clickable {@link #LESS}
     */
    private void showMore() {
        SpannableStringBuilder builder = new SpannableStringBuilder(mFullText + LESS);
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showLess();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#3268BF"));
                ds.setTextSize(28f);
                ds.setUnderlineText(false);
                ds.setTypeface(faces);
            }
        }, builder.length() - LESS.length(), builder.length(), 0);
        setText(builder, BufferType.SPANNABLE);
    }
}