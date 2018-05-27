package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LoadActivity extends AppCompatActivity {
    SQLiteDatabase db;
    String tableName;
    boolean isEdit;
    boolean isSaving;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        setTitle("Savings");

        // Determine which table to load from
        Intent intent = getIntent();
        isSaving = intent.getBooleanExtra("isSaving", false);
        isEdit = intent.getBooleanExtra("isEdit", false);
        final boolean isNewPlay = intent.getBooleanExtra("isNewPlay", false);
        if(isEdit || isNewPlay){
            tableName = "editModeSave";
        } else{
            tableName = "playModeSave";
        }

        // Choose which buttom to show
        Button saveButton = (Button) findViewById(R.id.save);
        Button loadButton = (Button) findViewById(R.id.load);
        if(isSaving){
            saveButton.setVisibility(View.VISIBLE);
            loadButton.setVisibility(View.GONE);
            saveButton.invalidate();
            loadButton.invalidate();
        } else{
            loadButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
            loadButton.invalidate();
            saveButton.invalidate();
        }

        // Create and / or show the table
        db = openOrCreateDatabase("bunnyWorld", MODE_PRIVATE, null);

        //// For debug use only
//        String dropTable = "DROP TABLE " + tableName;
//        System.out.println(dropTable);
//        db.execSQL(dropTable);
        ////

        Cursor tablesCursor = db.rawQuery(
                "SELECT * FROM sqlite_master WHERE type='table' AND name='" + tableName + "';", null);
        System.out.println("SELECT * FROM sqlite_master WHERE type='table' AND name='" + tableName + "';");
        if (tablesCursor.getCount() == 0){
            initializeTable(tableName);
        }
        showSavings(tableName);

    }

    public void onSave(View view){
        ListView listView = (ListView) findViewById(R.id.loadListView);
        int pos = listView.getCheckedItemPosition();
        if(pos == AdapterView.INVALID_POSITION){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please select a record first",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else{
            TextView selectedTextView = (TextView) adapter.getView(pos, null, listView);
            String gameName = selectedTextView.getText().toString();
            if(gameName.equals("++New Saving++")){
                Intent intent = new Intent(this, popSave.class);
                intent.putExtra("isEdit", isEdit);
                intent.putExtra("gameName", "");
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, popSave.class);
                intent.putExtra("isEdit", isEdit);
                intent.putExtra("gameName", gameName);
                startActivity(intent);
            }
        }
    }

    public void onLoad(View view){
        ListView listView = (ListView) findViewById(R.id.loadListView);
        int pos = listView.getCheckedItemPosition();
        if(pos == AdapterView.INVALID_POSITION){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please select a record first",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else{
            TextView selectedTextView = (TextView) adapter.getView(pos, null, listView);
            String gameName = selectedTextView.getText().toString();
            String sqlStr = "SELECT fileName from "
                    + tableName
                    + " where gameName = '"
                    + gameName
                    +"';";
            System.out.println(sqlStr);
            Cursor tableCursor = db.rawQuery(sqlStr, null);
            String fileName;
            if(tableCursor.moveToNext()){
                fileName = tableCursor.getString(0);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Load from " + fileName,
                        Toast.LENGTH_SHORT);
                toast.show();

//                     Pass the right fileName to the right Mode for actual serialization
                Intent intent;
                if(isEdit) {
                    intent = new Intent(LoadActivity.this, EditMode.class);
                } else {
                    intent = new Intent(LoadActivity.this, PlayMode.class);
                }
                intent.putExtra("fromLoad", true);
                intent.putExtra("fileName", fileName);
                startActivity(intent);
            }
        }
    }

    public void onDelete(View view){
        ListView listView = (ListView) findViewById(R.id.loadListView);
        int pos = listView.getCheckedItemPosition();
        if(pos == AdapterView.INVALID_POSITION){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please select a record first",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else{
            TextView selectedTextView = (TextView) adapter.getView(pos, null, listView);
            String gameName = selectedTextView.getText().toString();
            if(gameName.equals("++New Saving++")){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Hey buddy you cannot delete this " + gameName,
                        Toast.LENGTH_SHORT);
                toast.show();
            } else{
                String sqlStr = "DELETE from "
                        + tableName
                        + " where gameName = '"
                        + gameName
                        +"';";
                System.out.println(sqlStr);
                try{
                    db.execSQL(sqlStr);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Successfully delete " + gameName,
                            Toast.LENGTH_SHORT);
                    toast.show();

                    //refresh
                    String selectTable = "SELECT * from " + tableName;
                    Cursor tableCursor = db.rawQuery(selectTable, null);
                    adapter.changeCursor(tableCursor);
                    listView.setItemChecked(0, true);
                    listView.invalidateViews();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
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

        String insertFirst =  "INSERT INTO " + tableName + " VALUES "
                + "('++New Saving++', NULL, NULL);" ;
        System.out.println(insertFirst);
        db.execSQL(insertFirst);
        ////// For debug use only
//        String dummyInsert = "INSERT INTO " + tableName + " VALUES "
//                + "('dummyGame','/file/dummy.txt',NULL);" ;
//        System.out.println(dummyInsert);
//        db.execSQL(dummyInsert);
        //////
    }

    private void showSavings(String tableName){
        String selectTable;
        if(isSaving){
            selectTable = "SELECT * from " + tableName;
        }else{
            selectTable = "SELECT * from "
                    + tableName
                    + " WHERE gameName <> '++New Saving++'";
        }
        System.out.println(selectTable);
        Cursor tableCursor = db.rawQuery(selectTable, null);
        String[] fromArray = {"gameName"};
        int[] toArray = {android.R.id.text1};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1,
                tableCursor, fromArray, toArray, 0);
        ListView listView = (ListView) findViewById(R.id.loadListView);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.load_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.select_mode:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
