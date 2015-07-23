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

package bg.tudle.mtbtimer.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;

import bg.tudle.mtbtimer.MTBConstants;
import bg.tudle.mtbtimer.R;
import bg.tudle.mtbtimer.ui.fragment.MTBSettingsFragment;
import bg.tudle.mtbtimer.ui.fragment.MTBTimerFragment;

/**
 * @author Dimitar Nikolov
 */

/**
 * An Activity that contains two Fragments. One for timing and one for settings.
 */
public class MTBMainActivity extends ActionBarActivity implements MTBConstants, TabListener {

    private static final String TAG = MTBMainActivity.class.getSimpleName();

    /** use it for sending data to GoogleAnalitycs */
    private final EasyTracker mEasyTracker = EasyTracker.getInstance();

    // ---------------------------------------------------------------------------------------------
    // Activity life cycle methods
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setupActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEasyTracker.activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEasyTracker.activityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        switch (item.getItemId()) {
            case R.id.menu_feedback:
                intent.putExtra(Intent.EXTRA_TEXT,
                                getString(R.string.send_from_my_android));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.setData(Uri
                                   .parse(getString(R.string.mailto_dimitarniknikolov_gmail_com)));
                break;
            case R.id.menu_share:
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.download_apk_link_)
                        + DOWNLOAD_APP_LINK
                        + getString(R.string.send_from_my_android));
                intent.putExtra(Intent.EXTRA_SUBJECT,
                                getString(R.string.checkout_mtbtimer_for_android));
                intent.setData(Uri.parse("mailto:"));
                break;
            case R.id.menu_about:
                try {
                    PackageManager manager = getPackageManager();
                    PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                    String aboutMsg = getString(R.string.versioncode_) + info.versionCode
                        + getString(R.string._versionname_) + info.versionName;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name).setMessage(aboutMsg)
                           .setNeutralButton(R.string.ok, null).show();
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                break;
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    // ---------------------------------------------------------------------------------------------
    // Private help methods
    // ---------------------------------------------------------------------------------------------
    private void setupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.timer)
                                  .setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.settings)
                                  .setTabListener(this));
    }

    // ---------------------------------------------------------------------------------------------
    // Interface implementations
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
        Fragment fragment = null;
        switch (tab.getPosition()) {
            case 0:
                fragment = new MTBTimerFragment();
                break;
            case 1:
                fragment = new MTBSettingsFragment();
                break;
            default:
                break;
        }
        transaction.replace(android.R.id.content, fragment);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
    }

}
