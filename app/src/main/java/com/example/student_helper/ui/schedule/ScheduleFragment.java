package com.example.student_helper.ui.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.student_helper.R;
import com.example.student_helper.database.entity.ScheduleItem;
import com.example.student_helper.databinding.DialogAddScheduleBinding;
import com.example.student_helper.databinding.FragmentScheduleBinding;
import com.example.student_helper.utils.XPManager;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel viewModel;
    private ScheduleAdapter adapter;
    private int selectedDay = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        adapter = new ScheduleAdapter(new ArrayList<>(), item -> viewModel.deleteItem(item));
        binding.rvSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSchedule.setAdapter(adapter);

        // Auto-odabir današnjeg dana
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (today) {
            case Calendar.MONDAY:    selectedDay = 1; break;
            case Calendar.TUESDAY:   selectedDay = 2; break;
            case Calendar.WEDNESDAY: selectedDay = 3; break;
            case Calendar.THURSDAY:  selectedDay = 4; break;
            case Calendar.FRIDAY:    selectedDay = 5; break;
            default:                 selectedDay = 1;
        }
        selectChipForDay(selectedDay);
        loadDay(selectedDay);

        binding.chipGroupDays.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if      (id == R.id.chipMon) selectedDay = 1;
            else if (id == R.id.chipTue) selectedDay = 2;
            else if (id == R.id.chipWed) selectedDay = 3;
            else if (id == R.id.chipThu) selectedDay = 4;
            else if (id == R.id.chipFri) selectedDay = 5;
            loadDay(selectedDay);
        });

        binding.fabAddSchedule.setOnClickListener(v -> showAddScheduleDialog());
    }

    private void loadDay(int day) {
        viewModel.getByDay(day).observe(getViewLifecycleOwner(), items -> {
            adapter.updateItems(items);
            binding.tvEmptySchedule.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvSchedule.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void selectChipForDay(int day) {
        int chipId;
        switch (day) {
            case 1:  chipId = R.id.chipMon; break;
            case 2:  chipId = R.id.chipTue; break;
            case 3:  chipId = R.id.chipWed; break;
            case 4:  chipId = R.id.chipThu; break;
            default: chipId = R.id.chipFri;
        }
        binding.chipGroupDays.check(chipId);
    }

    private void showAddScheduleDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        DialogAddScheduleBinding db = DialogAddScheduleBinding.inflate(getLayoutInflater());
        dialog.setContentView(db.getRoot());

        final String[] startTime = {"09:00"};
        final String[] endTime   = {"10:30"};

        db.etStartTime.setOnClickListener(v ->
                new TimePickerDialog(requireContext(), (tp, h, m) -> {
                    startTime[0] = String.format("%02d:%02d", h, m);
                    db.etStartTime.setText(startTime[0]);
                }, 9, 0, true).show());

        db.etEndTime.setOnClickListener(v ->
                new TimePickerDialog(requireContext(), (tp, h, m) -> {
                    endTime[0] = String.format("%02d:%02d", h, m);
                    db.etEndTime.setText(endTime[0]);
                }, 10, 30, true).show());

        db.btnSaveSchedule.setOnClickListener(v -> {
            String subject = db.etSubject.getText().toString().trim();
            if (subject.isEmpty()) {
                Toast.makeText(requireContext(), "Unesite naziv predmeta!", Toast.LENGTH_SHORT).show();
                return;
            }
            ScheduleItem item = new ScheduleItem(
                    subject,
                    db.etRoom.getText().toString().trim(),
                    db.etProfessor.getText().toString().trim(),
                    selectedDay,
                    startTime[0],
                    endTime[0]
            );
            viewModel.addItem(item);
            XPManager.addXP(requireContext(), 10);
            Toast.makeText(requireContext(), "+10 XP! 🎉", Toast.LENGTH_SHORT).show();
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