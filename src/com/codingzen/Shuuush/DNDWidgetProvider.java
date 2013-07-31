package com.codingzen.Shuuush;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.codingzen.Shuuush.Activities.WidgetOnClickActivity;

public class DNDWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		super.onUpdate(context, appWidgetManager, appWidgetIds);

		Boolean isActive = Preferences.getBoolean(Preferences.IsShuuushEnabled);
		updateWidget(context, isActive);

		LogManager.i("DND Widget Provider onUpdate() executed.");

	}

	public static void updateWidget(Context context, Boolean isActive) {

		Context ctx = context.getApplicationContext();
		ComponentName widget = new ComponentName(ctx, DNDWidgetProvider.class);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);

		// Create an Intent to launch DNDService
		Intent intent = new Intent(ctx, WidgetOnClickActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

		// Get the layout for the App Widget and attach an on-click listener
		// to the button
		RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.dnd_widget);
		views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent);

		// String widgetText = ctx.getString(R.string.app_name);
		int icon = 0;

		if (isActive) {
			// widgetText += " Active";
			icon = R.drawable.ic_launcher_off;
		} else {
			// widgetText += " Inactive";
			icon = R.drawable.ic_launcher_on;
		}

		// views.setTextViewText(R.id.textView1, widgetText);
		views.setImageViewResource(R.id.widgetImage, icon);

		// Tell the AppWidgetManager to perform an update on the current app
		// widget
		appWidgetManager.updateAppWidget(widget, views);

		LogManager.i("Widget icon updated");
	}

}
