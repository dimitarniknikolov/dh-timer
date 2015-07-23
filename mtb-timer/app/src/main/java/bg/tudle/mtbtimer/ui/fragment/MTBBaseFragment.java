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
import android.support.v4.app.Fragment;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

import bg.tudle.mtbtimer.MTBConstants;
import bg.tudle.mtbtimer.R;

public class MTBBaseFragment extends Fragment implements MTBConstants {

    protected Tracker mTracker;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getActivity());
        mTracker = analytics.getTracker(getString(R.string.ga_tracking_id));
    }
}
