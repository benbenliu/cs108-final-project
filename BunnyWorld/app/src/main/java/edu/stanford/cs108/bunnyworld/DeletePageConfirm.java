package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashSet;

public class DeletePageConfirm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_page_confirm);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height * 0.2));

        TextView deleteConfirmText = (TextView) findViewById(R.id.deleteConfirmText);
        deleteConfirmText.setText("Are you sure to delete "
                + mySingleton.getInstance().savedDocument.currentPage.name
                + "?");
    }

    public void onDelete(View view){
        try{
            Page page = mySingleton.getInstance().savedDocument.currentPage;
            checkDependentShape(page);
            mySingleton.getInstance().savedDocument.deletePage(page.name);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee after check shape");
            Intent intent = new Intent(this, EditMode.class);
            startActivity(intent);
        } catch (Exception e){
            Toast toast = Toast.makeText(getApplicationContext(),
                    e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void checkDependentShape(Page page){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in checkDependentShape");
        HashSet<String> dependShapeNames = page.dependShapeName;
        String pageName = page.name;
        for(String name: dependShapeNames){
            System.out.println("depend shape name: ");
            System.out.println(name);
            if(mySingleton.getInstance().savedDocument.shapeDictionary.get(name) == null){
                continue;
            }
            String scripts = mySingleton.getInstance().savedDocument.shapeDictionary.get(name).getScript();
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in after get the final scripts");
            String new_scripts = deleteShapeScript(pageName, scripts);
            System.out.println(String.format("new_sciprts {}", new_scripts));
            mySingleton.getInstance().savedDocument.shapeDictionary.get(name).setScript(new_scripts);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
    }
    public String deleteShapeScript(String pageName, String script){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript");
        System.out.println(script);
        String[] clauses = script.split("\\*");
        String finalScripts = "";
        for(String clause: clauses){
            String[] splitStr = clause.split("\\|");
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript for loop");
            System.out.println(splitStr[1]);
            System.out.println(splitStr[2]);
            if(!(splitStr[1].equals("goto") && splitStr[2].equals(pageName))) {

                finalScripts += clause;
                finalScripts += "*";
            }
        }
        System.out.println(finalScripts);
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        return finalScripts;
    }

    public void onCancelDelete(View view){
        Intent intent = new Intent(this, EditMode.class);
        startActivity(intent);
    }
}
