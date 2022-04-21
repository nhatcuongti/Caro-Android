package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.Interface.EventAfterListen;
import com.example.myapplication.databinding.ActivityDangNhapBinding;
import com.example.myapplication.model.MatchDatabase;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DangNhapActivity extends AppCompatActivity {
    private ActivityDangNhapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDangNhapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.linkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(DangNhapActivity.this, DangKyActivity.class);
                    startActivity(intent);
                }
            });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.username.getText().toString();
                String password = binding.password.getText().toString();

                binding.progressBarWaiting.setVisibility(View.VISIBLE);
                UserDatabase.getInstance().listenCheckLogin(username, password, new EventAfterListen() {
                    @Override
                    public void getObjectAfterEvent(Object o) {
                        binding.progressBarWaiting.setVisibility(View.GONE);
                        Boolean isValid = (Boolean) o;

                        if (isValid){
                            User.setCurrentUser(new User(username, password));
                            Intent intent = new Intent(DangNhapActivity.this, TrangChu.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DangNhapActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}