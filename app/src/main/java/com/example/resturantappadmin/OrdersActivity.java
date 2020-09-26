package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        data.add(1);
        data.add(2);

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            View listItem= layoutInflater.inflate(R.layout.order_item, parent, false);
            RecommendedAdapter.holder myHolder=new RecommendedAdapter.holder(listItem);
            return myHolder;

        }

        @Override
        public void onBindViewHolder(final RecommendedAdapter.holder holder, int position) {
         final int table=data.get(position);
         holder.name.setText("Table no "+Integer.toString(table)+" has placed order");
            Glide.with(getApplicationContext())
                    .load(R.drawable.cook)
                    .into(holder.picture);
            holder.    cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext(),DetailedOrderActtivity.class);
                    i.putExtra("table",table);
                    startActivity(i);
                }
            });
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext(),FinalBillActivity.class);
                    i.putExtra("table",table);
                    startActivity(i);
                }
            });



        }

        @Override
        public int getItemCount(){

            return data.size();
        }
        class holder extends RecyclerView.ViewHolder  {
            TextView name;
            ImageButton picture;
            Button add;
            RatingBar ratingBar;
            LinearLayout cardView;
            public holder(@NonNull View itemView) {
                super(itemView);

                name=(TextView)itemView.findViewById(R.id.cousine_name);
                picture=(ImageButton) itemView.findViewById(R.id.picture);
                add=(Button) itemView.findViewById(R.id.add);
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
}