package com.example.musicplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MusicListAdapter;
import com.example.musicplayer.bean.Music;
import com.example.musicplayer.service.MusicService;


public class ServiceMusicActivity extends AppCompatActivity {
    private static final String TAG = "ServiceMusicActivity";
    private static final int UPDATE_PROGRESS = 1;

    private ImageView iv_music_play;
    private ImageView iv_music_next;
    private ImageView iv_music_pre;
    private TextView tv_music_name, tv_author, local_music;
    private SeekBar musicPlayBar;
    private RecyclerView recyclerView;
    private TextView tv_play_mode;

    private MusicService.MusicBinder musicBinder;
    private MusicConnection musicConnection;
    private MHandler mHandler;

    private Music curMusic;  // 当前播放器
    private int curMode = MusicService.MusicBinder.CYCLIC;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MHandler();
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        musicConnection = new MusicConnection();
        startService(musicServiceIntent);
        bindService(musicServiceIntent, musicConnection, BIND_AUTO_CREATE);
        init();
    }


    // 播放音乐
    public void play() {
        curMusic = musicBinder.getCurrentMusic();
        // 设置进度条的最大值
        musicPlayBar.setMax(musicBinder.getDuration());
        // 设置进度条的进度
        musicPlayBar.setProgress(musicBinder.getCurrentPosition());
        musicBinder.play();
        Log.i(TAG, "in play() method start updateProgress!");
        musicChanged();
        tv_music_name.setText(curMusic.getName());
        tv_author.setText(curMusic.getAuthor());
        updateProgress();
    }

    private void musicChanged() {
        // 改变当前歌曲的名字和作者
        if (musicBinder.isPlaying()) {
            // 显示暂停图标
            iv_music_play.setBackgroundResource(R.drawable.pause);
        } else {
            iv_music_play.setBackgroundResource(R.drawable.music_play);
            updateProgress();
        }
    }

        //初始化
    private void init() {
        local_music = (TextView) findViewById(R.id.local_music);
        local_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceMusicActivity.this, LocalMusicActivity.class);
                startActivity(intent);
            }
        });
        musicPlayBar = (SeekBar) findViewById(R.id.sb_music);
        tv_play_mode = (TextView) findViewById(R.id.tv_play_mode);
        tv_play_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (curMode) {
                    case MusicService.MusicBinder.CYCLIC:
                        // 设置为单曲循环
                        curMode = MusicService.MusicBinder.LOOP;
                        tv_play_mode.setText("单曲循环");
                        break;
                    case MusicService.MusicBinder.LOOP:
                        // 随机播放
                        curMode = MusicService.MusicBinder.RANDOM;
                        tv_play_mode.setText("随机播放");
                        break;
                    case MusicService.MusicBinder.RANDOM:
                        // 列表循环
                        curMode = MusicService.MusicBinder.CYCLIC;
                        tv_play_mode.setText("列表循环");
                        break;
                }
                musicBinder.setMode(curMode);
            }
        });
        iv_music_play = (ImageView) findViewById(R.id.iv_music_play);
        iv_music_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        iv_music_pre = (ImageView) findViewById(R.id.iv_music_pre);
        iv_music_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicBinder.pre();
                play();
            }
        });
        iv_music_next = (ImageView) findViewById(R.id.iv_music_next);
        iv_music_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicBinder.next();
                play();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.music_list);
        // 构建RecycleView
        recyclerView.setLayoutManager(new LinearLayoutManager(ServiceMusicActivity.this));
        musicPlayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicBinder.seekTo(progress);
            }

            /**
             * 开始触摸进度条
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * 停止触摸进度条
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_music_name = (TextView) findViewById(R.id.tv_music_name);
    }

    private class MusicConnection implements ServiceConnection {

        /**
         * 服务启动后的回调
         *
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
            recyclerView.setAdapter(new MusicListAdapter(musicBinder.getMusicList()));
        }

        /**
         * 服务结束后的回调
         *
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }

    /**
     * 进入界面后开始更新进度条
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (musicBinder != null) {
            Log.i(TAG, "start updateProgress!");
            updateProgress();
        }
    }

    /**
     * 退出后解除Service的绑定
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }

    /**
     * 停止更新进度条的进度
     */
    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    class MHandler extends Handler {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    updateProgress();
                    break;
            }
        }
    }

    /**
     * 更新进度条
     */
    public void updateProgress() {
        int currentPosition = musicBinder.getCurrentPosition();
        musicPlayBar.setProgress(currentPosition);
        mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
    }
}
