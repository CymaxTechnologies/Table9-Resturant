package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetailedOrderActtivity extends AppCompatActivity {
     ArrayList<Order>   order=new ArrayList<>();
     Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_order_acttivity);
        String table="1";
        c=getApplicationContext();
        String rest_id="123456789";
        final RecyclerView recycler=(RecyclerView)findViewById(R.id.order_id_description);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child(rest_id).child("orders").child(table).child("pending");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order.clear();
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    order.add(d.getValue(Order.class));
                }
                recycler.setAdapter(new recyclerView(order));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        public void onBindViewHolder(@NonNull holder holder, int position) {
                      final Order order=o.get(position);
                      holder.recyclerView.setAdapter(new single_line_adapter(order.getCuisines(),order.getCount()));
                      holder.textView.setText(order.getValue()+"");
                      holder.served.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("123456789").child("orders").child(order.table).child("history").push();
                              ref.setValue(order);
                              o.remove(order);
                              ref=FirebaseDatabase.getInstance().getReference().child("123456789").child("orders").child(order.table).child("pending").child(order.order_id);
                              ref.removeValue();
                              notifyDataSetChanged();
                              Toast.makeText(getApplicationContext(),"Order Served Succesfully",Toast.LENGTH_LONG).show();
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
            TextView textView;
            public holder(@NonNull View itemView) {
                super(itemView);
                recyclerView=itemView.findViewById(R.id.order_items);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(c));
                served=itemView.findViewById(R.id.served);
                accept=itemView.findViewById(R.id.accept);
                reject=itemView.findViewById(R.id.reject);
                textView=itemView.findViewById(R.id.total_value);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}