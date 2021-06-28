package com.example.project_layouts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {


    private static final String TABLE_NAME_SONGS = "songs1";
    private static final String S_COL2 = "name";
    private static final String S_COL3 = "path";
    private static final String S_COL4 = "genre";
    private static final String S_COL5 = "namep";


    private static final String TABLE_NAME_PLAYLIST = "playlist1";
    private static final String P_COL2 = "name";
    private static final String P_COL3 = "idSong";


    private static final String TABLE_NAME_RECOMMENDATION = "recommendationx";
    private static final String R_COL2 = "userID";
    private static final String R_COL3 = "name";
    private static final String R_COL4 = "generes";
    private static final String R_COL5 = "status";

    public Database(Context context){

        super(context,TABLE_NAME_RECOMMENDATION,null,1);


    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableSongs = "CREATE TABLE " + TABLE_NAME_SONGS+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                S_COL2+ " TEXT, "+ S_COL3+" TEXT, "+ S_COL4+ " TEXT, "+ S_COL5+" TEXT)";
        String createTablePlaylists = "CREATE TABLE " + TABLE_NAME_PLAYLIST+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                P_COL2+ " TEXT, "+ P_COL3+" TEXT)";

        String createTableRecommendation = "CREATE TABLE " + TABLE_NAME_RECOMMENDATION+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_COL2+ " TEXT NOT NULL, "+ R_COL3+" TEXT, " + R_COL4+" TEXT, " + R_COL5+" TEXT)";


        db.execSQL(createTableSongs);
        db.execSQL(createTablePlaylists);
        db.execSQL(createTableRecommendation);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getRecommendation(){
        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME_RECOMMENDATION +" WHERE status IS NULL ";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void putRecommendation(String id, String name , String genres)//genres = "Rock Pop Classical"
    {
        if(check_if_friend_exist_by_id(id) > 0)
            return;

        ContentValues contentValues = new ContentValues();
        contentValues.put(R_COL2, id);
        contentValues.put(R_COL3, name);
        contentValues.put(R_COL4, genres);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_RECOMMENDATION, null,contentValues);
    }

    public int check_if_friend_exist_by_id(String id){
        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME_RECOMMENDATION+" WHERE userID = '"+id+"'";
        Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        int count = data.getInt(0);
        return count;
    }

    public void updateStatus(String id, String put_status)
    {
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(R_COL5, put_status);
        db.update(TABLE_NAME_RECOMMENDATION, args, " userID = '"+id+"'",null);

    }




    public void delete()
    {
        SQLiteDatabase db  = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_RECOMMENDATION);
        db.execSQL("DELETE FROM " + TABLE_NAME_PLAYLIST);
        db.execSQL("DELETE FROM " + TABLE_NAME_SONGS);
    }


    public void addDataSongs(String name, String path ,String genre, String namep){

        ContentValues contentValues = new ContentValues();
        contentValues.put(S_COL2, name);
        contentValues.put(S_COL3, path);
        contentValues.put(S_COL4, genre);
        contentValues.put(S_COL5, namep);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_SONGS, null,contentValues);
    }




    public boolean addDataPlaylist( String name, String idSong){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(P_COL2, name);
        contentValues.put(P_COL3, idSong);

        long result = db.insert(TABLE_NAME_PLAYLIST, null,contentValues);
        if (result == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor getSongs(String name_playlist){
        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME_SONGS +" WHERE namep = '"+name_playlist+"'";
        Cursor data = db.rawQuery(query, null);
        return data;

    }
    public void updateGenere(String path, String genre) {
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(S_COL4, genre);
        db.update(TABLE_NAME_SONGS, args, "path = '"+path+"'",null);
    }
    public int[] getCountersForAllGenres(){
        int [] counters = new int [10];
        String [] genres = {"Blues", "Classical", "Country", "Disco", "Hiphop", "Jazz", "Metal", "Pop", "Reggae", "Rock"};
        for(int i=0 ; i< 10 ; i++){
            String genre = genres[i];
            SQLiteDatabase db  = this.getWritableDatabase();
            String query = "SELECT COUNT(*) FROM "+TABLE_NAME_SONGS +" WHERE genre = '"+genre+"'";
            Cursor data = db.rawQuery(query, null);
            data.moveToFirst();
            counters[i] = data.getInt(0);
        }
        return counters;
    }


    public Cursor getAllSongs(){
        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME_SONGS ;
        Cursor data = db.rawQuery(query, null);
        return data;

    }

    public Cursor getPlaylist(){
        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT DISTINCT name FROM "+TABLE_NAME_PLAYLIST ;
        Cursor data = db.rawQuery(query, null);
        return data;

    }
    public int check_num_of_songs_inPlaylist(String PlaylitName){
        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT  COUNT(*) FROM " +TABLE_NAME_SONGS+" WHERE namep  = '"+PlaylitName+"'";
        Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        int count = data.getInt(0);
        return count;
    }
    public int checke_exist(){
        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT  COUNT(*) FROM " + TABLE_NAME_PLAYLIST+" WHERE idSong IS NOT NULL";
        Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        int count = data.getInt(0);
        return count;
    }

    public void deletePlaylist(String namePlaylist)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete songs of playlistname
        db.execSQL("DELETE FROM "+ TABLE_NAME_SONGS+" WHERE namep = '"+namePlaylist+"'");
        db.execSQL("DELETE FROM "+ TABLE_NAME_PLAYLIST+" WHERE name = '"+namePlaylist+"'");

    }
    public void deleteSongsRow(String get_ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // get playlist name for delting from playlist table this song
        String query = "SELECT namep FROM "+TABLE_NAME_SONGS + " WHERE ID = '"+get_ID+"'";
        Cursor data = db.rawQuery(query, null);

        //delete from table songs
        db.execSQL("DELETE FROM "+ TABLE_NAME_SONGS+" WHERE ID = '"+get_ID+"'");
        // delete from table playlist

    }

    public void deletePlaylistRow(String namePlaylist ,String get_ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // get playlist name for delting from playlist table this song
        db.execSQL("DELETE FROM "+ TABLE_NAME_PLAYLIST+" WHERE name = '"+namePlaylist+"' AND idSong = '"+get_ID+"'");
    }

    public String getIDSong (String playlistName, String songName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT ID FROM "+TABLE_NAME_SONGS+ " WHERE name = '"+songName+"' AND namep = '"+playlistName+ "'";
        Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        String id = data.getString(0);
        return id;
    }

}
