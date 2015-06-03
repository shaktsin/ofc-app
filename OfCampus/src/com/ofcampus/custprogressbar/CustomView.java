/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.custprogressbar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class CustomView extends RelativeLayout {

	protected final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
	protected final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

	protected int minWidth;
	protected int minHeight;

	protected int backgroundColor;
	protected int beforeBackground;
	protected int backgroundResId = -1;

	// Indicate if user touched this view the last time
	public boolean isLastTouch = false;

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onInitDefaultValues();
		// onInitAttributes(attrs);
	}

	protected abstract void onInitDefaultValues();

	// Set atributtes of XML to View
	protected void setAttributes(AttributeSet attrs) {
		setMinimumHeight(Utils.dpToPx(minHeight, getResources()));
		setMinimumWidth(Utils.dpToPx(minWidth, getResources()));
		if (backgroundResId != -1 && !isInEditMode()) {
			setBackgroundResource(backgroundResId);
		}
		setBackgroundAttributes(attrs);
	}

	protected void setBackgroundAttributes(AttributeSet attrs) {
		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
		if (bacgroundColor != -1) {
			setBackgroundColor(getResources().getColor(bacgroundColor));
		} else {
			// Color by hexadecimal
			int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
			if (background != -1 && !isInEditMode()) {
				setBackgroundColor(background);
			} else {
				setBackgroundColor(backgroundColor);
			}
		}
	}

	/**
	 * Make a dark color to press effect
	 * 
	 * @return
	 */
	protected int makePressColor(int alpha) {
		int r = (backgroundColor >> 16) & 0xFF;
		int g = (backgroundColor >> 8) & 0xFF;
		int b = (backgroundColor >> 0) & 0xFF;
		r = (r - 30 < 0) ? 0 : r - 30;
		g = (g - 30 < 0) ? 0 : g - 30;
		b = (b - 30 < 0) ? 0 : b - 30;
		return Color.argb(alpha, r, g, b);
	}

}