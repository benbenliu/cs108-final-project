package edu.stanford.cs108.bunnyworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by ysun on 11/22/17.
 */

public class popShapeEditor extends Activity {
    public HashMap<String, String[]> scripts;
    public HashMap<String, String> scriptNamesTokens;
    private Document d;
    private Shape currShape;
    public ArrayList<String> scriptNames;

    public TextView shapeNameTextView;

    public CheckBox isMovableCheck;
    public CheckBox isHiddenCheck;

    public Spinner scriptsSpinner;
    public Spinner triggerSpinner;
    public Spinner actionSpinner;
    public Spinner objectSpinner;
    public Spinner onDropShapeSpinner;

    private String currSriptName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d = mySingleton.getInstance().savedDocument;
        currShape = d.currentPage.currentShape;
        scriptNames = new ArrayList<String>();
        scripts = new HashMap<String, String[]>();
        scriptNamesTokens = new HashMap<String, String>();

        // set pop up size
        setContentView(R.layout.edit_pop_shape_editor);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(width, (int)(height * 1));

        // script spinners, buttons
        triggerSpinner = (Spinner) findViewById(R.id.scriptTrigger);
        actionSpinner = (Spinner) findViewById(R.id.scriptAction);
        objectSpinner = (Spinner) findViewById(R.id.scriptObject);
        onDropShapeSpinner = (Spinner) findViewById(R.id.onDropShape);
        scriptsSpinner = (Spinner) findViewById(R.id.scripts);

        Button delScriptButton = (Button) findViewById(R.id.delScript);
        Button addScript = (Button) findViewById(R.id.addScript);
        Button editScript = (Button) findViewById(R.id.editScript);
        Button confirmChanges = (Button) findViewById(R.id.confirmChanges);

        onDropShapeSpinner.setVisibility(View.GONE);
//        objectSpinner.setVisibility(View.GONE);  this line is deleted because of a bug

        shapeNameTextView = (TextView) findViewById(R.id.shapeName);

        isMovableCheck = (CheckBox) findViewById(R.id.shapeMovable);
        isHiddenCheck = (CheckBox) findViewById(R.id.shapeHidden);

        //read in attributes
        shapeNameTextView.setText(currShape.getName());
        isMovableCheck.setChecked(currShape.isMovable());
        isHiddenCheck.setChecked(!currShape.isVisible());

        if (currShape.getText().length()>0) {
            EditText temp_text = (EditText) findViewById(R.id.textInput);
            temp_text.setText(currShape.getText());
            CheckBox temp_ita = (CheckBox) findViewById(R.id.italicChecker);
            temp_ita.setChecked(currShape.getItalic());
            CheckBox temp_bold = (CheckBox) findViewById(R.id.boldChecker);
            temp_bold.setChecked(currShape.getBold());
            CheckBox temp_ul = (CheckBox) findViewById(R.id.underlineChecker);
            temp_ul.setChecked(currShape.getUnderLined());
            EditText temp_f_size = (EditText) findViewById(R.id.fontBox);
            temp_f_size.setText(Float.toString(currShape.getFontSize()));
            int temp_c = currShape.getFontColor();
            int temp_a = (temp_c >> 24) & 0xff;
            int temp_r = (temp_c >> 16) & 0xff;
            int temp_g = (temp_c >>  8) & 0xff;
            int temp_b = (temp_c      ) & 0xff;
            SeekBar temp_sb0 = (SeekBar) findViewById(R.id.aset);
            SeekBar temp_sb1 = (SeekBar) findViewById(R.id.rset);
            SeekBar temp_sb2 = (SeekBar) findViewById(R.id.gset);
            SeekBar temp_sb3 = (SeekBar) findViewById(R.id.bset);
            temp_sb0.setProgress(temp_a);
            temp_sb1.setProgress(temp_r);
            temp_sb2.setProgress(temp_g);
            temp_sb3.setProgress(temp_b);
        }

        //read in scripts

        String scriptStr = currShape.getScript();


