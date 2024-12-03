package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.Database.UserDb;
import com.example.expensetracker.model.UserModel;

import java.io.FileInputStream;

public class LoginActivity extends AppCompatActivity {

    EditText edtUser, edtPass;
    Button btnSignIn;
    TextView tvSignup;

    UserDb userDb;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout_login);
        edtUser = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        userDb = new UserDb(LoginActivity.this);

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chuyen sang man hinh dang ky tai khoan
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        loginWithDataSQLite();
    }

    private void loginWithDataFile(){
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUser.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                if (TextUtils.isEmpty(user)){
                    edtUser.setError("Username can be not empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPass.setError("Password can be not empty");
                    return;
                }

                // doc du lieu tu file trong local storage de kiem tra thong tin tai khoan
                try {
                    FileInputStream fileInputStream = openFileInput("user.txt");
                    int read = -1;
                    StringBuilder builder = new StringBuilder();
                    while ((read = fileInputStream.read()) != -1){
                        builder.append((char) read);
                    }
                    fileInputStream.close();
                    // toan bo du lieu cua file duoc doc vao trong bien builder
                    // can xu ly de kiem tra xem tai khoan ma nguoi dung dang nhap
                    // co nam trong du lieu cua file khong?
                    boolean checkLogin = false;
                    String[] infoAccount = null;
                    infoAccount = builder.toString().trim().split("\n");
                    // duyet mang
                    for (String account : infoAccount){
                        String username = account.substring(0, account.indexOf("|")).trim();
                        String pass = account.substring(account.indexOf("|")+1).trim();
                        if (username.equals(user) && pass.equals(password)){
                            // dang nhap thanh cong
                            checkLogin = true;
                            break;
                        }
                    }
                    if (checkLogin){
                        //dang nhap thanh cong
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("USERNAME", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish(); //khong cho phep quay lai tro lai activity
                    }else {
                        //dang nhap that bai
                        Toast.makeText(LoginActivity.this, "Account Invalid", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void loginWithDataSQLite(){
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUser.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                if (TextUtils.isEmpty(user)){
                    edtUser.setError("Username can be not empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPass.setError("Password can be not empty");
                    return;
                }

                // doc du lieu tu file trong local storage de kiem tra thong tin tai khoan
                try {
                    UserModel data = userDb.getInfoUser(user, password);
                    assert data != null;
                    if (data.getUsername() !=null){
                        //thanh cong
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("USERNAME", data.getUsername());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();

                    } else {
                        //that bai
                        Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
    }
}