package com.example.student_helper.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.student_helper.database.entity.ScheduleItem;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM schedule_items WHERE dayOfWeek = :day ORDER BY startTime ASC")
    LiveData<List<ScheduleItem>> getByDay(int day);

    @Query("SELECT * FROM schedule_items WHERE dayOfWeek = :day ORDER BY startTime ASC")
    List<ScheduleItem> getByDaySync(int day);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScheduleItem item);

    @Delete
    void delete(ScheduleItem item);
}