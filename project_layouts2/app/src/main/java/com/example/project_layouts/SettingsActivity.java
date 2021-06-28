package com.example.project_layouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    ImageView image_profile;
    FirebaseStorage storage;
    FirebaseAuth auth;
    ValueEventListener eventListener;
    private Uri filePath;
    StorageReference storageReference;
    String userID, email,passwordOld;
    DatabaseReference reference;
    EditText description, password;
    private static final int IMAGE_REQUEST =1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        password = findViewById(R.id.update_password);
        description = findViewById(R.id.update_text);
        image_profile = (ImageView) findViewById(R.id.image_user1);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        get_description();

         reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("name");
         eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name_user = dataSnapshot.getValue(String.class);

                FirebaseStorage fb = FirebaseStorage.getInstance();
                StorageReference sr = fb.getReference("images/" + userID);
                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(SettingsActivity.this).load(uri).into(image_profile);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);
    }

    public void get_description()
    {

        reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                email = dataSnapshot.child("email").getValue().toString();
                passwordOld = dataSnapshot.child("password").getValue().toString();
                if (dataSnapshot.child("description").exists()) {
                    description.setText(dataSnapshot.child("description").getValue().toString());

                }

            }
                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }

            };
        reference.addListenerForSingleValueEvent(eventListener);

    }


    public void update_image (View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();


            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                image_profile.setImageBitmap(bitmap);



            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
    private void uploadImage(){
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            try {


                StorageReference ref = storageReference.child("images/" + userID);

                // adding listeners on upload
                // or failure of image
                ref.putFile(filePath)
                        .addOnSuccessListener(
                                new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(
                                            UploadTask.TaskSnapshot taskSnapshot) {

                                        // Image uploaded successfully
                                        // Dismiss dialog
                                        progressDialog.dismiss();
                                        Toast
                                                .makeText(SettingsActivity.this,
                                                        "Image Uploaded!!",
                                                        Toast.LENGTH_SHORT)
                                                .show();


                                        Toast.makeText(SettingsActivity.this, "Register sucssesfully", Toast.LENGTH_SHORT).show();

                                    }
                                })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                // Error, Image not uploaded
                                progressDialog.dismiss();
                                Toast
                                        .makeText(SettingsActivity.this,
                                                "Failed " + e.getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();

                            }
                        })
                        .addOnProgressListener(
                                new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    // Progress Listener for loading
                                    // percentage on the dialog box
                                    @Override
                                    public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress
                                                = (100.0
                                                * taskSnapshot.getBytesTransferred()
                                                / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage(
                                                "Uploaded "
                                                        + (int) progress + "%");
                                    }
                                });
            }
            catch(Exception e){
                System.out.println("Wrapper exception: " + e);

                // Printing the 'actual' exception:
                System.out.println("Underlying exception: " + e.getCause());

            }
        }

    }

    public void update_password (View view) {

        reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("password");
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reference.setValue(password.getText().toString());
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, passwordOld);

// Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "Password updated");
                                            } else {
                                                Log.d("TAG", "Error password not updated");
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("TAG", "Error auth failed");
                                }
                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);

    }

    public void update_description (View view) {

        reference = FirebaseDatabase.getInstance().getReference("users").child(userID).child("description");
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reference.setValue(description.getText().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);

    }
    public void save (View view){

        uploadImage();


    }



    public void search (View view){
        Intent intent = new Intent(SettingsActivity.this, FriendsRecommendation.class);
        startActivity(intent);
        finish();
    }
    public void home_page (View view){
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void player (View view){
        Intent intent = new Intent(SettingsActivity.this, MyPlaylistActivity.class);
        startActivity(intent);
        finish();
    }
    public void my_place (View view){
        Intent intent = new Intent(SettingsActivity.this, MyPlace.class);
        startActivity(intent);
        finish();
    }



    }