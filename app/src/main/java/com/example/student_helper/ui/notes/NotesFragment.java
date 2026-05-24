package com.example.student_helper.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.student_helper.R;
import com.example.student_helper.database.entity.Note;
import com.example.student_helper.databinding.DialogAddNoteBinding;
import com.example.student_helper.databinding.FragmentNotesBinding;
import com.example.student_helper.utils.XPManager;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private FragmentNotesBinding binding;
    private NotesViewModel viewModel;
    private NotesAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        adapter = new NotesAdapter(new ArrayList<>(), note -> viewModel.deleteNote(note));
        binding.rvNotes.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvNotes.setAdapter(adapter);

        viewModel.getNotes().observe(getViewLifecycleOwner(), notes -> {
            adapter.updateItems(notes);
            binding.tvEmptyNotes.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvNotes.setVisibility(notes.isEmpty() ? View.GONE : View.VISIBLE);
        });

        binding.fabAddNote.setOnClickListener(v -> showAddNoteDialog());
    }

    private void showAddNoteDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        DialogAddNoteBinding db = DialogAddNoteBinding.inflate(getLayoutInflater());
        dialog.setContentView(db.getRoot());

        db.btnSaveNote.setOnClickListener(v -> {
            String title = db.etNoteTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Unesite naslov!", Toast.LENGTH_SHORT).show();
                return;
            }

            String color;
            int checkedId = db.colorGroup.getCheckedRadioButtonId();
            if      (checkedId == R.id.colorBlue)   color = "BLUE";
            else if (checkedId == R.id.colorGreen)  color = "GREEN";
            else if (checkedId == R.id.colorPink)   color = "PINK";
            else if (checkedId == R.id.colorPurple) color = "PURPLE";
            else                                    color = "YELLOW";

            Note note = new Note(
                    title,
                    db.etNoteContent.getText().toString().trim(),
                    color
            );
            viewModel.addNote(note);
            XPManager.addXP(requireContext(), 10);
            Toast.makeText(requireContext(), "+10 XP! 📓", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}