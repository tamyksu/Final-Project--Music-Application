
package com.example.project_layouts;

        import android.app.AlertDialog;
        import android.content.Context;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.media.MediaPlayer;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;


        import java.io.IOException;
        import java.util.ArrayList;

public class CreatePlaylistAdapter extends RecyclerView.Adapter<CreatePlaylistAdapter.MyViewHolder> {
    public ArrayList<String> name_file = new ArrayList<>();
    private Context context;
    int index;
    String playlist_name_DB;
    static Database database;
    View view;
    ArrayList<MediaPlayer> media_player_arr;
    boolean flag =true;
    ArrayList<String> idS = new ArrayList<>();
    TextView playlist_name;



    public CreatePlaylistAdapter(Context context,ArrayList<String> name_file,ArrayList<MediaPlayer> media_player_arr,String playlist_name_DB){
        this.context = context;
        database = new Database(context);
        this.name_file = name_file ;
        this.media_player_arr = media_player_arr;
        this.playlist_name_DB = playlist_name_DB;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_file_music_create, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(contactView);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        index =position;
        try {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view =v;
                    if(context instanceof MyPlaylistActivity )//user in playlists list, he clicks on playlist item, now the screen move to music player
                    {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MediaPlayerActivity.class);
                        intent.putExtra("playlistName",name_file.get(position));
                        context.startActivity(intent);
                    }
                    else if(context instanceof MediaPlayerActivity){// user in music player he clicks on music file item, the music starts to play
                        try {
                            ((MediaPlayerActivity)context).control_music(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    else {//user in create playlist screen, by clicking he can play and pause music before he save the playlist
                        RecyclerView recyclerView = ((RecyclerView) v.getParent());
                        int position = recyclerView.getChildLayoutPosition(v);
                        if (flag == true) {

                            media_player_arr.get(position).start();
                            flag = false;
                        } else {
                            media_player_arr.get(position).pause();
                            flag = true;

                        }
                    }

                }
            });
              holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int position = ((RecyclerView) v.getParent()).getChildLayoutPosition(v);
                    index = position;
                    new AlertDialog.Builder(context)
                            .setTitle("Delete ")
                            .setMessage("Are you sure you want to delete  ?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    if (playlist_name_DB != " " && playlist_name_DB != "createPlaylist") {//user in music player and he wants to delete music file
                                        //get id of cuurent song
                                        Cursor dataSongs = database.getSongs(playlist_name_DB);

                                        while (dataSongs.moveToNext()) {
                                            idS.add(dataSongs.getString(0));// give me all the songs id of this playlist
                                        }
                                        String idSongDelete = idS.get(index);
                                        database.deleteSongsRow(idSongDelete);//delete music file from playlist
                                        database.deletePlaylistRow(playlist_name_DB, idSongDelete);
                                        idS.remove(index);

                                        if (idS.size() == 0)//this playlist in without any songs return back to playlists list screen
                                        {
                                            notifyItemRemoved(index);
                                            notifyDataSetChanged();
                                            Context context = view.getContext();
                                            Intent intent = new Intent(context, MyPlaylistActivity.class);
                                            context.startActivity(intent);
                                            return;

                                        }

                                    } else if (playlist_name_DB == "createPlaylist") {// user in playlist create screen - remove music file
                                        media_player_arr.remove(index);
                                        ((CreatePlaylist) context).update(index);
                                        notifyItemRemoved(index);
                                        return ;
                                    }
                                    else {// user in playlists list screen, user delete playlist
                                        database.deletePlaylist(name_file.get(index));

                                    }

                                    name_file.remove(index);
                                    notifyItemRemoved(index);
                                    notifyDataSetChanged();
                                }

                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    return  true;
                }
            });
            holder.bindData(position);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return name_file.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View itemView){
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name_item);
            playlist_name = (TextView)itemView.findViewById(R.id.file_playlist_name);

        }

        public void bindData(int indexArr){

            name.setText(name_file.get(indexArr));
            if(context instanceof MyPlaylistActivity ||  context instanceof MediaPlayerActivity){

                playlist_name.setText("Playlist name:");
            }

        }
    }

}
