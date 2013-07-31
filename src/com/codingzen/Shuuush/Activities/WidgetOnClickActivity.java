package com.codingzen.Shuuush.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;
import com.codingzen.Shuuush.R;
import com.codingzen.Shuuush.ToastManager;

public class WidgetOnClickActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		LogManager.i("WidgetOnClickActivity onCreate() fired.");

		handleClick();

		this.finish();
	}

	private void handleClick() {
		Boolean IsShuuushEnabled = Preferences.getBoolean(Preferences.IsShuuushEnabled);
		String actionToPerform = "";

		if (IsShuuushEnabled) {
			actionToPerform = ApplicationIntentActions.STOP_SHUUUSH;
			ToastManager.showToast(this, getString(R.string.app_name) + " Has Been Deactivated");
		} else {
			actionToPerform = ApplicationIntentActions.START_SHUUUSH;
			ToastManager.showToast(this, getString(R.string.app_name) + " Has Been Activated");
		}

		Intent intent = new Intent(actionToPerform);
		intent.putExtra("isManualStart", true);
		startService(intent);

	}

}
