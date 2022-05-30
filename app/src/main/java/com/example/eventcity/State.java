package com.example.eventcity;

public class State {
    private String name; // название
    private long datePost;  // дата
    private String date;  // дата
    private String time;  // время
    private String description; // описание
    private String flagResource; // ресурс флага

    public State(String name,String description, String date, String time,String flag){
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.flagResource = flag;
    }
    public State(Long datePost, String name, String description, String date, String time,String flag){
        this.datePost = datePost;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.flagResource = flag;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Long getDatePost() {
        return this.datePost;
    }
    public void setDatePost(Long datePost) {
        this.datePost = datePost;
    }

    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getFlagResource() {
        return this.flagResource;
    }
    public void setFlagResource(String flagResource) {
        this.flagResource = flagResource;
    }
}
