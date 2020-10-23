package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.EventListener;

public class NotificationActivity extends AppCompatActivity {
    String resturant_id="";
    String resturant_name="";
ArrayList<Notification> data=new ArrayList<>();
ArrayList<String> rec=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id=prefs.getString("resturant_id","123");
        resturant_name=prefs.getString("name","123");
        TextView t=(TextView)findViewById(R.id.rest);
        t.setText(resturant_name);
        final ProgressDialog progressDialog=new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);
        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.notification_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications");
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
      /*  ArrayList<String> not=new ArrayList<>();
        for(Notification n:data)
        {
            if(not.contains(n.user_id))
        }*/
    }
    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.holder>
    {
        @NonNull
        @Override
        public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull final holder holder, int position) {
             final Notification n=data.get(position);
             final String m="Table no "+n.table_no+" "+n.message;
             System.out.println(n.message);
             if(n.message.contains("New Order"))
             {
                 FirebaseDatabase.getInstance().getReference().child("user").child(n.user_id).child("profile").addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         UserProfile userProfile=dataSnapshot.getValue(UserProfile.class);
                         if(userProfile==null);
                         {
                             userProfile=new UserProfile();
                         }
                         String str="New order From Table no "+n.table_no+"  "+n.time;
                         holder.msg.setText(str);
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });

                 holder.img.setImageResource(R.drawable.new_order_icon);
                 holder.btn.setText("View Order");
                 holder.msg.setText(n.message);
                 holder.btn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").child(n.id).removeValue();
                         // Toast.makeText(getApplicationContext(),"Deleted Succesfully",Toast.LENGTH_SHORT).show();
                         data.remove(n);
                         rec.remove(n.user_id);
                         FirebaseDatabase.getInstance().getReference().child(n.resturant_id).child("orders").child(n.table_no).child("notification").child(n.id).removeValue();
                         Intent i=new Intent(getApplicationContext(),DetailedOrderActtivity.class);
                         i.putExtra("table",Integer.parseInt(n.getTable_no()));
                         startActivity(i);
                         notifyDataSetChanged();
                     }
                 });

                 return;
             }
         /*   if (n.message.contains("Order"))
            {
                holder.msg.setText(n.message);
                holder.btn.setText("View Order");
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").child(n.id).removeValue();
                        // Toast.makeText(getApplicationContext(),"Deleted Succesfully",Toast.LENGTH_SHORT).show();
                        data.remove(n);
                        rec.remove(n.user_id);
                        FirebaseDatabase.getInstance().getReference().child(n.resturant_id).child("orders").child(n.table_no).child("notification").child(n.id).removeValue();
                        startActivity(new Intent(getApplicationContext(),OrdersActivity.class));
                        notifyDataSetChanged();

                    }
                });
                holder.img.setImageResource(R.drawable.customer_icon);
                return;
            }*/
             if(n.message.contains("waiting"))
             {  final String x="New Arrival \n";

                 FirebaseDatabase.getInstance().getReference().child("user").child(n.user_id).child("profile").child("name").addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         String userProfile=dataSnapshot.getValue(String.class);

                         String a="From "+userProfile+"\n"+n.id;
                         holder.msg.setText(n.message);
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
                 holder.img.setImageResource(R.drawable.customer_icon);
                 holder.btn.setText("Manage tables");
                 holder.btn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").child(n.id).removeValue();
                        // Toast.makeText(getApplicationContext(),"Deleted Succesfully",Toast.LENGTH_SHORT).show();
                         data.remove(n);
                         rec.remove(n.user_id);
                         FirebaseDatabase.getInstance().getReference().child(n.resturant_id).child("orders").child(n.table_no).child("notification").child(n.id).removeValue();
                         startActivity(new Intent(getApplicationContext(),NewClientActivity.class));
                         notifyDataSetChanged();
                     }
                 });


             }

             else{
                     holder.msg.setText(m);

                 if (holder.msg.getText().toString().contains("Cutlery")) {
                     holder.img.setImageResource(R.drawable.cuttlery);
                     holder.btn.setText("Completed");
                 } else if (holder.msg.getText().toString().contains("waiter")) {
                     holder.img.setImageResource(R.drawable.callwaiter);
                     holder.btn.setText("Completed");
                 } else if (holder.msg.getText().toString().contains("bill")) {
                     holder.btn.setText("Settle Bill");
                     holder.img.setImageResource(R.drawable.cook);
                 } else {
                     holder.img.setImageResource(R.drawable.report);
                     holder.btn.setText("Serve now");
                 }


                 holder.btn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").child(n.id).removeValue();
                         Toast.makeText(getApplicationContext(), "Deleted Succesfully", Toast.LENGTH_SHORT).show();
                         data.remove(n);
                         FirebaseDatabase.getInstance().getReference().child(n.resturant_id).child("orders").child(n.table_no).child("notification").child(n.id).removeValue();
                         notifyDataSetChanged();
                     }
                 });

             }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_add_item)
        {
            startActivity(new Intent(getApplicationContext(),AdminHome.class));
        }
        if(item.getItemId()==R.id.menu_logout)
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(resturant_id);

            SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences((NotificationActivity.this));
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
           // editor.apply();

            editor.commit();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));

        }
        if(item.getItemId()==R.id.menu_orders)
        {
            startActivity(new Intent(getApplicationContext(),OrdersActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}