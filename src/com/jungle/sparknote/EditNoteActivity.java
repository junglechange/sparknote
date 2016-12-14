package com.jungle.sparknote;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jungle.sparknote.com.jungle.sparknote.dao.NoteDao;

import java.util.List;

/**
 * Created by Administrator on 2016/12/11 0011.
 */
public class EditNoteActivity extends Activity {
    EditText editText = null;
    public static final String returnKey = "edit_return";
    private static Button saveButton = null;
    private String curAction = null;
    private static NoteDao noteDao = null;
    private String noteID = "-1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteDao = NoteDao.getNoteDao(this.getApplicationContext());
        Intent intent = getIntent();
        setContentView(R.layout.edit);
        editText = (EditText)findViewById(R.id.edit_text);
        saveButton = (Button)findViewById(R.id.edit_save);
        curAction = intent.getAction();
        Bundle extras = intent.getExtras();
        if (intent.getAction().equalsIgnoreCase(SparkWidget.ClickWidgetAction)){
            int  mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            noteID = noteDao.getWidgetNoteId(String.valueOf(mAppWidgetId));
        }else{

        }
        String noteText = intent.getStringExtra(ListNotesActivity.NOTE_TEXT_KEY);
        if(noteText!=null){
            editText.setText(noteText);
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                returnResult();
            }
        });
//        saveButton.setOnClickListener(new SaveButtonClickLister(editText,this));
    }

    private void returnResult() {
        if (curAction.equalsIgnoreCase(SparkWidget.ClickWidgetAction)){
            EditText curEditText = editText;
            String text = curEditText.getText().toString();
            ListNotesActivity.configWidget(this,text);
            noteDao.saveOneNote(noteID,text);
        }else {
            EditText curEditText = editText;
            String text = curEditText.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(returnKey, text);
            if (text.equalsIgnoreCase("")){
                setResult(RESULT_CANCELED, intent);
                Toast.makeText(getApplicationContext(), "nothing", Toast.LENGTH_SHORT);
            }else {
                setResult(RESULT_OK, intent);
                Toast.makeText(getApplicationContext(), "save success", Toast.LENGTH_SHORT);
            }
        }

        //finish();
    }

    @Override
    protected void onStop() {
        Log.i("test","onStop");

        super.onStop();

    }

    @Override
    protected void onPause() {
        Log.i("test","onPause");
        //returnResult();
        super.onPause();



    }

    @Override
    public void onBackPressed() {
        returnResult();
        finish();
    }
}

class SaveButtonClickLister implements View.OnClickListener{
    EditText editText = null;
    Activity context = null;
    public SaveButtonClickLister(EditText editText,Activity context){
        this.editText = editText;
        this.context = context;
    }
    public void onClick(View view) {
        EditText curEditText = editText;
        String text = curEditText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EditNoteActivity.returnKey, text);
        context.setResult(context.RESULT_OK, intent);
        context.finish();
    }
}
