package com.uactiv.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/** Customizing AutoCompleteTextView to return Place Description   
 *  corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

	public static String placeid;
	
	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** Returns the place description corresponding to the selected item */
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		/** Each item in the autocompetetextview suggestion list is a hashmap object */
		HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
		placeid=hm.get("place_id");
		return hm.get("description");
	}
	
}
