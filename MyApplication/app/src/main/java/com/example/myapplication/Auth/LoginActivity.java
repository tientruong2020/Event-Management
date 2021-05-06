package com.example.myapplication.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ContentApp.BottomNavbarActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.ProfileFragment;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

//Login
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //    Login
    private EditText emailId, password;
    private Button btnSignIn;
    private TextView registerUser;
    private FirebaseAuth mFirebaseAuth;
//    End Login
    private Button backToStartBtn;
//    private Button doLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //        Login
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        registerUser = findViewById(R.id.textView3);
        mFirebaseAuth = FirebaseAuth.getInstance();
        btnSignIn = findViewById(R.id.doLoginBtn);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)){
                    Toast.makeText(LoginActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, pwd);
                }
//                if(email.isEmpty()){
//                    emailId.setError("Please enter your email");
//                    emailId.requestFocus();
//                }
//                else  if(pwd.isEmpty()){
//                    password.setError("Please enter your password");
//                    password.requestFocus();
//                }
//                else  if(email.isEmpty() && pwd.isEmpty()){
//                    Toast.makeText(LoginActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
//                }
//                else  if(!(email.isEmpty() && pwd.isEmpty())){
//                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(!task.isSuccessful()){
//                                Toast.makeText(LoginActivity.this,"Login Error, Please Login Again",Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                goToHome();
//                            }
//                        }
//                    });
//                }
//                else{
//                    Toast.makeText(LoginActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
//                }
            }
        });
//        End Login

        /* back to Start Screen */
        backToStartBtn = findViewById(R.id.lBackToStartBtn);
        backToStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToStart();
            }
        });
    }

    public void backToStart(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void goToHome(){
        Intent intent = new Intent(this, BottomNavbarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //    Login
    private void loginUser(String userEmail, String userPassword){

        mFirebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Update the profile "+"for better experience", Toast.LENGTH_SHORT).show();
                    goToHome();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}