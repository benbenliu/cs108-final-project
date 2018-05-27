package edu.stanford.cs108.bunnyworld;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

/**
 * Created by ysun on 11/22/17.
 */

public class popAddShape extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_add_shape);
        setTitle("Add Shape");

        // Set pop window size
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.3));

        // Set shapeListView
        Resources res = getResources();
        String imageNames[] = {"blank_text_shape", "bunny", "cute3", "fire0", "door", "evilbunny","mogu", "carrot", "carrot2", "death", "duck", "fire", "mystic", "cute1"};
        int number_o_files = imageNames.length;
        final shapeViewData[] shapeViewDatas = new shapeViewData[number_o_files];
        for (int idx = 0; idx < number_o_files; idx++) {
            int imageId = res.getIdentifier(imageNames[idx], "drawable", getPackageName());
            shapeViewDatas[idx] = new shapeViewData(imageNames[idx], imageId);
        }
        ArrayAdapter<shapeViewData> adapter =
                new ArrayAdapter<shapeViewData>(this, R.layout.edit_shape_view, shapeViewDatas){
            @Override
            public View getView(int position,
                                View convertView,
                                ViewGroup parent){
                shapeViewData currentData = shapeViewDatas[position];
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
        ListView shapeListView = (ListView) findViewById(R.id.shapeListView);
        shapeListView.setAdapter(adapter);

        shapeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                shapeViewData currentData = shapeViewDatas[position];
                Shape newShape = new Shape("", 160, 160, 0, 0);
                newShape.setImagePath(currentData.imageName);
                try{
                    mySingleton.getInstance().savedDocument.addShape(newShape);
                    Intent intent = new Intent(popAddShape.this, EditMode.class);
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

    public class shapeViewData{
        String imageName;
        int imageId;

        public shapeViewData(String imageName, int imageId){
            this.imageName = imageName;
            this.imageId = imageId;
        }
    }

    public void onCancelAddShape(View view){
        Intent intent = new Intent(this, EditMode.class);
        startActivity(intent);
    }
}
