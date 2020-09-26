package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.UUID;

public class AddItem extends AppCompatActivity {
    EditText name,ingredient,about,date,time,price,offer,discount;
    Button picture,video,add;
    String pic_path="";
    String video_path="";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("123456789").child("Cuisine");
    int PICK_IMAGE_REQUEST=1;
    Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().hide();
        Cuisine c=new Cuisine();
        name=(EditText)findViewById(R.id.name);
        ingredient=(EditText)findViewById(R.id.ingredient);
        about=(EditText)findViewById(R.id.about);
        date=(EditText)findViewById(R.id.dates);
        time=(EditText)findViewById(R.id.price);
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
            @Override
            public void onClick(final View v) {
                Cuisine c=new Cuisine();
                c.setCousine_name(name.getText().toString());
                c.setIngredients(ingredient.getText().toString());
                c.setAbout(ingredient.getText().toString());
                c.setAvailability_dates(date.getText().toString());
                c.setTimming(time.getText().toString());
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

}