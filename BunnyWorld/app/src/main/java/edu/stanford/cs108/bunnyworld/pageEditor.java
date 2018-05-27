package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by yueliu on 12/1/17.
 */

public class pageEditor extends AppCompatActivity {
    int backgroundImageId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);
        setTitle("Page Editor");

        Resources res = getResources();
        String imageNames[] = {"back1", "back2", "back3", "back4", "back5", "back6", "back7","back8", "back9", "back10",
                "back11","back12","back13","back14","back15",
        "back16","back17","back18","back19","back20","back21","back22","back23","back24","back25","back26",
                "back27","back28","back29","back30","back31","back32","back33","back34","back35",
        "back36","back37","back38","back39","back40","back41"};
        int number_o_files = imageNames.length;
        final pageViewData[] pageViewDatas = new pageViewData[number_o_files];
        for (int idx = 0; idx < number_o_files; idx++) {
            int imageId = res.getIdentifier(imageNames[idx], "drawable", getPackageName());
            pageViewDatas[idx] = new pageViewData(imageNames[idx], imageId);
        }

        ArrayAdapter<pageViewData> adapter =
                new ArrayAdapter<pageViewData>(this, R.layout.edit_shape_view, pageViewDatas){
                    @Override
                    public View getView(int position,
                                        View convertView,
                                        ViewGroup parent){
                        pageViewData currentData = pageViewDatas[position];
                        if(convertView == null){
                            convertView = getLayoutInflater()
                                    .inflate(R.layout.edit_shape_view, null, false);
                        }

                        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
                        TextView imageName = (TextView) convertView.findViewById(R.id.imageName);
                        imageView.setImageResource(currentData.imageId);
                        imageName.setText(currentData.imageName);

                        return convertView;
                    }
                };
        ListView pageListView = (ListView) findViewById(R.id.pageBackgroundListView);
        pageListView.setAdapter(adapter);

        pageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pageViewData currentData = pageViewDatas[position];
                System.out.println(currentData.imageName);
                System.out.println(currentData.imageId == R.drawable.carrot2);
                backgroundImageId=currentData.imageId;
                String toShow = "Selected image "+currentData.imageName+" as new background";
                Toast.makeText(pageEditor.this, toShow, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class pageViewData{
        String imageName;
        int imageId;

        public pageViewData(String imageName, int imageId){
            this.imageName = imageName;
            this.imageId = imageId;
        }
    }

    public void onDeletePage(View view) {
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in onDeletePage");
        Intent intent = new Intent(this, DeletePageConfirm.class);
        startActivity(intent);

    }

    public void onCancel(View view){
        Intent intent = new Intent(this, EditMode.class);
        startActivity(intent);
    }

    public void onChangePage(View view){
        //set background image id
        if(backgroundImageId != 0){
            mySingleton.getInstance().savedDocument.currentPage.backgroundImageId = backgroundImageId;
        }
//        mySingleton.getInstance().savedDocument.pageDictionary.get(mySingleton.getInstance().savedDocument.currentPage.name).backgroundImageId=backgroundImageId;
        Intent intent = new Intent(this, EditMode.class);
        //set new page name
        EditText newNameEdit = (EditText) findViewById(R.id.newPageName);
        CheckBox cb = (CheckBox) findViewById(R.id.tile_background);
        mySingleton.getInstance().savedDocument.currentPage.isBackgroundTiled = cb.isChecked();

        String newPageName = newNameEdit.getText().toString();
        if(newPageName.length() != 0){
            try{
                mySingleton.getInstance().savedDocument.renamePage(mySingleton.getInstance().savedDocument.currentPage.name,
                        newPageName);
                //go back
                startActivity(intent);
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else{
            startActivity(intent);
        }

    }
}
