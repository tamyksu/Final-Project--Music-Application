package com.example.project_layouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerActivity extends BaseActivity  implements  DialogAddSong.MyDialogFragmentListener {

    private SeekBar positionBar;
    private MediaPlayer mediaPlayer;
    TextView player_name , file_name;
    private ImageButton playButton;
    Handler handler = new Handler();
    String playlist_name;
    RecyclerView recyclerView;
    CreatePlaylistAdapter adapter;
    Database database;
    ArrayList<MediaPlayer> media_player_arr = new ArrayList<>();
    ArrayList<String> songs_names = new ArrayList<>();
    ArrayList<String> idS = new ArrayList<>();
    ArrayList<String> playlist_path = new ArrayList<>();
    ArrayList<String> nameP = new ArrayList<>();
    ArrayList<String> genre_song = new ArrayList<>();
    int position, position_fille_music;
    private int time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.player_screen);
        database = MyPlaylistActivity.database;
        playlist_name = getIntent().getStringExtra("playlistName");
        player_name = findViewById(R.id.playlist_name);
        file_name = findViewById(R.id.file_name);
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        playButton = (ImageButton) findViewById(R.id.player_button);
        player_name.setText(" Playlist Name: " + playlist_name);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview_requests);

    // by knowing the playlist name I can get the relevant music files
        Cursor dataSongs = database.getSongs(playlist_name);
        while (dataSongs.moveToNext()){
            idS.add(dataSongs.getString(0));
            songs_names.add(dataSongs.getString(1));
            playlist_path.add(dataSongs.getString(2));
            genre_song.add(dataSongs.getString(3));
            nameP.add(dataSongs.getString(4));
        }
        if (idS.size() == 0)
        {
            Intent intent = new Intent(MediaPlayerActivity.this, MyPlaylistActivity.class);
            startActivity(intent);
        }

        adapter = new CreatePlaylistAdapter(MediaPlayerActivity.this, songs_names,media_player_arr,playlist_name);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MediaPlayerActivity.this));
    }



    public void control_music(final int position) throws IOException {

        position_fille_music = position;
        this.position =position;
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
        }

        Uri mediaPath = Uri.parse(playlist_path.get(position));

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(getApplicationContext(), mediaPath);
            mediaPlayer.prepare();
        time = mediaPlayer.getDuration();
        positionBar.setMax(time);
        UpdateSeekBar p = new UpdateSeekBar();
        new Thread(p).start();

        mediaPlayer.start();
        file_name.setText("Playing: " +  songs_names.get(position));

            positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                        positionBar.setProgress(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });



    }

    public void play(View view) {
        if(mediaPlayer == null)
        {
            Toast.makeText(getApplicationContext(),"Choose song first!",Toast.LENGTH_SHORT).show();
        }
        else {
            mediaPlayer.start();
            file_name.setText("Playing: " + songs_names.get(position));
        }
    }


    public void pause(View view){

    mediaPlayer.pause();
    }
    public class UpdateSeekBar implements Runnable {

        @Override
        public void run() {

            positionBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this,10);

        }
    }

    public void addNewSong(View view){// user add new music file
        FragmentManager fm = getSupportFragmentManager();
        DialogAddSong editNameDialog = new DialogAddSong(playlist_name);
       editNameDialog.show(fm, "fragment_edit_name");
    }

    public void onReturnValue(String file)//update the musics files
    {

        Toast.makeText(this,"Song Added",Toast.LENGTH_SHORT).show();
        Cursor dataSongs = database.getSongs(playlist_name);
       super.classifySong(file);
        idS.clear();
        songs_names.clear();
        playlist_path.clear();
        genre_song.clear();
        nameP.clear();
        while (dataSongs.moveToNext()){
            idS.add(dataSongs.getString(0));
            songs_names.add(dataSongs.getString(1));
            playlist_path.add(dataSongs.getString(2));
            genre_song.add(dataSongs.getString(3));
            nameP.add(dataSongs.getString(4));
        }

        adapter = new CreatePlaylistAdapter(MediaPlayerActivity.this, songs_names,media_player_arr,playlist_name);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MediaPlayerActivity.this));

    }


    public void friendRecommendation (View view){
        Intent intent = new Intent(MediaPlayerActivity.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }
    public void home_page (View view){
        Intent intent = new Intent(MediaPlayerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void my_place (View view){
        Intent intent = new Intent(MediaPlayerActivity.this, MyPlace.class);
        startActivity(intent);
        finish();
    }
    public void player(View view)
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
        }
        Intent intent = new Intent(MediaPlayerActivity.this, MyPlaylistActivity.class);
        intent.putExtra("CameFrom","MediaPlayerActivity");
        startActivity(intent);
    }

}