        if (!scriptStr.equalsIgnoreCase("")){
            String[] splitedScriptStr = scriptStr.split("\\*");
            // need improvement here in case there are spaces in file or shape name
            for (String scriptName : splitedScriptStr){
                String[] scriptContent = scriptName.split("\\|");
                if(scriptContent != null){
                    scripts.put(scriptName, scriptContent);
                    String scriptWithSpace = joinStrs(scriptContent, " ");
                    scriptNamesTokens.put(scriptWithSpace, scriptName);
                }
            }

            Set<String> scriptNameSet = scripts.keySet();
            String[] scriptNameList = scriptNameSet.toArray(new String[scriptNameSet.size()]);
            for (int idx = 0; idx < scriptNameList.length; idx++){
                String oriStr = scriptNameList[idx];
                String[] tokens = oriStr.split("\\|");
                String scriptNameWithSpace = joinStrs(tokens, " ");
                scriptNameList[idx] = scriptNameWithSpace;
            }


            ArrayAdapter<String> shapeNameAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, scriptNameList);
            scriptsSpinner.setAdapter(shapeNameAdapter);
            if (currSriptName != null){
                String currSriptNameWithSpace = joinStrs(currSriptName.split("\\|")," ");
                scriptsSpinner.setSelection(getSpinnerIdx(scriptsSpinner, currSriptNameWithSpace));
                String[] scriptContent = scripts.get(currSriptName);
                System.out.println();

                if (scriptContent.length == 3){
                    onDropShapeSpinner.setVisibility(View.GONE);
                    onDropShapeSpinner.setAdapter(null);
                    triggerSpinner.setSelection(getSpinnerIdx(triggerSpinner, scriptContent[0]));
                    actionSpinner.setSelection(getSpinnerIdx(actionSpinner, scriptContent[1]));
                    objectSpinner.setSelection(getSpinnerIdx(objectSpinner, scriptContent[2]));
                } else if (scriptContent.length == 4){
                    onDropShapeSpinner.setVisibility(View.VISIBLE);
                    triggerSpinner.setSelection(getSpinnerIdx(triggerSpinner, scriptContent[0]));

                    onDropShapeSpinner.setSelection(getSpinnerIdx(onDropShapeSpinner, scriptContent[1]));
                    System.out.println("hgkjgbkhg");
                    System.out.println(scriptContent[1]);

                    actionSpinner.setSelection(getSpinnerIdx(actionSpinner, scriptContent[2]));
                    objectSpinner.setSelection(getSpinnerIdx(objectSpinner, scriptContent[3]));
                }
            }


        }



        triggerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                String actionSelected = parent.getSelectedItem().toString();
                switch (actionSelected) {
                    case "on drop":
                        Set<String> shapeNameSet = d.shapeDictionary.keySet();
                        String[] shapeNameList = shapeNameSet.toArray(new String[shapeNameSet.size()]);
                        ArrayAdapter<String> shapeNameAdapter = new ArrayAdapter<String>(view.getContext(),
                                android.R.layout.simple_spinner_item, shapeNameList);
                        onDropShapeSpinner.setAdapter(shapeNameAdapter);
                        onDropShapeSpinner.setVisibility(View.VISIBLE);
                        break;
                    case "on click":
                        onDropShapeSpinner.setVisibility(View.GONE);
                        onDropShapeSpinner.setAdapter(null);
                        break;
                    case "on enter":
                        onDropShapeSpinner.setVisibility(View.GONE);
                        onDropShapeSpinner.setAdapter(null);
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                String actionSelected = parent.getSelectedItem().toString();
                switch (actionSelected) {
                    case "goto":
                        Set<String> pageNamesSet = d.pageDictionary.keySet();
                        String[] pageNamesList = pageNamesSet.toArray(new String[pageNamesSet.size()]);
                        ArrayAdapter<String> pageNamesAdapter = new ArrayAdapter<String>(view.getContext(),
                                android.R.layout.simple_spinner_item, pageNamesList);
                        objectSpinner.setAdapter(pageNamesAdapter);
                        objectSpinner.setVisibility(View.VISIBLE);
                        break;

                    case "play":
                        String[] audioNames = {"carrotcarrotcarrot", "evillaugh", "fire", "hooray",
                                "munch", "munching", "woof"};
                        ArrayAdapter<String> audioNamesAdapter = new ArrayAdapter<String>(view.getContext(),
                                android.R.layout.simple_spinner_item, audioNames);
                        objectSpinner.setAdapter(audioNamesAdapter);
                        objectSpinner.setVisibility(View.VISIBLE);
                        break;
                    case "hide":
                    case "show":
                        Set<String> shapeNameSet = d.shapeDictionary.keySet();
                        String[] shapeNameList = shapeNameSet.toArray(new String[shapeNameSet.size()]);
                        ArrayAdapter<String> shapeNameAdapter = new ArrayAdapter<String>(view.getContext(),
                                android.R.layout.simple_spinner_item, shapeNameList);
                        objectSpinner.setAdapter(shapeNameAdapter);
                        break;
                }

            }
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        scriptsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                String actionSelectedWithSpace = parent.getSelectedItem().toString();
                System.out.println("Test:" + actionSelectedWithSpace);

                String actionSelected = scriptNamesTokens.get(actionSelectedWithSpace);
                currSriptName = actionSelected;

                System.out.println("Test:" + currSriptName);
                String[] scriptsContext = scripts.get(actionSelected);
                if (scriptsContext.length == 3){
                    onDropShapeSpinner.setVisibility(View.GONE);
                    onDropShapeSpinner.setAdapter(null);
                    triggerSpinner.setSelection(getSpinnerIdx(triggerSpinner, scriptsContext[0]));
                    actionSpinner.setSelection(getSpinnerIdx(actionSpinner, scriptsContext[1]));
                    objectSpinner.setSelection(getSpinnerIdx(objectSpinner, scriptsContext[2]));
                } else if (scriptsContext.length == 4){
                    onDropShapeSpinner.setVisibility(View.VISIBLE);
                    triggerSpinner.setSelection(getSpinnerIdx(triggerSpinner, scriptsContext[0]));

                    onDropShapeSpinner.setSelection(getSpinnerIdx(onDropShapeSpinner, scriptsContext[1]));
                    System.out.println("hgkjgbkhg");
                    System.out.println(scriptsContext[1]);

                    actionSpinner.setSelection(getSpinnerIdx(actionSpinner, scriptsContext[2]));
                    objectSpinner.setSelection(getSpinnerIdx(objectSpinner, scriptsContext[3]));
                }
            }
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        addScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("111111111");
                String[] scriptContext = readFromSpinners(v);
                System.out.println("22222222221");
                if(scriptContext != null){
                    String scriptName = joinStrs(scriptContext, "|");
                    String scriptNameString = joinStrs(scriptContext, " ");
                    scripts.put(scriptName, scriptContext);
                    scriptNamesTokens.put(scriptNameString, scriptName);
                    currSriptName = scriptName;

                    System.out.println("currSriptName");
                    System.out.println(currSriptName);
                }
                refreshScriptsSpinner(v);
            }
        });

        delScriptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (currSriptName == null || !scripts.containsKey(currSriptName)){
                    Toast.makeText(v.getContext(), "cannot delete", Toast.LENGTH_SHORT).show();
                } else {
                    scripts.remove(currSriptName);
                    String scriptWithSpace = joinStrs(currSriptName.split("\\|"), " ");
                    scriptNamesTokens.remove(scriptWithSpace);
                    refreshScriptsSpinner(v);
                }

            }
        });

        editScript.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (currSriptName == null || !scripts.containsKey(currSriptName)){
                    Toast.makeText(v.getContext(), "cannot edit", Toast.LENGTH_SHORT).show();
                } else {
                    scripts.remove(currSriptName);
                    String[] tokens = currSriptName.split("\\|");
                    String scriptWithSpace = joinStrs(tokens, " ");
                    scriptNamesTokens.remove(scriptWithSpace);

                    String[] scriptContext = readFromSpinners(v);
                    if(scriptContext != null) {
                        String scriptName = joinStrs(scriptContext, "|");
                        String scriptNameString = joinStrs(scriptContext, " ");
                        scripts.put(scriptName, scriptContext);
                        scriptNamesTokens.put(scriptNameString, scriptName);
                        currSriptName = scriptName;
                    }
                    refreshScriptsSpinner(v);
                }

            }
        });

        final View colorView = (View) findViewById(R.id.bgcolor);
        final SeekBar sb0 = (SeekBar) findViewById(R.id.aset);
        final SeekBar sb1 = (SeekBar) findViewById(R.id.rset);
        final SeekBar sb2 = (SeekBar) findViewById(R.id.gset);
        final SeekBar sb3 = (SeekBar) findViewById(R.id.bset);

        sb0.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int i, boolean b) {
                // + "" converts int i to a String
                int alpha = sb0.getProgress();
                int red = sb1.getProgress();
                int green = sb2.getProgress();
                int blue = sb3.getProgress();
                colorView.setBackgroundColor(Color.argb(alpha,red,green,blue));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int i, boolean b) {
                // + "" converts int i to a String
                int alpha = sb0.getProgress();
                int red = sb1.getProgress();
                int green = sb2.getProgress();
                int blue = sb3.getProgress();
                colorView.setBackgroundColor(Color.argb(alpha,red,green,blue));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int i, boolean b) {
                // + "" converts int i to a String
                int alpha = sb0.getProgress();
                int red = sb1.getProgress();
                int green = sb2.getProgress();
                int blue = sb3.getProgress();
                colorView.setBackgroundColor(Color.argb(alpha,red,green,blue));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int i, boolean b) {
                // + "" converts int i to a String
                int alpha = sb0.getProgress();
                int red = sb1.getProgress();
                int green = sb2.getProgress();
                int blue = sb3.getProgress();
                colorView.setBackgroundColor(Color.argb(alpha,red,green,blue));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        confirmChanges.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText theText = (EditText) findViewById(R.id.textInput);
                System.out.println("toStr Start1");
                String theText_value = theText.getText().toString();
                System.out.println("toStr End1");
                if (theText_value.length()>0) {
                    currShape.setText(theText_value);

                    View colorView = (View) findViewById(R.id.bgcolor);
                    SeekBar sb0 = (SeekBar) findViewById(R.id.aset);
                    SeekBar sb1 = (SeekBar) findViewById(R.id.rset);
                    SeekBar sb2 = (SeekBar) findViewById(R.id.gset);
                    SeekBar sb3 = (SeekBar) findViewById(R.id.bset);
                    int alpha = sb0.getProgress();
                    int red = sb1.getProgress();
                    int green = sb2.getProgress();
                    int blue = sb3.getProgress();
                    currShape.setFontColor(Color.argb(alpha,red,green,blue));

                    EditText fsize = (EditText) findViewById(R.id.fontBox);
                    if (fsize.getText().toString().length()>0) {
                        float fsize_value = Float.parseFloat(fsize.getText().toString());
                        currShape.setFontSize(fsize_value);
                    } else {
                        currShape.setFontSize(20);
                    }
                    CheckBox cb_italic = (CheckBox) findViewById(R.id.italicChecker);
                    boolean italic_ind = cb_italic.isChecked();
                    CheckBox cb_bold = (CheckBox) findViewById(R.id.boldChecker);
                    boolean bold_ind = cb_bold.isChecked();
                    CheckBox cb_underline = (CheckBox) findViewById(R.id.underlineChecker);
                    boolean ul_ind = cb_underline.isChecked();

                    currShape.setItalic(italic_ind);
                    currShape.setBold(bold_ind);
                    currShape.setUnderLined(ul_ind);
                }

                //Shape currShape = d.currentPage.currentShape;
                String finalScripts = "";
                for (String scriptName: scripts.keySet()){
                    String[] scriptContent = scripts.get(scriptName);
                    if(scriptContent.length == 3 ){
                        if(scriptContent[1].equals("goto")){
                            mySingleton.getInstance().savedDocument.pageDictionary.get(scriptContent[2]).dependShapeName.add(currShape.name);
                        }
                        if(scriptContent[1].equals("hide")){
                            mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[2]).dependShapeName.add(currShape.name);
                        }
                        if(scriptContent[1].equals("show")){
                            mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[2]).dependShapeName.add(currShape.name);
                        }
                    }
                    if(scriptContent.length == 4){
                        mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[1]).dependShapeName.add(currShape.name);
                        if(scriptContent[2].equals("goto")){
                            mySingleton.getInstance().savedDocument.pageDictionary.get(scriptContent[3]).dependShapeName.add(currShape.name);
                        }
                        if(scriptContent[2].equals("hide")){
                            mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[3]).dependShapeName.add(currShape.name);
                        }
                        if(scriptContent[2].equals("show")){
                            mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[3]).dependShapeName.add(currShape.name);
                        }
                    }
                    String oneScript = joinStrs(scriptContent, "|");
                    finalScripts += oneScript;
                    finalScripts += "*";
                }
                if(finalScripts.length() > 0) {
                    finalScripts = finalScripts.substring(0, finalScripts.length() - 1);
                }
                currShape.setScript(finalScripts);
                System.out.println("toStr Start3");
                String newName = shapeNameTextView.getText().toString();
                System.out.println("toStr End3");
                if(!newName.equals(currShape.name)){
                    try{
                        d.renameShape(currShape, newName);

                    } catch (Exception e){
                        // Notify the user of if the name is already taken
                        Toast toast = Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                currShape.setMovable(isMovableCheck.isChecked());
                currShape.setVisible(!isHiddenCheck.isChecked());



                System.out.println("siyueeeeeeeeeeeeeeeee");
                System.out.println(currShape.getScript());
                currShape.addTriggers();
                Intent intent = new Intent(popShapeEditor.this, EditMode.class);
                startActivity(intent);
            }
        });

        System.out.println("8888888888888");

    }

    private String joinStrs(String[] strs, String seperator){
        StringBuilder strBuilder = new StringBuilder();
        for(String str : strs){
            strBuilder.append(str);
            strBuilder.append(seperator);
        }


        String result = strBuilder.toString();
        result = result.substring(0, result.length() - seperator.length());
        return result;

    }

    private int getSpinnerIdx(Spinner spinner, String str){
        int idx = 0;
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i) != null){
                System.out.println("toStr Start5");
                if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(str)){
                    idx = i;
                    break;
                }
                System.out.println("toStr End5");
            }
        }
        return idx;
    }

    private void refreshScriptsSpinner(View v){
        Set<String> scriptNameSet = scripts.keySet();
        String[] scriptNameList = scriptNameSet.toArray(new String[scriptNameSet.size()]);
        for(int idx = 0; idx < scriptNameList.length; idx++){
            String[] tokens = scriptNameList[idx].split("\\|");
            String scriptName = joinStrs(tokens," ");
            scriptNameList[idx] = scriptName;
        }
        ArrayAdapter<String> shapeNameAdapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_spinner_item, scriptNameList);
        scriptsSpinner.setAdapter(shapeNameAdapter);
        if (currSriptName != null){
            String currSriptNameWithSpace = joinStrs(currSriptName.split("\\|")," ");
            scriptsSpinner.setSelection(getSpinnerIdx(scriptsSpinner, currSriptNameWithSpace));
        }

    }

    String[] readFromSpinners(View v){
        String[] scriptContext = null;
        if (triggerSpinner != null && triggerSpinner.getSelectedItem() != null &&
                actionSpinner != null && actionSpinner.getSelectedItem() != null &&
                objectSpinner != null && objectSpinner.getSelectedItem() != null){
            if (onDropShapeSpinner != null && onDropShapeSpinner.getSelectedItem() != null){
                scriptContext = new String[4];
                System.out.println("toStr Star6");

                scriptContext[0] = triggerSpinner.getSelectedItem().toString();
                scriptContext[1] = onDropShapeSpinner.getSelectedItem().toString();
                scriptContext[2] = actionSpinner.getSelectedItem().toString();
                scriptContext[3] = objectSpinner.getSelectedItem().toString();

                System.out.println("toStr End6");
            } else {
                scriptContext = new String[3];
                System.out.println("toStr Start7");
                scriptContext[0] = triggerSpinner.getSelectedItem().toString();
                scriptContext[1] = actionSpinner.getSelectedItem().toString();
                scriptContext[2] = objectSpinner.getSelectedItem().toString();
                System.out.println("toStr End7");
            }
        } else {
            Toast.makeText(v.getContext(), "Some element not selected.", Toast.LENGTH_SHORT).show();
        }
        return scriptContext;
    }
}
