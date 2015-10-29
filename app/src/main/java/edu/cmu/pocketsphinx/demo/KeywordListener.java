package edu.cmu.pocketsphinx.demo;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.os.AsyncTask;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import com.gulaxoft.callmelater.CallManager;
import com.gulaxoft.callmelater.MainActivity;
import com.gulaxoft.callmelater.NoiseLevelMeter;
import com.gulaxoft.callmelater.Settings;

public class KeywordListener implements
        RecognitionListener {
    private MainActivity MainActiv;

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "call me later";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    private static NoiseLevelMeter noiseMeter;
    private static int noiseLevel = 0;

    private static int phraseCount = 0;

    public static int getNoiseLevel() {
        noiseLevel = noiseMeter.getNoiseLevel();
        return noiseLevel;
    }

    public KeywordListener(final MainActivity mainActivity) {
        MainActiv = mainActivity;

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(mainActivity);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    MainActiv.initFailed();
                } else {
                    if (Settings.isBlockingEnabled()) switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        if (Settings.isBlockingEnabled()) {
            String text = hypothesis.getHypstr();
            if (text.equals(KEYPHRASE)) {
                CallManager.blockCall();
                Settings.disableBlocking();
                switchSearch(KWS_SEARCH);
            }
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    public void stopRecognizing() {
        recognizer.stop();
    }

    public void shutDownRecognizing() {
        recognizer.removeListener(this);
        recognizer.shutdown();
    }

    public void startRecognizing() {
        switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        recognizer.startListening(searchName);
    }

    private void setupRecognizer(File assetsDir) throws IOException {

        noiseMeter = new NoiseLevelMeter();

        float threshold = 1e-40f;
        /* Threshold
        if (noiseLevel < 15000) {
            threshold = 1e-20f;
        } else if (noiseLevel < 30000) {
            threshold = 1e-30f;
        } else if (noiseLevel < 35000){
            threshold = 1e-35f;
        }
        */

        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, com.gulaxoft.callmelater.R.string.kws_caption);


        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                        // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(threshold)
                .getRecognizer();

        recognizer.addListener(this);
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
    }

    @Override
    public void onError(Exception error) {
        MainActiv.setCaptionText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    public void onDestroy() {
        recognizer.stop();
        recognizer.shutdown();
    }
}
