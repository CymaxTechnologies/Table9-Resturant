package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {
    ArrayList<Cuisine> data=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        getSupportActionBar().hide();
        ImageButton btn=(ImageButton) findViewById(R.id.cartbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddItem.class));
            }
        });
        ImageButton order=(ImageButton) findViewById(R.id.order);
        ImageButton not=(ImageButton)findViewById(R.id.notification);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OrdersActivity.class));
               //   finish();
            }
        });
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
            //    finish();
            }
        });
        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.cart_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("123456789").child("Cuisine");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    Cuisine co=d.getValue(Cuisine.class);
                    data.add(co);
                }
                recyclerView.setAdapter(new RecommendedAdapter());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.holder> implements Filterable {

        Context c;
        ArrayList<Cuisine> all=new ArrayList<>();

        @NonNull
        @Override
        public RecommendedAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.menuitem, parent, false);
            RecommendedAdapter.holder myHolder=new RecommendedAdapter.holder(listItem);
            return myHolder;

        }

        @Override
        public void onBindViewHolder(final RecommendedAdapter.holder holder, int position) {
            final Cuisine cuisine = data.get(position);
            holder.name.setText(cuisine.getCousine_name());
            holder.description.setText(cuisine.getAbout());
            holder.availability.setText(cuisine.getTimming());
            holder.ratingBar.setRating(5);
        }

        @Override
        public int getItemCount(){

            return data.size();
        }
        class holder extends RecyclerView.ViewHolder  {
            TextView name,description,availability;
            ImageButton picture;
            Button add;
            RatingBar ratingBar;
            public holder(@NonNull View itemView) {
                super(itemView);

                name=(TextView)itemView.findViewById(R.id.cousine_name);
                description=(TextView)itemView.findViewById(R.id.description);
                availability=(TextView)itemView.findViewById((R.id.about));
                picture=(ImageButton)itemView.findViewById(R.id.picture);
                add=(Button)itemView.findViewById(R.id.add);
                ratingBar=(RatingBar)itemView.findViewById(R.id.ratingBar);


            }
        }
        private Filter exampleFilter = new Filter() {
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

    }
}