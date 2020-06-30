package com.example.videodemo.codec;

import android.media.MediaFormat;

/**
 * Created by Miracle on 2020/6/28
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public interface IDecoder extends Runnable {
    /**
     * 暂停解码
     */
    void pause();

    /**
     * 继续解码
     */
    void goOn();

    /**
     * 停止解码
     */
    void stop();

    /**
     * 是否正在解码
     *
     * @return
     */
    boolean isDecodeing();

    /**
     * 是否正在快进
     *
     * @return
     */
    boolean isSeeking();


    /**
     * 是否停止解码
     *
     * @return
     */
    boolean isStop();

    /**
     * 设置状态监听器
     */
    void setStateListener(IDecoderStateListener listener);

    /**
     * 获取视屏宽
     *
     * @return
     */
    int getWidth();

    /**
     * 获取视频高
     *
     * @return
     */
    int getHeight();

    /**
     * 获取视频时长
     *
     * @return
     */
    long getDuration();

    /**
     * 获取视频旋转角度
     *
     * @return
     */
    int getRotationAngle();

    /**
     * 获取音视频对应的格式参数
     *
     * @return
     */
    MediaFormat getMediaFormat();

    /**
     * 获取音视频对应的媒体轨道
     *
     * @return
     */
    int getTrack();

    /**
     * 获取解码的文件路径
     *
     * @return
     */
    String getFilePath();
}
