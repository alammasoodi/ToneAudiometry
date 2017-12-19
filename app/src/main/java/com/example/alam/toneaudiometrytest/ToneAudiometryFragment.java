package com.example.alam.toneaudiometrytest;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    Button leftEarButton,rightEarButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_audiometry, container, false);
        AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        if(am.isWiredHeadsetOn()) {
            MediaPlayer AudioObj = new MediaPlayer();
            AudioObj.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    AudioTrack tone = generateTone(200, 5000);
                    tone.play();
//                    mediaPlayer.setVolume(0, 0.1f);
//                    mediaPlayer.start();

                }
            });

            AudioObj.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                AssetFileDescriptor afd = getActivity().getAssets().openFd("beep-11.mp3");
                AudioObj.setDataSource(afd.getFileDescriptor());
            }catch (IOException e){}
            AudioObj.prepareAsync();
        } else{
            Toast.makeText(getActivity(),"Plz insert headphone",Toast.LENGTH_SHORT).show();
        }

        return v;
    }
    public void handleHeadphonesState(Context context){

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
