package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

/**
 * Created by siyuewu on 12/6/17.
 */

public class leavePageConfirm extends AppCompatActivity {
    Document d;
    Boolean isEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_page_confirm);
        d = mySingleton.getInstance().savedDocument;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height * 0.2));
        Intent intent = getIntent();
        isEdit = intent.getBooleanExtra("is edit", false);
        TextView deleteConfirmText = (TextView) findViewById(R.id.leaveConfirmText);
        deleteConfirmText.setText("Do you want to leave without saving ?");
    }

//    public void onCancelLeavePage(View view){
//        Intent intent = new Intent(this, EditMode.class);
//        startActivity(intent);
//    }

    public void onSaveLeavePage(View view){
        Intent intent = new Intent(this, LoadActivity.class);

        intent.putExtra("isEdit", isEdit);
        intent.putExtra("isSaving", true);
        startActivity(intent);
    }

    public void onLeavePage(View view){
//        mySingleton.getInstance().currentPageView.setPage(d.pageDictionary.get("page1"));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
