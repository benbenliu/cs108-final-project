package edu.stanford.cs108.bunnyworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class PlayModeStarter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mode_starter);
        setTitle("Play Mode Starter");
    }

    public void goPlayMode(View view){
        Intent intent = new Intent(this, LoadActivity.class);
        intent.putExtra("isNewPlay", true);
        startActivity(intent);
    }

    public void goLoad(View view){
        Intent intent = new Intent(this, LoadActivity.class);
        intent.putExtra("isNewPlay", false);
        startActivity(intent);
    }


    public void goDefault(View view){
        Intent intent = new Intent(this, PlayMode.class);
        intent.putExtra("fromPreload", true);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.play_starter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.select_mode:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
