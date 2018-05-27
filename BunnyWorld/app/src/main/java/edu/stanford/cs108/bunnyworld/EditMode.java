package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class EditMode extends AppCompatActivity {
    public Document d;
//    private static final int MENU_SELECT_GAME = Menu.FIRST;
//    private static final int MENU_SAVE_GAME = MENU_SELECT_GAME+1;
//    private static final int GO_TO_PAGE = MENU_SAVE_GAME+1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // determine how to create the document
        Intent intent = getIntent();
        if(intent.getBooleanExtra("fromLoad", false)){
            System.out.println("!!!!!!!!!!!!!!!!!!!fromLoad");
            try{

                String fileName = intent.getStringExtra("fileName");
                File dir = getFilesDir();
//                System.out.println("00000000000000 ");
                File f = new File(dir, fileName);

                int length = (int) f.length();
                byte[] bytes = new byte[length];

                FileInputStream in = new FileInputStream(f);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
//                System.out.println("22222222222 ");
                String jsonStr = new String(bytes);
                System.out.println(jsonStr);
//                System.out.println(jsonStr);
//                BufferedReader reader = new BufferedReader(new FileReader(f));
//                String jsonStr = reader.readLine();
//                assert reader.readLine()==null : "Error: there are more than one line in the file";
                d = Document.fromString(jsonStr);
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
        }else {
            d = mySingleton.getInstance().savedDocument;
            System.out.println("!!!!!!!!!!!!!!!!!!!else");
        }
        System.out.println("!!!!!!!!!!!!!!!!");
        d.isEdit = true;
        System.out.println("!!!!!!!!!!!!!!!!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);
        mySingleton.getInstance().currentPageView = (PageView) findViewById(R.id.pageView);
        String curr_page = mySingleton.getInstance().savedDocument.currentPage.name;
        setTitle("Edit Mode-"+curr_page);

        //setup background image:
//        if(d.currentPage.backgroundImageId != -1){
//        mySingleton.getInstance().currentPageView.setBackgroundResource(d.currentPage.backgroundImageId);
        updateBackground();
//        }

        // Pop up shapeBox
        Button shapeBox = (Button) findViewById(R.id.editShapeBox);
        shapeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySingleton.getInstance().savedDocument.saved=false;
                startActivity(new Intent(EditMode.this, popAddShape.class));
            }
        });


        // Pop up shape editor
        Button shapeEditor = (Button) findViewById(R.id.editShapeEditor);
        Button delShapeButton = (Button) findViewById(R.id.delShape);

        if (d.currentPage.currentShape == null){
            shapeBox.setVisibility(View.VISIBLE);
            delShapeButton.setVisibility(View.GONE);
            shapeBox.invalidate();
            delShapeButton.invalidate();

        }

        shapeEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySingleton.getInstance().savedDocument.saved = false;
                if (d.currentPage.currentShape == null){
                    Toast.makeText(v.getContext(), "Please Select a Shape", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(EditMode.this, popShapeEditor.class));
                }

            }
        });

        if (d.currentPage.currentShape != null){
            shapeBox.setVisibility(View.GONE);
            delShapeButton.setVisibility(View.VISIBLE);
            shapeBox.invalidate();
            delShapeButton.invalidate();
            System.out.print("button change.");
        }
        

    }

    public void updateBackground(){
        PageView pv = (PageView) findViewById(R.id.pageView);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), d.currentPage.backgroundImageId);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);

        if(d.currentPage.isBackgroundTiled){
            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT);
        }
        pv.setBackground(bitmapDrawable);
    }

    @Override
    protected void onResume() {
        updateBackground();
//      pv.setBackgroundResource(d.currentPage.backgroundImageId);

        System.out.println("ffffffffffff"+mySingleton.getInstance().savedDocument.currentPage.name);
        setTitle("Edit Mode-"+mySingleton.getInstance().savedDocument.currentPage.name);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);

        // find the submenu under go to page
        MenuItem menuItem = menu.findItem(R.id.menu_edit_goto_page);
        SubMenu subMenu = menuItem.getSubMenu();
        int subID = Menu.FIRST;
        //add items to the subMenu by looping through the page dictionary
        for(String pageName : d.pageDictionary.keySet()){
            subMenu.add(3, subID, SubMenu.NONE, pageName);
            subID++;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.select_mode:
                if(! mySingleton.getInstance().savedDocument.saved){
                    intent = new Intent(this, leavePageConfirm.class);
                    intent.putExtra("is edit", true);
                    startActivity(intent);
                }else{
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.save_game:
                intent = new Intent(this, LoadActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("isSaving", true);
                mySingleton.getInstance().savedDocument = d;
                startActivity(intent);
                return true;
            case R.id.add_page:
                mySingleton.getInstance().savedDocument.saved = false;
                intent = new Intent(this, popAddPage.class);
                startActivity(intent);
                return true;
            case R.id.edit_page:
                mySingleton.getInstance().savedDocument.saved = false;
                intent = new Intent(this, pageEditor.class);
                startActivity(intent);
                return true;
            default: break;
        }
        // let the selected page draw itself after we switch to it in the edit mode
        for(int i=Menu.FIRST; i < Menu.FIRST+d.pageDictionary.size(); i++){
            if(id==i){
                String pageName = item.getTitle().toString();
                System.out.println("switch to page "+pageName);
                Page selectedPage = d.pageDictionary.get(pageName);
                PageView pv = findViewById(R.id.pageView);
                pv.setPageAndDraw(selectedPage);
                String curr_page = mySingleton.getInstance().savedDocument.currentPage.name;
                setTitle("Edit Mode-"+curr_page);

//                if(mySingleton.getInstance().savedDocument.currentPage.backgroundImageId != -1){
                updateBackground();
//                pv.setBackgroundResource(mySingleton.getInstance().savedDocument.currentPage.backgroundImageId);
//                }

                return true;
            }
        }
        try{
            // do nothing, the function should not come to this spot
        }catch(Exception e){
            System.out.println("invalid menu option");
        }
        return true;
    }

//    public void onSave(View view){
//        Intent intent = new Intent(this, popSave.class);
//        intent.putExtra("isEdit", true);
//        mySingleton.getInstance().savedDocument = d;
//        startActivity(intent);
//    }

    public void onCopy(View view){
        mySingleton.getInstance().savedDocument.saved = false;
        if(d.currentPage.currentShape == null){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please select a shape before copy.", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            d.copiedShape = d.currentPage.currentShape;
            d.copyNum = 2;
        }
    }

    public void onPaste(View view){
        mySingleton.getInstance().savedDocument.saved = false;
        if(d.copiedShape == null){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "There's nothing in the copy board to be pasted. Please copy one first.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Shape pasted = Shape.deepCopy(d.copiedShape);
            pasted.setPositionX(0);
            pasted.setPositionY(0);
            String pastedName = d.copiedShape.getName()+"-"+Integer.toString(d.copyNum);
            while(d.shapeDictionary.containsKey(pastedName)){
                d.copyNum++;
                pastedName = d.copiedShape.getName()+"-"+Integer.toString(d.copyNum);
            }
            pasted.setName(pastedName);
            try{
                d.addShape(pasted);
                mySingleton.getInstance().currentPageView.draw();
            } catch (Exception e){
                Toast toast = Toast.makeText(getApplicationContext(),
                        e.getMessage(),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void deleteShape(View view) throws Exception {
        mySingleton.getInstance().savedDocument.saved = false;
        Shape shape = mySingleton.getInstance().savedDocument.currentPage.currentShape;
        Page page = mySingleton.getInstance().savedDocument.currentPage;
        checkDependentShape(shape);
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee after check shape");
        Toast toast = Toast.makeText(getApplicationContext(),
                ""+shape.name+" deleted", Toast.LENGTH_SHORT);
        toast.show();
        mySingleton.getInstance().savedDocument.deleteShape(shape);
        mySingleton.getInstance().savedDocument.currentPage.currentShape=null;
        Button shapeBox = findViewById(R.id.editShapeBox);
        Button delShapeButton = findViewById(R.id.delShape);
        // show button/ del button
        if (d.currentPage.currentShape == null){
            shapeBox.setVisibility(View.VISIBLE);
            delShapeButton.setVisibility(View.GONE);
            shapeBox.invalidate();
            delShapeButton.invalidate();
        }

        if (d.currentPage.currentShape != null){
            shapeBox.setVisibility(View.GONE);
            delShapeButton.setVisibility(View.VISIBLE);
            shapeBox.invalidate();
            delShapeButton.invalidate();
        }
        mySingleton.getInstance().currentPageView.setPageAndDraw(page);


        //to be implemented by Siyue
    }
    public void checkDependentShape(Shape s){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in checkDependentShape");
        HashSet<String> dependShapeNames = s.dependShapeName;
        System.out.println(s.name);
        System.out.println(dependShapeNames);
        String shapeName = s.name;
        for(String name: dependShapeNames){
            System.out.println("depend shape name: ");
            System.out.println(name);
            System.out.println(mySingleton.getInstance().savedDocument.shapeDictionary.keySet().toString());
            Shape dependShape = mySingleton.getInstance().savedDocument.shapeDictionary.get(name);
            System.out.println(dependShape);
            if(dependShape == null){
                continue;
            }
            String scripts = dependShape.getScript();
            dependShape.dependShapeName.remove(shapeName);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in after get the final scripts");
            String new_scripts = deleteShapeScript(shapeName, scripts);
            System.out.println(String.format("new_sciprts {}", new_scripts));
            mySingleton.getInstance().savedDocument.shapeDictionary.get(name).setScript(new_scripts);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
    }
    public String deleteShapeScript(String name, String script){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript");
        System.out.println(script);
        String[] clauses = script.split("\\*");
        String finalScripts = "";
        for(String clause: clauses){
            String[] splitStr = clause.split("\\|");
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript for loop");
            System.out.println(splitStr[1]);
            System.out.println(splitStr[2]);
            if(splitStr[0].equals("on drop")){
                if(!(splitStr[1].equals(name)) && !(splitStr[2].equals("hide") && splitStr[3].equals(name))
                        && !(splitStr[2].equals("show") && splitStr[3].equals(name))){
                    finalScripts += clause;
                    finalScripts += "*";
                }
            }

            else{
                if(!(splitStr[1].equals("hide") && splitStr[2].equals(name))
                        && !(splitStr[1].equals("show") && splitStr[2].equals(name))){
                    finalScripts += clause;
                    finalScripts += "*";
                }
            }
        }
        System.out.println(finalScripts);
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        return finalScripts;
    }

}