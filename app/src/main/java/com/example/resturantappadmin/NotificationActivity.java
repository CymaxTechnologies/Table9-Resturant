package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
ArrayList<Notification> data=new ArrayList<>();
String restuarant_id="123456789";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        final ProgressDialog progressDialog=new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);
        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.notification_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(restuarant_id).child("notifications");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
               for(DataSnapshot  d:dataSnapshot.getChildren())
               {
                   data.add(d.getValue(Notification.class));
               }
               recyclerView.setAdapter(new NotificationAdapter());
               progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
             progressDialog.dismiss();
            }
        });
    }
    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.holder>
    {
        @NonNull
        @Override
        public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull holder holder, int position) {
             final Notification n=data.get(position);
             String m="Table no "+n.table_no+" "+n.message;
             holder.msg.setText(m);
             if(holder.msg.getText().toString().contains("Cutlery"))
             {
                 holder.img.setImageResource(R.drawable.cuttlery);
             }
             else if(holder.msg.getText().toString().contains("waiter"))
             {
                 holder.img.setImageResource(R.drawable.callwaiter);
             }
             else if(holder.msg.getText().toString().contains("bill"))
             {
                 holder.img.setImageResource(R.drawable.cook);
             }
             else
             {
                 holder.img.setImageResource(R.drawable.report);
             }
             holder.btn.setText("Serve now");
             holder.btn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
               FirebaseDatabase.getInstance().getReference().child(restuarant_id).child("notifications").child(n.id).removeValue();
                     Toast.makeText(getApplicationContext(),"Deleted Succesfully",Toast.LENGTH_SHORT).show();
                     data.remove(n);
               FirebaseDatabase.getInstance().getReference().child(n.resturant_id).child("orders").child(n.table_no).child("notification").child(n.id).removeValue();
                   notifyDataSetChanged();
                 }
             });


        }

        @Override
        public int getItemCount() {
            return data.size();
        }

         class holder extends RecyclerView.ViewHolder
        {
              ImageView img;
              TextView msg;
              Button btn;
            public holder(@NonNull View itemView) {
                super(itemView);
                img=itemView.findViewById(R.id.picture);
                msg=itemView.findViewById(R.id.cousine_name);
                btn=itemView.findViewById(R.id.add);
            }
        }

    }
}