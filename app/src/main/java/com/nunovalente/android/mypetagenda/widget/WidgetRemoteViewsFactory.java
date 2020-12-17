package com.nunovalente.android.mypetagenda.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.local.NoteDao;
import com.nunovalente.android.mypetagenda.data.local.NoteDatabase;
import com.nunovalente.android.mypetagenda.model.Note;

import java.util.ArrayList;
import java.util.List;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private List<Note> mNoteList = new ArrayList<>();
    private final Context context;
    private int mAppWidgetId;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        NoteDatabase db;
        db = NoteDatabase.getInstance(context);
        NoteDao notesDao = db.noteDao();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        String petId = sharedPreferences.getString(context.getString(R.string.widget_pet_id), "");

        mNoteList = notesDao.getAllNotesValue(petId);
    }

    @Override
    public void onDestroy() {
        mNoteList.clear();
    }

    @Override
    public int getCount() {
        if(mNoteList != null) {
            return mNoteList.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_remote_view);
        Note note = mNoteList.get(position);
        remoteViews.setTextViewText(R.id.remote_view_note, note.getText());
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
