package com.jungle.sparknote.com.jungle.sparknote.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/11 0011.
 */
public class NoteDao {
    private static Context context = null;
    private static Map<String,String> data = null;
    private static final String noteFileName = "note-data";
    private static final String widgetIdNoteIdMapFileName = "widget-note-data";
    private static NoteDao noteDao = null;
    public void saveNoteData(Map<String,String> data){
        SharedPreferences.Editor editor = context.getSharedPreferences(noteFileName,
                context.MODE_PRIVATE).edit();
        for (Map.Entry<String,String> entry : data.entrySet()){
            editor.putString(entry.getKey(),entry.getValue());
        }
        editor.commit();
    }


    public Map<String,String> getNoteData(){

                    SharedPreferences pref = context.getSharedPreferences(noteFileName,
                            context.MODE_PRIVATE);
        data = (Map<String,String>)pref.getAll();


        return data;
    }

    public void saveOneNote(String noteId, String noteValue){
        SharedPreferences.Editor editor = context.getSharedPreferences(noteFileName,
                context.MODE_PRIVATE).edit();
        editor.putString(noteId,noteValue);
        editor.commit();
    }

    public void removeOneNote(String id){
        SharedPreferences.Editor editor = context.getSharedPreferences(noteFileName,
                context.MODE_PRIVATE).edit();
        editor.remove(id);
        editor.commit();
    }

    public void saveWidgetNoteMap(String widgetId, String noteId){
        SharedPreferences.Editor editor = context.getSharedPreferences(widgetIdNoteIdMapFileName,
                context.MODE_PRIVATE).edit();
        editor.putString(widgetId,noteId);
        editor.commit();
    }

    public void removeWidgetNoteMap(String widgetId){
        SharedPreferences.Editor editor = context.getSharedPreferences(widgetIdNoteIdMapFileName,
                context.MODE_PRIVATE).edit();
        editor.remove(widgetId);
        editor.commit();
    }

    public String getWidgetNoteId(String widgetId){
        String noteId = context.getSharedPreferences(widgetIdNoteIdMapFileName,
                context.MODE_PRIVATE).getString(widgetId,"-1");
        return  noteId;
    }


    public static NoteDao getNoteDao(Context context){
        if (context==null){
            noteDao=null;
            return noteDao;
        }
        NoteDao.context = context;
        if (noteDao == null){
            synchronized (NoteDao.class){
                if (noteDao==null){
                    noteDao = new NoteDao();
                }
            }
        }
        return noteDao;
    }
}
