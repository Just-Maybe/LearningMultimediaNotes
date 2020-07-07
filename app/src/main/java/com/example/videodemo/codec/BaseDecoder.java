package com.example.videodemo.codec;

import android.icu.text.MeasureFormat;
import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Miracle on 2020/6/28
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public abstract class BaseDecoder implements IDecoder {
    //-------------线程相关------------------------
    /**
     * 解码器是否在运行
     */
    private boolean mIsRunning = true;
    /**
     * 线程等待锁
     */
    private Object mLock = new Object();

    /**
     * 是否可以进入解码
     */
    private boolean mReadyForDecode = false;

    //-------------线程相关------------------------
    /**
     * 音视频解码器
     */
    protected MediaCodec mCodec;
    /**
     * 音频数据读取器
     */
    protected IExtractor mExtractor;

    /**
     * 解码输入缓存区
     */
    protected ByteBuffer[] mInputBuffers;

    protected ByteBuffer[] mOutputBuffers;
    /**
     * 解码数据信息
     */
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private DecodeState mState = DecodeState.STOP;

    protected IDecoderStateListener mStateListener;

    /**
     * 流数据是否结束
     */
    private boolean mIsEOS = false;

    protected int mVideoWidth = 0;

    protected int mVideoHeight = 0;

    String mFilePath;
    private long mDuration;
    private long mEndPos;

    public BaseDecoder(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    @Override
    public void pause() {
        mState = DecodeState.DECODING;
    }

    @Override
    public void goOn() {
        mState = DecodeState.DECODING;
        notifyDecode();
    }

    @Override
    public void stop() {
        mState = DecodeState.STOP;
        mIsRunning = false;
        notifyDecode();
    }

    @Override
    public boolean isDecodeing() {
        return false;
    }

    @Override
    public boolean isSeeking() {
        return false;
    }

    @Override
    public boolean isStop() {
        return false;
    }

    @Override
    public void setStateListener(IDecoderStateListener listener) {
        mStateListener = listener;
    }

    @Override
    public int getWidth() {
        return mVideoWidth;
    }

    @Override
    public int getHeight() {
        return mVideoHeight;
    }

    @Override
    public long getDuration() {
        return mDuration;
    }

    @Override
    public int getRotationAngle() {
        return 0;
    }

    @Override
    public MediaFormat getMediaFormat() {
        return mExtractor.getFormat();
    }

    @Override
    public int getTrack() {
        return 0;
    }

    @Override
    public String getFilePath() {
        return mFilePath;
    }

    @Override
    public void run() {
        mState = DecodeState.STATE;
        mStateListener.decoderPrepare(this);

        // 解码步骤 1： 初始化，并启动解码器
        if (!init()) return;

        while (mIsRunning) {
            if (mState != DecodeState.STATE &&
                    mState != DecodeState.DECODING &&
                    mState != DecodeState.SEEKING) {
                waitDecode();
            }
            if (!mIsRunning || mState == DecodeState.STOP) {
                mIsRunning = false;
                break;
            }

            //如果数据没有解码完毕,将数据推入解码器解码
            if (!mIsEOS) {
                // 解码步骤 2： 将数据压入解码器输入缓冲
                mIsEOS = pushBufferToDecoder();
            }
            //解码步骤 3： 将解码好的数据从缓冲区拉取出来
            int index = pullBufferFromDecoder();
            if (index >= 0) {
                //解码步骤 4：渲染
                render(mOutputBuffers, mBufferInfo);
                //解码步骤 5：释放输出缓冲
                mCodec.releaseOutputBuffer(index, true);
                if (mState == DecodeState.STATE) {
                    mState = DecodeState.PAUSE;
                }
            }
            //解码步骤 6：判断解码是否完成
            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                mState = DecodeState.FINISH;
                mStateListener.decoderFinish(this);
            }
        }
        doneDecode();
        //解码步骤 7：释放解码器
        release();
    }


    private boolean init() {
        //1.检查参数是否完整
        if (mFilePath.isEmpty() || !new File(mFilePath).exists()) {
            mStateListener.decoderError(this, "文件为空");
            return false;
        }
        //调用虚函数，检查子类参数是否完整
        if (!check()) return false;
        //2.初始化数据提取器
        mExtractor = initExtractor(mFilePath);
        if (mExtractor == null || mExtractor.getFormat() == null) return false;

        //3.初始化参数
        if (!initParams()) return false;

        //4.初始化渲染器
        if (!initRender()) return false;
        //5.初始化解码器
        if (!initCodec()) return false;
        return true;
    }

    protected abstract boolean check();

    protected abstract IExtractor initExtractor(String mFilePath);

    private boolean initParams() {
        try {
            MediaFormat format = mExtractor.getFormat();
            mDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000;
            if (mEndPos == 0L) mEndPos = mDuration;

            initSpecParams(mExtractor.getFormat());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private boolean initCodec() {
        try {
            //1.根据音视频编码格式初始化解码器
            String type = mExtractor.getFormat().getString(MediaFormat.KEY_MIME);
            mCodec = MediaCodec.createDecoderByType(type);
            //2.配置解码器
            if (!configCodec(mCodec, mExtractor.getFormat())) {
                waitDecode();
            }
            //3.启动解码器
            mCodec.start();
            //4.获取解码器缓冲区
            mInputBuffers = mCodec.getInputBuffers();
            mOutputBuffers = mCodec.getOutputBuffers();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 解码线程进入等待
     */
    private void waitDecode() {
        try {
            if (mState == DecodeState.PAUSE) {
                mStateListener.decoderPause(this);
            }
            synchronized (mLock) {
                mLock.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知解码线程继续运行
     */
    protected void notifyDecode() {
        synchronized (mLock) {
            mLock.notifyAll();
        }
        if (mState == DecodeState.DECODING) {
            mStateListener.decoderRunning(this);
        }
    }

    /**
     * 将数据压入解码器输入缓冲
     *
     * @return
     */
    private boolean pushBufferToDecoder() {
        int inputBufferIndex = mCodec.dequeueInputBuffer(2000);
        boolean isEndOfStream = false;
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mInputBuffers[inputBufferIndex];
            int sampleSize = mExtractor.readBuffer(inputBuffer);
            if (sampleSize < 0) {
                mCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isEndOfStream = true;
            } else {
                mCodec.queueInputBuffer(inputBufferIndex, 0,
                        sampleSize, mExtractor.getCurrentTimestamp(), 0);
            }
        }
        return isEndOfStream;
    }

    private int pullBufferFromDecoder() {
        // 查询是否有解码完成的数据，index >=0 时，表示数据有效，并且index为缓冲区索引
        int index = mCodec.dequeueInputBuffer(1000);
        while (index != 0) {
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //输出格式改变了
            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                //输入缓冲改变了
            } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                //没有可用数据，等会再来
                mOutputBuffers = mCodec.getOutputBuffers();
            } else {
                return index;
            }

        }
        return -1;
    }

    protected abstract boolean configCodec(MediaCodec mCodec, MediaFormat format);

    protected abstract void initSpecParams(MediaFormat format);

    protected abstract boolean initRender();

    abstract void render(ByteBuffer[] mOutputBuffers, MediaCodec.BufferInfo mBufferInfo);

    abstract void doneDecode();

    private void release() {
        try {
            mState = DecodeState.STOP;
            mIsEOS = false;
            mExtractor.stop();
            mCodec.stop();
            mCodec.release();
            mStateListener.decoderDestroy(this);
        } catch (Exception e) {
        }

    }
}
