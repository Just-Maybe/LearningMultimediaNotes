package com.example.videodemo.codec;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MMExtractor {
    private String path;
    /**
     * 音视频分离器
     **/
    private MediaExtractor mExtractor;

    /**
     * 音频通道索引
     */
    private int mAudioTrack = -1;

    /**
     * 视频通道索引
     */
    private int mVideoTrack = -1;

    /**
     * 当前帧时间戳
     */
    private long mCurSampleTime = 0;

    /**
     * 开始解码时间点
     */
    private long mStartPos = 0;

    public MMExtractor(String path) {
        this.path = path;
        mExtractor = new MediaExtractor();
        try {
            mExtractor.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视频格式参数
     *
     * @return
     */
    public MediaFormat getVideoFormat() {
        //2.1获取视频多媒体格式
        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat mediaFormat = mExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                mVideoTrack = i;
                break;
            }
        }
        if (mVideoTrack >= 0) {
            return mExtractor.getTrackFormat(mVideoTrack);
        }
        return null;
    }

    /**
     * 获取音频格式参数
     *
     * @return
     */
    public MediaFormat getAudioFormat() {
        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat mediaFormat = mExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                mAudioTrack = i;

            }
        }
        if (mAudioTrack > 0) {
            return mExtractor.getTrackFormat(mAudioTrack);
        }
        return null;
    }

    /**
     * 读取视频数据
     *
     * @param byteBuffer
     * @return
     */
    public int readBuffer(ByteBuffer byteBuffer) {
        // 3.提取数据
        byteBuffer.clear();
        selectSourceTrack();
        int readSampleCount = mExtractor.readSampleData(byteBuffer, 0);
        if (readSampleCount < 0) {
            return -1;
        }
        mCurSampleTime = mExtractor.getSampleTime();
        mExtractor.advance();
        return readSampleCount;
    }

    /**
     * 选择通道
     */
    private void selectSourceTrack() {
        if (mVideoTrack >= 0) {
            mExtractor.selectTrack(mVideoTrack);
        } else if (mAudioTrack >= 0) {
            mExtractor.selectTrack(mAudioTrack);
        }
    }

    /**
     * Seek到指定位置，并返回实际帧的时间戳
     *
     * @param pos
     * @return
     */
    public long seek(long pos) {
        mExtractor.seekTo(pos, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        return mExtractor.getSampleTime();
    }

    /**
     * 停止读取
     */
    public void stop(){

    }
}
