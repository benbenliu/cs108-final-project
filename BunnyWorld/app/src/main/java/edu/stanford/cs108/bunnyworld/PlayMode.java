package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

public class PlayMode extends AppCompatActivity {
    public Document d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // determine how to create the document
        Intent intent = getIntent();
        if(intent.getBooleanExtra("fromLoad", false)){
            System.out.println("!!!!!!!!!!!!!!!!!!!fromLoad");
            try{
                String fileName = intent.getStringExtra("fileName");
                System.out.println("!!!!!!!!!!!!!!!!fileName is :" + fileName);
                File dir = getFilesDir();
                File f = new File(dir, fileName);
                int length = (int) f.length();
                byte[] bytes = new byte[length];
                FileInputStream in = new FileInputStream(f);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
                String jsonStr = new String(bytes);
                System.out.println("!!!!!!!!!!!!!!!!jsonStr:"+jsonStr);
                d = Document.fromString(jsonStr);
                System.out.println("!!!!!!!!!!!!!!!!5");
                if(d == null){
                    System.out.println("!!!!!!!!!!!!!!!!document is null");
                } else{
                    System.out.println("!!!!!!!!!!!!!!!!document is not null");
                }
                mySingleton.getInstance().savedDocument = d;
            } catch(Exception ignored){
                System.out.println("!!!!!!!!!!!!!!!!error is :" + ignored.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ooooops, unknow failure when loading the document", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if(intent.getBooleanExtra("startNew", false)){
            d = new Document();
            mySingleton.getInstance().savedDocument = d;
            System.out.println("!!!!!!!!!!!!!!!!!!!startNew");
        } else if(intent.getBooleanExtra("fromPreload", false)) {
            try {
                InputStream rawStream = getResources().openRawResource(R.raw.default_game);
                BufferedReader reader = new BufferedReader(new InputStreamReader(rawStream));

                String jsonStr = "";
                String str;
                while ((str = reader.readLine()) != null) {
                    jsonStr += str + "\n";
                }
                d = Document.fromString(jsonStr);
                if(d == null){
                    System.out.println("!!!!!!!!!!!!!!!!document is null");
                } else{
                    System.out.println("!!!!!!!!!!!!!!!!document is not null");
                }
                mySingleton.getInstance().savedDocument = d;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            d = mySingleton.getInstance().savedDocument;
            System.out.println("!!!!!!!!!!!!!!!!!!!else");
        }
        System.out.println("!!!!!!!!!!!!!!!!");
        d.isEdit = false;
        System.out.println("!!!!!!!!!!!!!!!!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mode);
        setTitle("Play Mode-"+d.currentPage.name);
        PageView pageView = (PageView) findViewById(R.id.pageView2);
        mySingleton.getInstance().currentPageView = pageView;

        // set up background image if needed
//        if(d.currentPage.backgroundImageId != -1){
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), d.currentPage.backgroundImageId);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
        if(d.currentPage.isBackgroundTiled){
            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
        pageView.setBackground(bitmapDrawable);
//        mySingleton.getInstance().currentPageView.setBackgroundResource(d.currentPage.backgroundImageId);

//        }

        Button shapeEditor = (Button) findViewById(R.id.playPossession);

        shapeEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySingleton.getInstance().savedDocument.saved=false;
                startActivity(new Intent(PlayMode.this, popPossession.class));
            }
        });
        // if from load, meaning the page is seen for the first time, then call onEnter
        if(intent.getBooleanExtra("fromLoad", false)){
            pageView.onEnter();
        }

        // pick up button will delete shape in play mode and add to possession
        Button pickup = (Button) findViewById(R.id.playAddToPossession);
        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySingleton.getInstance().savedDocument.saved=false;
                if (d.currentPage.currentShape == null) {
                    Toast.makeText(v.getContext(), "Please Select a Shape", Toast.LENGTH_SHORT).show();
                } else {
                    Shape selectedShape = d.currentPage.currentShape;
                    d.possessionList.add(selectedShape);
                    d.currentPage.shapeList.remove(selectedShape);
                    d.currentPage.currentShape = null;
                    selectedShape.onCancel();
                    mySingleton.getInstance().currentPageView.draw();
                }
            }
        });
    }

    public void updateTitle(){
        String curr_page = mySingleton.getInstance().savedDocument.currentPage.name;
        setTitle("Play Mode-"+curr_page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.play_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.select_mode:
                if(! mySingleton.getInstance().savedDocument.saved){
                    intent = new Intent(this, leavePageConfirm.class);
                    intent.putExtra("is edit", false);
                    startActivity(intent);
                }else{
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
//                intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
                break;

            case R.id.save_game:
                intent = new Intent(this, LoadActivity.class);
                intent.putExtra("isEdit", false);
                intent.putExtra("isSaving", true);
                mySingleton.getInstance().savedDocument = d;
                startActivity(intent);
                break;
            default: break;
        }
        return true;
    }
}
