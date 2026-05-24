package com.example.student_helper.ui.schedule;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student_helper.database.AppDatabase;
import com.example.student_helper.database.entity.ScheduleItem;

import java.util.List;
import java.util.concurrent.Executors;

public class ScheduleViewModel extends AndroidViewModel {

    private final AppDatabase db;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<ScheduleItem>> getByDay(int day) {
        return db.scheduleDao().getByDay(day);
    }

    public void addItem(ScheduleItem item) {
        Executors.newSingleThreadExecutor().execute(() -> db.scheduleDao().insert(item));
    }

    public void deleteItem(ScheduleItem item) {
        Executors.newSingleThreadExecutor().execute(() -> db.scheduleDao().delete(item));
    }
}