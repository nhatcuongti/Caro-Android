package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
    private String ID, User1, User2;
    private int Winner;
    ArrayList<Move> Move;
    String nextMove;
//    ArrayList<Move> listMove;

    public Match(){

    }

    public Match(String ID, String user1, String user2, int winner) {
        this.ID = ID;
        User1 = user1;
        User2 = user2;
        Winner = winner;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUser1() {
        return User1;
    }

    public void setUser1(String user1) {
        User1 = user1;
    }

    public String getUser2() {
        return User2;
    }

    public void setUser2(String user2) {
        User2 = user2;
    }

    public Integer getWinner() {
        return Winner;
    }

    public void setWinner(Integer winner) {
        Winner = winner;
    }

    public ArrayList<com.example.myapplication.model.Move> getMove() {
        return Move;
    }

    public void setMove(ArrayList<com.example.myapplication.model.Move> move) {
        Move = move;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }

    public String getNextMove() {
        return nextMove;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("ID" , ID);
        map.put("User1", User1);
        map.put("User2", User2);
        map.put("Winner", 0);
        map.put("Move", Move);
        map.put("nextMove", nextMove);

        return map;
    }

    private static Match currentMatch;

    public static void setCurrentMatch(Match match){
        currentMatch = match;
    }

    public static Match getCurrentMatch(){
        return currentMatch;
    }
}
