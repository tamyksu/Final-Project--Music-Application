package com.example.project_layouts;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyPlaylistActivity extends AppCompatActivity {
    ArrayList<String> playlist_names;
    static Database database;
    String CameFrom;
    ArrayList<MediaPlayer> dummy = new ArrayList<>();
    RecyclerView recyclerView;
    CreatePlaylistAdapter adapter;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.playlist);
            database = new Database((getApplicationContext()));
            playlist_names = new ArrayList<String>();
            CameFrom = getIntent().getStringExtra("CameFrom");
            int checkDB = database.checke_exist();
            if (checkDB > 0)
            {
                Cursor dataPlaylist = database.getPlaylist();//read  from DB

                while (dataPlaylist.moveToNext()){
                    playlist_names.add(dataPlaylist.getString(0));// get all playlists names
                }

            }

                recyclerView = (RecyclerView) findViewById(R.id.recycleview_requests);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView = (RecyclerView) findViewById(R.id.recycleview_requests);
                adapter = new CreatePlaylistAdapter(MyPlaylistActivity.this, playlist_names, dummy," ");
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MyPlaylistActivity.this));

        }

        public void create_playlist(View view){
            Intent intent = new Intent(MyPlaylistActivity.this, CreatePlaylist.class);
            startActivity(intent);
            finish();

        }

    public void my_place(View view)
    {
        Intent intent = new Intent(MyPlaylistActivity.this, MyPlace.class);
        startActivity(intent);
    }
    public void friendRecommendation (View view){
        Intent intent = new Intent(MyPlaylistActivity.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }
    public void home_page (View view){
        Intent intent = new Intent(MyPlaylistActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void player (View view){
        Intent intent = new Intent(MyPlaylistActivity.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }





    }
