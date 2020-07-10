package com.example.videodemo.audio_record_sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.videodemo.BasePermissionsActivity;
import com.example.videodemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miracle on 2020/7/9
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class AudioRecordActivity extends BasePermissionsActivity implements View.OnClickListener {
    private static final String TAG = AudioRecordActivity.class.getSimpleName();

    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    private static final int SAMPLE_RATE_INHZ = 44100;

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;


    private static final String pcmFileName = Environment.getExternalStorageDirectory() + "/test.pcm";
    private Button btnStartRecord, btnStopRecord;  //开始录音，结束录音
    private AudioRecord audioRecord;//声明AudioRecord对象
    private int recordBufSize = 0; //recordBuffer的大小
    private boolean isRecording;
    private Button btnConvert;//音频格式转换
    private Button btnPlay, btnStop; //播放 停止音频
    private AudioTrack audioTrack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        btnStartRecord = findViewById(R.id.btn_start_record);
        btnStartRecord.setOnClickListener(this);
        btnStopRecord = findViewById(R.id.btn_stop_record);
        btnStopRecord.setOnClickListener(this);
        btnConvert = findViewById(R.id.btn_convert);
        btnConvert.setOnClickListener(this);
        btnPlay = findViewById(R.id.btn_start_play);
        btnPlay.setOnClickListener(this);
        btnStop = findViewById(R.id.btn_stop_play);
        btnStop.setOnClickListener(this);
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_record:
                startRecord();
                break;
            case R.id.btn_stop_record:
                stopRecord();
                break;
            case R.id.btn_convert:
                PcmToWaUtils pcmToWaUtils = new PcmToWaUtils(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
                File pcmFile = new File(pcmFileName);
                File wavFile = new File(Environment.getExternalStorageDirectory() + "/test.wav");
                if (!wavFile.mkdirs()) {
                    Log.e(TAG, "wavFile Directory not created");
                }
                if (wavFile.exists()) {
                    wavFile.delete();
                }
                pcmToWaUtils.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());
                break;
            case R.id.btn_start_play:
                playInModeStream();
//                playInModeStatic();
                break;
            case R.id.btn_stop_play:
                stopPlay();
                break;
        }
    }


    /**
     * 开始录音
     */
    private void startRecord() {
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

        final byte data[] = new byte[minBufferSize];
        final File file = new File(pcmFileName);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        audioRecord.startRecording();
        isRecording = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream os = new FileOutputStream(file);
                    if (os != null) {
                        while (isRecording) {
                            int read = audioRecord.read(data, 0, minBufferSize);
                            // 如果读取音频数据没有出现错误，就将数据写入到文件
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                os.write(data);
                            }
                        }
                    }
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        isRecording = false;
        // 释放资源
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    /**
     * 播放，使用stream模式
     */
    private void playInModeStream() {
        /*
         * SAMPLE_RATE_INHZ 对应pcm音频的采样率
         * channelConfig 对应pcm音频的声道
         * AUDIO_FORMAT 对应pcm音频的格式
         * */
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioTrack = new AudioTrack(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    new AudioFormat.Builder()
                            .setEncoding(AUDIO_FORMAT)
                            .setChannelMask(channelConfig)
                            .build(),
                    minBufferSize, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
        }
        audioTrack.play();

        File file = new File(Environment.getExternalStorageDirectory() + "/test.pcm");
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] tempBuffer = new byte[minBufferSize];
                        while (fileInputStream.available() > 0) {
                            int readCount = fileInputStream.read(tempBuffer);
                            if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                                continue;
                            }
                            if (readCount != 0 && readCount != -1) {
                                audioTrack.write(tempBuffer, 0, readCount);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void playInModeStatic() {
        // static模式，需要将音频数据一次性write到AudioTrack的内部缓冲区
        new AsyncTask<Void, Void, byte[]>() {

            @Override
            protected byte[] doInBackground(Void... voids) {
                try {
                    File file = new File(Environment.getExternalStorageDirectory() + "/test.wav");
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] temp = new byte[1024];
                    int count;
                    while ((count = fileInputStream.read(temp)) != -1) {
                        outputStream.write(temp, 0, count);
                    }
                    return outputStream.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(byte[] audioData) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    audioTrack = new AudioTrack(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build(),
                            new AudioFormat.Builder().setSampleRate(22050)
                                    .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                    .build(),
                            audioData.length,
                            AudioTrack.MODE_STATIC,
                            AudioManager.AUDIO_SESSION_ID_GENERATE);
                }
                Log.d(TAG, "Writing audio data...");
                audioTrack.write(audioData, 0, audioData.length);
                Log.d(TAG, "Starting playback");
                audioTrack.play();
                Log.d(TAG, "Playing");
            }
        }.execute();
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (audioTrack != null) {
            Log.e(TAG, "Stop");
            audioTrack.stop();
            audioTrack.release();
        }
    }
}
