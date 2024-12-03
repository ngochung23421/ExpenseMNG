package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntegerRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.Database.UserDb;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class SignupActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword, edtEmail, edtPhone;
    Button btnSignup;
    TextView tvLogin;
    UserDb userDb;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        userDb = new UserDb(SignupActivity.this);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chuyen ve trang dang nhap
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        signupWithDataSQlite();
    }

    private void signupWithDataSQlite(){
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                // luu thong tin user va password thanh 1 file trong local storage
                if (TextUtils.isEmpty(user)){
                    edtUsername.setError("Username can be not empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password can be not empty");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email can be not empty");
                    return;
                }

                try {
                    // luu vao sqlite dâtbase
                    long insert = userDb.insertDataUser(user,password,email,phone);
                    if (insert == -1){
                        //that bai
                        Toast.makeText(SignupActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                    } else {
                        //thanh cong
                        edtUsername.setText("");
                        edtPassword.setText("");
                        edtEmail.setText("");
                        edtPhone.setText("");
                        Toast.makeText(SignupActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private  void signupWithDataFile(){
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                // luu thong tin user va password thanh 1 file trong local storage
                if (TextUtils.isEmpty(user)){
                    edtUsername.setError("Username can be not empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password can be not empty");
                    return;
                }
                // tien hanh ghi du lieu vao file luu trong loval Storage
                FileOutputStream fileOutputStream = null;
                try {
                    user = user + "|";
                    fileOutputStream = openFileOutput("user.txt", Context.MODE_APPEND);
                    fileOutputStream.write(user.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write(password.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write('\n');
                    fileOutputStream.close();
                    edtUsername.setText("");
                    edtPassword.setText("");
                    Toast.makeText(SignupActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
