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

package bg.tudle.mtbtimer;

/**
 * @author tudle
 */
public interface MTBConstants {

    // Application state
    public static final String KEY_IS_RUNNING = "isRunning";

    // ShraredPreferences keys
    public static final String PREF_KEY_IS_RUNNING = "isRunning";
    public static final String PREF_KEY_SECONDS = "seconds";
    public static final String PREF_KEY_PRE_START_DURATION = "pre_start_seconds";
    public static final String PREF_KEY_SPEECH_STEP_DURATION = "speech_step";
    public static final String PREF_KEY_FINAL_START_MIN = "finalStartMin";
    public static final String PREF_KEY_FINAL_START_SEC = "finalStartSec";
    public static final String PREF_KEY_FINAL_STOP_MIN = "finalStopMin";
    public static final String PREF_KEY_FINAL_STOP_SEC = "finalStopSec";

    // Default SharedPreferences values
    public static final int DEFAULT_PRE_START_DURATION = 30;
    public static final int DEFAULT_SPEECH_STEP_DURATION = 10;
    public static final int DEFAULT_FINAL_START_MIN = 3;
    public static final int DEFAULT_FINAL_START_SEC = 0;
    public static final int DEFAULT_FINAL_STOP_MIN = 3;
    public static final int DEFAULT_FINAL_STOP_SEC = 10;

    // Download link for MTBTimer.apk file
    public static final String DOWNLOAD_APP_LINK =
        "https://www.dropbox.com/s/nwxhl4ccvj42fem/MTBTimer.apk";

}
