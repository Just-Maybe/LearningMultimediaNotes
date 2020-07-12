package com.example.videodemo.video_decoder;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by Miracle on 2020/7/7
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class VideoDecoderThread extends Thread {
    private static final String TAG = "VideoDecoder";
    private static final String VIDEO = "video/";
    private MediaExtractor mExtractor;
    private MediaCodec mDecoder;

    private boolean eosReceived;

    public boolean init(Surface surface, String filePath) {
        eosReceived = false;
        try {
            mExtractor = new MediaExtractor();
            mExtractor.setDataSource(filePath);
            for (int i = 0; i < mExtractor.getTrackCount(); i++) {
                MediaFormat format = mExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith(VIDEO)) {
                    mExtractor.selectTrack(i);
                    mDecoder = MediaCodec.createDecoderByType(mime);
                    mDecoder.configure(format, surface, null, 0);
                    mDecoder.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        ByteBuffer[] inputBuffers = mDecoder.getInputBuffers();

        boolean isInput = true;
        boolean first = false;
        long startWhen = 0;

        while (!eosReceived) {
            if (isInput) {
                int inputIndex = mDecoder.dequeueInputBuffer(10000);
                ByteBuffer inputBuffer = inputBuffers[inputIndex];

                int sampleSize = mExtractor.readSampleData(inputBuffer, 0);

                if (mExtractor.advance() && sampleSize > 0) {
                    mDecoder.queueInputBuffer(inputIndex, 0, sampleSize, mExtractor.getSampleTime(), 0);
                } else {
                    Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
                    mDecoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isInput = false;
                }
            }

            int outIndex = mDecoder.dequeueOutputBuffer(info, 10000);
            switch (outIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                    mDecoder.getOutputBuffers();
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : " + mDecoder.getOutputFormat());
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    Log.d(TAG, "INFO_TRY_AGAIN_LATER");
                    break;

                default:
                    if (!first) {
                        startWhen = System.currentTimeMillis();
                        first = true;
                    }
                    try {
                        long sleepTime = (info.presentationTimeUs / 1000) - (System.currentTimeMillis() - startWhen);

                        if (sleepTime > 0) {
                            Thread.sleep(sleepTime);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mDecoder.releaseOutputBuffer(outIndex, true);
                    break;
            }
            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                break;
            }
        }
        if (mDecoder != null) {
            mDecoder.stop();
            mDecoder.release();
        }
        if (mExtractor != null) {
            mExtractor.release();
        }
    }

    public void close() {
        eosReceived = true;
    }
}
