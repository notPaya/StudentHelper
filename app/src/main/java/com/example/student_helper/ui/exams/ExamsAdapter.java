package com.example.student_helper.ui.exams;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_helper.R;
import com.example.student_helper.database.entity.Exam;
import com.example.student_helper.databinding.ItemExamBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.VH> {

    public interface OnDeleteListener {
        void onDelete(Exam exam);
    }

    private List<Exam> items;
    private final OnDeleteListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public ExamsAdapter(List<Exam> items, OnDeleteListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        final ItemExamBinding binding;
        public VH(ItemExamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExamBinding b = ItemExamBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Exam exam = items.get(position);
        holder.binding.tvExamSubject.setText(exam.subject);
        holder.binding.tvExamDate.setText("📅 " + sdf.format(new Date(exam.examDate)));
        holder.binding.tvExamNote.setText(exam.note);
        holder.binding.tvExamNote.setVisibility(exam.note.isEmpty() ? View.GONE : View.VISIBLE);

        long daysLeft = TimeUnit.MILLISECONDS.toDays(exam.examDate - System.currentTimeMillis());

        if (daysLeft <= 0) {
            holder.binding.tvDaysCount.setText("!");
            holder.binding.layoutCountdown.setBackgroundColor(Color.parseColor("#FF5252"));
        } else if (daysLeft == 1) {
            holder.binding.tvDaysCount.setText("1");
            holder.binding.layoutCountdown.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.examUrgent));
        } else if (daysLeft <= 7) {
            holder.binding.tvDaysCount.setText(String.valueOf(daysLeft));
            holder.binding.layoutCountdown.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.examSoon));
        } else {
            holder.binding.tvDaysCount.setText(String.valueOf(daysLeft));
            holder.binding.layoutCountdown.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.examOk));
        }

        holder.binding.btnDeleteExam.setOnClickListener(v -> listener.onDelete(exam));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateItems(List<Exam> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}