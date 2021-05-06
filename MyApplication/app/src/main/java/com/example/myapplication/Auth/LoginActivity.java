package com.example.myapplication.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.ContentApp.BottomNavbarActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.ProfileFragment;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

//Login
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //    Login
    EditText emailId, password;
    Button btnSignIn;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    End Login
    private Button backToStartBtn;
//    private Button doLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //        Login
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btnSignIn = findViewById(R.id.doLoginBtn);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    Toast.makeText(LoginActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//                    startActivity(i);
                    goToHome();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else  if(!validEmail(email)){
                    Toast.makeText(LoginActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Login Error, Please Login Again",Toast.LENGTH_SHORT).show();
                            }
                            else{
//                                Intent intToHome = new Intent(LoginActivity.this, HomeActivity.class);
//                                startActivity(intToHome);
                                goToHome();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });
//        End Login

        backToStartBtn = findViewById(R.id.lBackToStartBtn);
//        doLoginBtn = findViewById(R.id.doLoginBtn);

        /* back to Start Screen */
        backToStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToStart();
            }
        });

        /* go to Home */
//        doLoginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                goToHome();
//            }
//        });
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

    private boolean validEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}