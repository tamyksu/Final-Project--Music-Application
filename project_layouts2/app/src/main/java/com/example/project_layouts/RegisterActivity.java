package com.example.project_layouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, nickname;
    Button register,add_image;
   FirebaseAuth auth;
    private Uri filePath;

   private static final int IMAGE_REQUEST =1;

    FirebaseStorage storage;
    ImageView image_display;
    String userID;
    boolean flag =false;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        email = findViewById(R.id.email);
        image_display = findViewById(R.id.image_display);
        password = findViewById(R.id.password);
        register = findViewById(R.id.sign_up);
        nickname = findViewById(R.id.name);
        add_image = findViewById(R.id.add_image);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_name = nickname.getText().toString();

                if (testDetails(txt_email, txt_password, txt_name))
                    registerUser(txt_email, txt_password, txt_name);
                else {
                    Toast.makeText(RegisterActivity.this, "one of details is incorrect!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    public boolean testDetails(String txt_email, String txt_password, String txt_name)
    {
        if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){

            return false;
        }else if (txt_password.length()<5){

            return false;
        }
        else if (txt_name.equals("")||txt_email.equals("") )
        {

            return false;
        }
        return true;

    }

    public void backToMainActivity(View view){
        Intent intent = new Intent(getApplicationContext(), com.example.project_layouts.MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
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
                image_display.setImageBitmap(bitmap);
                flag = true;
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
        StorageReference ref
                = storageReference
                .child(
                        "images/"
                                + userID);

        // adding listeners on upload
        // or failure of image
        ref.putFile(filePath)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot)
                            {

                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                                Toast
                                        .makeText(RegisterActivity.this,
                                                "Image Uploaded!!",
                                                Toast.LENGTH_SHORT)
                                        .show();


                                Toast.makeText(RegisterActivity.this, "Register sucssesfully",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, com.example.project_layouts.MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(RegisterActivity.this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();


                        Intent intent = new Intent(RegisterActivity.this, com.example.project_layouts.MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(
                                    UploadTask.TaskSnapshot taskSnapshot)
                            {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int)progress + "%");
                            }
                        });
    }
}

  private void registerUser(final String email, final String password, final String name){
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>(){
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){

                FirebaseUser user = auth.getCurrentUser();
                 userID = user.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
                Map<String, String> userData = new HashMap<>();

                userData.put("name", name);
                userData.put("email", email);
                userData.put("password", password);
                userData.put("id", userID);

                String emailWithoutDots = email;
                while(emailWithoutDots.equals(emailWithoutDots.replace(".", "_")) == false)
                    emailWithoutDots = emailWithoutDots.replace(".", "_");
                reference.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(flag == true)
                        {
                            uploadImage();
                        }
                        else {

                            Toast.makeText(RegisterActivity.this, "Register sucssesfully",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, com.example.project_layouts.MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }
                });


            }else {
                Toast.makeText(RegisterActivity.this, "registration failled", Toast.LENGTH_SHORT).show();
            }
        }
    });

    }
}


