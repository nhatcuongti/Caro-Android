package com.example.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Interface.EventAfterListen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MatchDatabase {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static MatchDatabase instance = null;

    private void MatchDatabase(){

    }

    public static MatchDatabase getInstance(){
        if (instance == null)
            instance = new MatchDatabase();

        return instance;
    }

    public void listenCheckFindingMatch(EventAfterListen eventAfterListen){
        String path = "/Match";
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);

        databaseReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isFindingMatch = false;
                for (DataSnapshot childrenSnapshot : snapshot.getChildren()){
                    Match match = childrenSnapshot.getValue(Match.class);
                    if (match.getUser2().equals("")){
                        isFindingMatch = true;
                        eventAfterListen.getObjectAfterEvent(match);
                        return;
                    }
                }

                eventAfterListen.getObjectAfterEvent(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateMatch(Match match, EventAfterListen eventAfterListen){
        String path = "/Match";
        DatabaseReference databaseReference = firebaseDatabase.getReference(path).child(match.getID());
        databaseReference.updateChildren(match.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                eventAfterListen.getObjectAfterEvent(null);
            }
        });
    }

    public void removeMatch(String idMatch, EventAfterListen eventAfterListen){
        String path = "/Match";
        DatabaseReference databaseReference = firebaseDatabase.getReference(path).child(idMatch);
        databaseReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                eventAfterListen.getObjectAfterEvent(null);
            }
        });
    }

    public void getLastMatch(EventAfterListen eventAfterListen){
        String path = "/Match";
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        Query query = databaseReference.orderByKey().limitToLast(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot lastSnapshot = null;
                for (DataSnapshot childSnapshot : snapshot.getChildren())
                    lastSnapshot = childSnapshot;

                Match match = lastSnapshot.getValue(Match.class);
                eventAfterListen.getObjectAfterEvent(match.getID());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createMatch(String user1, EventAfterListen eventAfterListen){
        getLastMatch(new EventAfterListen() {
            @Override
            public void getObjectAfterEvent(Object o) {
                String path = "/Match";
                DatabaseReference databaseReference = firebaseDatabase.getReference(path);
                String lastID = (String) o;
                String ID = String.valueOf(Integer.valueOf(lastID).intValue() + 1);

                Match match = new Match(ID, user1, "", 0);
                databaseReference.child(ID).setValue(match.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        eventAfterListen.getObjectAfterEvent(match);
                    }
                });
            }
        });
    }

    public void checkFoundMatch(String idMatch, EventAfterListen eventAfterListen){
        String path = "/Match/" + idMatch;
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Match match = snapshot.getValue(Match.class);

                if (!match.getUser2().equals("")){
                    eventAfterListen.getObjectAfterEvent(match);
                    databaseReference.removeEventListener(this);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.addValueEventListener(eventListener);
    }
}
