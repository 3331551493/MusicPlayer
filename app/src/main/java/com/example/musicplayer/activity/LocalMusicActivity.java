package com.example.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;

import java.io.IOException;

public class LocalMusicActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LocalMusicActivity";
    private Button get_music, start, pause, stop;
    private static final int CHOOSE_MUSIC = 1;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String musicPath;
    private TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalMusicActivity.this.finish();
            }
        });
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        get_music = (Button) findViewById(R.id.get_music);
        get_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(LocalMusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LocalMusicActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("audio/*");
                    startActivityForResult(intent, CHOOSE_MUSIC);
                }
            }
        });
    }

    //申请读取权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LocalMusicActivity.this, "申请权限成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LocalMusicActivity.this, "申请权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //获取本地音乐
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_MUSIC:
                if (resultCode == RESULT_OK) {
                    handlerMusic(data);
                }
                break;
        }
    }

    //处理上传的本地文件
    @TargetApi(19)
    public void handlerMusic(Intent data) {
        musicPath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document id类型的Uri，则通过document id进行处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Audio.Media._ID + "=" + id;
                musicPath = getMusicPath(uri, selection);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            musicPath = getMusicPath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            musicPath = uri.getPath();
        }
        initMediaPlayer(musicPath);
    }

    //获取文件位置
    private String getMusicPath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public void initMediaPlayer(String path) {
        if (path != null) {
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                break;
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case R.id.stop:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                    initMediaPlayer(musicPath);
                }
                break;
        }
    }
}
