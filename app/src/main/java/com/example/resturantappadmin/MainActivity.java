package com.example.resturantappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
     EditText email,password;
     Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().equals("admin@gmail.com")&&password.getText().equals("123456"))
                {
                    startActivity(new Intent(getApplicationContext(),AdminHome.class));
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(),AdminHome.class));
                    finish();

                }
            }
        });

    }
}