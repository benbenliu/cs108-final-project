package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class EditModeStarter extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Mode Starter");
        setContentView(R.layout.activity_edit_mode_starter);

//        ////// For debug use only
//        TextView output = (TextView) findViewById(R.id.showDir);
//        output.setText("Dir");
//
//        File dir = getFilesDir();
//
//        if (dir.isDirectory()) {
//            String[] fileArray = dir.list();
//            String result = dir.getAbsolutePath() + "\n\n";
//
//            for (String fileName : fileArray) {
//                File f = new File(fileName);
////                if(f.exists()){
////                    System.out.println("File not deleted");
////                } else {
////                    System.out.println("File deleted");
////                }
//                result += fileName + "\n";
//            }

//            output.setText(result);
//        }
        //////
    }

    public void goEditMode(View view){
        Intent intent = new Intent(this, EditMode.class);
        intent.putExtra("startNew", true);
        startActivity(intent);
    }

    public void goLoad(View view){
        Intent intent = new Intent(this, LoadActivity.class);
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }

    public void goDefault(View view){
        Intent intent = new Intent(this, EditMode.class);
        intent.putExtra("isEdit", true);
        intent.putExtra("fromPreload", true);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_starter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.select_mode:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
