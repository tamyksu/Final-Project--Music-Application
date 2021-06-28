package com.example.project_layouts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyPlace extends AppCompatActivity {
ImageView image;
TextView nameUser, textUser;
DatabaseReference reference;
FirebaseAuth auth;
String userID;
ValueEventListener eventListener;
RequestAdapter adapter;
ArrayList<String> arrayID = new ArrayList<>();
ArrayList<String> names = new ArrayList<>();
RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.personal);
        image = (ImageView) findViewById(R.id.imageUser1);
        nameUser = (TextView) findViewById(R.id.name_user1);
        textUser = (TextView) findViewById(R.id.text_user);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        get_description();// get description

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("name");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name_user = dataSnapshot.getValue(String.class);
                nameUser.setText(name_user);
                FirebaseStorage fb = FirebaseStorage.getInstance();
                StorageReference sr = fb.getReference("images/" + userID);
                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(MyPlace.this).load(uri).into(image);// get user image

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview_requests);


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("users").child(userID).child("requests");
        ValueEventListener  eventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String nameDB = ds.getValue().toString();
                    arrayID.add(nameDB);
                   DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("users").child(nameDB);
                    ValueEventListener  eventListener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            names.add(dataSnapshot.child("name").getValue().toString());
                            adapter = new RequestAdapter(MyPlace.this, arrayID, names);// get list of friend requests
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MyPlace.this));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                    }
                };
                        reference2.addListenerForSingleValueEvent(eventListener2);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference1.addListenerForSingleValueEvent(eventListener1);

    }
    public void get_description()
    {
        reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("description").exists()) {
                    textUser.setText(dataSnapshot.child("description").getValue().toString());
                }
                else{
                    textUser.setText("Description: empty....");

                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }

        };
        reference.addListenerForSingleValueEvent(eventListener);

    }

    public void viewFriends(View view)
    {
        Intent intent = new Intent(MyPlace.this, FriendList.class);
        startActivity(intent);
        finish();
    }
    public void friendRecommendation (View view){
        Intent intent = new Intent(MyPlace.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }
    public void home_page (View view){
        Intent intent = new Intent(MyPlace.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void player (View view){
        Intent intent = new Intent(MyPlace.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }
    public void my_place (View view){
        Intent intent = new Intent(MyPlace.this, MyPlace.class);
        startActivity(intent);
        finish();
    }
    public void settings (View view){
        Intent intent = new Intent(MyPlace.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

}

