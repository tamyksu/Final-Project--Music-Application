package com.example.project_layouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.util.ArrayList;


public class CreatePlaylist extends BaseActivity {

    private static final int SONG_REQUEST =1;
    private String filePath="";
    Database database;
    Button savep;
    EditText file_name, name_playlist;
    CreatePlaylistAdapter adapter;

    RecyclerView recyclerView;

    MediaPlayer mediaPlayer;
    Uri uri_file;

     ArrayList<MediaPlayer> media_player_arr = new ArrayList<MediaPlayer>();
    ArrayList<String> music_path = new ArrayList<String>();
    ArrayList<String> music_name = new ArrayList<String>();
    ArrayList<String> songs_names = new ArrayList<>();
    ArrayList<String> idS = new ArrayList<>();
    ArrayList<String> playlist_path = new ArrayList<>();
    ArrayList<String> nameP = new ArrayList<>();
    ArrayList<String> genre_song = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_create);
        database = MyPlaylistActivity.database;
        savep = findViewById(R.id.savep);
        savep.setVisibility(View.INVISIBLE);
        file_name =  (EditText) findViewById(R.id.name_file);
        name_playlist =  (EditText) findViewById(R.id.name_playlist);
        music_path = new ArrayList<String>();

    }



    public void add_music_fille(View view){
            file_name.getText().clear();
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SONG_REQUEST);

    }

    public void AddDataPlaylist(String name,String idSong){
        boolean insertData = database.addDataPlaylist(name, idSong);
        if(insertData) {
            Toast.makeText(this, "Data Successfully Insered!", Toast.LENGTH_SHORT);
        }
        else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT);
        }
    }


public void update (int position)
{
    music_path.remove(position);
    music_name.remove(position);
}

@Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == SONG_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            filePath = data.getData().toString();
            uri_file= data.getData();
        }
    }
    public void save_playlist(View view) {

       String name_playlist1 =  name_playlist.getText().toString();
       //get songs id  from DB
        int countSongs = database.check_num_of_songs_inPlaylist(name_playlist1);
        for (int i=0 ; i<music_path.size();i++)
        {
            String str = Uri.parse(music_path.get(i)).getPath();
            String arr[] = str.split(":");
            String file =arr[1];
            database.addDataSongs(music_name.get(i), file, "", name_playlist1);

            classifySong(file);

            Cursor dataSongs = database.getSongs(name_playlist1);

            while (dataSongs.moveToNext()){
                idS.add(dataSongs.getString(0));
                songs_names.add(dataSongs.getString(1));
                playlist_path.add(dataSongs.getString(2));
                genre_song.add(dataSongs.getString(3));
                nameP.add(dataSongs.getString(4));

            }

        }


       Cursor dataSongs = database.getSongs(name_playlist1);
        ArrayList<String> listIDSongs = new ArrayList<>();
        while (dataSongs.moveToNext()){
            listIDSongs.add(dataSongs.getString(0));
            AddDataPlaylist(name_playlist1,dataSongs.getString(0));
        }

       if(checkPlaylistDetails(name_playlist1))
       {
           Toast.makeText(CreatePlaylist.this, "The playlist created successfully", Toast.LENGTH_SHORT).show();

          Intent intent = new Intent(CreatePlaylist.this, MyPlaylistActivity.class);
           intent.putExtra("CameFrom", "MusicService");
           startActivity(intent);
           finish();
       }
       else{
           Toast.makeText(CreatePlaylist.this, "Name playlist is empty!", Toast.LENGTH_SHORT).show();

       }
    }
    boolean checkPlaylistDetails(String name_playlist1){
        if(name_playlist1 == null || name_playlist1.equals("") ) return false;
        return true;
    }

    public void save(View view) throws IOException {

        savep.setVisibility(View.VISIBLE);
        if(filePath=="" || file_name.getText().toString()=="")
            Toast.makeText(CreatePlaylist.this, "file music or name are empty!", Toast.LENGTH_SHORT).show();
        else {
           mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(CreatePlaylist.this, uri_file);
            media_player_arr.add(mediaPlayer);
            mediaPlayer.prepare();
           music_path.add(filePath);
            music_name.add(file_name.getText().toString());
            file_name.getText().clear();
            recyclerView = (RecyclerView) findViewById(R.id.recycleview_requests);
            adapter = new CreatePlaylistAdapter(CreatePlaylist.this, music_name,media_player_arr,"createPlaylist");
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(CreatePlaylist.this));

        }

    }

    public void my_place(View view)
    {
        Intent intent = new Intent(CreatePlaylist.this, MyPlace.class);
        startActivity(intent);
    }
    public void search (View view){
        Intent intent = new Intent(CreatePlaylist.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }
    public void home_page (View view){
        Intent intent = new Intent(CreatePlaylist.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void player (View view){
        Intent intent = new Intent(CreatePlaylist.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }












}
