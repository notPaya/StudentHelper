package com.example.student_helper.ui.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_helper.R;
import com.example.student_helper.database.entity.Note;
import com.example.student_helper.databinding.ItemNoteBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.VH> {

    public interface OnDeleteListener {
        void onDelete(Note note);
    }

    private List<Note> items;
    private final OnDeleteListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public NotesAdapter(List<Note> items, OnDeleteListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        final ItemNoteBinding binding;
        public VH(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding b = ItemNoteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Note note = items.get(position);
        holder.binding.tvNoteTitle.setText(note.title);
        holder.binding.tvNoteContent.setText(note.content);
        holder.binding.tvNoteContent.setVisibility(note.content.isEmpty() ? View.GONE : View.VISIBLE);
        holder.binding.tvNoteDate.setText(sdf.format(new Date(note.createdAt)));

        int colorRes;
        switch (note.color) {
            case "BLUE":   colorRes = R.color.noteBlue;   break;
            case "GREEN":  colorRes = R.color.noteGreen;  break;
            case "PINK":   colorRes = R.color.notePink;   break;
            case "PURPLE": colorRes = R.color.notePurple; break;
            default:       colorRes = R.color.noteYellow;
        }
        holder.binding.noteCard.setCardBackgroundColor(
                holder.itemView.getContext().getColor(colorRes));
        holder.binding.btnDeleteNote.setOnClickListener(v -> listener.onDelete(note));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateItems(List<Note> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}