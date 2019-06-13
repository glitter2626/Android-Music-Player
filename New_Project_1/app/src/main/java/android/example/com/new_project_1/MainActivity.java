package android.example.com.new_project_1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mRelative;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent speechRecognizerIntent;

    private String speechInput;

    private TextView tSong;
    private ImageView iCover;
    private Button bPrevious;
    private Button bPlay;
    private Button bNext;
    private Button bMode;

    private MediaPlayer mMediaPlayer;

    private int order;
    private ArrayList<File> ArrayMusic;
    private String name;

    // use for music seekbar
    private SeekBar seekBar;
    private TextView musicText;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // music seekbar setting
        seekBar = findViewById(R.id.seekBar);
        musicText = findViewById(R.id.musicTimeText);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mMediaPlayer != null && b){
                    mMediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    bPlay.setText("play");
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null) {
                    mMediaPlayer.start();
                    bPlay.setText("pause");
                }
            }
        });

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mMediaPlayer != null){
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    int minutes = mCurrentPosition / 60;
                    int seconds = mCurrentPosition % 60;
                    String time;
                    if(seconds < 10)
                        time = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
                    else
                        time = String.valueOf(minutes) + ":" + String.valueOf(seconds);
                    musicText.setText(time);
                }
                mHandler.postDelayed(this, 1000);
            }
        });



        mRelative = findViewById(R.id.parentLayout);

        tSong = findViewById(R.id.song);
        iCover = findViewById(R.id.albumCover);
        bPrevious = findViewById(R.id.btn_previous);
        bPlay = findViewById(R.id.btn_play);
        bNext = findViewById(R.id.btn_next);
        bMode = findViewById(R.id.btn_mode);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        ArrayMusic = (ArrayList) bundle.getParcelableArrayList("song_music");
        order = bundle.getInt("song_order");

        readMusicAndSetMediaPlayer();
        tSong.setSelected(true);

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                Toast.makeText(MainActivity.this, "begin", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(result != null) {
                    speechInput = result.get(0);

                    Toast.makeText(MainActivity.this, speechInput, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        mRelative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(speechRecognizerIntent);
                        speechInput = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        break;
                }

                return false;
            }
        });
    }


    public void modeChange(View view) {
        if(bMode.getText().toString().equals("mode_b")) {
            bMode.setText("mode_v");
            bPrevious.setVisibility(View.GONE);
            bPlay.setVisibility(View.GONE);
            bNext.setVisibility(View.GONE);
        }
        else if(bMode.getText().toString().equals("mode_v")) {
            bMode.setText("mode_b");
            bPrevious.setVisibility(View.VISIBLE);
            bPlay.setVisibility(View.VISIBLE);
            bNext.setVisibility(View.VISIBLE);
        }
    }

    public void playPauseClick(View view) {
       playAndPause();
    }

    public void playPreviousClick(View view) {
       playThePrevious();
    }

    public void playNextClick(View view) {
        playTheNext();
    }

    private void playAndPause() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            bPlay.setText("play");
        }
        else {
            mMediaPlayer.start();
            bPlay.setText("pause");

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playTheNext();
                }
            });
        }
    }

    private void playThePrevious() {
        order = order - 1;
        if(order < 0) {
            order = order + ArrayMusic.size();
        }
        //Toast.makeText(MainActivity.this, order, Toast.LENGTH_LONG).show();
        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mMediaPlayer.release();

        readMusicAndSetMediaPlayer();
    }

    private void playTheNext() {
        order = order + 1;
        if(order == ArrayMusic.size()) {
            order = order - ArrayMusic.size();
        }

        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mMediaPlayer.release();

        readMusicAndSetMediaPlayer();
    }

    private void readMusicAndSetMediaPlayer() {
        name = ArrayMusic.get(order).getName();
        Uri uri = Uri.parse(ArrayMusic.get(order).toString());
        tSong.setText(name);
        if(bPlay.getText().toString().equals("play")) {
            bPlay.setText("pause");
        }


        mMediaPlayer = MediaPlayer.create(MainActivity.this, uri);

        // music seekbar max time setting
        seekBar.setMax(mMediaPlayer.getDuration() / 1000);

        mMediaPlayer.start();



        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playTheNext();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mMediaPlayer != null)
            mMediaPlayer.release();

        super.onDestroy();

    }

}
