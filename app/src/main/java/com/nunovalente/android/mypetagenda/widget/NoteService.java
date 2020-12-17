package com.nunovalente.android.mypetagenda.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.nunovalente.android.mypetagenda.R;

public class NoteService extends IntentService {

    public static final String ACTION_SHOW_NOTES = "com.nunovalente.android.mypetagenda.action.show_notes";


    public NoteService() {
        super("NoteService");
    }

    private void handleActionUpdateNotes() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NoteWidget.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_notes_list);

        NoteWidget.updateNoteWidgets(this, appWidgetManager, appWidgetIds);
    }

    public static void startActionUpdateNotes(Context context) {
        Intent intent = new Intent(context, NoteService.class);
        intent.setAction(ACTION_SHOW_NOTES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            if(ACTION_SHOW_NOTES.equals(action)) {
                handleActionUpdateNotes();
            }
        }
    }
}
