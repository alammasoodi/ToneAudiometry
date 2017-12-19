package com.example.alam.toneaudiometrytest;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by alam on 18/12/17.
 */

public class ToneAudiometryFragment extends Fragment {
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_audiometry, container, false);
        AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        if(am.isWiredHeadsetOn()) {
            MediaPlayer AudioObj = new MediaPlayer();
            AudioObj.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {

                    mediaPlayer.setVolume(0, 0.1f);
                    mediaPlayer.start();

                }
            });

            AudioObj.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                AssetFileDescriptor afd = getActivity().getAssets().openFd("test2.mp3");
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
    }
