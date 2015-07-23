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

package bg.tudle.mtbtimer.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.RemoteViews;

import bg.tudle.mtbtimer.MTBConstants;
import bg.tudle.mtbtimer.R;
import bg.tudle.mtbtimer.widget.MTBWidgetProvider;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MTBTimerService extends Service implements MTBConstants, OnInitListener {

    private static final String TAG = MTBTimerService.class.getSimpleName();

    private int mSpeechStepDuration;
    private int mFinalStartMin;
    private int mFinalStartSec;
    private int mFinalStopMin;
    private int mFinalStopSec;

    private String[] mSecondsArray;

    private SharedPreferences mPrefs;

    private Timer timer = null;
    private TextToSpeech mTextToSpeech;

    // ---------------------------------------------------------------------------------------------
    // Service life cycle methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MTBTimerService.this);
        setTimeMembersFromPrefs();
        mSecondsArray = getResources().getStringArray(R.array.times);
        timer = new Timer();
        mTextToSpeech = new TextToSpeech(this, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        stopTimer();
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    // ---------------------------------------------------------------------------------------------
    // Interface implementations
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale[] langs = {
                    Locale.UK, Locale.US
            };
            for (Locale lang : langs) {
                int result = mTextToSpeech.setLanguage(lang);
                if ((result != TextToSpeech.LANG_MISSING_DATA)
                        && (result != TextToSpeech.LANG_NOT_SUPPORTED)) {
                    startTimer();
                    break;
                } else {
                    Log.e("TTS", String.format("%s Language is not supported", lang));
                }
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
            // TODO told to the user that TTS is not supported
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private help methods
    // ---------------------------------------------------------------------------------------------
    private void setTimeMembersFromPrefs() {
        mSpeechStepDuration = mPrefs.getInt(PREF_KEY_SPEECH_STEP_DURATION,
                DEFAULT_SPEECH_STEP_DURATION);
        mFinalStartMin = mPrefs.getInt(PREF_KEY_FINAL_START_MIN,
                DEFAULT_FINAL_START_MIN);
        mFinalStartSec = mPrefs.getInt(PREF_KEY_FINAL_START_SEC,
                DEFAULT_FINAL_START_SEC);
        mFinalStopMin = mPrefs.getInt(PREF_KEY_FINAL_STOP_MIN,
                DEFAULT_FINAL_STOP_MIN);
        mFinalStopSec = mPrefs.getInt(PREF_KEY_FINAL_STOP_SEC,
                DEFAULT_FINAL_STOP_SEC);
    }

    private void startTimer() {
        int preStartDuration = mPrefs.getInt(PREF_KEY_PRE_START_DURATION,
                DEFAULT_PRE_START_DURATION);
        mPrefs.edit().putInt(PREF_KEY_SECONDS, preStartDuration * -1).commit();
        final TimerTask updateTimerValuesTask = new TimerTask() {
            @Override
            public void run() {
                processSecond();
            }
        };
        mPrefs.edit().putBoolean(PREF_KEY_IS_RUNNING, true).commit();
        timer.schedule(updateTimerValuesTask, 1000, 1000);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(),
                MTBWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setImageViewResource(R.id.btn, android.R.drawable.ic_media_pause);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        mPrefs.edit().putBoolean(PREF_KEY_IS_RUNNING, false).commit();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(),
                MTBWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setImageViewResource(R.id.btn, android.R.drawable.ic_media_play);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private void processSecond() {
        int seconds = mPrefs.getInt(PREF_KEY_SECONDS, DEFAULT_PRE_START_DURATION);
        seconds++;
        mPrefs.edit().putInt(PREF_KEY_SECONDS, seconds).commit();
        updateWidgets();
        speakOut(seconds);
    }

    private void speakOut(int second) {
        if (isBeforeStart(second)) {
            // speak before start every second
            speakPreStartTime(second);
        } else {
            int minute = second / 60;
            if (isTimeInFinalTime(minute, second)) {
                // speak in final time every second
                speakFinalTime(minute, second);
            } else if ((second % mSpeechStepDuration) == 0) {
                // speak in run time only on speech step
                speakRunTime(minute, second);
            }
            if (isTimeOutsideFinalTime(minute, second % 60)) {
                stopTimer();
            }
        }
    }

    private void updateWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName thisWidget = new ComponentName(getApplicationContext(),
                MTBWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                    R.layout.widget_layout);
            int mCurrentsecond = mPrefs.getInt(PREF_KEY_SECONDS, 0);
            String timeStr = String
                    .format("%02d", (mCurrentsecond / 60))
                    + getString(R.string.m);
            timeStr += "\n" + String
                    .format("%02d", (mCurrentsecond % 60))
                    + getString(R.string.s);
            remoteViews.setTextViewText(R.id.update_days, timeStr);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private void speakPreStartTime(int second) {
        Log.d("tudle", "in pre start time");
        speakOut(mSecondsArray[Math.abs(second)]);
    }

    private void speakRunTime(int minute, int second) {
        Log.d("tudle", "in run time");
        String text;
        if (second <= 59) {
            // from 0 to 59 seconds
            text = mSecondsArray[Math.abs(second)];
        } else if ((second % 60) == 0) {
            // bigger than one minute
            String minSpeech = mSecondsArray[minute];
            text = minSpeech
                    + " "
                    + (minute > 1 ? getString(R.string.minutes)
                            : getString(R.string.minute));
        } else {
            // exactly minutes
            second = second % 60;
            text = mSecondsArray[Math.abs(second)];
        }
        speakOut(text);
    }

    private void speakFinalTime(int minute, int second) {
        Log.d("tudle", "in final time");
        String text;
        if (second <= 59) {
            // from 0 to 59 seconds
            text = mSecondsArray[Math.abs(second)];
        } else if ((second % 60) == 0) {
            // bigger than one minute
            String minSpeech = mSecondsArray[minute];
            text = minSpeech
                    + " "
                    + (minute > 1 ? getString(R.string.minutes)
                            : getString(R.string.minute));
        } else {
            // exactly minutes
            second = second % 60;
            text = mSecondsArray[Math.abs(second)];
        }
        speakOut(text);
    }

    private boolean isBeforeStart(int second) {
        return (-5 <= second) && (second <= 0);
    }

    private boolean isTimeInFinalTime(int min, int sec) {
        return (mFinalStartMin < min) || ((mFinalStartMin == min) && (mFinalStartSec <= sec % 60));
    }

    private boolean isTimeOutsideFinalTime(int min, int sec) {
        return (mFinalStopMin <= min) && (mFinalStopSec <= sec);
    }

    private void speakOut(String text) {
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
