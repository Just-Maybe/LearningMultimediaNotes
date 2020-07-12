package com.example.videodemo.media_muxer;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.videodemo.BasePermissionsActivity;
import com.example.videodemo.R;

import java.io.IOException;
import java.security.Permission;

/**
 * Created by Miracle on 2020/7/11
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class H264Activity extends BasePermissionsActivity implements View.OnClickListener, SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = H264Activity.class.getSimpleName();
    Camera camera;
    SurfaceHolder surfaceHolder;
    int width = 1280;
    int height = 720;
    int framerate = 30;
    H264Encoder encoder;
    private SurfaceView surfaceView;
    private Button btnMuxer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h264);
        initView();
        checkSupportH264Codec();
    }

    private void initView() {
        btnMuxer = findViewById(R.id.btn_muxer);
        btnMuxer.setOnClickListener(this);
        surfaceView = findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    private void checkSupportH264Codec() {
        if (supportH264Codec()) {
            Toast.makeText(this, "support H264 hard codec", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "not support H264 hard codec", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 遍历支持的编码格式信息
     *
     * @return
     */
    private boolean supportH264Codec() {
        if (Build.VERSION.SDK_INT >= 18) {
            for (int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);
                String[] types = codecInfo.getSupportedTypes();
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase("video/avc")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters = camera.getParameters();
        parameters.set("orientation", "portrait");  //竖屏预览
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewSize(1280, 720);
        camera.setParameters(parameters);
        camera.setPreviewCallback(this);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder = new H264Encoder(width, height, framerate);
        encoder.startEncoder();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "enter surfaceChanged method");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera = null;
        }
        if (encoder != null) {
            encoder.stopEncoder();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (encoder != null) {
            encoder.putData(data);
        }
    }
}
