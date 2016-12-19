package com.jungle.sparknote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class MainActivity extends Activity {
    private Button buttonEdit = null;
    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        setContentView(R.layout.main);
        buttonEdit = (Button) findViewById(R.id.button_edit_note);
        imageView = (ImageView) findViewById(R.id.mainimg);
        imageView.setImageBitmap(DownImage.bitmap);
        DownImage downImage = new DownImage(imageView);
        downImage.execute();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownImage downImage = new DownImage(imageView);
                downImage.execute();
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListNotesActivity.class);
                startActivity(intent);
            }
        });
    }
}

class DownImage extends AsyncTask {
    public static String IMG_URL = "http://unsplash.it/400/600/?random";
    private ImageView imageView = null;
    public static Bitmap bitmap = null;

    public DownImage(ImageView out) {
        this.imageView = out;
    }

    @Override
    protected void onPostExecute(Object o) {
        imageView.setImageBitmap((Bitmap) o);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String url = IMG_URL;
        try {
            InputStream is = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.e("sparknote", e.getMessage());
        }
        return bitmap;
    }
}


