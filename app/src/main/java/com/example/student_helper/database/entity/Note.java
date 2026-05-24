package com.example.student_helper.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String content;
    public String color;
    public long createdAt;

    public Note(String title, String content, String color) {
        this.title = title;
        this.content = content;
        this.color = color;
        this.createdAt = System.currentTimeMillis();
    }
}