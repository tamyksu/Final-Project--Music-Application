
package com.example.project_layouts;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder1>{

    public ArrayList<String> IDArray,NickNameArray;
    private Context context;
    FirebaseAuth auth;
    String userID;
    int position ;


public RequestAdapter(Context context, ArrayList<String> IDArray, ArrayList<String> NickNameArray){
        this.context = context;
        this.IDArray = IDArray ;
        this.NickNameArray = NickNameArray;
        this.context = context;
        }

@NonNull
@Override
public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.request_item, parent, false);

        MyViewHolder1 myViewHolder = new MyViewHolder1(contactView ,new MyClickListener() {
            @Override
            public void add(int p) {//add friend in fire base
                // Implement your functionality for onEdit here

                //remove from request list
                FirebaseDatabase.getInstance().getReference("users").child(userID).child("requests").child(IDArray.get(p)).removeValue();
                //add to friend list
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("friends").child(IDArray.get(p));
                reference.setValue(IDArray.get(p));

                //add your id to user's friend list
                reference = FirebaseDatabase.getInstance().getReference("users").child(IDArray.get(p)).child("friends").child(userID);
                reference.setValue(userID);
                NickNameArray.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }

            @Override
            public void remove( int p) {
                position =p;
                new AlertDialog.Builder( context)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete "+NickNameArray.get(p)+" ?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                // remove from request list
                                FirebaseDatabase.getInstance().getReference("users").child(userID).child("requests").child(IDArray.get(position)).removeValue();

                                NickNameArray.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        return myViewHolder;
        }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, final int position) {
        try {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = v.getContext();
                    Intent intent = new Intent(context, UserProfile.class);
                    intent.putExtra("name", NickNameArray.get(position));
                    intent.putExtra("id", IDArray.get(position));
                    context.startActivity(intent);

                }

            });

            holder.bindData(position);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        @Override
        public int getItemCount () {
            return NickNameArray.size();
        }


        public class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
            Button add;
            Button delete;
            public TextView name;
            MyClickListener listener;
            ImageView image_user;

            public MyViewHolder1(View itemView, MyClickListener listener) {
                super(itemView);
                image_user = (ImageView) itemView.findViewById(R.id.image_user);
                name = (TextView) itemView.findViewById(R.id.name_user);
                add = (Button) itemView.findViewById(R.id.add);
                delete = (Button) itemView.findViewById(R.id.remove);

                this.listener = listener;

                add.setOnClickListener(this);
                delete.setOnClickListener(this);

            }


            public void bindData(int indexArr) {

                FirebaseStorage fb = FirebaseStorage.getInstance();
                StorageReference sr = fb.getReference("images/" + IDArray.get(indexArr));
                // Adding listener for a single change
                // in the data at this location.
                // this listener will triggered once
                // with the value of the data at the location

                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(context).load(uri).into(image_user);
                    }


                });

                name.setText(NickNameArray.get(indexArr));

            }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                listener.add(this.getLayoutPosition());
                break;
            case R.id.remove:
                listener.remove(this.getLayoutPosition());
                break;
            default:
                break;
        }
    }
}
    public interface MyClickListener {
        void add(int p);
        void remove(int p);
    }
    }


