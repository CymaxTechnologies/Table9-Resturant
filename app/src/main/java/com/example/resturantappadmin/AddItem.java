package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddItem extends AppCompatActivity {
    EditText name,ingredient,about,date,time,price,offer,discount;
    Button picture,video,add;
    String pic_path="";
    String video_path="";
    String resturant_id;
    String resturant_name;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    int PICK_IMAGE_REQUEST=1;
    Uri filePath;
    EditText sd,ed;
    EditText st,et;
    String sdate="";
    String edate="";
    String stime="";
    String etime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().hide();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id=prefs.getString("resturant_id","123");
        resturant_name=prefs.getString("name","123");
        TextView t=(TextView)findViewById(R.id.rest);
        t.setText(resturant_name);
        myRef = database.getReference().child(resturant_id).child("Cuisine");
        sd=(EditText) findViewById(R.id.date_start);
        ed=(EditText) findViewById(R.id.date_end);
        st=(EditText) findViewById(R.id.time_start);
        et=(EditText) findViewById(R.id.time_end) ;
        final DatePickerDialog[] picker = new DatePickerDialog[1];
        final TimePickerDialog[] timePicker = new TimePickerDialog[1];
        sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker[0] = new DatePickerDialog(AddItem.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                sd.setText(dayOfMonth + "/" + (monthOfYear + 1) );
                                sdate=dayOfMonth+"/"+monthOfYear;
                            }
                        }, year, month, day);
                picker[0].setTitle("Select starting Availability date");
                picker[0].show();
            }
        });
        ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker[0] = new DatePickerDialog(AddItem.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edate=dayOfMonth+"/"+monthOfYear;
                                ed.setText( dayOfMonth + "/" + (monthOfYear + 1));
                            }
                        }, year, month, day);
                picker[0].setTitle("Select ending Availability date");
                picker[0].show();
            }
        });
        st.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();

                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                timePicker[0] = new TimePickerDialog(AddItem.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time=getTime(selectedHour,selectedMinute);

                        st.setText( time);
                        stime=time;
                    }
                }, hour, minute, true);


                timePicker[0].setTitle("Select Time");
                timePicker[0].show();

            }
        });
        et.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                timePicker[0] = new TimePickerDialog(AddItem.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time=getTime(selectedHour,selectedMinute);

                        et.setText( time);
                        etime=time;
                    }
                }, hour, minute, true);
                timePicker[0].setTitle("Select Time");
                timePicker[0].show();

            }
        });

        Cuisine c=new Cuisine();
        name=(EditText)findViewById(R.id.name);
        ingredient=(EditText)findViewById(R.id.ingredient);
        about=(EditText)findViewById(R.id.about);


        offer=(EditText)findViewById(R.id.offer);
        price=(EditText)findViewById(R.id.price);
        discount=(EditText)findViewById(R.id.discount);
        picture=(Button)findViewById(R.id.pictre);
        video=(Button)findViewById(R.id.video);
        add=(Button)findViewById(R.id.addItem);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
                uploadImage();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final View v) {
                String date=sdate+"-"+edate;
                String t=stime+"-"+etime;
                Cuisine c=new Cuisine();
                c.setCousine_name(name.getText().toString());
                c.setIngredients(ingredient.getText().toString());
                c.setAbout(ingredient.getText().toString());
                c.setAvailability_dates(date);
                c.setTimming(t);
                c.setOffer(offer.getText().toString());
                c.setDiscount_price(discount.getText().toString());
                c.setPrice(price.getText().toString());
                c.setPicture(pic_path);
                c.setVideo(video_path);
                myRef.child(c.cousine_name).setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(v,"Added succesfullly", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });


            }
        });


    }
    private void SelectImage()
    {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            filePath = data.getData();


        }
    }
    private void uploadImage()
    {
        if (filePath != null) {

            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            final StorageReference ref
                    =
                    FirebaseStorage.getInstance().getReference().child(
                            "images/"
                                    + UUID.randomUUID().toString());


            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(AddItem.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                           pic_path=uri.toString();
                                        }
                                    });
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast
                                    .makeText(AddItem.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
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
    private String getTime(int hr,int min) {
        Time tme = new Time(hr,min,0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }
}