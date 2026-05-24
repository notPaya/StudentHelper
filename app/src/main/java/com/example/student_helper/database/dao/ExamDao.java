package com.example.student_helper.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.student_helper.database.entity.Exam;

import java.util.List;

@Dao
public interface ExamDao {

    @Query("SELECT * FROM exams ORDER BY examDate ASC")
    LiveData<List<Exam>> getAll();

    @Query("SELECT * FROM exams ORDER BY examDate ASC")
    List<Exam> getAllSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Exam exam);

    @Delete
    void delete(Exam exam);
}