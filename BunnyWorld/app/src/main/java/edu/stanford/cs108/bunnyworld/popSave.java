package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class popSave extends AppCompatActivity {
    SQLiteDatabase db;
    String tableName;
    boolean isEdit;
    String overwriteGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_save);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height * 0.2));

        // Determine which table to save to
        Intent intent = getIntent();
        overwriteGameName = intent.getStringExtra("gameName");
        ((EditText) findViewById(R.id.nameText)).setText(overwriteGameName);
        isEdit = intent.getBooleanExtra("isEdit", false);
        if(isEdit){
            tableName = "editModeSave";
        } else{
            tableName = "playModeSave";
        }

        // Create the table if not exists
        db = openOrCreateDatabase("bunnyWorld", MODE_PRIVATE, null);

        Cursor tablesCursor = db.rawQuery(
                "SELECT * FROM sqlite_master WHERE type='table' AND name='" + tableName + "';", null);
        System.out.println("SELECT * FROM sqlite_master WHERE type='table' AND name='" + tableName + "';");
        if (tablesCursor.getCount() == 0){
            initializeTable(tableName);
        }
    }

    private void initializeTable(String tableName){
        String createTable = "CREATE TABLE " + tableName + " ("
                + "gameName TEXT, "
                + "fileName TEXT, "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "unique(gameName)"
                + ");";
        System.out.println(createTable);
        db.execSQL(createTable);
    }

    public void onSave(View view){
        mySingleton.getInstance().savedDocument.saved=true;
        EditText nameText = (EditText) findViewById(R.id.nameText);
        String gameName = nameText.getText().toString();
        final String fileName;
        if(isEdit) {
            fileName = "edit_" + gameName + ".json";
        } else {
            fileName = "play_" + gameName + ".json";
        }

        String sqlStr;
        if(overwriteGameName.equals(gameName)){ // if this is indeed an overwrite
            sqlStr = "";
        }else{
            sqlStr = "INSERT INTO " + tableName + " VALUES "
                    + "('"
                    + gameName
                    + "','"
                    + fileName
                    + "',NULL);" ;
            System.out.println(sqlStr);
        }
        try{
            // Do the serialization
            String jsonStr = mySingleton.getInstance().savedDocument.toString();
            File dir = getFilesDir();
            File f = new File(dir, fileName);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            System.out.println(this.getFilesDir().getAbsolutePath());
            FileOutputStream stream = new FileOutputStream(f);
            try {
                stream.write(jsonStr.getBytes());
            } finally {
                stream.close();
            }
//            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
//            writer.write(jsonStr);
//            writer.close();

            // Save the (gameName, fileName) tuple into db
            if(!sqlStr.equals("")){
                db.execSQL(sqlStr);
            }

            // Notify the user of this successful save
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Successfully save " + gameName, Toast.LENGTH_SHORT);
            toast.show();

            // Redirect to the EditMode / PlayMode activity
            Intent intent;
            if(isEdit){
                intent = new Intent(this, EditMode.class);
            } else {
                intent = new Intent(this, PlayMode.class);
            }
            startActivity(intent);
        } catch (IOException e){
            Toast toast = Toast.makeText(getApplicationContext(),
                    e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e){
            System.out.println(e);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "The input name already exists, Please try another one", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onCancel(View view){
        // Redirect to the EditMode / PlayMode activity
        Intent intent;
        if(isEdit){
            intent = new Intent(this, EditMode.class);
        } else {
            intent = new Intent(this, PlayMode.class);
        }
        startActivity(intent);
    }
}