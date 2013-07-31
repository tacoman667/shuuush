package com.codingzen.Shuuush;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class MyGestureDetector extends SimpleOnGestureListener {

	private static final int SWIPE_MIN_DISTANCE = 120;

	Runnable flingAction;

	public MyGestureDetector(Runnable flingAction) {
		this.flingAction = flingAction;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return super.onDown(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		final float distanceTimeFactor = 0.4f;
		final float totalDx = Math.abs((distanceTimeFactor * velocityX / 2));

		try {
			if (totalDx > SWIPE_MIN_DISTANCE) {
				flingAction.run();
				return true;
			}
		} catch (Exception e) {
			LogManager.e("Error with onFling", e);
		}
		return false;
	}
}
