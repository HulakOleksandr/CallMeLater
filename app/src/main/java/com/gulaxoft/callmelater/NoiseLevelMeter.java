package com.gulaxoft.callmelater;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

/**
 * Class that implements getting a level of noise of environment
 */
public class NoiseLevelMeter {

    private static int[] mSampleRates = new int [] {8000, 11025, 22050, 44100};

    // returns Audio Recorder with optimal settings
    public AudioRecord findAudioRecord() {
        String arTAG = "findAudioRecord exec";
        for (int rate: mSampleRates) {
            for (short audioFormat: new short[] {AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[] {AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d(arTAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            Log.d(arTAG, "bufferSize: GOOD VALUE");
                            AudioRecord recorder = new AudioRecord(AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                Log.d(arTAG, "RECORDER INITIALIZED");
                                return recorder;
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(arTAG, rate + " Exception, keep trying.", ex);
                    }
                }
            }
        }
        return null;
    }

    public int getNoiseLevel() {
        boolean recorder=true;
        int NoiseLevel = 0;
        int minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord ar = findAudioRecord();

        short[] buffer = new short[minSize];

        ar.startRecording();
        while(recorder) {
            ar.read(buffer, 0, minSize);
            System.out.println("listening the noise...");
            for (short s : buffer) {
                // DETECT VOLUME
                if (Math.abs(s) > 1) {
                    NoiseLevel=Math.abs(s);
                    System.out.println("Blow Value=" + NoiseLevel);
                    ar.stop();
                    recorder=false;
                    break;
                }
            }
        }
        ar.release();
        return NoiseLevel;
    }
}
