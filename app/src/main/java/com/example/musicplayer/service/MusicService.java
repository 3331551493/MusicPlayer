package com.example.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.musicplayer.R;
import com.example.musicplayer.bean.Music;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private MediaPlayer mPlayer;
    private List<Music> musicList;
    private int currentIndex = 0;

    /**
     * 获取音乐数据
     *
     * @return
     */
    private void getMusicData() {
        musicList = new ArrayList<>();
        Music music1 = new Music();
        Music music2 = new Music();
        Music music3 = new Music();
        Music music4 = new Music();
        music1.setName("病名为爱");
        music1.setPath(R.raw.demo1);
        music1.setAuthor("镜音双子");
        music2.setName("黑暗骑士");
        music2.setPath(R.raw.demo2);
        music2.setAuthor("林俊杰&五月天");
        music3.setName("奇迹再现");
        music3.setPath(R.raw.demo3);
        music3.setAuthor("毛毛");
        music4.setName("不朽之罪");
        music4.setPath(R.raw.demo4);
        music4.setAuthor("双笙");
        musicList.add(music1);
        musicList.add(music2);
        musicList.add(music3);
        musicList.add(music4);
    }


    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        getMusicData();
        mPlayer = MediaPlayer.create(MusicService.this, musicList.get(currentIndex).getPath());
        Log.d(TAG, "Service 已经启动！");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }


    public class MusicBinder extends Binder implements MediaPlayer.OnCompletionListener {
        public static final int LOOP = 1;       //列表循环
        public static final int CYCLIC = 2;     //单曲循环
        public static final int RANDOM = 3;     //随机播放


        private int mode = CYCLIC;

        public void setMode(int mode) {
            this.mode = mode;
        }

        public List<Music> getMusicList() {
            return musicList;
        }

        public Music getCurrentMusic() {
            return musicList.get(currentIndex);
        }


        /**
         * 判断当前歌曲是否正在播放
         *
         * @return
         */
        public boolean isPlaying() {
            return mPlayer.isPlaying();
        }

        /**
         * 播放或暂停歌曲
         */
        public void play() {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
        }

        /**
         * 获取歌曲的长度，单位为毫秒
         *
         * @return
         */
        public int getDuration() {
            return mPlayer.getDuration();
        }

        /**
         * 获取歌曲当前的播放进度，单位为毫秒
         *
         * @return
         */
        public int getCurrentPosition() {
            return mPlayer.getCurrentPosition();
        }

        /**
         * 设置歌曲的播放进度
         *
         * @param mesc
         */
        public void seekTo(int mesc) {
            mPlayer.seekTo(mesc);
        }

        //下一首
        public void next() {
            mPlayer.release();
            currentIndex = ++currentIndex % musicList.size();
            Music curMusic = musicList.get(currentIndex);
            mPlayer = MediaPlayer.create(MusicService.this, curMusic.getPath());
        }

        public void pre() {
            mPlayer.release();
            currentIndex = --currentIndex % musicList.size();
            if (currentIndex < 0) {
                currentIndex = musicList.size() - 1;
            }
            Music curMusic = musicList.get(currentIndex);
            mPlayer = MediaPlayer.create(MusicService.this, curMusic.getPath());
        }

        //设置播放模式
        @Override
        public void onCompletion(MediaPlayer mp) {
            switch (mode) {
                case CYCLIC:
                    next();
                    break;
                case LOOP:
                    currentIndex--;
                    next();
                    break;
                case RANDOM:
                    Random random = new Random();       //建立随机数
                    currentIndex = random.nextInt(musicList.size() );
                    play();
            }
        }
    }

}
