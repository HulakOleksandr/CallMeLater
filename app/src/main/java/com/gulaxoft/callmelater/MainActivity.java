/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package com.gulaxoft.callmelater;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.cmu.pocketsphinx.demo.KeywordListener;

public class MainActivity extends FragmentActivity {

    ViewPager viewPager;
    PagerAdapter pAdapter;
    ComponentName component;
    private static boolean initSuccess = true;

    static KeywordListener KeyListener;

    @Override
    public void onCreate(Bundle state) {

        super.onCreate(state);
        setContentView(R.layout.activity_main);
        getActionBar().setIcon(new ColorDrawable(0));
        viewPager = (ViewPager) findViewById(R.id.pager);
        pAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pAdapter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Settings.userInCall = CallManager.isUserInCall(this);
        startService(new Intent(this, TouchService.class));
        component = new ComponentName(this, CallReceiver.class);
        this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        KeyListener = new KeywordListener(this);
        CallReceiver.setMainActivity(this);
    }

    public void setCaptionText (String text) {
        ((TextView)findViewById(R.id.captionText)).setText(text);
    }

    public void addCaptionText(String text) {
        if (((TextView)findViewById(R.id.captionText)).getText().equals("Hello"))
            ((TextView)findViewById(R.id.captionText)).setText("");
        ((TextView)findViewById(R.id.captionText)).setText(
                ((TextView)findViewById(R.id.captionText)).getText() + text
        );
    }

    public void setResultText (String text) {
        ((TextView)findViewById(R.id.resultText)).setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                openHome();
                return true;
            case R.id.action_rate:
                openSettings();
                return true;
            case R.id.action_about:
                openAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initFailed() {
        initSuccess = false;
        setCaptionText("Failed to initialize");
        setResultText("Please, close applications that use microphone and restart this app");
        this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        ((ImageButton)findViewById(R.id.turnBtn)).setImageResource(R.drawable.ic_recognition_disabled);
        findViewById(R.id.turnBtn).setEnabled(false);
        Settings.disableRecognition();
    }

    private void openAbout() {
        viewPager.setCurrentItem(2);
    }

    private void openSettings() {
        viewPager.setCurrentItem(1);
    }

    private void openHome() {
        viewPager.setCurrentItem(0);
    }

    public static void startRecognizing() {
        KeyListener.startRecognizing();
    }
    public static void stopRecognizing() {
        KeyListener.stopRecognizing();
    }

    public static boolean isInitSuccess() {
        return initSuccess;
    }

    @Override
    public void onDestroy() {
        this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        super.onDestroy();
        try {
            if (initSuccess) KeyListener.onDestroy();
            stopService(new Intent(this, TouchService.class));
        } catch (Exception ignored) {
        }
    }

    public void onClose() {
        super.onDestroy();

        System.exit(0);
        finish();
    }
}