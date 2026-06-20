package com.example.student_helper.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.student_helper.database.AppDatabase;
import com.example.student_helper.database.entity.Exam;
import com.example.student_helper.database.entity.ScheduleItem;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final MutableLiveData<List<ScheduleItem>> todayClasses = new MutableLiveData<>();
    private final MutableLiveData<Exam> nextExam = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<ScheduleItem>> getTodayClasses() { return todayClasses; }
    public LiveData<Exam> getNextExam() { return nextExam; }

    public void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            int day;
            switch (dow) {
                case Calendar.MONDAY:    day = 1; break;
                case Calendar.TUESDAY:   day = 2; break;
                case Calendar.WEDNESDAY: day = 3; break;
                case Calendar.THURSDAY:  day = 4; break;
                case Calendar.FRIDAY:    day = 5; break;
                case Calendar.SATURDAY:  day = 6; break;
                case Calendar.SUNDAY:    day = 7; break;
                default:                 day = 1;
            }
            todayClasses.postValue(db.scheduleDao().getByDaySync(day));

            List<Exam> exams = db.examDao().getAllSync();
            long now = System.currentTimeMillis();
            Exam next = null;
            for (Exam e : exams) {
                if (e.examDate > now) {
                    if (next == null || e.examDate < next.examDate) {
                        next = e;
                    }
                }
            }
            nextExam.postValue(next);
        });
    }
}