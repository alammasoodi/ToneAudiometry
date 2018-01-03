package com.example.alam.toneaudiometrytest;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by alam on 18/12/17.
 */

public class ToneAudiometryFragment extends Fragment {
    View v;
    Button leftEarButton,rightEarButton;
    int soundHertz = 250;
    int rightTone = 0,savedTone;
    AudioTrack tone;
    CountDownTimer mCountDownTimer;
    long milliLeft;
    float savedVolume[];
    float increaseVolume[];
    boolean isPaused = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_audiometry, container, false);
        AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        //if(am.isWiredHeadsetOn()) {

            startPlayingTones(soundHertz, rightTone);
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
    public void startPlayingTones(int hertz, final int rightEarTone){
        increaseVolume = new float[]{0f};
        tone = generateTone(hertz, 6000);
        if(rightEarTone == 0) {
            tone.setStereoVolume(increaseVolume[0], 0);
        }
        else
        {
            tone.setStereoVolume(0, increaseVolume[0]);

        }
        try {
            tone.play();
        }catch (IllegalStateException ie){
            ie.printStackTrace();
        }
        timerStart(6000,rightEarTone,increaseVolume);

       }

       public void timerStart(long timeLengthMilli, final int rightEarTone, final float increaseVolume[]) {
        mCountDownTimer = new CountDownTimer(timeLengthMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
                if(rightEarTone == 0) {
                    tone.setStereoVolume(increaseVolume[0], 0);
                }
                else
                {
                    tone.setStereoVolume(0, increaseVolume[0]);

                }
                increaseVolume[0] = increaseVolume[0] + 0.002f;
            }

            @Override
            public void onFinish() {
//                tone.flush();
//                tone.stop();
//                tone.release();
                if(rightEarTone == 0){
                    rightTone = 1;
                    startPlayingTones(soundHertz, rightTone);
                }
                else{
                    rightTone = 0;

                    if(soundHertz<4000) {
                        soundHertz = soundHertz *2;
                        startPlayingTones(soundHertz, rightTone);

                    }
                }



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

    @Override
    public void onPause(){
        super.onPause();
        mCountDownTimer.cancel();
        tone.pause();
        savedTone = rightTone;
        isPaused = true;

    }
    @Override
    public void onResume() {
        super.onResume();
        if(isPaused) {
            if(soundHertz<4000) {

                tone.play();
                timerStart(milliLeft, savedTone, increaseVolume);

            }
        }
    }
    }
