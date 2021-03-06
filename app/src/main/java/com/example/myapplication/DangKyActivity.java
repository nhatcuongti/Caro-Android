package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.Interface.EventAfterListen;
import com.example.myapplication.Utils.PreferenceManager;
import com.example.myapplication.databinding.ActivityDangKyBinding;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserDatabase;

public class DangKyActivity extends AppCompatActivity {

    private ActivityDangKyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDangKyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDatabase userDatabase = UserDatabase.getInstance();
                String username = binding.username.getText().toString();
                String password = binding.password.getText().toString();
                binding.proressBarWaiting.setVisibility(View.VISIBLE);
                userDatabase.listenCheckExistUser(username, new EventAfterListen() {
                    @Override
                    public void getObjectAfterEvent(Object o) {
                        binding.proressBarWaiting.setVisibility(View.GONE);
                        Boolean isValid = isValid((Boolean) o);

                        if (isValid){
                            userDatabase.updateData(new User(username, password));
                            PreferenceManager preferenceManager = PreferenceManager.getInstance(getApplicationContext());
                            User.setCurrentUser(new User(username, password));
                            Intent intent = new Intent(DangKyActivity.this, TrangChu.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    public boolean isValid(Boolean isUserExists){

        if (binding.username.getText().toString().equals("")) { // Username tr???ng
            Toast.makeText(this, "B???n ch??a ??i???n Username", Toast.LENGTH_LONG).show();
            return  false;
        }
        else if (binding.password.getText().toString().equals("")) { // Password tr???ng
            Toast.makeText(this, "B???n ch??a ??i???n Password", Toast.LENGTH_LONG).show();
            return  false;

        }
        else if (binding.retypePassword.getText().toString().equals("")) { // Retype password tr???ng
            Toast.makeText(this, "B???n ch??a ??i???n Retype Password", Toast.LENGTH_LONG).show();
            return  false;

        }
        else if (!binding.password.getText().toString().equals(binding.retypePassword.getText().toString())) { // Password v?? retype password kh??c nhau
            Toast.makeText(this, "Password kh??ng gi???ng nhau", Toast.LENGTH_LONG).show();
            return  false;

        }
        else if (isUserExists){
            Toast.makeText(this, "Username n??y ???? t???n t???i, vui l??ng nh???t l???i", Toast.LENGTH_LONG).show();
            return  false;
        }


        return true;
    }
}