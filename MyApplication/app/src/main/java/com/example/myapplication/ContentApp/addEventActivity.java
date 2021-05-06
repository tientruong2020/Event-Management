package com.example.myapplication.ContentApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class addEventActivity extends AppCompatActivity {
    private Button backHomeBtn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        backHomeBtn = findViewById(R.id.backHomeBtn);
        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backHome();
            }
        });
    }
    private void backHome(){
        Intent intent = new Intent(this,BottomNavbarActivity.class);
        startActivity(intent);
    }
}