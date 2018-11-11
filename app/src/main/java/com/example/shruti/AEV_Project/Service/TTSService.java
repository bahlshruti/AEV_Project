package com.example.shruti.AEV_Project.Service;

import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.example.shruti.AEV_Project.Interface.ServiceCallbacks;

import java.util.HashMap;
import java.util.Locale;


public class TTSService extends TextToSpeechService  implements TextToSpeech.OnInitListener{

    private String content = null;

    private TextToSpeech tts;
    private static final String TAG="TTSService";
    private int status;


    public boolean bound;

    private Object ref;
    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private ServiceCallbacks serviceCallbacks;

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        public TTSService getService() {
            // Return this instance of MyService so clients can call public methods
            return TTSService.this;
        }
    }

    @Override
    protected int onIsLanguageAvailable(String s, String s1, String s2) {
        return 0;
    }

    @Override
    protected String[] onGetLanguage() {
        return new String[0];
    }

    @Override
    protected int onLoadLanguage(String s, String s1, String s2) {
        return 0;
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop service");
    }


    @Override
    protected void onSynthesizeText(SynthesisRequest synthesisRequest, SynthesisCallback synthesisCallback) {

    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.v(TAG, "onbind_service");
        bound = true;
        return  binder;
    }

    //onUnbind is only called when all clients have disconnected.
    // Thus bound will stay true until all clients have disconnected.

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onunbinding service");
        bound = false;
        return super.onUnbind(intent);
    }

    public void setCallbacks(ServiceCallbacks callbacks)
    {
        Log.i(TAG, "setting Callbacks");
        serviceCallbacks = callbacks;
    }
    @Override
    public void onCreate() {

        Log.v(TAG, "oncreate_service");
        super.onCreate();
    }

    @Override
    public void onInit(int i) {

        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.CANADA);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            }

            else
            {
                Log.v(TAG, "calling say function");
                say(content);
            }
        }
        else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "onstart service");

        if (intent !=null && intent.getExtras()!=null)
        {
            content = intent.getExtras().getString("content_to_speak");
            Log.i(TAG, "str = "+content);

        }
        tts = new TextToSpeech(this,this);  // OnInitListener

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        Log.v(TAG, "Destroy Service");
        // TODO Auto-generated method stub
        if (tts != null) {

            tts=null;
        }
        serviceCallbacks = null;
        Log.i(TAG, "bound ="+bound);
        super.onDestroy();
    }

    private void say(String str) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        tts.setSpeechRate(0.75f);
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, map);

        //Utteranceprogresslistener used to identify whenn TTS is completed...
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                //speakig started
                Log.i(TAG, "started speaking ");
            }

            @Override
            public void onDone(String s) {
                //speaking stopped
                Log.v(TAG, "about to listen "+ s);

                if (serviceCallbacks != null) {
                    serviceCallbacks.doSomething();
                }
            }

            @Override
            public void onError(String s) {
                // there was an error
                Log.e("error","error:"+s);
            }

        });
    }



}


