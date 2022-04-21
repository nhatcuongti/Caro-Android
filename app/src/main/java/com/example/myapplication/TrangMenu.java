package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.Interface.EventAfterListen;
import com.example.myapplication.databinding.ActivityTrangMenuBinding;
import com.example.myapplication.model.Match;
import com.example.myapplication.model.MatchDatabase;
import com.example.myapplication.model.User;

public class TrangMenu extends AppCompatActivity {

    ActivityTrangMenuBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrangMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    public void startGame(){
        // Find Match
        MatchDatabase.getInstance().listenCheckFindingMatch(new EventAfterListen() {
            @Override
            public void getObjectAfterEvent(Object o) {
                Boolean isHavingFindingMatch = (o != null) ? true : false;
                if (isHavingFindingMatch) { // Nếu tìm thấy trận đấu đang chờ
                    Match match = (Match) o;
                    User user = User.getCurrentUser();
                    match.setUser2(user.getUsername());
                    MatchDatabase.getInstance().updateMatch(match);
                }
                else { // Nếu không tìm thấy trận đấu đang chờ
//                    MatchDatabase.getInstance().createMatch();
                    User user = User.getCurrentUser();
                    MatchDatabase.getInstance().createMatch(user.getUsername());
                }
            }
        });
    }
}