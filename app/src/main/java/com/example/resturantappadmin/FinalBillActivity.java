package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class FinalBillActivity extends AppCompatActivity {
      TextView date,tt,name,phone,ta;
      String resturant_id="";
      int total=0;
      String table="";
      ImageButton img;
      boolean flag=true;
      Bitmap b;
      ArrayList<Cuisine> cuisineArrayList=new ArrayList<>();
      ArrayList<Integer> integers=new ArrayList<>();
      ArrayList<String> prices=new ArrayList<>();
      DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(resturant_id);
      String resturant_name;
      String user_id="";
      String user_name="";
      String user_no="";
      ExtendedFloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_bill);
        final ProgressDialog progressDialog=new ProgressDialog(FinalBillActivity.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
        table=(String)getIntent().getStringExtra("table") ;
        //tt=(TextView)findViewById(R.id.ctable);
        name=(TextView)findViewById(R.id.cname);
        phone=(TextView)findViewById(R.id.cphone);
        floatingActionButton=(ExtendedFloatingActionButton)findViewById(R.id.extended_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                            {
                                if(!flag)
                                {
                                    Toast.makeText(getApplicationContext(),"Bill is already generated You can share by pressing share Icon",Toast.LENGTH_LONG).show();
                                    break;

                                }
                                flag=false;
                                String key= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("history").push().getKey();
                                final OrderHistory history=new OrderHistory();
                                history.setCount(integers);
                                history.setCuisines(cuisineArrayList);
                                history.setPrices(prices);
                                history.setResturant_id(resturant_id);
                                history.setUser_id(user_id);
                                history.setTable(table);
                                history.setDate(LocalDate.now().toString());
                                history.setTime(LocalTime.now().toString());
                                history.setPayment_method("");
                                history.setRating("");
                                history.setOrder_id(key);
                                history.setResturant_name(resturant_name);
                                FirebaseDatabase.getInstance().getReference().child(resturant_id).child("history").child(key).setValue(history).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if(user_id!="")
                                        {
                                            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).removeValue();
                                            String k=    FirebaseDatabase.getInstance().getReference().child("user").child(user_id).child("history").push().getKey();

                                            FirebaseDatabase.getInstance().getReference().child("user").child(user_id).child("history").child(k).setValue(history);
                                            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("noitifications").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot d:dataSnapshot.getChildren())
                                                    {
                                                        Notification n=d.getValue(Notification.class);
                                                        if(n.table_no.equals(table))
                                                        {
                                                            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").child(d.getKey()).removeValue();
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(user_id).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("user").child(user_id).child("my_orders").child(resturant_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });

                                        }

                                    }
                                });

                                File file = new File(Environment.getExternalStorageDirectory() + "/Table9/");
                                if (!file.mkdirs()) {
                                    file.mkdirs();
                                }
                                PDFHelper pdfHelper=new PDFHelper(file,getApplicationContext());
                                View view =(ScrollView)findViewById(R.id.scrol );

                                b=pdfHelper.getBitmapFromView(view);
                                //img.setImageBitmap(b);
                                String message;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    message="Thank you for visiting us\nHere is your bill\nDate : "+LocalDate.now().toString()+"\n Time : "+LocalTime.now();
                                }
                                else
                                {
                                    message="Thank you for visiting us\nHere is your bill";

                                }
//                                shareImage(b,message);
                                pdfHelper.saveImageToPDF(view,b,new Date().toString()+"slip");
                                //finish();
                                Toast.makeText(getApplicationContext(),"Bill Generated Succesfully",Toast.LENGTH_LONG).show();
                                break;

                            }



                            case DialogInterface.BUTTON_NEGATIVE:

                            {
                                dialog.dismiss();
                                break;
                            }

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(FinalBillActivity.this);
                builder.setMessage("You want to settle payment to generate bill?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });
       // tt.setText(table);
       // resturant_id=(String)getIntent().getStringExtra("resturant_id");
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id=prefs.getString("resturant_id","123");
        resturant_name=prefs.getString("name","123");
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_id=dataSnapshot.getValue(String.class);
                FirebaseDatabase.getInstance().getReference().child("user").child(user_id).child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                        user_name=userProfile.getName();
                        user_no=userProfile.getPhone();
                        phone.setText(user_no);
                        name.setText(user_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextView resturant=(TextView)findViewById(R.id.rest);
        resturant.setText(resturant_name);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.list_all_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FinalBillActivity.this));
        TextView ta=(TextView)findViewById(R.id.table);
        TextView date=(TextView)findViewById(R.id.date);
       // img=(ImageButton)findViewById(R.id.pdf);
        final TextView t=(TextView)findViewById(R.id.final_bill_total);
        date.setText(new Date().toString());

       //  table=getIntent().getIntExtra("table",0);
        ta.setText("Table no "+table);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("history");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    Order o=d.getValue(Order.class);

                    cuisineArrayList.addAll(o.getCuisines());
                    integers.addAll(o.getCount());
                }

                     progressDialog.dismiss();   /* for(Cuisine c:cuisineArrayList)
                {
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(resturant_id);
                   ref.child("Cuisine").child(c.getCousine_name()).child("price").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           prices.add(dataSnapshot.getValue(String.class));
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                           Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                       }
                   }) ;

                }*/
               requestPermission();
                RecyclerView recyclerView=(RecyclerView)findViewById(R.id.list_all_orders);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(FinalBillActivity.this));
                int i=0;
                for(Cuisine c:cuisineArrayList)
                {
                    prices.add(c.price);
                    total+=Integer.parseInt(c.price)*integers.get(i);
                    i++;
                }
                t.setText("Total Rs: "+Integer.toString(total));
                recyclerView.setAdapter(new Adapterx());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                   Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                   progressDialog.dismiss();
            }
        });

    }
 class Adapterx extends RecyclerView.Adapter<Adapterx.holder>
 {


     @NonNull
     @Override
     public Adapterx.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         return new Adapterx.holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.total_item,parent,false));
     }

     @Override
     public void onBindViewHolder(@NonNull Adapterx.holder holder, int position) {
         String s=Integer.toString(position+1)+". "+cuisineArrayList.get(position).getCousine_name()+"   X  "+Integer.toString(integers.get(position))+"    "+prices.get(position);
         holder.txt.setText(s);
         holder.total.setText("Rs: "+Integer.toString(Integer.parseInt(prices.get(position))*integers.get(position)));
     }

     @Override
     public int getItemCount() {
         return prices.size();
     }
     class holder extends RecyclerView.ViewHolder{
         TextView txt,total;
         public holder(@NonNull View itemView) {
             super(itemView);
             txt=itemView.findViewById(R.id.product);
             total=itemView.findViewById(R.id.total_bill);

         }
     }
 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.share)
        {
            if(b==null)
            {
                Toast.makeText(getApplicationContext(),"Please finalize bill first",Toast.LENGTH_LONG).show();
                return true;
            }
            String message;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                message="Thank you for visiting us\nHere is your bill\nDate : "+LocalDate.now().toString()+"\n Time : "+LocalTime.now();
            }
            else
            {
                message="Thank you for visiting us\nHere is your bill";

            }
            shareImage(b,message);
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(FinalBillActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(FinalBillActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(FinalBillActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
        }
    }

    void shareImage(Bitmap bitmap, String text){
        //bitmap is ur image and text is which is written in edtitext
        //you will get the image from the path
        String pathofBmp=
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        bitmap,"title", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "T9 App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Chose method to share receipt"));
    }

}


