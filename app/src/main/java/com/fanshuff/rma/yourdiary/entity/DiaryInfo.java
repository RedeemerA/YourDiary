package com.fanshuff.rma.yourdiary.entity;

public class DiaryInfo {
    //主键
    private int id ;
    //标题
    private String title;
    //作者
    private String author;
    //内容
    private String Content;
    //照片
    private byte[] photo;
    //日记创建时间
    private String time;

    public DiaryInfo() {
    }

    public DiaryInfo(int id, String title, String author, String content, byte[] photo, String time) {
        this.id = id;
        this.title = title;
        this.author = author;
        Content = content;
        this.photo = photo;
        this.time = time;
    }

    public DiaryInfo(String title, String author, String content, byte[] photo, String time) {
        this.title = title;
        this.author = author;
        Content = content;
        this.photo = photo;
        this.time = time;
    }

    public DiaryInfo(String title, String author, String content, String time) {
        this.title = title;
        this.author = author;
        Content = content;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return Content;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
