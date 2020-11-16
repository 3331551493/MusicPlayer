package com.example.musicplayer.bean;

import java.io.Serializable;

public class Music implements Serializable {

    private String name = null; //音乐名字
    private int path;  // 资源地址(R)
    private String author;

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
