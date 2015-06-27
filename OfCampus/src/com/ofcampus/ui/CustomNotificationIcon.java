package com.ofcampus.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomNotificationIcon extends View {

	public CustomNotificationIcon(Context context) {
		super(context);
	}

	public CustomNotificationIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomNotificationIcon(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int height = getHeight();
		final int width = getWidth();
		Paint mPaint = new Paint();
		mPaint.setColor(Color.GREEN);

		canvas.drawCircle(width / 2, height / 2, width, mPaint);

	}

}
