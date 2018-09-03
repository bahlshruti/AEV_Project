package com.example.shruti.speech_to_text;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.SpeechRecognizer;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {
    /** Called when the activity is first created. */

    private TextToSpeech tts;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    private static final int REQUEST_RECORD_PERMISSION = 100;

    String text;
    boolean flag=false;
    String response="yes";

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speech!=null)
        {
            speech.destroy();
            speech=null;
        }

    }

        @Override
        public void onInit(int status) {

            if (status == TextToSpeech.SUCCESS) {
                Log.e("as","IN Success");
                int result = tts.setLanguage(Locale.ENGLISH);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");

                }
                else {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {

                        }

                        @Override
                        public void onDone(String s) {
                            startVoiceInput();
                        }

                        @Override
                        public void onError(String s) {
                            Log.e("error","error:"+s);
                        }
                    });
                    }
            } else {
                Log.e("TTS", "Initilization Failed!");
            }

        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView image=(ImageView) findViewById(R.id.imageView);
        text="welcome to AEV! do you want to turn on voice mode?";
        tts = new TextToSpeech(this, this);
    }

    private void startSpeechRecogniser() {
        if (speech==null) {

            speech = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            speech.setRecognitionListener(new listener());
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        }
    }

    public void startVoiceInput()
    {
        Log.e("Per","Permissions");
        ActivityCompat.requestPermissions
                (MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
    }

    public void listen()
    {
        Log.i("listening", "listening");
        startSpeechRecogniser();
        speech.startListening(recognizerIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 try{

                     listen();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(MainActivity.this, "unable to listen", Toast
                                .LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    class listener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.i("", "onreadyforspeech ");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i("", "onbegofspeech ");
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            Log.i("", "onendofspeech ");
        }

        @Override
        public void onError(int i) {
            String errorMessage = getErrorText(i);
            Toast.makeText(MainActivity.this, errorMessage, Toast
                    .LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {

            ArrayList<String> Result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.i("onresult", "onResults: " +Result);
            Toast.makeText(MainActivity.this, Result.get(0),Toast.LENGTH_SHORT).show();

            if (Result.get(0).equals("yes") || Result.get(0).equals("no")) {

                if (!flag) {
                    response = Result.get(0);
                    confirmation(Result);
                }
                else if (Result.get(0).equals("yes") && response.equals("yes")) {
                    Intent intent = new Intent(MainActivity.this, CommandActivity.class);
                    startActivity(intent);

                } else if ((Result.get(0).equals("no") && response.equals("no")) || (Result.get(0).equals("no") && response.equals("yes"))) {
                    flag = false;
                    text = "Do you want to turn on voice mode?";
                    tts = new TextToSpeech(MainActivity.this, MainActivity.this);

                } else {
                    // result="yes"; response="no"...
                    speech.stopListening();
                    // exit the application...
                    onDestroy();
                    finish();
                }
            } else {
                flag = false;
                text = "Sorry ! please repeat again !";
                tts = new TextToSpeech(MainActivity.this, MainActivity.this);

            }

        }

        public void confirmation(ArrayList Result)
        {
            flag=true;
            text="Did u say"+Result;
            tts = new TextToSpeech(MainActivity.this,MainActivity.this);

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    }
        public static String getErrorText(int errorCode) {
            String message;
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //No recognition result matched
                    message = "No match";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "error from server";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }
            return message;
        }

}

