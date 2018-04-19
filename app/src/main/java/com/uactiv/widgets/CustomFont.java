package com.uactiv.widgets;

import android.content.Context;
import android.widget.TextView;

public final class CustomFont {
	
	public static void setFont(Context context, TextView textView, int fontValue) {
        String fontPath = null;
        switch (fontValue) {
            case 0:
                fontPath = "fonts/Brandon_bld.otf";
                break;
            case 1:
                fontPath = "fonts/Brandon_med.otf";
                break;
            case 2:
                fontPath = "fonts/Brandon_reg.otf";
                break;
        }

        if(null != fontPath) {
            textView.setTypeface(Typefaces.get(context, fontPath));
        }
	}
}