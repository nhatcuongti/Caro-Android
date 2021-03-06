package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.myapplication.Interface.EventAfterListen;
import com.example.myapplication.databinding.ActivityTrangMenuBinding;
import com.example.myapplication.databinding.DialogTimTranDauBinding;
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

        String[] minuteList = {"1", "2", "5"};
        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, minuteList);
        binding.minuteSpinner.setAdapter(ad);

        binding.btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        binding.btnLichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangMenu.this, LichSuActivity.class);
                startActivity(intent);
            }
        });
    }

    public void moveToTranDauActivity(Match match){
        Intent intent = new Intent(TrangMenu.this, MainActivity.class);
        match.setNextMove(match.getUser1());
        Match.setCurrentMatch(match);
        startActivity(intent);
    }

    Dialog dialog ;

    public void startDialog(Match match){
        dialog = new Dialog(this);
        DialogTimTranDauBinding dialogBinding = DialogTimTranDauBinding.inflate(getLayoutInflater());
        View view = dialogBinding.getRoot();
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialogBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEvent(match);
            }
        });
    }

    public void cancelEvent(Match match){
        MatchDatabase.getInstance().removeMatch(match.getID(), new EventAfterListen() {
            @Override
            public void getObjectAfterEvent(Object o) {
                dialog.dismiss();
            }
        });
    }

    // Khi b???m v??o h???y th?? x??a tr???n ?????u m???i nh???t .

    public void startGame(){
        // Find Match

        MatchDatabase.getInstance().listenCheckFindingMatch(new EventAfterListen() {
            @Override
            public void getObjectAfterEvent(Object o) {
                Boolean isHavingFindingMatch = (o != null) ? true : false;
                if (isHavingFindingMatch) { // N???u t??m th???y tr???n ?????u ??ang ch???
                    Match match = (Match) o;
                    User user = User.getCurrentUser();
                    match.setUser2(user.getUsername());
                    MatchDatabase.getInstance().updateMatch(match, new EventAfterListen() {
                        @Override
                        public void getObjectAfterEvent(Object o) {
                            moveToTranDauActivity(match);
                        }
                    });
                }
                else { // N???u kh??ng t??m th???y tr???n ?????u ??ang ch???
//                    MatchDatabase.getInstance().createMatch();
                    User user = User.getCurrentUser();
                    MatchDatabase.getInstance().createMatch(user.getUsername(), new EventAfterListen() {
                        @Override
                        public void getObjectAfterEvent(Object o) {
                            Match match = (Match) o;
                            startDialog(match);

                            MatchDatabase.getInstance().checkFoundMatch(match.getID(), new EventAfterListen() {
                                @Override
                                public void getObjectAfterEvent(Object o) {
                                    dialog.dismiss();
                                    Match matchFull = (Match) o;
                                    moveToTranDauActivity(matchFull);
                                }
                            });
                        }
                    });


                }
            }
        });
    }
}