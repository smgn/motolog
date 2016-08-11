package com.kaetter.motorcyclemaintenancelog;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class RefuelWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);

            Intent intent = new Intent(context, RefuelActivity.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.refuelwidget);
            views.setOnClickPendingIntent(R.id.refuel_linear_layout, pendingIntent);

            mgr.updateAppWidget(appWidgetId, views);
        }
	}
}
