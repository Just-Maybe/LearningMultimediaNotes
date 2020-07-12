package com.example.videodemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videodemo.audio_record_sample.AudioRecordActivity;
import com.example.videodemo.camera.CameraActivity;
import com.example.videodemo.draw_picture.DrawPictureActivity;
import com.example.videodemo.home.Module;
import com.example.videodemo.home.ModuleAdapter;
import com.example.videodemo.media_muxer.H264Activity;
import com.example.videodemo.media_muxer.MediaMuxerActivity;
import com.example.videodemo.video_decoder.VideoDecoderActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private List<Module> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModuleAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
//        dataList.add(new Module("MediaCodec视频编解码", VideoDecoderActivity.class));
        dataList.add(new Module("通过三种方式绘制图片", DrawPictureActivity.class));
        dataList.add(new Module("使用 AudioRecord 采集音频PCM并保存到文件", AudioRecordActivity.class));
        dataList.add(new Module("使用 Camera API 采集视频数据", CameraActivity.class));
//        dataList.add(new Module("收集Camera数据，并转码为H264存储到文件", H264Activity.class));
//        dataList.add(new Module("音视频采集+混合，存储到文件", MediaMuxerActivity.class));

        adapter.update(dataList);
    }

}
