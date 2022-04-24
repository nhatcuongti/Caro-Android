package com.example.myapplication.model;

public class Move {
    String ID;
    String IDMatch;
    String IDUser;
    int PosX;
    int PosY;
    boolean isX;

    public Move() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIDMatch() {
        return IDMatch;
    }

    public void setIDMatch(String IDMatch) {
        this.IDMatch = IDMatch;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public int getPosX() {
        return PosX;
    }

    public void setPosX(int posX) {
        PosX = posX;
    }

    public int getPosY() {
        return PosY;
    }

    public void setPosY(int posY) {
        PosY = posY;
    }

    public boolean isX() {
        return isX;
    }

    public void setX(boolean x) {
        isX = x;
    }
}
