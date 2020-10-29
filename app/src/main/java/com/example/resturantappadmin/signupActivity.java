package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class signupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
  EditText name,contact,address,city,state,pincode,category,password,cpassword;
  Spinner gst;
  String gstr[]={"0%","5%","12%","18%","24%"};
  CheckBox inclusive;
  TextView image ,locationx,lat,log;
  ImageView picture;
  Button signup;
    String verificationCode="Abcdek2k3h4j2";
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    ProgressDialog progressDialog;



  Resturant resturant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        resturant=new Resturant();
        progressDialog=new ProgressDialog(signupActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");
        progressDialog.setTitle("T9 App");
        gst=(Spinner)findViewById(R.id.rgst);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gstr);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         checkLocationPermission();
        gst.setAdapter(aa);
        gst.setSelection(1);
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

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        resturant.setDate(formattedDate);
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(signupActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
                verifyCode();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(signupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                verificationCode = s;
                Toast.makeText(signupActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
        locationx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Getting your location",Toast.LENGTH_LONG).show();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    progressDialog.show();

                if(name.getText().toString().isEmpty()||city.getText().toString().isEmpty()||pincode.getText().toString().isEmpty()||password.getText().toString().isEmpty())
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Name,City,Pincode ,mobile and password are necessary",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(password.getText().toString().length()<6)
                    {
                        Toast.makeText(getApplicationContext(),"Password length should be minn 6 digits",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(!password.getText().toString().equals(cpassword.getText().toString()))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Confirm password does not match",Toast.LENGTH_LONG).show();
                        return;
                    }
                    FirebaseDatabase.getInstance().getReference().child("resturants").child(contact.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"This number is already registered",Toast.LENGTH_LONG).show();
                                return;
                            }else
                            {
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        "+91"+contact.getText().toString(),                     // Phone number to verify
                                        60,                           // Timeout duration
                                        TimeUnit.SECONDS,                // Unit of timeout
                                        signupActivity.this,        // Activity (for callback binding)
                                        mCallback);
                                AlertDialog.Builder builder = new AlertDialog.Builder(signupActivity.this);
                                builder.setTitle("Enter Six digit code");

// Set up the input
                                final EditText input = new EditText(signupActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                builder.setView(input);

// Set up the buttons
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String  m_Text = input.getText().toString();
                                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, m_Text);
                                        FirebaseAuth.getInstance().signInWithCredential(credential)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(getApplicationContext(),"Your request has been submitted will be appproved soon",Toast.LENGTH_LONG).show();
                                                            verifyCode();
                                                            finish();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                                        }

                                                    }
                                                });
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                           progressDialog.dismiss();
                        }
                    });


                }

            }
        });
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(signupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(signupActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            try {
                locationManager.requestSingleUpdate( LocationManager.GPS_PROVIDER, new MyLocationListenerGPS(), null );
            } catch ( SecurityException e ) { e.printStackTrace(); }


            // return;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        resturant.setGst(Integer.parseInt(gstr[position].substring(0,1)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

        if (requestCode == 1
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            resturant.image = data.getData().toString();
            uploadImage();

        }
    }
    private void uploadImage()
    {
        if (resturant.image != null) {

            final ProgressDialog progressDialog
                    = new ProgressDialog(signupActivity.this);
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
                                            .makeText(signupActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            resturant.image=uri.toString();
                                            if(!resturant.image.equals(""))
                                            {
                                                Glide.with(signupActivity.this)
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
                                    .makeText(signupActivity.this,
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
    void  verifyCode()
    {
      resturant.data_id=contact.getText().toString();
      resturant.name=name.getText().toString();
      resturant.category=category.getText().toString();
      resturant.contact=contact.getText().toString();
      resturant.address=address.getText().toString();
      resturant.city=city.getText().toString();
      resturant.state=state.getText().toString();
      resturant.pincode=pincode.getText().toString();
      resturant.password=password.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("resturants_request").child(resturant.city.toLowerCase()).child(resturant.data_id).setValue(resturant).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {//progressDialog.dismiss();
                FirebaseDatabase.getInstance().getReference().child("resturants").child(resturant.city.toLowerCase()).child(resturant.data_id).setValue(resturant);
                    Toast.makeText(getApplicationContext(),"Your request has been submitted will be approved soon ",Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                   // progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });





    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location required")
                        .setMessage("Allow to get your location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(signupActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    class MyLocationListenerGPS implements LocationListener {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onLocationChanged(Location location) {
            resturant.latitude=Double.toString(location.getLatitude());
            resturant.longitude=Double.toString(location.getLongitude());
            locationx.setText("Current location selected");

        }
}}