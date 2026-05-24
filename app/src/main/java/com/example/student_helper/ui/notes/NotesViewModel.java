package com.example.student_helper.ui.notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student_helper.database.AppDatabase;
import com.example.student_helper.database.entity.Note;

import java.util.List;
import java.util.concurrent.Executors;

public class NotesViewModel extends AndroidViewModel {

    private final AppDatabase db;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
    }

    public LiveData<List<Note>> getNotes() {
        return db.noteDao().getAll();
    }

    public void addNote(Note note) {
        Executors.newSingleThreadExecutor().execute(() -> db.noteDao().insert(note));
    }

    public void deleteNote(Note note) {
        Executors.newSingleThreadExecutor().execute(() -> db.noteDao().delete(note));
    }
}