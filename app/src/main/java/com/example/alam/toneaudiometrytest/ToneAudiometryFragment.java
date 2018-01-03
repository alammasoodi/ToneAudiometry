package com.example.alam.toneaudiometrytest;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by alam on 18/12/17.
 */

public class ToneAudiometryFragment extends Fragment {
    View v;
    CountDownTimer mainCounter;
    Button leftEarButton,rightEarButton;
    int soundHertz = 250;
    int leftEar = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_audiometry, container, false);
        AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        //if(am.isWiredHeadsetOn()) {
       // for(int i=0 ;i<10 ;i++){
            startPlayingTones(soundHertz,leftEar);
        //}
//            mainCounter = new CountDownTimer(60000,1000) {
//                @Override
//                public void onTick(long l) {
//                }
//
//                @Override
//                public void onFinish() {
//
//                }
//            }.start();



       //} else{
//            Toast.makeText(getActivity(),"Plz insert headphone",Toast.LENGTH_SHORT).show();
//        }

        return v;
    }
    public void handleHeadphonesState(Context context){

    }
    public void startPlayingTones(int hertz, final int ear){
        final float[] increaseVolume = {0f};
        final AudioTrack tone = generateTone(hertz, 5000);
        if(ear == 0) {
            tone.setStereoVolume(increaseVolume[0], 0);
        }
        else
        {
            tone.setStereoVolume(0, increaseVolume[0]);

        }
        tone.play();

        CountDownTimer mCountDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(ear == 0) {
                    tone.setStereoVolume(increaseVolume[0], 0);
                }
                else
                {
                    tone.setStereoVolume(0, increaseVolume[0]);

                }
                increaseVolume[0] = increaseVolume[0] + 0.1f;
            }

            @Override
            public void onFinish() {
//                tone.flush();
//                tone.stop();
//                tone.release();
                if(ear == 0){
                    leftEar = 1;
                }
                else{
                    leftEar = 0;
                    if(soundHertz<8000) {
                        soundHertz = soundHertz *2;
                    }
                }
                startPlayingTones(soundHertz,leftEar);



            }
        };
        mCountDownTimer.start();
    }
    private AudioTrack generateTone(double freqHz, int durationMs)
    {
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        return track;
    }
    }
