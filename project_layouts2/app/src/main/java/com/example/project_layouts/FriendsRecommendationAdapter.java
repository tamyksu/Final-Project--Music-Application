package com.example.project_layouts;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendsRecommendationAdapter extends RecyclerView.Adapter<FriendsRecommendationAdapter.ViewHolder> {
    private ArrayList <ArrayList<String>> users;
    Context context;

    public FriendsRecommendationAdapter(ArrayList<ArrayList<String>> users , Context context){
        this.users = users;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card_recommendation,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, genre , id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            genre = itemView.findViewById(R.id.item_genre);
            id = itemView.findViewById(R.id.id_user);

        }

        void setData(int indexArr) {
            FirebaseStorage fb = FirebaseStorage.getInstance();
            String  user_ID = users.get(indexArr).get(0);
            String user_name = users.get(indexArr).get(1);
            String  user_genre = users.get(indexArr).get(2);

            StorageReference sr = fb.getReference("images/" + user_ID);
            // Adding listener for a single change
            // in the data at this location.
            // this listener will triggered once
            // with the value of the data at the location

            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {


                    Picasso.with(context).load(uri).into(image);


                }

            });

            name.setText(user_name);

            genre.setText(user_genre);
            id.setText((user_ID));
        }
    }



}
