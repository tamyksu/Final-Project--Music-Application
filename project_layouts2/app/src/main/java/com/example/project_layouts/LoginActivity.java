package com.example.project_layouts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

public class LoginActivity extends BaseActivity {
    ImageView image_profile;
    FirebaseAuth auth;
    RecyclerView recyclerView;
    BaseActivity baseActivity ;
    SongsRecommendationAdapter songsRecommendationAdapter;
    TextView nameUser;
    String userID;



    @Override
    protected String[] getSongsURLsArray() {
        return super.getSongsURLsArray();
    }

    @Override
    protected String[] getSongsAlbumsNamesArray() {
        return super.getSongsAlbumsNamesArray();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home1);
        baseActivity = new BaseActivity();

        super.startService();
        super.bindService();

        image_profile = (ImageView) findViewById(R.id.image_profile);
        nameUser =  findViewById(R.id.nameUser2);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();

        String []  songsURL = getSongsURLsArray();
        String []  songsAlbum =  getSongsAlbumsNamesArray();

        songsRecommendationAdapter = new SongsRecommendationAdapter(songsURL,songsAlbum,LoginActivity.this);
        recyclerView = (RecyclerView)findViewById(R.id.songs_recommendation_recycler);
        recyclerView.setAdapter(songsRecommendationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(LoginActivity.this));

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
                        Picasso.with(LoginActivity.this).load(uri).into(image_profile);

                    }
                });
            }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                reference.addListenerForSingleValueEvent(eventListener);




    }



    public void player (View view) {
        Intent intent = new Intent(LoginActivity.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }



    public void friendRecommendation (View view){

        Intent intent = new Intent(LoginActivity.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }

    public void my_place(View view)
    {
        Intent intent = new Intent(LoginActivity.this, MyPlace.class);
        startActivity(intent);
        finish();
    }
}
