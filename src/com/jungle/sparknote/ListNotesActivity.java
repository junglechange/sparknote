package com.jungle.sparknote;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.jungle.sparknote.com.jungle.sparknote.dao.NoteDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class ListNotesActivity extends Activity {
    private static String editButtonID = "button_edit_note";
    public static final String noteIDKey = "id";
    public static Map<String, String> listData = null;
    private static ListView listView = null;
    private static int listViewCheckTextViewIDPre = 0;
    private static String selectedStr = "";
    private static Integer selectedID = 0;
    private Button buttonDelete = null;
    private Button buttonEdit = null;
    private Button buttonSave = null;
    private List<String> deleteNotes = null;

    private static Map<Integer, String> listViewID_listNoteMap = null;

    public static final int NEW_REQ_CODE = 1;
    public static final int EDIT_REQ_CODE = 2;
    public static final String NOTE_TEXT_KEY = "notetext";

    private static NoteDao noteDao = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        noteDao = NoteDao.getNoteDao(this.getApplicationContext());
        deleteNotes = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_notes);
        listView = (ListView) findViewById(R.id.listView);
        buttonSave = (Button) findViewById(R.id.listsave);
        buttonDelete = (Button) findViewById(R.id.listdelete);
        buttonEdit = (Button) findViewById(R.id.listedit);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                noteDao.removeNotes(deleteNotes);
                noteDao.saveNoteData(listData);
                deleteNotes = new ArrayList<String>();
                Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT).show();
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ListNotesActivity.this, EditNoteActivity.class);
                intent.putExtra(NOTE_TEXT_KEY, selectedStr);
                intent.setAction("NoteEditAction");
                startActivityForResult(intent, EDIT_REQ_CODE);
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                listData.remove(listViewID_listNoteMap.get(selectedID));
                deleteNotes.add(listViewID_listNoteMap.get(selectedID));
                setListView(listData);
            }
        });
        Button buttonNew = (Button) findViewById(R.id.listnew);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ListNotesActivity.this, EditNoteActivity.class);
                intent.setAction("NoteEditAction");
                startActivityForResult(intent, NEW_REQ_CODE);
            }
        });
        listData = noteDao.getNoteData();
        setListView(listData);
    }

    private void setListView(Map<String, String> listData) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.getApplicationContext(), R.layout.list_view);
        Map<Integer, String> listViewID_listNoteMapTmp = new HashMap<Integer, String>();
        Integer listViewID = 0;
        for (Map.Entry<String, String> entry : listData.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(noteIDKey)) {
                continue;
            }
            listViewID_listNoteMapTmp.put(listViewID++, entry.getKey());
            arrayAdapter.add(entry.getValue());
        }
        listViewID_listNoteMap = listViewID_listNoteMapTmp;
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                long rowid = l;
                TextView item = (TextView) view;
                String itemText = item.getText().toString();
                selectedStr = itemText;
                selectedID = position;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == NEW_REQ_CODE) {
                Object currentIdStr = listData.get(noteIDKey);
                Object returnData = data.getStringExtra(EditNoteActivity.returnKey);
                if (currentIdStr == null) {
                    currentIdStr = "1";
                }
                Integer currentId = Integer.valueOf(currentIdStr.toString());
                currentId++;
                listData.put(currentId.toString(), returnData.toString());
                listData.put(noteIDKey, currentId.toString());
            } else if (requestCode == EDIT_REQ_CODE) {
                Object returnData = data.getStringExtra(EditNoteActivity.returnKey);
                listData.put(listViewID_listNoteMap.get(selectedID), returnData.toString());
            }
            setListView(listData);
        }
    }

    @Override
    public void onBackPressed() {
        configWidget(this, selectedStr);
        finish();
    }

    public static void configWidget(Activity context, String text) {
        Intent intent = context.getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (intent.getAction() == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
                    || intent.getAction() == SparkWidget.ClickWidgetAction) {
                int mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                RemoteViews remoteViews = new RemoteViews(context.getApplicationContext().getPackageName(),
                        R.layout.simple_widget);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                remoteViews.setTextViewText(R.id.textView3, text);

                Intent intent1 = new Intent(context, EditNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent1.setAction(SparkWidget.ClickWidgetAction);
                intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                intent1.putExtra(NOTE_TEXT_KEY, text);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, mAppWidgetId, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                remoteViews.setOnClickPendingIntent(R.id.textView3, pendingIntent);
                appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
                if (intent.getAction() == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE) {
                    noteDao.saveWidgetNoteMap(String.valueOf(mAppWidgetId), listViewID_listNoteMap.get(selectedID));
                }
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                context.setResult(RESULT_OK, resultValue);
            }

        }
    }
}
