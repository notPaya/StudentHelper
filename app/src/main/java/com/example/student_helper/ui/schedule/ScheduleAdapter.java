package com.example.student_helper.ui.schedule;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_helper.database.entity.ScheduleItem;
import com.example.student_helper.databinding.ItemScheduleBinding;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.VH> {

    public interface OnDeleteListener {
        void onDelete(ScheduleItem item);
    }

    private List<ScheduleItem> items;
    private final OnDeleteListener listener;

    public ScheduleAdapter(List<ScheduleItem> items, OnDeleteListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        final ItemScheduleBinding binding;
        public VH(ItemScheduleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScheduleBinding b = ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ScheduleItem item = items.get(position);
        holder.binding.tvSubject.setText(item.subject);
        holder.binding.tvStartTime.setText(item.startTime);
        holder.binding.tvEndTime.setText(item.endTime);
        holder.binding.tvRoom.setText(item.room.isEmpty() ? "" : "📍 " + item.room);
        holder.binding.tvProfessor.setText(item.professor.isEmpty() ? "" : "👤 " + item.professor);
        holder.binding.btnDeleteSchedule.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateItems(List<ScheduleItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}