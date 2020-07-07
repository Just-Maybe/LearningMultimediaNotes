package com.example.videodemo.codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.nio.ByteBuffer;

/**
 * Created by Miracle on 2020/6/30
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class VideoDecoder extends BaseDecoder {
    private static final String TAG = "VideoDecoder";
    private SurfaceView mSurfaceView;
    private Surface mSurface;


    public VideoDecoder(String mFilePath, SurfaceView sfv, Surface surface) {
        super(mFilePath);
        this.mSurfaceView = sfv;
        this.mSurface = surface;
    }

    @Override
    protected boolean check() {
        if (mSurfaceView == null && mSurface == null) {
            Log.w(TAG, "SurfaceView和Surface都为空，至少需要一个不为空");
            mStateListener.decoderError(this, "显示器为空");
            return false;
        }
        return true;
    }

    @Override
    protected IExtractor initExtractor(String mFilePath) {
        return new VideoExtractor(mFilePath);
    }

    @Override
    protected boolean configCodec(final MediaCodec mCodec, final MediaFormat format) {
        if (mSurface != null) {
            mCodec.configure(format, mSurface, null, 0);
            notifyDecode();
        } else {
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback2() {
                @Override
                public void surfaceRedrawNeeded(SurfaceHolder holder) {

                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mSurface = holder.getSurface();
                    configCodec(mCodec, format);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
            return false;
        }
        return true;
    }

    @Override
    protected void initSpecParams(MediaFormat format) {

    }

    @Override
    protected boolean initRender() {
        return true;
    }

    @Override
    void render(ByteBuffer[] mOutputBuffers, MediaCodec.BufferInfo mBufferInfo) {

    }

    @Override
    void doneDecode() {

    }
}
