package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ResturantSettings extends AppCompatActivity {
    Resturant resturant=new Resturant();
    String resturant_id="";
    String citi="";
    EditText name,contact,address,city,state,pincode,category,password,cpassword;
    Spinner gst;
    String gstr[]={"0%","5%","12%","18%","24%"};
    CheckBox inclusive;
    TextView image ,locationx,lat,log;
    ImageView picture;
    Button signup;
    String filePath;
    int PICK_IMAGE_REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Edit Resturant Profile");
        resturant_id= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("resturant_key","");
        citi= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("city","1234");
        Toast.makeText(getApplicationContext(),resturant_id +" "+city,Toast.LENGTH_LONG).show();
        if(resturant_id.equals("")||citi.equals(""))
        {
            Toast.makeText(ResturantSettings.this,"Error Occured",Toast.LENGTH_LONG).show();
            finish();
        }
        gst=(Spinner)findViewById(R.id.rgst);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gstr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gst.setAdapter(aa);
        gst.setSelection(1);
        contact.setEnabled(false);
        inclusive=(CheckBox)findViewById(R.id.rinclusive);
        inclusive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    resturant.setInclusive("yes");
                }
                else
                {
                    resturant.setInclusive("no");

                }
            }
        });

        name=(EditText) findViewById(R.id.rname);
        category=(EditText) findViewById(R.id.rcategory);
        contact=(EditText) findViewById(R.id.rcontact);
        address=(EditText) findViewById(R.id.raddress);
        city=(EditText) findViewById(R.id.rcity);
        state=(EditText) findViewById(R.id.rstate);
        pincode=(EditText) findViewById(R.id.rpin);
        password=(EditText) findViewById(R.id.rpassword);
        locationx=(TextView) findViewById(R.id.rlocation);
        image=(TextView)findViewById(R.id.rimage);
        lat=(TextView)findViewById(R.id.rlat);
        log=(TextView)findViewById(R.id.rlong);
        signup=(Button)findViewById(R.id.rsignup);
        picture=(ImageView)findViewById(R.id.rpicture);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        cpassword=(EditText)findViewById(R.id.rcpassword);
        Date c = Calendar.getInstance().getTime();
        cpassword.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        gst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {  case 0:
                    {
                        resturant.gst=0;
                        break;
                    }
                    case 1:
                    {
                        resturant.gst=5;
                        break;
                    }
                    case 2:
                    {
                        resturant.gst=12;
                        break;
                    }
                    case 3:
                    {
                        resturant.gst=18;
                        break;
                    }
                    case 4:
                    {
                        resturant.gst=24;
                        break;
                    }


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("resturants").child(citi).child(resturant_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resturant=dataSnapshot.getValue(Resturant.class);
                name.setText(resturant.name);
                contact.setText(resturant.contact);
                category.setText(resturant.category);
                address.setText(resturant.address);
                city.setText(resturant.city);
                state.setText(resturant.state);
                pincode.setText(resturant.pincode);
                if(resturant.gst==0)
                {
                    gst.setSelection(0);
                }
                else if(resturant.gst==5)
                {
                    gst.setSelection(1);
                }
                else if(resturant.gst==12)
                {
                    gst.setSelection(2);
                }
                else if(resturant.gst==18)
                {
                    gst.setSelection(3);
                }
                else
                {
                    gst.setSelection(4);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
       // resturant.setDate(formattedDate);

     signup.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             ProgressDialog progressDialog=new ProgressDialog(ResturantSettings.this);
             progressDialog.setTitle("T9 App");
             progressDialog.setMessage("Please wait");

             if(name.getText().toString().isEmpty()||city.getText().toString().isEmpty()||pincode.getText().toString().isEmpty())
             {
                 progressDialog.dismiss();
                 Toast.makeText(getApplicationContext(),"Name,City,Pincode ,mobile and password are necessary",Toast.LENGTH_LONG).show();
             }
             else
             {
                /* if(password.getText().toString().length()<6)
                 {
                     Toast.makeText(getApplicationContext(),"Password length should be minn 6 digits",Toast.LENGTH_LONG).show();
                     return;
                 }
                 if(!password.getText().toString().equals(cpassword.getText().toString()))
                 {
                     progressDialog.dismiss();
                     Toast.makeText(getApplicationContext(),"Confirm password does not match",Toast.LENGTH_LONG).show();
                     return;
                 }*/
         }
             resturant.data_id=contact.getText().toString();
             resturant.name=name.getText().toString();
             resturant.category=category.getText().toString();
             resturant.contact=contact.getText().toString();
             resturant.address=address.getText().toString();
             resturant.city=city.getText().toString();
             resturant.state=state.getText().toString();
             resturant.pincode=pincode.getText().toString();


             FirebaseDatabase.getInstance().getReference().child("resturants").child(citi).child(resturant_id).setValue(resturant).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful())
                     {
                         Toast.makeText(ResturantSettings.this,"Succesfully Updated",Toast.LENGTH_LONG).show();;

                     }else
                     {
                         Toast.makeText(ResturantSettings.this,"Error occured",Toast.LENGTH_LONG).show();;

                     }
                 }
             });
     }});

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
                1);
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
           filePath  = data.getData().toString();
            // Bitmap photo = (Bitmap) data.getExtras().get("data");
            View view=getLayoutInflater().inflate(R.layout.enlarg_image_layout,null);
            ImageView imageView=view.findViewById(R.id.imageView);
            imageView.setImageURI(data.getData());
            AlertDialog alertDialog=new AlertDialog.Builder(ResturantSettings.this).setView(view).setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resturant.image=filePath;
                    uploadImage();
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
            alertDialog.show();


        }
    }
    private void uploadImage()
    {
        if (resturant.image != null) {

            final ProgressDialog progressDialog
                    = new ProgressDialog(ResturantSettings.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            final StorageReference ref
                    =
                    FirebaseStorage.getInstance().getReference().child(
                            "images/"
                                    + UUID.randomUUID().toString());


            ref.putFile(Uri.parse(resturant.image))
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(ResturantSettings.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            resturant.image=uri.toString();
                                            if(!resturant.image.equals(""))
                                            {
                                                Glide.with(ResturantSettings.this)
                                                        .load(resturant.image)
                                                        .circleCrop()
                                                        .into(picture);
                                            }

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
                                    .makeText(ResturantSettings.this,
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

}