package com.example.myapplication.model;

import androidx.annotation.NonNull;

import com.example.myapplication.Interface.EventAfterListen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDatabase {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static UserDatabase instance = null;

    private UserDatabase(){

    }

    public static UserDatabase getInstance(){
        if (instance == null)
            instance = new UserDatabase();

        return instance;
    }

   public void updateData(User user){
        String path = "/User";
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);
        databaseReference.child(user.getUsername()).setValue(user.toMap());
   }

   public void listenCheckLogin(String username, String password, EventAfterListen eventAfterListen){
       String path = "/User";
       DatabaseReference databaseReference = firebaseDatabase.getReference(path);

       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               Boolean isExists = false;
               for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                   User user = childSnapshot.getValue(User.class);
                   if (user.username.equals(username) && user.password.equals(password)){
                       isExists = true;
                       eventAfterListen.getObjectAfterEvent(isExists);
                       return;
                   }
               }

               eventAfterListen.getObjectAfterEvent(isExists);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
   }

    public void listenCheckExistUser(String username, EventAfterListen eventAfterListen){
        String path = "/User";
        DatabaseReference databaseReference = firebaseDatabase.getReference(path);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isExists = false;
                for (DataSnapshot childSnapshot : snapshot.getChildren())
                    if (childSnapshot.getKey().equals(username)){
                        isExists = true;
                        eventAfterListen.getObjectAfterEvent(isExists);
                        return;
                    }

                eventAfterListen.getObjectAfterEvent(isExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void listenUpdateUserData(User user, EventAfterListen eventAfterListen){

    }
}
