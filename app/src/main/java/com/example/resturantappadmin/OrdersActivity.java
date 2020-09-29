package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    ArrayList<Integer> data=new ArrayList<>();
    Integer total_tables=0;
    ArrayList<Integer> all_tables=new ArrayList<>();
    String resturant_id="123456789";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        final ProgressDialog progressDialog=new ProgressDialog(OrdersActivity.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total_tables=Integer.parseInt(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for (int i=1;i<=10;i++)
        {
            all_tables.add(i);
        }




        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecommendedAdapter());
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("123456789").child("orders");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                 for(DataSnapshot d:dataSnapshot.getChildren())
                 {
                     data.add(Integer.parseInt(d.getKey()));
                 }
                recyclerView.setAdapter(new RecommendedAdapter());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                 progressDialog.dismiss();
            }
        });

    }
    public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.holder> {

        Context c;
        ArrayList<Cuisine> all=new ArrayList<>();

        @NonNull
        @Override
        public RecommendedAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.tables, parent, false);
            RecommendedAdapter.holder myHolder=new RecommendedAdapter.holder(listItem);
            return myHolder;

        }

        @Override
        public void onBindViewHolder(final RecommendedAdapter.holder holder, int position) {
         final int table=all_tables.get(position);
         holder.name.setText("Table no "+Integer.toString(table));
         if(data.contains(table))
         {
             Glide.with(getApplicationContext()).load(R.drawable.table_occupied).into(holder.picture);
             FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(Integer.toString(table)).child("pending").addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(dataSnapshot.hasChildren())
                   {
                       Glide.with(getApplicationContext()).load(R.drawable.waiting).into(holder.wait);
                   }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
                 });
             FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(Integer.toString(table)).child("notification").addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if(dataSnapshot.hasChildren())
                  {
                      Glide.with(getApplicationContext()).load(R.drawable.not_24).into(holder.not);
                  }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
            // Glide.with(getApplicationContext()).load(R.drawable.notification_active).into(holder.not);
             holder.not.setPadding(0,0,0,0);
         //    Glide.with(getApplicationContext()).load(R.drawable.table_occupied).into(holder.picture);
            // holder.cardView.setBackgroundColor(Color.GREEN);
             holder.cardView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent i=new Intent(getApplicationContext(),DetailedOrderActtivity.class);
                     i.putExtra("table",table);
                     startActivity(i);
                 }
             });
         }



        }

        @Override
        public int getItemCount(){

            return all_tables.size();
        }
        class holder extends RecyclerView.ViewHolder  {
            TextView name;
            ImageView picture;
            ImageView not,wait;
            CardView cardView;
            public holder(@NonNull View itemView) {
                super(itemView);
                picture=(ImageView)itemView.findViewById(R.id.emptytable) ;
                name=(TextView)itemView.findViewById(R.id.tableno);
                not=(ImageView)itemView.findViewById(R.id.notification_present) ;
                wait=(ImageView)itemView.findViewById(R.id.waiting_prsent) ;
                cardView=itemView.findViewById(R.id.card_order);




            }

      /*  private Filter exampleFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Cuisine> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(all);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Cuisine  item : data) {
                        if (item.getCousine_name().toLowerCase().contains(filterPattern) ||item.getAbout().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.clear();
                data.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

        @Override
        public Filter getFilter() {
            return exampleFilter;
        }

    }*/
        }}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.goto_notification)
        {
            startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
           // finish();
        }
        if(item.getItemId()==R.id.goto_addcuisine)
        {
            startActivity(new Intent(getApplicationContext(),AdminHome.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

