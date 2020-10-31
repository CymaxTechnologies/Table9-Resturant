package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewClientActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String table;
    String  resturant_id;
    String resturant_name;
    Notification no;
    ArrayList<Notification> data=new ArrayList<>();
    ArrayList<String> rec=new ArrayList<>();

    ArrayList<String> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id=prefs.getString("resturant_id","123");
        resturant_name=prefs.getString("name","123");
        recyclerView=(RecyclerView)findViewById(R.id.new_arrival_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(NewClientActivity.this));
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("new_arrivals").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    data.add(d.getValue(Notification.class));
                }
                recyclerView.setAdapter(new New_Arrival_Adapter());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("total_tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                table=dataSnapshot.getValue(String.class);
                for(int i=1;i<=Integer.parseInt(table);i++)
                {
                    list.add(Integer.toString(i));
                }
                FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d:dataSnapshot.getChildren())
                        {
                            String s=d.getValue(String.class);
                            list.remove(s);
                        }
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


    }
    class New_Arrival_Adapter extends RecyclerView.Adapter<New_Arrival_Adapter.holder>
    {
        @NonNull
        @Override
        public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
            View v=layoutInflater.inflate(R.layout.new_arrival_card,parent,false);
            holder h=new holder(v);

            return h;
        }

        @Override
        public void onBindViewHolder(@NonNull holder holder, int position) {
            final Notification n=data.get(position);
          holder.txt.setText(n.message);
           holder. reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(n.user_id).setValue("not_available").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("new_arrivals").child(n.id).removeValue();
                            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(n.user_id).removeValue();
                            try {
                       //         NotiHelper.SendNotification(n.user_id,"Alert","We can not assign a table to you Now? Try after sometime");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            notifyDataSetChanged();
                        }
                    });

                }
            });
           holder.accept.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   no=n;
                   Intent i=new Intent(getApplicationContext(),SelectTableActivity.class);
                   i.putExtra("resturant_id",resturant_id);
                   startActivityForResult(i,0);
               }
           });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class  holder extends RecyclerView.ViewHolder
        {
            Button accept,reject;
            TextView txt;
            public holder(@NonNull View itemView) {
                super(itemView);
                txt=itemView.findViewById(R.id.user_phone);
                accept=itemView.findViewById(R.id.new_aarival_assign);
                reject=itemView.findViewById(R.id.new_arrival_reject);

            }
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final String returnedResult = data.getData().toString();
                FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(returnedResult).child("user").setValue(no.user_id);
                FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(no.user_id).setValue(returnedResult).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       FirebaseDatabase.getInstance().getReference().child(resturant_id).child("new_arrivals").child(no.id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                            //   FirebaseDatabase.getInstance().getReference().child(resturant_id).child("table_assignment").child(no.user_id).removeValue();
                       FirebaseDatabase.getInstance().getReference().child(resturant_id).child("outside").child(no.user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               Notification no=new Notification();
                               no.setResturant_id(resturant_id);
                               no.setTable_no(returnedResult);
                               no.setMessage("New Order at table "+returnedResult);
                               no.setTime(new Date().toString());
                               no.setUser_id(no.user_id);

                               for(DataSnapshot d:dataSnapshot.getChildren())
                               {
                                   Order  o=d.getValue(Order.class);

                                   DatabaseReference key=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(returnedResult).child("pending");

                                   o.setTable(returnedResult);
                                   key.child(o.order_id).setValue(o);
                                   FirebaseDatabase.getInstance().getReference().child("user").child(o.getCustomer_id()).child("my_orders").child(resturant_id).child(o.order_id).setValue(o);
                                   d.getRef().removeValue();
                                   DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").push();
                                   no.setId(reference.getKey());
                                   reference.setValue(no);
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                           }
                       });
                        try {
                            //         NotiHelper.SendNotification(n.user_id,"Alert","We can not assign a table to you Now? Try after sometime");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //notifyDataSetChanged();
                    }
                });

            }
        }
    }
}