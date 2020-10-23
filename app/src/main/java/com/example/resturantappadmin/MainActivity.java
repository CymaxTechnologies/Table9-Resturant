package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
     EditText email,password;
     Button login;
     TextView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signup=(TextView)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),signupActivity.class));

            }
        });
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        login=(Button) findViewById(R.id.login_button);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
      String  resturant_id=prefs.getString("resturant_id","123");
      String resturant__name=prefs.getString("name","123");
      if(!resturant_id.equals("123"))
      {
          startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
          finish();
      }
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
                            Toast.makeText(getApplicationContext(),"Enter",Toast.LENGTH_SHORT).show();

                            if(true)
                            {
                                  Resturant r=dataSnapshot.getValue(Resturant.class);
                                  if(r.password.equals(password.getText().toString()))
                                  {
                                      if(r.verified.equals("no"))
                                      {
                                          Toast.makeText(getApplicationContext(),"Your resturant is not approved yet",Toast.LENGTH_LONG).show();
                                          return;
                                      }
                                      final String PREF_FILE_1 = "pref_file_1";
                                      SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                                      editor.putString("resturant_id", r.getData_id());
                                      FirebaseMessaging.getInstance().subscribeToTopic(r.getData_id());
                                      editor.putString("name",r.name);
                                    //  editor.apply();
                                      editor.commit();
                                      Toast.makeText(getApplicationContext(),"Login Succesfully",Toast.LENGTH_SHORT).show();

                                      Intent i=   new Intent(getApplicationContext(),NotificationActivity.class);

                                      startActivity(i);
                                      finish();
                                  }
                                  else
                                  {
                                      Toast.makeText(getApplicationContext(),"Password dont match",Toast.LENGTH_SHORT).show();

                                  }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No such number registeres",Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });



                }
            }
        });

    }
}