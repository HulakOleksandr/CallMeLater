package com.gulaxoft.callmelater;

public class Settings {
    private static boolean BLOCKING_ENABLED = false;
    private static boolean RECOGNITION_ENABLED = true;

    public static boolean isBlockingEnabled() {
        return BLOCKING_ENABLED;
    }
    public static void enableBlocking() {
        BLOCKING_ENABLED = true;
    }
    public static void disableBlocking() {
        BLOCKING_ENABLED = false;
    }

    public static boolean isRecognitionEnabled() {
        return RECOGNITION_ENABLED;
    }
    public static void enableRecognition() {
        RECOGNITION_ENABLED = true;
    }
    public static void disableRecognition() {
        RECOGNITION_ENABLED = false;
    }

    public static boolean userInCall = false;
}
