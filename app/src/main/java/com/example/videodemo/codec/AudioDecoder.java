package com.example.videodemo.codec;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * Created by Miracle on 2020/6/30
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class AudioDecoder extends BaseDecoder {
    /**
     * 采样率
     */
    private int mSampleRate = -1;

    /**
     * 声音通道数量
     */
    public int mChannels = 1;
    /**
     * PCM采样位数
     */
    private int mPCMEncodeBit = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 音频播放器
     */
    private AudioTrack mAudioTrack;

    /**
     * 音频数据缓存
     */
    private int[] mAudioOutTempBuf;

    public AudioDecoder(String mFilePath) {
        super(mFilePath);
    }

    @Override
    protected boolean check() {
        return true;
    }

    @Override
    protected IExtractor initExtractor(String mFilePath) {
        return new AudioExtractor(mFilePath);
    }

    @Override
    protected boolean configCodec(MediaCodec mCodec, MediaFormat format) {
        mCodec.configure(format, null, null, 0);
        return true;
    }

    @Override
    protected void initSpecParams(MediaFormat format) {
        try {
            mChannels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);

            mPCMEncodeBit = format.containsKey(MediaFormat.KEY_PCM_ENCODING) ?
                    format.getInteger(MediaFormat.KEY_PCM_ENCODING) : AudioFormat.ENCODING_PCM_16BIT;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean initRender() {
        int channel = mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
        //获取最小缓冲区
        int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, channel, mPCMEncodeBit);

        mAudioOutTempBuf = new int[minBufferSize / 2];

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,//播放类型：音乐
                mSampleRate, //采样率
                channel, //通道
                mPCMEncodeBit,//采样位数
                minBufferSize,//缓冲区大小
                AudioTrack.MODE_STREAM //播放模式：数据流动态写入，另一种是一次性写入
        );
        mAudioTrack.play();
        return false;
    }

    @Override
    void render(ByteBuffer[] mOutputBuffers, MediaCodec.BufferInfo mBufferInfo) {
        if (mAudioOutTempBuf.length < mBufferInfo.size / 2) {
            mAudioOutTempBuf = new int[mBufferInfo.size / 2];
        }
//        mOutputBuffers
    }

    @Override
    void doneDecode() {
        mAudioTrack.stop();
        mAudioTrack.release();
    }
}
