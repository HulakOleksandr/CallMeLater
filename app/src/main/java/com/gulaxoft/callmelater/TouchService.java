package com.gulaxoft.callmelater;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class TouchService extends Service implements View.OnTouchListener {
    WindowManager mWindowManager;
    LinearLayout mDummyView;

    public TouchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mWindowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        mDummyView = new LinearLayout(this);

        LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        mDummyView.setLayoutParams(dummyParams);
        mDummyView.setOnTouchListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                1, /* width */
                1, /* height */
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        try {
            mWindowManager.addView(mDummyView, wmParams);
        } catch (Exception e) {
            Log.e("TouchServiceOnStart", e.toString());
        }
        return Service.START_STICKY;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (Settings.isBlockingEnabled()) {
            Settings.disableBlocking();
            MainActivity.KeyListener.stopRecognizing();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mDummyView);
    }
}
