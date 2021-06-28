package com.example.project_layouts;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity {
    Button login, register;
    EditText email, password;
    FirebaseAuth auth;
    String emailUser, PasswordUser;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Database database = new Database(getApplicationContext());
       // database.delete();

        email = (EditText) findViewById(R.id.email_login);
        password = (EditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register_button);
        auth = FirebaseAuth.getInstance();
        email.setText("ben@gmail.com");
        password.setText("11221122");
    }




    public void loginButton(View view){

        emailUser = email.getText().toString();
        PasswordUser =  password.getText().toString();
        if(emailUser.equals("") ||PasswordUser.equals("")
                || emailUser.contains("@") == false){
            Toast.makeText(getApplicationContext(),
                    "email or password is empty or not in the format", Toast.LENGTH_LONG).show();

        }
        auth.signInWithEmailAndPassword(emailUser,
                PasswordUser)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "login failed! email or password are incorrect", Toast.LENGTH_LONG).show();
            }
        });

    }


    public void registrationButton(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }}




