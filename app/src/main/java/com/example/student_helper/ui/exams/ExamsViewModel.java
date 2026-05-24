package com.example.student_helper.ui.exams;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student_helper.database.AppDatabase;
import com.example.student_helper.database.entity.Exam;

import java.util.List;
import java.util.concurrent.Executors;

public class ExamsViewModel extends AndroidViewModel {

    private final AppDatabase db;

    public ExamsViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<Exam>> getExams() {
        return db.examDao().getAll();
    }

    public void addExam(Exam exam) {
        Executors.newSingleThreadExecutor().execute(() -> db.examDao().insert(exam));
    }

    public void deleteExam(Exam exam) {
        Executors.newSingleThreadExecutor().execute(() -> db.examDao().delete(exam));
    }
}