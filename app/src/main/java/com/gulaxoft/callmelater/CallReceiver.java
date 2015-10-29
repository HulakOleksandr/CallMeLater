package com.gulaxoft.callmelater;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    private String lastIncomingNumber = null;
    private static MainActivity MainActiv = null;

    public CallReceiver() {
        super();
    }

    public static void setMainActivity(MainActivity mainActivity) {
        MainActiv = mainActivity;
    }

    @Override
    public void onReceive (final Context context, final Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                try {
                    lastIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    // if user is not speaking with smb else now
                    if (!Settings.userInCall) {
                        Settings.userInCall = true;
                        if (Settings.isRecognitionEnabled()) {
                            Settings.enableBlocking();
                            MainActivity.KeyListener.startRecognizing();
                        }
                    } else {
                    }
                } catch (Exception ignore) {
                }
            } else {
                Settings.disableBlocking();
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    MainActivity.KeyListener.stopRecognizing();
                    Settings.userInCall = false;
                } else {
                    Settings.userInCall = true;
                }
            }
        } catch (Exception e) {
                Log.e("CallReceiverError", e.toString());
        }
    }

    public String getLastIncomingNumber() {
        return lastIncomingNumber;
    }

}
