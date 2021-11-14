package com.fanshuff.rma.yourdiary.entity;

public class AuthorInfo {

    private int id;

    private String authorName;

    private String birthday;

    public AuthorInfo(int id, String authorName, String birthday) {
        this.id = id;
        this.authorName = authorName;
        this.birthday = birthday;
    }

    public AuthorInfo(String authorName, String birthday) {
        this.authorName = authorName;
        this.birthday = birthday;
    }

    public AuthorInfo() {
    }

    public int getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
