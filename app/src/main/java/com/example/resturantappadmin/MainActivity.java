package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
     EditText email,password;
     Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        login=(Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()||password.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All feilds are necessary",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    FirebaseDatabase.getInstance().getReference().child("resturants").child(email.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(true)
                            {
                                  Resturant r=dataSnapshot.getValue(Resturant.class);
                                  if(r.password.equals(password.getText().toString()))
                                  {
                                      SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                                      editor.putString("resturant_id", r.contact);
                                      editor.putString("name",r.name);
                                      editor.apply();
                                      editor.commit();
                                   Intent i=   new Intent(getApplicationContext(),OrdersActivity.class);

                                      startActivity(i);
                                      finish();
                                  }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No such number registeres",Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
            }
        });

    }
}