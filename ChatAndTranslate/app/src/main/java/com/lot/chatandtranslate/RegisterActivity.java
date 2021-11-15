package com.lot.chatandtranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {

    EditText inputEmail;
    ShowHidePasswordEditText inputPassword;
    Button buttonRegister;
    ImageView backImg;
    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = findViewById(R.id.register_id);
        inputPassword = findViewById(R.id.register_password);
        buttonRegister = findViewById(R.id.btn_register);
        backImg = findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttemptRegisteration();
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AttemptRegisteration() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if(email.isEmpty() || !email.contains("@gmail.com")) {
            showError(inputEmail, "옳바르지 않은 이메일입니다.");
        } else if(password.isEmpty() || password.length() < 5) {
            showErrorPass(inputPassword, "패스워드는 5글자 이상이어야 합니다.");
        } else {
            mLoadingBar.setTitle("회원가입");
            mLoadingBar.setMessage("잠시만 기다려주세요...");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void showError(EditText field, String text) {
        field.setError(text);
        field.requestFocus();
    }
    private void showErrorPass(ShowHidePasswordEditText field, String text) {
        field.setError(text);
        field.requestFocus();
    }
}