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

package bg.tudle.mtbtimer.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import bg.tudle.mtbtimer.R;
import bg.tudle.mtbtimer.service.MTBTimerService;

public class MTBTimerFragment extends MTBBaseFragment implements
        OnClickListener, OnSharedPreferenceChangeListener {

    private static final String TAG = MTBSettingsFragment.class.getSimpleName();

    private boolean bRunning = false;
    private int mCurrentsecond;

    private SharedPreferences mPrefs;

    private TextView mTxtTime;
    private Button mBtnStartStop;

    // ---------------------------------------------------------------------------------------------
    // Fragment life cycle methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        mTracker.sendView("TimerFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        setClickListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupViews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_RUNNING, bRunning);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        // reset the timer if is not running
        if (!bRunning) {
            mPrefs.edit().putInt(PREF_KEY_SECONDS, 0).commit();
        }
        mTracker.sendTiming("StopTimer", mCurrentsecond * 1000, "Lap",
                "stop timer onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onDetach();
    }

    // ---------------------------------------------------------------------------------------------
    // Private help methods
    // ---------------------------------------------------------------------------------------------
    private void bindViews() {
        mTxtTime = (TextView) getView().findViewById(R.id.timer_txt_time);
        mBtnStartStop = (Button) getView().findViewById(R.id.timer_btn_stop_timer);
    }

    private void setClickListeners() {
        mBtnStartStop.setOnClickListener(this);
    }

    private void setupViews() {
        bRunning = mPrefs.getBoolean(KEY_IS_RUNNING, false);
        mBtnStartStop.setText(bRunning ? R.string.stop : R.string.start);
        displayTime();
    }

    private void displayTime() {
        mCurrentsecond = mPrefs.getInt(PREF_KEY_SECONDS, 0);
        String timeStr = String
                .format("%02d", (mCurrentsecond / 60))
                + getString(R.string.m) + ":";
        mTxtTime.setText(timeStr);
        timeStr += String
                .format("%02d", (mCurrentsecond % 60))
                + getString(R.string.s);
        mTxtTime.setText(timeStr);
    }

    private void startTimer() {
        Intent intent = new Intent(getActivity(), MTBTimerService.class);
        getActivity().startService(intent);
        bRunning = true;
    }

    private void stopTimer() {
        Intent intent = new Intent(getActivity(), MTBTimerService.class);
        getActivity().stopService(intent);
        bRunning = false;
    }

    // ---------------------------------------------------------------------------------------------
    // Interface implementations
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) { // View.OnClickListener
        switch (v.getId()) {
            case R.id.timer_btn_stop_timer:
                if (bRunning) {
                    stopTimer();
                    mBtnStartStop.setText(R.string.start);
                    mTracker.sendEvent("UI", "Click", "onStop", -1l);
                    mTracker.sendTiming("StopTimer", mCurrentsecond * 1000, "Lap",
                            "Force Finishing");
                } else {
                    startTimer();
                    mBtnStartStop.setText(R.string.stop);
                    mTracker.sendEvent("UI", "Click", "onStart", 1l);
                }
                break;
            default:
                break;
        }
    }

    // SharedPreferences.OnSharedPreferenceChangeListener
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (isAdded()) {
            if (key.equals(PREF_KEY_SECONDS)) {
                displayTime();
            } else if (key.equals(PREF_KEY_IS_RUNNING)) {
                bRunning = prefs.getBoolean(KEY_IS_RUNNING, false);
                mBtnStartStop.setText(bRunning ? R.string.stop : R.string.start);
            }
        }
    }
}
