package com.nunovalente.android.mypetagenda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.model.Note;

import java.util.List;

public class RecyclerNoteAdapter extends RecyclerView.Adapter<RecyclerNoteAdapter.MyNotesViewHolder> {

    private final List<Note> mNoteList;
    private final RecyclerItemClickListener listener;

    public RecyclerNoteAdapter(List<Note> mNoteList, RecyclerItemClickListener listener) {
        this.mNoteList = mNoteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_adapter, parent, false);
        return new MyNotesViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotesViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        holder.mNoteText.setText(note.getText());
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public class MyNotesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private final TextView mNoteText;

        public MyNotesViewHolder(@NonNull View itemView) {
            super(itemView);

            mNoteText = itemView.findViewById(R.id.tv_note_adapter);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            listener.onLongClicked(position);
            return true;
        }
    }
}
