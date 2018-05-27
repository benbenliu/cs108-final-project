package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

/**
 * Created by yueliu on 11/30/17.
 */

public class popAddPage extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_add_new_page);
        setTitle("Add Page");

        // Set pop window size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.3));
    }

    public void onAddNewPage(View view){
        EditText newPageName = (EditText) findViewById(R.id.newPageName);
        String name = newPageName.getText().toString();
        Document d = mySingleton.getInstance().savedDocument;
        Intent intent = new Intent(this, EditMode.class);

        if(name.length() == 0){
            //default
            int i = 2;
            while(d.pageDictionary.containsKey("page"+String.valueOf(i))){
                i++;
            }
            name="page"+String.valueOf(i);
        }
        try{
            d.addPage(name);
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        startActivity(intent);
    }

    public void onCancelAdding(View view){
        Intent intent = new Intent(this, EditMode.class);
        startActivity(intent);
    }
}
