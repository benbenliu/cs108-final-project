package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Bunny World");
    }

    public void goEditModeStarter(View view) {
        Intent intent = new Intent(this, EditModeStarter.class);
        startActivity(intent);
    }

    public void goPlayModeStarter(View view) {
        Intent intent = new Intent(this, PlayModeStarter.class);
        startActivity(intent);
    }

}
