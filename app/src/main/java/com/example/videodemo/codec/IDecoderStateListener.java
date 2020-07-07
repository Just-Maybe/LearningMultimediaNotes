package com.example.videodemo.codec;

/**
 * Created by Miracle on 2020/6/28
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public interface IDecoderStateListener {
    void decoderPrepare(BaseDecoder decoder);


    void decoderFinish(BaseDecoder decoder);

    void decoderPause(BaseDecoder decoder);

    void decoderRunning(BaseDecoder decoder);

    void decoderError(BaseDecoder decoder, String errorMsg);

    void decoderDestroy(BaseDecoder decoder);
}
