package edu.stanford.cs108.bunnyworld;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;


/**
 * Created by ysun on 11/22/17.
 */


public class popPossession extends Activity {
    private Document d;
    //private Shape selectedShape;
    ArrayList<Shape> possessionShapeList;
    ArrayList<Shape> currPageShapeList;
    Shape[] possessionShapeArr;
    int selectPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set poop window style and size
        setContentView(R.layout.play_pop_possession);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.3));

        //load info
        d = mySingleton.getInstance().savedDocument;
        possessionShapeList = d.possessionList;
        //selectedShape = d.currentPage.currentShape;
        currPageShapeList = d.currentPage.shapeList;

        if (possessionShapeList.size() == 0){
            Toast.makeText(this, "No item in possession yet",Toast.LENGTH_SHORT).show();
        } else {
            // construct adapter
            possessionShapeArr = possessionShapeList.toArray(new Shape[possessionShapeList.size()]);
            String[] possessionShapeNamesArr = new String[possessionShapeList.size()];
            String[] possessionShapeFileNamesArr = new String[possessionShapeList.size()];
            for (int idx = 0; idx < possessionShapeArr.length; idx++){
                possessionShapeNamesArr[idx] = possessionShapeArr[idx].getName();
                possessionShapeFileNamesArr[idx]= possessionShapeArr[idx].getContent();
            }
            Resources res = getResources();
            final popPossession.possessionViewData[] shapeViewDatas = new popPossession.possessionViewData[possessionShapeFileNamesArr.length];
            for (int idx = 0; idx < possessionShapeFileNamesArr.length; idx++) {
                int imageId = res.getIdentifier(possessionShapeFileNamesArr[idx], "drawable", getPackageName());
                shapeViewDatas[idx] = new popPossession.possessionViewData(possessionShapeNamesArr[idx], imageId);
            }

            ArrayAdapter<popPossession.possessionViewData> adapter =
                    new ArrayAdapter<possessionViewData>(this, R.layout.edit_shape_view, shapeViewDatas){
                        @Override
                        public View getView(int position,
                                            View convertView,
                                            ViewGroup parent){
                            popPossession.possessionViewData currentData = shapeViewDatas[position];
                            if(convertView == null){
                                convertView = getLayoutInflater()
                                        .inflate(R.layout.edit_shape_view, null, false);
                            }

                            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
                            TextView imageName = (TextView) convertView.findViewById(R.id.imageName);
                            imageView.setImageResource(currentData.imageId);
                            imageName.setText(currentData.shapeName);

                            return convertView;
                        }
                    };

            ListView possessionListView = (ListView) findViewById(R.id.playShapeListView);
            possessionListView.setAdapter(adapter);

            possessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    popPossession.possessionViewData currentData = shapeViewDatas[position];
                    Shape selectedShape = possessionShapeArr[position];
                    try{
                        mySingleton.getInstance().savedDocument.possessionList.remove(position);
                        mySingleton.getInstance().savedDocument.currentPage.shapeList.add(selectedShape);
                        Intent intent = new Intent(popPossession.this, PlayMode.class);
                        startActivity(intent);
                    } catch (Exception e){
                        Toast toast = Toast.makeText(getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }


    }

    public class possessionViewData{
        String shapeName;
        int imageId;

        public possessionViewData(String shapeName, int imageId){
            this.shapeName = shapeName;
            this.imageId = imageId;
        }
    }

    public void onCancelPossession(View view){
        Intent intent = new Intent(popPossession.this, PlayMode.class);
        startActivity(intent);
    }
}
