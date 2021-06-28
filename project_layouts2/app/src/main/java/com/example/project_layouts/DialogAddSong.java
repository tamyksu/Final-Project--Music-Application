package com.example.project_layouts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

public class DialogAddSong extends DialogFragment {
    EditText file_name;
    private String filePath="";
    Database database;
    Button cancel,save;
    ImageButton addSong;
    String name_playlist;
    private static final int SONG_REQUEST =1;
    Uri uri_file;

    public DialogAddSong() {
        // Empty constructor required for DialogFragment
    }
    public DialogAddSong(String name_playlist) {
        this.name_playlist = name_playlist;
        database = MyPlaylistActivity.database;
        // Empty constructor required for DialogFragment
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.icons, null);
        return view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog builder = new AlertDialog.Builder(getContext()).create();
        View settings_layout = getActivity().getLayoutInflater().inflate(R.layout.icons, null);
        builder.setView(settings_layout);
        file_name =  (EditText) settings_layout.findViewById(R.id.name_file);
        save = (Button) settings_layout.findViewById(R.id.save);
        cancel = (Button) settings_layout.findViewById(R.id.cancel);
        addSong = (ImageButton) settings_layout.findViewById(R.id.addSong);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// save music file
                if(filePath=="" || file_name.getText().toString()=="")
                    Toast.makeText(getContext(), "file music or name are empty!", Toast.LENGTH_SHORT).show();
                else {
                    String new_song = file_name.getText().toString();
                    String str = Uri.parse(filePath).getPath();
                    String arr[] = str.split(":");
                    String file =arr[1];
                    database.addDataSongs(new_song, file, "", name_playlist);//update in local storage - songs table
                    String id_Song = database.getIDSong(name_playlist, new_song);
                    database.addDataPlaylist(name_playlist, id_Song);// update in local storage - playlists table
                    MyDialogFragmentListener activity = (MyDialogFragmentListener) getActivity();
                    activity.onReturnValue(file);
                    dismiss();
                }
            }
        });
        addSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SONG_REQUEST);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder;
    }


    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == SONG_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            filePath = data.getData().toString();
            uri_file = data.getData();
        }

    }

    public interface MyDialogFragmentListener {
        public void onReturnValue(String filePath);
    }

    }
