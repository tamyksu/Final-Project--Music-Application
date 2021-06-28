package com.example.project_layouts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;



public class SongsRecommendationAdapter extends RecyclerView.Adapter<SongsRecommendationAdapter.ViewHolder> {
    private String [] songsURLs;
    private String [] songsNames;
    Context context;

    public SongsRecommendationAdapter(String [] songsURLs, String [] songsNames, Context context){
        if(songsURLs != null){
            this.songsURLs = songsURLs;
            this.songsNames = songsNames;
            this.context =context;
        }
        else{
            this.songsURLs = new String[1];
            this.songsURLs[0] = "no recommended songs";
            this.songsNames = new String[1];
            this.songsNames[0] = "no recommended songs";
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_song_recommendation,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setData(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(songsURLs != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(songsURLs[position]));
                        context.startActivity(intent);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            });
    }

    @Override
    public int getItemCount() {
        return songsURLs.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.item_song_name);
        }

        void setData(int indexArr) {
            name.setText(songsNames[indexArr]);
        }
    }

}

