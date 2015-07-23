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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.HashMap;

import bg.tudle.mtbtimer.R;

public class MTBSettingsFragment extends MTBBaseFragment {

    private static final String TAG = MTBSettingsFragment.class.getSimpleName();

    private int mPreStartDuration;
    private int mSpeechStepDuration;
    private int mFinalStartMin;
    private int mFinalStartSec;
    private int mFinalStopMin;
    private int mFinalStopSec;
    private String[] mSecondsArray;
    private String[] mMinutesArray;

    private SharedPreferences mPrefs;

    private Spinner mSpnPreStartDuration;
    private Spinner mSpnSpeechStep;
    private Spinner mSpnFinalStartMin;
    private Spinner mSpnFinalStartSec;
    private Spinner mSpnFinalStopMin;
    private Spinner mSpnFinalStopSec;

    // ---------------------------------------------------------------------------------------------
    // Fragment life cycle methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSecondsArray = getResources().getStringArray(R.array.pre_start_times);
        mMinutesArray = getResources().getStringArray(R.array.minutes_array);
        mTracker.sendView("SettingsFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initTimeMembersFromRrefs();
        setClickListeners();
    }

    // ---------------------------------------------------------------------------------------------
    // Private help methods
    // ---------------------------------------------------------------------------------------------
    private void bindViews(View v) {
        mSpnPreStartDuration = (Spinner) v
            .findViewById(R.id.settings_spn_pre_start_duration);
        mSpnSpeechStep = (Spinner) v
            .findViewById(R.id.settings_spn_speech_step);
        mSpnFinalStartMin = (Spinner) v
            .findViewById(R.id.settings_spn_start_final_minutes);
        mSpnFinalStartSec = (Spinner) v
            .findViewById(R.id.settings_spn_start_final_seconds);
        mSpnFinalStopMin = (Spinner) v
            .findViewById(R.id.settings_spn_stop_final_minutes);
        mSpnFinalStopSec = (Spinner) v
            .findViewById(R.id.settings_spn_stop_final_seconds);
    }

    private void initTimeMembersFromRrefs() {
        mPreStartDuration = mPrefs.getInt(PREF_KEY_PRE_START_DURATION,
                                          DEFAULT_PRE_START_DURATION);
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

    private void setClickListeners() {
        setPreStartSpnListener();
        setOnRunSpnListener();
        setFinalStartSpnListener();
        setFinalStopSpnListener();
    }

    private void setPreStartSpnListener() {
        int preStartSelectedIndex = -1;
        for (String string : mSecondsArray) {
            preStartSelectedIndex++;
            if (string.equals(String.valueOf(mPreStartDuration))) {
                break;
            }
        }
        mSpnPreStartDuration.setPromptId(R.string.pre_start_duration);
        mSpnPreStartDuration.setSelection(preStartSelectedIndex);
        mSpnPreStartDuration
            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter,
                                           View view, int position, long id) {
                    mPrefs.edit()
                          .putInt(PREF_KEY_PRE_START_DURATION,
                                  Integer.parseInt(mSecondsArray[position]))
                          .commit();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(PREF_KEY_PRE_START_DURATION,
                                mSecondsArray[position]);
                    mTracker.send("Prefs", hashMap);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
    }

    private void setOnRunSpnListener() {
        int speechStepIndex = -1;
        for (String string : mSecondsArray) {
            speechStepIndex++;
            if (string.equals(String.valueOf(mSpeechStepDuration))) {
                break;
            }
        }
        mSpnSpeechStep.setPromptId(R.string.speech_step);
        mSpnSpeechStep.setSelection(speechStepIndex);
        mSpnSpeechStep
            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter,
                                           View view, int position, long id) {
                    mPrefs.edit()
                          .putInt(PREF_KEY_SPEECH_STEP_DURATION,
                                  Integer.parseInt(mSecondsArray[position]))
                          .commit();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(PREF_KEY_SPEECH_STEP_DURATION,
                                mSecondsArray[position]);
                    mTracker.send("Prefs", hashMap);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
    }

    private void setFinalStartSpnListener() {
        // Final start minutes
        int speechStepIndex = -1;
        for (String string : mMinutesArray) {
            speechStepIndex++;
            if (string.equals(String.valueOf(mFinalStartMin))) {
                break;
            }
        }
        mSpnFinalStartMin.setPrompt("Final start minute");
        mSpnFinalStartMin.setSelection(speechStepIndex);
        mSpnFinalStartMin
            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter,
                                           View view, int position, long id) {
                    mPrefs.edit()
                          .putInt(PREF_KEY_FINAL_START_MIN,
                                  Integer.parseInt(mMinutesArray[position]))
                          .commit();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(PREF_KEY_FINAL_START_MIN,
                                mMinutesArray[position]);
                    mTracker.send("Prefs", hashMap);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        // Final start seconds
        speechStepIndex = -1;
        for (String string : mSecondsArray) {
            speechStepIndex++;
            if (string.equals(String.valueOf(mFinalStartSec))) {
                break;
            }
        }
        // mSpnFinalStartSec.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mSpnFinalStartSec.setPrompt("Final start second");
        mSpnFinalStartSec.setSelection(speechStepIndex);
        mSpnFinalStartSec
            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter,
                                           View view, int position, long id) {
                    mPrefs.edit()
                          .putInt(PREF_KEY_FINAL_START_SEC,
                                  Integer.parseInt(mSecondsArray[position]))
                          .commit();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(PREF_KEY_FINAL_START_SEC,
                                mSecondsArray[position]);
                    mTracker.send("Prefs", hashMap);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

    }

    private void setFinalStopSpnListener() {
        // Final stop minutes
        int speechStepIndex = -1;
        for (String string : mMinutesArray) {
            speechStepIndex++;
            if (string.equals(String.valueOf(mFinalStopMin))) {
                break;
            }
        }
        mSpnFinalStopMin.setPrompt("Final stop minute");
        mSpnFinalStopMin.setSelection(speechStepIndex);
        mSpnFinalStopMin
            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter,
                                           View view, int position, long id) {
                    mPrefs.edit()
                          .putInt(PREF_KEY_FINAL_STOP_MIN,
                                  Integer.parseInt(mMinutesArray[position]))
                          .commit();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(PREF_KEY_FINAL_STOP_MIN,
                                mMinutesArray[position]);
                    mTracker.send("Prefs", hashMap);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        // Final stop seconds
        speechStepIndex = -1;
        for (String string : mSecondsArray) {
            speechStepIndex++;
            if (string.equals(String.valueOf(mFinalStopSec))) {
                break;
            }
        }
        mSpnFinalStopSec.setPrompt("Final stop second");
        mSpnFinalStopSec.setSelection(speechStepIndex);
        mSpnFinalStopSec
            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter,
                                           View view, int position, long id) {
                    mPrefs.edit()
                          .putInt(PREF_KEY_FINAL_STOP_SEC,
                                  Integer.parseInt(mSecondsArray[position]))
                          .commit();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(PREF_KEY_FINAL_STOP_SEC,
                                mSecondsArray[position]);
                    mTracker.send("Prefs", hashMap);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
    }
}
