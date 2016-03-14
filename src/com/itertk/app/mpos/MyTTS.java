package com.itertk.app.mpos;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Administrator on 2014/10/31.
 * Tts 语音
 */
public class MyTTS {
    private TextToSpeech textToSpeech;
    boolean isSupport;

    public MyTTS(Context context){
        isSupport = false;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    if(textToSpeech.setLanguage(Locale.CHINESE) == TextToSpeech.LANG_AVAILABLE ){
                        isSupport = true;
                    }
                }
            }
        });
    }

    public void speak(String string){
        if(isSupport)
        textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null);
    }
}
