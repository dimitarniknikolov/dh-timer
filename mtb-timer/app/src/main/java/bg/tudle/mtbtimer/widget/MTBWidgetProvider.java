/*
 * Copyright (C) 2013 Dimitar Nikolov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bg.tudle.mtbtimer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import bg.tudle.mtbtimer.MTBConstants;
import bg.tudle.mtbtimer.R;
import bg.tudle.mtbtimer.service.MTBTimerService;
import bg.tudle.mtbtimer.ui.MTBMainActivity;

public class MTBWidgetProvider extends AppWidgetProvider {

    private static final String TAG = MTBWidgetProvider.class.getSimpleName();

    private static final String ACTION_START_STOP = "ACTION_START_STOP";

    private SharedPreferences mPrefs;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                                                            R.layout.widget_layout);

            // Register an onClickListener
            final Intent intent = new Intent(context, MTBWidgetProvider.class);

            intent.setAction(ACTION_START_STOP);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                                                           0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.btn, pendingIntent);

            final Intent actovity = new Intent(context, MTBMainActivity.class);

            final PendingIntent pendingActivity = PendingIntent.getActivity(context,
                                                                            0, actovity, 0);
            remoteViews.setOnClickPendingIntent(R.id.root_widget, pendingActivity);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            Log.d(TAG, "updated");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive " + intent.getAction());
        if (intent.getAction().equals(ACTION_START_STOP)) {
            if (mPrefs == null) {
                mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            }

            if (!mPrefs.getBoolean(MTBConstants.PREF_KEY_IS_RUNNING, false)) {
                context.startService(new Intent(context.getApplicationContext(),
                                                MTBTimerService.class));
            } else {
                context.stopService(new Intent(context.getApplicationContext(),
                                               MTBTimerService.class));
            }
        }
        super.onReceive(context, intent);
    }
}
