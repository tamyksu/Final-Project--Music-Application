package com.example.project_layouts;

        import android.app.AlertDialog;
        import android.content.Context;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;


        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>{
    public ArrayList<String> IDArray,NickNameArray;
    private Context context;



    public ContactsAdapter(Context context, ArrayList<String> IDArray,ArrayList<String> NickNameArray){
        this.context = context;
        this.IDArray =IDArray;
        this.NickNameArray = NickNameArray ;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.user, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(contactView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("name",NickNameArray.get(position));
                intent.putExtra("id",IDArray.get(position));
                intent.putExtra("CameFrom","FriendList");
                context.startActivity(intent);

            }

        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v){
                new AlertDialog.Builder(context)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete "+NickNameArray.get(position)+" ?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();
                                String userID = user.getUid();
                                 FirebaseDatabase.getInstance().getReference("users").child(IDArray.get(position)).child("friends").child(userID).removeValue();
                                FirebaseDatabase.getInstance().getReference("users").child(userID).child("friends").child(IDArray.get(position)).removeValue();

                                NickNameArray.remove(position);


                                notifyItemRemoved(position);

                                notifyDataSetChanged();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



                return false;
            }

        });
        holder.bindData(position);

    }


    @Override
    public int getItemCount() {
        return NickNameArray.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        ImageView image_user;

        public MyViewHolder(View itemView){
            super(itemView);
            image_user = (ImageView)itemView.findViewById(R.id.image_user);
            name = (TextView)itemView.findViewById(R.id.name_user);

        }


        public void bindData(int indexArr){

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
    }

}
