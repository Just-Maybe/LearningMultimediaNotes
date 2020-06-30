package com.example.videodemo.codec;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * Created by Miracle on 2020/6/28
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public interface IExtractor {
    /**
     * 获取音视频格式参数
     *
     * @return
     */
    MediaFormat getFormat();

    /**
     * 读取音视频数据
     *
     * @param byteBuffer
     * @return
     */
    int readBuffer(ByteBuffer byteBuffer);

    /**
     * 获取当前帧时间
     *
     * @return
     */
    long getCurrentTimestamp();

    /**
     * Seek到指定位置，并返回实际帧的时间戳
     *
     * @param pos
     * @return
     */
    long seek(long pos);

    void setStartPos(long pos);

    /**
     * 停止读取数据
     */
    void stop();
}
