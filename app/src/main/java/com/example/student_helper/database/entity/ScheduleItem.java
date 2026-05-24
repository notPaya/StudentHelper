package com.example.student_helper.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedule_items")
public class ScheduleItem {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String subject;
    public String room;
    public String professor;
    public int dayOfWeek; // 1=Pon, 2=Uto, 3=Sri, 4=Čet, 5=Pet
    public String startTime;
    public String endTime;

    public ScheduleItem(String subject, String room, String professor,
                        int dayOfWeek, String startTime, String endTime) {
        this.subject = subject;
        this.room = room;
        this.professor = professor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}