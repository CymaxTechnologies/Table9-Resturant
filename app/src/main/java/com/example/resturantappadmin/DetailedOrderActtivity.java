package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetailedOrderActtivity extends AppCompatActivity {
     ArrayList<Order>   order=new ArrayList<>();
     ArrayList<Notification> data=new ArrayList<>();
     Context c;
     String resturant_id="";
     String table="";
     String resturant_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_order_acttivity);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id=prefs.getString("resturant_id","123");
        resturant_name=prefs.getString("name","123");
        //table=(String)getIntent().getStringExtra("table");
        TextView t=(TextView)findViewById(R.id.rest);
        t.setText(resturant_name);

        final ProgressDialog progressDialog=new ProgressDialog(DetailedOrderActtivity.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);
        table=Integer.toString((Integer)getIntent().getIntExtra("table",0)) ;
        c=getApplicationContext();
        TextView textView=(TextView)findViewById(R.id.table_no_detail) ;
        textView.setText("Table no "+table);
        //String rest_id="123456789";
        final RecyclerView recycler=(RecyclerView)findViewById(R.id.order_id_description);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        final RecyclerView recycler1=(RecyclerView)findViewById(R.id.order_id_notification);
        recycler1.setHasFixedSize(true);
        recycler1.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                data.clear();
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    data.add(d.getValue(Notification.class));
                }
                recycler1.setAdapter(new NotificationAdapter());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(table).child("pending");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order.clear();
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    order.add(d.getValue(Order.class));
                }
                progressDialog.dismiss();
                recycler.setAdapter(new recyclerView(order));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                 progressDialog.dismiss();
            }
        });

    }


    class recyclerView extends RecyclerView.Adapter<recyclerView.holder>
    {
     ArrayList<Order> o;
       recyclerView(ArrayList<Order> o)
       {
           this.o=o;
       }


        @NonNull
        @Override
        public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.orderldescription,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull final holder holder, int position) {
                      final Order order=o.get(position);
                      holder.time.setText("Order at "+order.getCheck_in_time());
                      holder.recyclerView.setAdapter(new single_line_adapter(order.getCuisines(),order.getCount()));
                      holder.textView.setText(order.getValue()+"");
                      String status=order.status;
                      if(status.equals("Accepted"))
                      {
                          holder.time.setText("Order at "+order.getCheck_in_time());
                          holder.accept.setBackgroundColor(Color.GRAY);
                          holder.reject.setBackgroundColor(Color.GRAY);
                          holder.accept.setEnabled(false);
                          holder.reject.setEnabled(false);

                      }
                      if(status.equals("Served"))
                      {
                          holder.time.setText("Order at "+order.getCheck_in_time());
                          holder.accept.setBackgroundColor(Color.GRAY);
                          holder.reject.setBackgroundColor(Color.GRAY);
                          holder.accept.setEnabled(false);
                          holder.reject.setEnabled(false);
                          holder.served.setEnabled(false);
                          holder.served.setText("Completed");
                      }
                      holder.served.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              holder.served.setText("Completed");

                              Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_baseline_check_24);
                              holder.reject.setEnabled(false);
                              holder.reject.setBackgroundColor(Color.GRAY);
                              holder.accept.setBackgroundColor(Color.GRAY);
                              holder.served.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                              DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(order.table).child("history").push();
                              ref.setValue(order);
                          //    o.remove(order);

                              FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(order.table).child("pending").child(order.getOrder_id()).child("status").setValue("Served");
                              ref=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(order.table).child("pending").child(order.order_id);
                             // ref.removeValue();
                              notifyDataSetChanged();
                              FirebaseDatabase.getInstance().getReference().child("user").child(order.getCustomer_id()).child("my_orders").child(resturant_id).child(order.order_id).child("status").setValue("Served");

                              Toast.makeText(getApplicationContext(),"Order Served Succesfully",Toast.LENGTH_LONG).show();
                          }
                      });
                      holder.accept.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              FirebaseDatabase.getInstance().getReference().child("user").child(order.getCustomer_id()).child("my_orders").child(resturant_id).child(order.order_id).child("status").setValue("Accepted");
                              FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(order.table).child("pending").child(order.getOrder_id()).child("status").setValue("Accepted");

                              //FirebaseDatabase.getInstance().getReference().child("user").child(order.customer_id).child("my_orders").child(resturant_id).child(order.getOrder_id()).child("status").setValue("Accepted");
                              holder.accept.setEnabled(false);
                              holder.reject.setEnabled(false);
                              holder.reject.setBackgroundColor(Color.GRAY);
                              holder.accept.setBackgroundColor(Color.GRAY);
                              Toast.makeText(getApplicationContext(),"Accepted",Toast.LENGTH_SHORT).show();
                          }
                      });
                      holder.reject.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              FirebaseDatabase.getInstance().getReference().child("user").child(order.customer_id).child("my_orders").child(resturant_id).child(order.getOrder_id()).child("status").setValue("Rejected");
                              holder.accept.setEnabled(false);
                              holder.reject.setEnabled(false);
                              holder.reject.setBackgroundColor(Color.GRAY);
                              holder.accept.setBackgroundColor(Color.GRAY);
                              DatabaseReference ref;
                              ref=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(order.table).child("pending").child(order.order_id);
                              ref.removeValue();
                              notifyDataSetChanged();
                              Toast.makeText(getApplicationContext(),"Rejected",Toast.LENGTH_SHORT).show();

                          }
                      });

                              ;
        }

        @Override
        public int getItemCount() {
            return o.size();
        }
        class holder extends RecyclerView.ViewHolder{
            RecyclerView recyclerView;
            Button served;
            Button accept,reject;
            TextView textView,time;
            public holder(@NonNull View itemView) {
                super(itemView);
                recyclerView=itemView.findViewById(R.id.order_items);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(c));
                served=itemView.findViewById(R.id.served);
                accept=itemView.findViewById(R.id.accept);
                reject=itemView.findViewById(R.id.reject);
                textView=itemView.findViewById(R.id.total_value);
                time=itemView.findViewById(R.id.id_time);

            }
        }
    }
    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.holder>
    {
        @NonNull
        @Override
        public NotificationAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NotificationAdapter.holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull final NotificationAdapter.holder holder, int position) {
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
                    holder.btn.setText("Completed");

                    Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_baseline_check_24);
                    holder.btn.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    FirebaseDatabase.getInstance().getReference().child(resturant_id).child("notifications").child(n.id).removeValue();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.decmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.finalize)
        {
            Intent i=new Intent(getApplicationContext(),FinalBillActivity.class);
            i.putExtra("table",table);
            i.putExtra("resturant_id",resturant_id);

            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}