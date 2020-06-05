package com.vapp.shakeme2.ui.home;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vapp.shakeme2.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SensorEventListener
    {
        public WebView gif_v;
        public SeekBar sb;
        public TextView sensitivity;
        public SensorManager sm;
        public Sensor accel;
        public Button dog_b;
        public Button liz_b;
        public float xAccel, yAccel, zAccel;
        public boolean initialized = false;
        public double NOISE = 5.0;
        public ArrayList<Double> previousNoise;
        public final int MAXNOISECOUNT = 3;
        public MediaPlayer mp;
        public boolean playing = false;
        public String fille =  "file:///android_asset/dog.gif";
        public int mfille = R.raw.batman_on_drugs;

        private HomeViewModel homeViewModel;

        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
                View root = inflater.inflate(R.layout.fragment_home, container, false);
                dog_b = root.findViewById(R.id.butt1);
                liz_b = root.findViewById(R.id.butt2);
                sb = root.findViewById(R.id.seekBar);
                sb.setProgress(50);
                sensitivity = root.findViewById(R.id.sctrl);

                dog_b.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                                mfille = R.raw.batman_on_drugs;
                                gif_v.loadUrl("file:///android_asset/dog.gif");
                                mp = MediaPlayer.create(getContext(),R.raw.batman_on_drugs);
                            }
                    });

                liz_b.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                                gif_v.loadUrl("file:///android_asset/liz.gif");
                                mp = MediaPlayer.create(getContext(),R.raw.liz_on_drugs);
                            }
                    });

                mp = MediaPlayer.create(getContext(),mfille);
                mp.setVolume(1.0f, 1.0f);
                mp.setLooping(true);

                gif_v = (WebView) root.findViewById(R.id.gifview);
                gif_v.loadUrl(fille);
                gif_v.setVerticalScrollBarEnabled(false);
                gif_v.setHorizontalScrollBarEnabled(false);
                gif_v.setVisibility(View.INVISIBLE);
                gif_v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

                previousNoise = new ArrayList<Double>();

                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                    {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                            {
                                sensitivity.setText("Sensitivity : " + progress + "");

                                NOISE = 10-(progress/20);
                            }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar)
                            {

                            }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar)
                            {

                            }
                    });

                return root;
            }

        @Override
        public void onResume()
            {
                super.onResume();
                sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
            }

        @Override
        public void onPause()
            {
                super.onPause();
                sm.unregisterListener(this);
            }

        private void playGif(){
            mp.start();
            gif_v.setVisibility(View.VISIBLE);
        }

        private void stopGif(){
            mp.pause();
            mp.seekTo(mp.getCurrentPosition());
            gif_v.setVisibility(View.INVISIBLE);
        }


        @Override
        public void onSensorChanged(SensorEvent event)
            {
                if (!initialized)
                {
                    xAccel = event.values[0];
                    yAccel = event.values[1];
                    zAccel = event.values[2];
                    initialized = true;
                }
                else {
                    float dAX = xAccel - event.values[0];
                    float dAY = yAccel - event.values[1];
                    float dAZ = zAccel - event.values[2];
                    double noiseVector = Math.sqrt(Math.pow(dAX,2)+Math.pow(dAY,2)+Math.pow(dAZ,2));

                    xAccel = event.values[0];
                    yAccel = event.values[1];
                    zAccel = event.values[2];
                    previousNoise.add(noiseVector);
                    while (previousNoise.size() > MAXNOISECOUNT)
                    {
                        previousNoise.remove(0);
                    }
                    if (previousNoise.size() == MAXNOISECOUNT && !playing)
                    {
                        double sum = 0;
                        for (int i = 0; i < MAXNOISECOUNT; i++) sum += previousNoise.get(i);
                        if (sum/MAXNOISECOUNT > NOISE){
                            playGif();
                            playing = true;
                        }
                    }
                    else if (playing)
                    {
                        double sum = 0;
                        for (int i = 0; i < MAXNOISECOUNT; i++) sum += previousNoise.get(i);
                        if (sum/MAXNOISECOUNT < NOISE)
                        {
                            if (mp.isPlaying())
                            {
                                stopGif();
                            }
                            playing = false;
                        }
                    }
                }
            }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }

    }

