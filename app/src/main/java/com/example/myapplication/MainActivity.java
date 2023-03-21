package com.example.myapplication;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity  implements Runnable{
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SeekBar seekBar; 
    private SeekBar volumeBar;
    private boolean wasPlaying = false;
    private FloatingActionButton fabPlayPause;
    private TextView seekBarHint; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabPlayPause = findViewById(R.id.fabPlayPause);
        seekBarHint = findViewById(R.id.seekBarHint);
        seekBar = findViewById(R.id.seekBar);
        fabPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong(); 
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE); 
                int timeTrack = (int) Math.ceil(progress/1000f); 
                if (timeTrack < 10) {
                    seekBarHint.setText("00:0" + timeTrack);
                } else if (timeTrack < 60){
                    seekBarHint.setText("00:" + timeTrack);
                } else if (timeTrack >= 60) {
                    seekBarHint.setText("01:" + (timeTrack - 60));
                }

              
                double percentTrack = progress / (double) seekBar.getMax();       
                seekBarHint.setX(seekBar.getX() + Math.round(seekBar.getWidth()*percentTrack*0.92));
                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) { 
                    clearMediaPlayer();
                    fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                    MainActivity.this.seekBar.setProgress(0);
                }
            }
           
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.INVISIBLE); 
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress()); 
                }
            }
        });
    }

    
    public void playSong() {
        try { 
            if (mediaPlayer != null && mediaPlayer.isPlaying()) { 
                mediaPlayer.pause (); 
                mediaPlayer.seekTo (seekBar.getProgress ());
                seekBar.getProgress (); 
                wasPlaying = true;
                fabPlayPause.setImageDrawable (ContextCompat.getDrawable (MainActivity.this, android.R.drawable.ic_media_play));
            } else{
                fabPlayPause.setImageDrawable (ContextCompat.getDrawable (MainActivity.this, android.R.drawable.ic_media_pause));
                mediaPlayer.start();
                new Thread(this).start();
                wasPlaying = false; 
            }
            volumeBar=findViewById(R.id.volumeBar);
            volumeBar.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            float volumeNum= progress/100f;
                            mediaPlayer.setVolume(volumeNum, volumeNum );
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    }
            );



            if (!wasPlaying) {
                if (mediaPlayer == null) { 
                    mediaPlayer = new MediaPlayer(); 
                }
                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));
                AssetFileDescriptor descriptor = getAssets().openFd("Н.А.Римский-Корсаков - Полёт шмеля.mp3");
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mediaPlayer.prepare(); 
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start(); 
                new Thread(this).start(); 
            }

            wasPlaying = false; 

        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

 
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }

   
    private void clearMediaPlayer() {
        mediaPlayer.stop(); 
        mediaPlayer.release(); 
        mediaPlayer = null; 
    }
    @Override
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration(); 

      
        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {

                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition); 

        }
    }
}
