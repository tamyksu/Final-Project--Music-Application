package com.example.project_layouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class UserProfile extends AppCompatActivity {
String userID, nameUser,activeUserID;
TextView name;
Database database;
TextView description;
ValueEventListener eventListener;
Button user_button;
DatabaseReference reference;
ImageView image_profile;
String CameFrom;
ImageView image;
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_user_profile);
        auth = FirebaseAuth.getInstance();
        userID = getIntent().getStringExtra("id");
        CameFrom = getIntent().getStringExtra("CameFrom");
        nameUser = getIntent().getStringExtra("name");

        user_button =  findViewById(R.id.user_button);
        image = (ImageView)findViewById(R.id.private_count);
        if(CameFrom.equals( "FriendList")){
            image.setVisibility(View.INVISIBLE);
            user_button.setText("Remove Friend");

        }
        database = new Database((getApplicationContext()));

        image_profile = (ImageView) findViewById(R.id.image_profile2);
        FirebaseUser user = auth.getCurrentUser();
        activeUserID = user.getUid();
        name = findViewById(R.id.nameUser);
        description = findViewById(R.id.description);
        FirebaseStorage fb = FirebaseStorage.getInstance();
        StorageReference sr = fb.getReference("images/" + userID);
        get_description();
        name.setText(nameUser);
        // Adding listener for a single change
        // in the data at this location.
        // this listener will triggered once
        // with the value of the data at the location
        sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(UserProfile.this).load(uri).into(image_profile);

            }
        });


    }

    public void get_description()
    {

        reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.child("description").exists()) {
                    description.setText(dataSnapshot.child("description").getValue().toString());

                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }

        };
        reference.addListenerForSingleValueEvent(eventListener);

    }


    public void remove_friend(View view){
        if (user_button.getText().toString().equals("Add Friend"))
        {
            reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("requests").child(activeUserID);
            reference.setValue(activeUserID);

            database.updateStatus(userID,"request");


            user_button.setText("Waiting for approve");
        }
        else if  (user_button.getText().toString() == "Remove Friend"){
            new AlertDialog.Builder(UserProfile.this)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete " + nameUser + " ?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {



                            FirebaseDatabase.getInstance().getReference("users").child(activeUserID).child("friends").child(userID).removeValue();
                            FirebaseDatabase.getInstance().getReference("users").child(userID).child("friends").child(activeUserID).removeValue();
                            user_button.setText("Add Friend");

                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

                }

    public void my_place(View view)
    {
        Intent intent = new Intent(UserProfile.this, MyPlace.class);
        startActivity(intent);

    }

    public void friendRecommendation (View view){
        Intent intent = new Intent(UserProfile.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }
    public void home_page (View view){
        Intent intent = new Intent(UserProfile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void player (View view){
        Intent intent = new Intent(UserProfile.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }
}
