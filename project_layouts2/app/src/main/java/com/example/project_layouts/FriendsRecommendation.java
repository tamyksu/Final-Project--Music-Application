package com.example.project_layouts;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.Reference;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

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
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

public class FriendsRecommendation extends AppCompatActivity {
    String userID,idRecomendation;
    Direction dir;
    int counter =0;
    Database database;
    ArrayList<String> items;
    DatabaseReference reference;
    FirebaseAuth auth;
    TextView nameUser ,id_recommendation;
    private static final String TAG = "FriendsRecommendation";
    FriendsRecommendationAdapter adapter;
    CardStackLayoutManager manager;
    ValueEventListener eventListener;
    ArrayList idGotRight =new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_recommendation);
        database = new Database((getApplicationContext()));
        CardStackView cardStackView = findViewById(R.id.card_stack_view);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("requests");

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                       if( idGotRight.contains(ds.getValue(String.class))){// users sends request to each other (both swipe right)- make them friends
                           make_friend(ds.getValue(String.class));
                           ds.getRef().removeValue();//delete user from recommendations list
                           DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("users").child(ds.getValue(String.class)).child("requests");
                           reference1.getRef().removeValue();

                       }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        manager = new CardStackLayoutManager(this, new CardStackListener() {

            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {

                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);

                if (direction == Direction.Right) {
                    reference.addListenerForSingleValueEvent(eventListener);
                    dir = direction;
                    //send request to user
                    reference = FirebaseDatabase.getInstance().getReference("users").child(idRecomendation).child("requests").child(userID);
                    reference.setValue(userID);
                    idGotRight.add(idRecomendation);
                    database.updateStatus(idRecomendation,"request");
                }

                if (direction == Direction.Left) {
                    dir = direction;
                    database.updateStatus(idRecomendation,"reject");

                }

                }


            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                counter++;
                nameUser = view.findViewById(R.id.item_name);
                 id_recommendation = view.findViewById(R.id.id_user);
                idRecomendation = id_recommendation.getText().toString();
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + nameUser.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                nameUser = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + nameUser.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new FriendsRecommendationAdapter(addList(),FriendsRecommendation.this);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());


    }

    public void make_friend(String idUser){
        //add user id to your friendlist
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("friends").child(idUser);
        reference.setValue(idUser);

        //add your id to user's friend list
        reference = FirebaseDatabase.getInstance().getReference("users").child(idUser).child("friends").child(userID);
        reference.setValue(userID);

        database.updateStatus(idUser,"accept");
    }



    private ArrayList<ArrayList<String>>  addList() {

        Cursor dataUsers = database.getRecommendation();
        ArrayList<ArrayList<String>> all = new ArrayList<>();

        while (dataUsers.moveToNext()){
            items = new ArrayList<>();
            items.add(dataUsers.getString(1));//id
            items.add(dataUsers.getString(2));//name
            items.add(dataUsers.getString(3));//favorites
            all.add(items);
        }
        return all;
    }

    public void home_page (View view){
        Intent intent = new Intent(FriendsRecommendation.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void player (View view){
        Intent intent = new Intent(FriendsRecommendation.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }
    public void my_place (View view){
        Intent intent = new Intent(FriendsRecommendation.this, MyPlace.class);
        startActivity(intent);
        finish();
    }
    public void friendRecommendation (View view){
        Intent intent = new Intent(FriendsRecommendation.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }


    public void view_profile (View view){
        Intent intent = new Intent(FriendsRecommendation.this, UserProfile.class);
        intent.putExtra("name",nameUser.getText());
        intent.putExtra("id",idRecomendation);
        intent.putExtra("CameFrom","FriendsRecommendation");
        startActivity(intent);
        finish();

    }
}
