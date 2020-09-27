package com.example.resturantappadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
     EditText email,password;
     ImageButton login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        login=(ImageButton) findViewById(R.id.login_button);

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