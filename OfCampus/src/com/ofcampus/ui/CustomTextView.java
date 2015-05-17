/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ofcampus.R;

public class CustomTextView extends TextView {

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);

	}

	public CustomTextView(Context context) {
		super(context);
		init(null);
	}

	private void init(AttributeSet attrs) {
		try {
			if (attrs != null) {
				TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
				String fontName = a.getString(R.styleable.CustomTextView_fontName);
				if (fontName != null) {
					Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
					setTypeface(myTypeface);
				}
				a.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}