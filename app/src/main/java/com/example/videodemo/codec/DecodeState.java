package com.example.videodemo.codec;

/**
 * Created by Miracle on 2020/6/28
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public enum DecodeState {
    /**
     * 开始状态
     **/
    STATE,
    /**
     * 解码中
     **/
    DECODING,
    /**
     * 解码暂停
     **/
    PAUSE,
    /**
     * 正在快进
     **/
    SEEKING,
    /**
     * 解码完成
     **/
    FINISH,
    /**
     * 解码器释放
     **/
    STOP
}
