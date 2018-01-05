package com.example.alam.toneaudiometrytest;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.UiAutomation;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by alam on 18/12/17.
 */

public class ToneAudiometryFragment extends Fragment implements View.OnClickListener {
    View v;
    Button leftEarButton,rightEarButton;
    int soundHertz = 250;
    int rightTone = 0,savedTone;
    AudioTrack tone;
    CountDownTimer mCountDownTimer;
    ObjectAnimator progressAnimator;
    long milliLeft;
    float savedVolume[];
    float increaseVolume[];
    ProgressBar progressBar;
    boolean isPaused = false;
    int leftCounts = 0,rightCounts = 0;
    boolean isButtonClicked = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_audiometry, container, false);
        AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        leftEarButton = (Button) v.findViewById(R.id.left_ear_button);
        rightEarButton = (Button) v.findViewById(R.id.right_ear_button);
        leftEarButton.setOnClickListener(this);
        rightEarButton.setOnClickListener(this);
        progressBar = (ProgressBar)v.findViewById(R.id.progressbar);
        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.buttonColor), android.graphics.PorterDuff.Mode.SRC_IN);
        progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), 500);
        progressAnimator.setDuration(60000);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
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
            rightEarButton.setEnabled(false);
        }
        else
        {
            tone.setStereoVolume(0, increaseVolume[0]);
            leftEarButton.setEnabled(false);

        }
        try {
            tone.play();
        }catch (IllegalStateException ie){
            ie.printStackTrace();
        }
        timerStart(6000,rightEarTone,increaseVolume);

       }

       public void timerStart(final long timeLengthMilli, final int rightEarTone, final float increaseVolume[]) {
        mCountDownTimer = new CountDownTimer(timeLengthMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
                if(rightEarTone == 0) {
                    tone.setStereoVolume(increaseVolume[0], 0);
                    rightEarButton.setEnabled(false);


                }
                else
                {
                    tone.setStereoVolume(0, increaseVolume[0]);
                    leftEarButton.setEnabled(false);


                }
                increaseVolume[0] = increaseVolume[0] + 0.002f;
            }

            @Override
            public void onFinish() {
                if(rightEarTone == 0){
                    rightTone = 1;
                    startPlayingTones(soundHertz, rightTone);
                    rightEarButton.setEnabled(true);
                }
                else{
                    rightTone = 0;
                    leftEarButton.setEnabled(true);
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
        try {
            tone.pause();
        }
        catch (IllegalStateException ie){
            ie.printStackTrace();
        }
        savedTone = rightTone;
        isPaused = true;

    }
    @Override
    public void onResume() {
        super.onResume();
        if(isPaused) {
            if(soundHertz<4000) {
                try {
                    tone.play();
                }
                catch (IllegalStateException ie){
                    ie.printStackTrace();
                }
                timerStart(milliLeft, savedTone, increaseVolume);


            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.left_ear_button:
                playNext(0);
                rightEarButton.setOnClickListener(this);
                Toast.makeText(getActivity(),"you listened to the sound of "+soundHertz+" Hz from left ear",Toast.LENGTH_SHORT).show();
                break;
            case R.id.right_ear_button:
                playNext(1);
                leftEarButton.setOnClickListener(this);
                Toast.makeText(getActivity(),"you listened to the sound of "+soundHertz+" Hz from right ear",Toast.LENGTH_SHORT).show();

                break;
        }
    }

    public void playNext(int nextEar){
        //progressBar.setProgress(progressBar.getProgress()+25);
        mCountDownTimer.cancel();
        try {
            tone.stop();
            tone.release();
        }catch (IllegalStateException ie){
            ie.printStackTrace();
        }
        float[] resetVolume = new float[]{0f};
        timerStart(500,nextEar,resetVolume);

    }
}
