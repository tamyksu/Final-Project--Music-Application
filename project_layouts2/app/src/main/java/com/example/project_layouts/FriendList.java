
package com.example.project_layouts;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;

public class FriendList extends AppCompatActivity {
    TextView search_user;
    Boolean flag = false;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    String userID;
    ContactsAdapter adapter;
    ArrayList<String> arrayID = new ArrayList<>();
    ArrayList<String> arrayNickNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        recyclerView = (RecyclerView)findViewById(R.id.recycleview_requests);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("friends");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String nameDB = ds.getValue(String.class);
                    arrayID.add(nameDB);// get id from user's friends

                }
                buildArrayNames();//get names from user's friends
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);

    }



    public void buildArrayNames() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String idFriend = ds.child("id").getValue(String.class);
                    if (arrayID.contains(idFriend)) {
                        arrayNickNames.add(ds.child("name").getValue(String.class));
                    }
                }
                adapter = new ContactsAdapter(FriendList.this, arrayID, arrayNickNames);// make a list
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(FriendList.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);

    }


    public void my_place(View view)
    {
        Intent intent = new Intent(FriendList.this, MyPlace.class);
        startActivity(intent);
        finish();

    }
    public void search (View view){
        Intent intent = new Intent(FriendList.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }
    public void home_page (View view){
        Intent intent = new Intent(FriendList.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void player (View view){
        Intent intent = new Intent(FriendList.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }
}