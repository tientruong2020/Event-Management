package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FBProfileActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private TextView name, email;
    private Button logoutFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_b_profile);

        name = findViewById(R.id.fbT1);
        email = findViewById(R.id.fbT2);
        logoutFB = findViewById(R.id.logout_fb);

        if(mFirebaseUser != null){
            name.setText(mFirebaseUser.getDisplayName());
            email.setText(mFirebaseUser.getEmail());
        }

        logoutFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                openLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mFirebaseUser == null){
            openLogin();
        }
    }

    private void openLogin(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}