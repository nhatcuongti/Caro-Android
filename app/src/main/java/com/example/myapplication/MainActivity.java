package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Interface.EventAfterListen;
import com.example.myapplication.model.Match;
import com.example.myapplication.model.MatchDatabase;
import com.example.myapplication.model.Move;
import com.example.myapplication.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    GridView gridBanCo;
    AdapterGridViewCustom adapterGridView;
    ArrayList<clsTextView > listTextView;
    TextView txtCurrentPlayer,txtCountDownTime;
    ImageView imgNewGame,imgDanhLai,imgMusic;

    User curUser;
    String partnerId;

    boolean isXplayer,isClickNewGame=false,isPlayMusic=false,isWantNewGame=false;
    int totalOVuong=266,numberOfColumn=14,numberOfRow=totalOVuong/numberOfColumn,oDaDanh=0;//266
    int chessBoard[][]=new int[numberOfRow][numberOfColumn];
    int currentRow=-1,currentColumn=-1;
    CountDownTimer waitTimer;
    int secondsPerPlayer=1*60, thoigian=secondsPerPlayer;
    Stack stack_O_Da_Danh=new Stack();
    MediaPlayer mediaPlayer=null;

    SQLiteDatabase db;
    Calendar calendar=Calendar.getInstance();
    Match curMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran_dau);

        init();
        addData();
        txtCurrentPlayer.setText("X");

        curUser = User.getCurrentUser();

        curMatch = Match.getCurrentMatch();

        if(curUser.getUsername().equals(curMatch.getUser1())) {
            isXplayer = true;
            partnerId = curMatch.getUser2();
        }
        else {
            isXplayer = false;
            partnerId = curMatch.getUser1();
        }

        MatchDatabase.getInstance().listenChangedMoveMatch(curMatch.getID(), new EventAfterListen() {
            @Override
            public void getObjectAfterEvent(Object o) {
                Match match = (Match) o;
                if(match.getMove() != null) {
                    int size = match.getMove().size();
                    Move move = match.getMove().get(size - 1);
                    int index = move.getPosY() * numberOfColumn + move.getPosX();
                    if (move.isX()) {
                        chessBoard[move.getPosY()][move.getPosX()] = 66;//66 ?????i di???n cho x
                        listTextView.set(index, new clsTextView("X", "#ff0000"));
                        adapterGridView.notifyDataSetChanged();
                        txtCurrentPlayer.setText("O");

                        if (isEndGame(66, move.getPosY(), move.getPosX())) {
                            EndGame(66);
                        }
                    } else {
                        chessBoard[move.getPosY()][move.getPosX()] = 88;//66 ?????i di???n cho x
                        listTextView.set(index, new clsTextView("O", "#00cc00"));
                        adapterGridView.notifyDataSetChanged();
                        txtCurrentPlayer.setText("X");

                        if (isEndGame(88, move.getPosY(), move.getPosX())) {
                            EndGame(88);
                        }
                    }

                    curMatch = match;
                    Match.setCurrentMatch(match);

                }

            }
        });

        gridBanCo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l)//index b???t ?????u t??? 0
            {


                currentRow=index/numberOfColumn;
                currentColumn=index%numberOfColumn;

                if(chessBoard[currentRow][currentColumn]==66 || chessBoard[currentRow][currentColumn]==88)
                {
                    Toast.makeText(MainActivity.this,"?? n??y ???? ???????c ????nh, vui l??ng ????nh ?? kh??c!",Toast.LENGTH_SHORT).show();
                    return;
                }



                if(waitTimer != null) {
                    waitTimer.cancel();
                    waitTimer = null;
                }
                CountDownTime();

                if(curMatch.getNextMove().equals(curUser.getUsername())) {
                    if (isXplayer) {
                        chessBoard[currentRow][currentColumn] = 66;//66 ?????i di???n cho x
                        listTextView.set(index, new clsTextView("X", "#ff0000"));
                        adapterGridView.notifyDataSetChanged();
                        stack_O_Da_Danh.push(new Integer(index));
//                    isXplayer=!isXplayer; // set false
                        txtCurrentPlayer.setText("O");

                        if (isEndGame(66, currentRow, currentColumn)) {
                            EndGame(66);
                        }
                    } else {
                        chessBoard[currentRow][currentColumn] = 88;//88 ?????i di???n cho O
                        listTextView.set(index, new clsTextView("O", "#00cc00"));
                        adapterGridView.notifyDataSetChanged();
                        stack_O_Da_Danh.push(new Integer(index));
//                    isXplayer=!isXplayer;
                        txtCurrentPlayer.setText("X");

                        if (isEndGame(88, currentRow, currentColumn)) {
                            EndGame(88);
                        }
                    }
                    curMatch.setNextMove(partnerId);
                    Move move = new Move();
                    move.setIDUser(curUser.getUsername());
                    move.setPosX(currentColumn);
                    move.setPosY(currentRow);
                    move.setID(curMatch.getMove() == null ? "0" : ((Integer)curMatch.getMove().size()).toString());
                    move.setX(isXplayer);

                    if (curMatch.getMove() == null)
                        curMatch.setMove(new ArrayList<>());

                    curMatch.getMove().add(move);
                    MatchDatabase.getInstance().updateMatch(curMatch, new EventAfterListen() {
                        @Override
                        public void getObjectAfterEvent(Object o) {

                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this,"Ch??a ?????n l?????t c???a b???n!",Toast.LENGTH_SHORT).show();
                    return;
                }
                oDaDanh++;
                if(oDaDanh>=totalOVuong)
                {
                    EndGame(44);//44 ?????i di???n cho game h??a
                    return;
                }
            }
        });
        imgNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewGame();
            }
        });
        imgDanhLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DanhLai();
            }
        });
        imgMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayMusic();
            }
        });
    }

    private void NewGame()
    {
        isClickNewGame=true;
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Th??ng b??o");
        alertDialog.setIcon(R.drawable.icongame);
        alertDialog.setMessage("B???n c?? ch???c ch???n mu???n tho??t kh???i tr???n ?????u n??y?");
        alertDialog.setPositiveButton("C??", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // Intent intent=new Intent(MainActivity.this,MainActivity.class);
               // startActivity(intent);

                mediaPlayer.pause();
                mediaPlayer=null;
                isWantNewGame=true;
                onBackPressed();
            }
        });
        alertDialog.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CountDownTime();
            }
        });
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        if(isWantNewGame)
        {
            super.onBackPressed();
        }
        else
        {
            NewGame();
        }
    }
    private void addData()
    {
        for (int i=0;i<totalOVuong;i++)
            listTextView.add(new clsTextView("","#ff0000"));
        adapterGridView.notifyDataSetChanged();
    }

    private void init() {
        txtCurrentPlayer=(TextView)findViewById(R.id.txtCurrentPlayer2);
        isXplayer=true;
        gridBanCo=(GridView)findViewById(R.id.gridBanCo2);
        txtCountDownTime=(TextView)findViewById(R.id.txtCountDownTime2);
        imgNewGame=(ImageView)findViewById(R.id.imgNewGame2);
        imgDanhLai=(ImageView)findViewById(R.id.imgDanhLai2);
        imgMusic=(ImageView)findViewById(R.id.imgMusic2);

        Intent intent=getIntent();
        secondsPerPlayer=intent.getIntExtra("timePerPlay",1)*60;
        thoigian=secondsPerPlayer;
        isPlayMusic=intent.getBooleanExtra("isPlayMusic",false);


        listTextView=new ArrayList<>();
        adapterGridView=new AdapterGridViewCustom(this,R.layout.o_caro,listTextView);
        gridBanCo.setAdapter(adapterGridView);

        int phut= secondsPerPlayer/60;
        int giay=secondsPerPlayer%60;
        String strMinute=phut<10?"0"+phut:phut+"";
        String strSecond=giay<10?"0"+giay:giay+"";
        txtCountDownTime.setText(strMinute+":"+strSecond);

        mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.song);
        mediaPlayer.setLooping(true);
        if(isPlayMusic)
        {
            mediaPlayer.start();
        }
        else {
            imgMusic.setImageResource(R.drawable.mute);
        }
    }
    private void PlayMusic()
    {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imgMusic.setImageResource(R.drawable.mute);
        }
        else {
            mediaPlayer.start();
            imgMusic.setImageResource(R.drawable.loaloa);
        }
    }
    private  void EndGame(int quanco)
    {
//        if(quanco==88)
//        {
//            ghiFile(88);
//        }
//        else if(quanco==66)
//        {
//            ghiFile(66);
//        }
//        else
//        {
//            ghiFile(44);
//        }
        DialogEndGame(quanco);

        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }
    }
    private void DanhLai()
    {
        if(stack_O_Da_Danh.isEmpty())
        {
            Toast.makeText(MainActivity.this,"b??n c??? ch??a ????nh qu??n c??? n??o!",Toast.LENGTH_LONG).show();
            return;
        }
        Integer index= (Integer) stack_O_Da_Danh.pop();
        listTextView.set(index,new clsTextView("","#000000"));
        adapterGridView.notifyDataSetChanged();
        chessBoard[index/numberOfColumn][index%numberOfColumn]=0;//gi?? tr??? m???c ?????nh c???a int l?? 0
        if(isXplayer)//?????n l?????t X ????nh th?? l?????t v???a m??i ????nh l?? O
        {
            txtCurrentPlayer.setText("O");
            isXplayer=!isXplayer;
        }
        else{
            txtCurrentPlayer.setText("X");
            isXplayer=!isXplayer;
        }

        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }
        CountDownTime();
    }
    private String getNowDateTime()
    {
        int nam=calendar.get(Calendar.YEAR);
        int thang=calendar.get(Calendar.MONTH);
        int ngay=calendar.get(Calendar.DATE);
        int gio=calendar.get(Calendar.HOUR_OF_DAY);//tr??? v??? gi???i ?????nh d???ng 24 gi???
        int phut=calendar.get(Calendar.MINUTE);
        int giay=calendar.get(Calendar.SECOND);

        calendar.set(nam,thang,ngay,gio,phut,giay); //set l???i ng??y th??ng n??m hi???n t???i c???a calender ????? d??ng h??m getTime() ph??a d?????i
        SimpleDateFormat dinhDangNgay=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dinhDangNgay.format(calendar.getTime());

    }
    private void ghiFile(int winner)
    {
        String strWinner="";
        if(winner==66)
            strWinner="X th???ng";
        else if(winner==88)
            strWinner="O th???ng";
        else strWinner="H??a";

        db=openOrCreateDatabase("carohistory.db",MODE_PRIVATE,null);
        String sql="Insert into tblcaro(time,winner) values ('"+getNowDateTime()+"','"+strWinner+"');";
        db.execSQL(sql);
        db.close();
    }
    private void DialogEndGame(int quanCoWin)
    {
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//b??? title c???a dialog, d??ng n??y ph???i n???m tr??n d??ng setContentView
        dialog.setContentView(R.layout.layout_end_game);
        //dialog.setTitle("form ????ng nh???p");//title c???a dialog th?? 1 s??? m??y c?? 1 s??? m??y ko c??
        dialog.setCanceledOnTouchOutside(false);//true - m???c ?????nh: th?? khi click ra ngo??i dialog s??? t??? ????ng dialog
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        //dialog b???ng 18/20 chi???u r???ng m??n h??nh, 6/7 chi???u cao m??n h??nh
        dialog.getWindow().setLayout((18 * width)/20, (6* height)/7);

        TextView winer;
        ImageView btnNewGame,btnHome = null;
        winer=(TextView) dialog.findViewById(R.id.txtThang);
        btnNewGame=(ImageView)dialog.findViewById(R.id.imgNewGameInLaoutEndGame);
        btnHome=(ImageView)dialog.findViewById(R.id.imgHomeInLayoutEndGame);

        if(quanCoWin==88)
        {
            winer.setText("O th???ng");
        }
        else if(quanCoWin==66)
        {
            winer.setText("X th???ng");

        }
        else if(quanCoWin==44)
        {
            winer.setText("H??a");

        }

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                mediaPlayer=null;
                isWantNewGame=true;
                onBackPressed();

            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                mediaPlayer=null;
               Intent intent=new Intent(MainActivity.this,TrangChu.class);
               startActivity(intent);
               finish();

            }
        });

        dialog.show();
    }
    private boolean isEndGame(int quanCo,int currentRow,int currentColumn)
    {
        return isEndHorizontal(quanCo,currentRow,currentColumn) || isEndVertical(quanCo,currentRow,currentColumn) ||
            isEndPrimary(quanCo,currentRow,currentColumn) || isEndSubsidiary(quanCo,currentRow,currentColumn);
    }

    private boolean isEndHorizontal(int quanCo,int currentRow,int currentColumn)
    {
        int countLeft=0,countRight=0;
        boolean isBlockedAbove=false,isBlockedBelove=false;
        for(int i=currentColumn ; i>=0 ; i--)//?????m b??n tr??i c???a ?? hi???n t???i v?? c??? ?? hi???n t???i
        {
            if(chessBoard[currentRow][i]==quanCo)
            {
                countLeft++;
            }
            else
            {
                if((chessBoard[currentRow][i]==66 && quanCo==88) ||
                        (chessBoard[currentRow][i]==88 && quanCo==66))
                    isBlockedAbove=true;
                break;
            }
        }
        for(int i=currentColumn+1 ; i<numberOfColumn ; i++)//?????m b??n ph???i c???a ?? hi???n t???i
        {
            if(chessBoard[currentRow][i]==quanCo)
            {
                countRight++;
            }
            else
            {
                if((chessBoard[currentRow][i]==66 && quanCo==88) ||
                        (chessBoard[currentRow][i]==88 && quanCo==66))
                    isBlockedBelove=true;
                break;
            }
        }
        if(isBlockedAbove || isBlockedBelove)
            return countLeft+countRight>=5;
        else if (!isBlockedAbove && !isBlockedBelove)
            return countLeft+countRight>=4;
        else  return countLeft+countRight>=5;
    }

    private boolean isEndVertical(int quanCo,int currentRow,int currentColumn)
    {
        int countAbove=0,countBelow=0;
        boolean isBlockedAbove=false,isBlockedBelove=false;
        for(int i=currentRow ; i>=0 ; i--)//?????m ph??a tr??n c???a ?? hi???n t???i v?? c??? ?? hi???n t???i
        {
            if(chessBoard[i][currentColumn]==quanCo)
            {
                countAbove++;
            }
            else {
                if((chessBoard[i][currentColumn]==66 && quanCo==88) ||
                        (chessBoard[i][currentColumn]==88 && quanCo==66))
                    isBlockedAbove=true;
                break;
            }
        }
        for(int i=currentRow+1 ; i<numberOfRow ; i++)//?????m ph??a d?????i c???a ?? hi???n t???i
        {
            if(chessBoard[i][currentColumn]==quanCo)
            {
                countBelow++;
            }
            else {
                if((chessBoard[i][currentColumn]==66 && quanCo==88) ||
                        (chessBoard[i][currentColumn]==88 && quanCo==66))
                    isBlockedBelove=true;
                break;
            }
        }
        if(isBlockedAbove || isBlockedBelove)
            return countAbove+countBelow>=5;
        else if (!isBlockedAbove && !isBlockedBelove)
            return countAbove+countBelow>=4;
        else  return countAbove+countBelow>=5;
    }

    private boolean isEndPrimary(int quanCo,int currentRow,int currentColumn)
    {
        int countAbove=0,countBelow=0;
        boolean isBlockedAbove=false,isBlockedBelove=false;
        for(int i=0 ; i<=currentColumn ; i++)//?????m ???????ng ch??o ch??nh ph??a tr??n c???a ?? hi???n t???i v?? c??? ?? hi???n t???i
        {
            if(currentRow-i<0 || currentColumn-i<0)
                break;
            if(chessBoard[currentRow-i][currentColumn-i]==quanCo)
            {
                countAbove++;
            }
            else {
                if((chessBoard[currentRow-i][currentColumn-i]==66 && quanCo==88) ||
                        (chessBoard[currentRow-i][currentColumn-i]==88 && quanCo==66))
                    isBlockedAbove=true;
                break;
            }
        }
        for(int i=1 ; i<numberOfColumn - currentColumn ; i++)//?????m ???????ng ch??o ch??nh ph??a d?????i c???a ?? hi???n t???i
        {
            if(currentRow+i>=numberOfRow || currentColumn+i>=numberOfColumn)
                break;
            if(chessBoard[currentRow+i][currentColumn+i]==quanCo)
            {
                countBelow++;
            }
            else {
                if((chessBoard[currentRow+i][currentColumn+i]==66 && quanCo==88) ||
                        (chessBoard[currentRow+i][currentColumn+i]==88 && quanCo==66))
                    isBlockedBelove=true;
                break;
            }
        }
        if(isBlockedAbove || isBlockedBelove)
            return countAbove+countBelow>=5;
        else if (!isBlockedAbove && !isBlockedBelove)
            return countAbove+countBelow>=4;
        else  return countAbove+countBelow>=5;
    }

    private boolean isEndSubsidiary(int quanCo,int currentRow,int currentColumn)
    {
        int countAbove=0,countBelow=0;
        boolean isBlockedAbove=false,isBlockedBelove=false;
        for(int i=0 ; i<=currentRow ; i++)//?????m ???????ng ch??o ph??? ph??a tr??n c???a ?? hi???n t???i v?? c??? ?? hi???n t???i
        {
            if(currentRow-i<0 || currentColumn+i>=numberOfColumn)
                break;
            if(chessBoard[currentRow-i][currentColumn+i]==quanCo)
            {
                countAbove++;
            }
            else {
                if((chessBoard[currentRow-i][currentColumn+i]==66 && quanCo==88) ||
                        (chessBoard[currentRow-i][currentColumn+i]==88 && quanCo==66))
                    isBlockedAbove=true;
                break;
            }
        }
        for(int i=1 ; i<=currentColumn ; i++)//?????m ???????ng ch??o ph??? ph??a d?????i c???a ?? hi???n t???i
        {
            if(currentRow+i>=numberOfRow || currentColumn-i<0)
                break;
            if(chessBoard[currentRow+i][currentColumn-i]==quanCo)
            {
                countBelow++;
            }
            else {
                if((chessBoard[currentRow+i][currentColumn-i]==66 && quanCo==88) ||
                        (chessBoard[currentRow+i][currentColumn-i]==88 && quanCo==66))
                    isBlockedBelove=true;
                break;
            }
        }
        if(isBlockedAbove || isBlockedBelove)
            return countAbove+countBelow>=5;
        else if (!isBlockedAbove && !isBlockedBelove)
            return countAbove+countBelow>=4;
        else  return countAbove+countBelow>=5;
    }

    private void CountDownTime()  {
        if(isClickNewGame)
        {
            thoigian=thoigian;
            isClickNewGame=false;
        }

        else
        {
            thoigian=secondsPerPlayer;
        }

        waitTimer = new CountDownTimer(thoigian*1000+1000, 1000)// + th??m 1s ????? ch???y h??m n??y set l???i th???i gian cho view l?? 00:00
        {

            public void onTick(long millisUntilFinished) {
                //called every 1000 milliseconds, which could be used to
                //send messages or some other action
                int phut= thoigian/60;
                int giay=thoigian%60;
                String strMinute=phut<10?"0"+phut:phut+"";
                String strSecond=giay<10?"0"+giay:giay+"";

                txtCountDownTime.setText(strMinute+":"+strSecond);
                thoigian-=1;
            }

            public void onFinish() {
                //After secondPerPlayer milliseconds (60 sec) finish current
                //if you would like to execute something when time finishes
                if(isXplayer)
                    EndGame(88);//n???u h???t th???i gian m?? ??ang l?? l?????t X ????nh th?? O th???ng.
                else
                    EndGame(66);
            }
        }.start();
    }

}