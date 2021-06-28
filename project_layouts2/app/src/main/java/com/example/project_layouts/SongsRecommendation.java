package com.example.project_layouts;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SongsRecommendation extends BaseActivity {

    RecyclerView recyclerView;
    SongsRecommendationAdapter songsRecommendationAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.songs_recommendation);

        songsRecommendationAdapter = new SongsRecommendationAdapter(super.getSongsURLsArray(), super.getSongsAlbumsNamesArray(),SongsRecommendation.this);
        recyclerView = (RecyclerView)findViewById(R.id.songs_recommendation_recycler);
        recyclerView.setAdapter(songsRecommendationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SongsRecommendation.this));

    }

}
