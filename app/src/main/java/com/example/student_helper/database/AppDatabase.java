package com.example.student_helper.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.student_helper.database.dao.ExamDao;
import com.example.student_helper.database.dao.NoteDao;
import com.example.student_helper.database.dao.ScheduleDao;
import com.example.student_helper.database.entity.Exam;
import com.example.student_helper.database.entity.Note;
import com.example.student_helper.database.entity.ScheduleItem;

@Database(
        entities = {ScheduleItem.class, Exam.class, Note.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ScheduleDao scheduleDao();
    public abstract ExamDao examDao();
    public abstract NoteDao noteDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "student_helper_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}