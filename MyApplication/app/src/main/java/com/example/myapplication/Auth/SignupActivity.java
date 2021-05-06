package com.example.myapplication.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

//Register
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
//    Register define
    private EditText fullName, emailId, password, confirmPW;
    private Button btnSignUp;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mRootRef;
    ProgressDialog pd;
//    End Register define
    private Button backToStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        Register
        fullName = findViewById(R.id.editTextTextPersonName);
        emailId = findViewById(R.id.editTextTextEmailAddress2);
        password = findViewById(R.id.editTextTextPassword2);
        confirmPW = findViewById(R.id.editTextTextPassword3);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        pd = new ProgressDialog(this);
        btnSignUp = findViewById(R.id.button2);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fullName.getText().toString();
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                String cfpwd = confirmPW.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(cfpwd)){
                    Toast.makeText(SignupActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                } else if (pwd.length() < 8){
                    Toast.makeText(SignupActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                } else if (!pwd.equals(cfpwd)){
                    Toast.makeText(SignupActivity.this, "Confirm password wrong", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, email, pwd);
                }
//                if(email.isEmpty()){
//                    emailId.setError("Please enter email id");
//                    emailId.requestFocus();
//                }
//                else  if(pwd.isEmpty()){
//                    password.setError("Please enter your password");
//                    password.requestFocus();
//                }
//                else  if(email.isEmpty() && pwd.isEmpty()){
//                    Toast.makeText(SignupActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
//                }
//                else  if(!(email.isEmpty() && pwd.isEmpty())){
//                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(!task.isSuccessful()){
//                                Toast.makeText(SignupActivity.this,"SignUp Unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
//                            }
//                            else {
//                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
//                            }
//                        }
//                    });
//                }
//                else{
//                    Toast.makeText(SignupActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();
//
//                }
            }
        });
//        End Register

        backToStartBtn = findViewById(R.id.suBackToStart);

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
    // RegisterUser
    private void registerUser(String fName, String userEmail, String userPassword){

        pd.setMessage("Please wait");
        pd.show();

        mFirebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("Full_Name", fName);
                map.put("Email", userEmail);
                map.put("Password", userPassword);
                map.put("ID", mFirebaseAuth.getCurrentUser().getUid());

                mRootRef.child("Users").child(mFirebaseAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(SignupActivity.this, "Update the profile "+"for better experience", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}