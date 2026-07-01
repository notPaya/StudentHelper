package com.example.student_helper.ui.exams;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.student_helper.R;
import com.example.student_helper.database.entity.Exam;
import com.example.student_helper.utils.NotificationHelper;
import com.example.student_helper.utils.XPManager;
import com.example.student_helper.utils.LevelUpHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExamsFragment extends Fragment {

    private ExamsViewModel viewModel;
    private ExamsAdapter adapter;
    private RecyclerView rvExams;
    private TextView tvEmptyExams;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exams, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExamsViewModel.class);

        rvExams = view.findViewById(R.id.rvExams);
        tvEmptyExams = view.findViewById(R.id.tvEmptyExams);
        FloatingActionButton fabAddExam = view.findViewById(R.id.fabAddExam);

        adapter = new ExamsAdapter(new ArrayList<>(), exam -> viewModel.deleteExam(exam));
        rvExams.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvExams.setAdapter(adapter);

        viewModel.getExams().observe(getViewLifecycleOwner(), exams -> {
            adapter.updateItems(exams);
            tvEmptyExams.setVisibility(exams.isEmpty() ? View.VISIBLE : View.GONE);
            rvExams.setVisibility(exams.isEmpty() ? View.GONE : View.VISIBLE);
        });

        fabAddExam.setOnClickListener(v -> showAddExamDialog());
    }

    private void showAddExamDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exam, null);
        dialog.setContentView(dialogView);

        final long[] selectedDateMs = {0L};
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        TextInputEditText etExamSubject = dialogView.findViewById(R.id.etExamSubject);
        TextInputEditText etExamDate    = dialogView.findViewById(R.id.etExamDate);
        TextInputEditText etExamNote    = dialogView.findViewById(R.id.etExamNote);
        Switch switchNotification       = dialogView.findViewById(R.id.switchNotification);
        MaterialButton btnSaveExam      = dialogView.findViewById(R.id.btnSaveExam);

        etExamDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                cal.set(y, m, d, 9, 0, 0);
                selectedDateMs[0] = cal.getTimeInMillis();
                etExamDate.setText(sdf.format(new Date(selectedDateMs[0])));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveExam.setOnClickListener(v -> {
            String subject = etExamSubject.getText().toString().trim();
            if (subject.isEmpty()) {
                Toast.makeText(requireContext(), "Unesite naziv ispita!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDateMs[0] == 0L) {
                Toast.makeText(requireContext(), "Odaberite datum!", Toast.LENGTH_SHORT).show();
                return;
            }
            Exam exam = new Exam(
                    subject,
                    selectedDateMs[0],
                    etExamNote.getText().toString().trim(),
                    switchNotification.isChecked()
            );
            viewModel.addExam(exam);
            if (switchNotification.isChecked()) {
                NotificationHelper.scheduleExamNotification(
                        requireContext(), exam.id, subject, selectedDateMs[0]);
            }
            boolean leveledUp = XPManager.addXP(requireContext(), 15);
            Toast.makeText(requireContext(), "+15 XP!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            if (leveledUp) {
                LevelUpHelper.showLevelUpDialog(requireContext(), XPManager.getLevel(requireContext()));
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}