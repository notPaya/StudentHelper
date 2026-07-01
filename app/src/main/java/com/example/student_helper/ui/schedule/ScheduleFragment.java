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
import com.google.android.material.chip.Chip;
import com.example.student_helper.database.entity.ScheduleItem;
import com.example.student_helper.databinding.DialogAddScheduleBinding;
import com.example.student_helper.databinding.FragmentScheduleBinding;
import com.example.student_helper.utils.XPManager;
import com.example.student_helper.utils.LevelUpHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel viewModel;
    private ScheduleAdapter adapter;
    private int selectedDay = 1;

    private Chip[] dayChips;

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

        dayChips = new Chip[]{
                binding.chipMon, binding.chipTue, binding.chipWed,
                binding.chipThu, binding.chipFri, binding.chipSat, binding.chipSun
        };

        // Postavi listener za svaki chip
        for (int i = 0; i < dayChips.length; i++) {
            final int day = i + 1;
            dayChips[i].setOnClickListener(v -> {
                selectedDay = day;
                selectChipForDay(day);
                loadDay(day);
            });
        }

        // Auto-odabir danasnjeg dana (1=Pon ... 7=Ned)
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (today) {
            case Calendar.MONDAY:    selectedDay = 1; break;
            case Calendar.TUESDAY:   selectedDay = 2; break;
            case Calendar.WEDNESDAY: selectedDay = 3; break;
            case Calendar.THURSDAY:  selectedDay = 4; break;
            case Calendar.FRIDAY:    selectedDay = 5; break;
            case Calendar.SATURDAY:  selectedDay = 6; break;
            case Calendar.SUNDAY:    selectedDay = 7; break;
            default:                 selectedDay = 1;
        }
        selectChipForDay(selectedDay);
        loadDay(selectedDay);

        binding.fabAddSchedule.setOnClickListener(v -> showAddScheduleDialog());
    }

    private void selectChipForDay(int day) {
        for (int i = 0; i < dayChips.length; i++) {
            dayChips[i].setChecked((i + 1) == day);
        }
    }

    private void loadDay(int day) {
        viewModel.getByDay(day).observe(getViewLifecycleOwner(), items -> {
            adapter.updateItems(items);
            binding.tvEmptySchedule.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvSchedule.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        });
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
            boolean leveledUp = XPManager.addXP(requireContext(), 10);
            Toast.makeText(requireContext(), "+10 XP! 🎉", Toast.LENGTH_SHORT).show();
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
        binding = null;
    }
}