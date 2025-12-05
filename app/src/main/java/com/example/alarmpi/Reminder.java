package com.example.alarmpi;

import java.util.Date;

public class Reminder {
    private String id;
    private String title;
    private String description;
    private Date dateTime;
    private boolean isActive;
    private boolean isSelected;

    // Конструктор
    public Reminder(String title, String description, Date dateTime) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.isActive = true;
        this.isSelected = false;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateTime() { return dateTime; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
