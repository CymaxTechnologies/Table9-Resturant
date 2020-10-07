package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {
    ArrayList<Cuisine> data=new ArrayList<>();
    String resturant_id,resturant__name;
     ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_home);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id=prefs.getString("resturant_id","123");
        resturant__name=prefs.getString("name","123");
        TextView t=(TextView)findViewById(R.id.rest);
        t.setText(resturant__name);
        progressDialog=new ProgressDialog(AdminHome.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
      //  getSupportActionBar().hide();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);
      //  Glide.with()
        ImageButton btn=(ImageButton) findViewById(R.id.cartbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),AddItem.class);
                startActivity(i);
            }
        });


        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.cart_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(resturant_id).child("Cuisine");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    Cuisine co=d.getValue(Cuisine.class);
                    data.add(co);
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
            Glide.with(getApplicationContext())
                    .load(cuisine.getPicture())

                    .into(holder.picture);

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    DatabaseReference dbr=FirebaseDatabase.getInstance().getReference().child(resturant_id).child("Cuisine").child(cuisine.getCousine_name());
                    dbr.removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Succesfuly removed",Toast.LENGTH_LONG).show();
                        }
                    });
                    notifyDataSetChanged();
                }
            });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.notification_present:
            {
                startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
                break;
            }
            case R.id.food:
            {
                startActivity(new Intent(getApplicationContext(),OrdersActivity.class));
                break;
            }
            case R.id.logout:
            {
                FirebaseAuth.getInstance().signOut();;
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}