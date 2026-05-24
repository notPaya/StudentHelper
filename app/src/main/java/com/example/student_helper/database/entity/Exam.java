package com.example.student_helper.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exams")
public class Exam {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String subject;
    public long examDate;
    public String note;
    public boolean notificationEnabled;

    public Exam(String subject, long examDate, String note, boolean notificationEnabled) {
        this.subject = subject;
        this.examDate = examDate;
        this.note = note;
        this.notificationEnabled = notificationEnabled;
    }
}