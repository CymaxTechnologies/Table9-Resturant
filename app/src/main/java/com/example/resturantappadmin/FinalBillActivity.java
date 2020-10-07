package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class FinalBillActivity extends AppCompatActivity {
      TextView date,tt;
      String resturant_id="";
      int total=0;
      String table;
      ImageButton img;


      ArrayList<Cuisine> cuisineArrayList=new ArrayList<>();
      ArrayList<Integer> integers=new ArrayList<>();
      ArrayList<String> prices=new ArrayList<>();
      DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(resturant_id);
      String resturant_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_bill);
        final ProgressDialog progressDialog=new ProgressDialog(FinalBillActivity.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
        table=(String)getIntent().getStringExtra("table") ;
       // resturant_id=(String)getIntent().getStringExtra("resturant_id");
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id=prefs.getString("resturant_id","123");
        resturant_name=prefs.getString("name","123");
        TextView resturant=(TextView)findViewById(R.id.rest);
        resturant.setText(resturant_name);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.list_all_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FinalBillActivity.this));
        TextView ta=(TextView)findViewById(R.id.table);
        TextView date=(TextView)findViewById(R.id.date);
        img=(ImageButton)findViewById(R.id.pdf);
        final TextView t=(TextView)findViewById(R.id.final_bill_total);
        date.setText(new Date().toString());

       //  table=getIntent().getIntExtra("table",0);
        ta.setText("Table no "+table);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("history");

        ref.addValueEventListener(new ValueEventListener() {
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
        if(item.getItemId()==R.id.edit)
        {

        }
        else
        {
            final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("history").push();
            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("history").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ref.child("items").setValue(dataSnapshot.getValue());
                    ref.child("total").setValue(total);
                    //finish();

                   // FirebaseDatabase.getInstance().getReference().child(resturant_id).child(Integer.toString(table)).removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //create folder
            File file = new File(Environment.getExternalStorageDirectory() + "/Table9/");
            if (!file.mkdirs()) {
                file.mkdirs();
            }
            PDFHelper pdfHelper=new PDFHelper(file,getApplicationContext());
            View view =(ScrollView)findViewById(R.id.scrol );

            Bitmap b=pdfHelper.getBitmapFromView(view);
            img.setImageBitmap(b);
           pdfHelper.saveImageToPDF(view,b,new Date().toString()+"slip");
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
}


