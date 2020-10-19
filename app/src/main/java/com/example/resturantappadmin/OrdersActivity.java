package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.style.TtsSpan;
import android.util.Log;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class OrdersActivity extends AppCompatActivity {
    ArrayList<Integer> data = new ArrayList<>();
    Integer total_tables = 0;
    ArrayList<Integer> all_tables = new ArrayList<>();
    String resturant_id = "";
    String resturant__name = "";
    RequestQueue mreRequestQueue;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        resturant_id = prefs.getString("resturant_id", "123");
        resturant__name = prefs.getString("name", "123");

        TextView t = (TextView) findViewById(R.id.rest);
        t.setText(resturant__name);
       // FirebaseMessaging.getInstance().subscribeToTopic("all");
        final ProgressDialog progressDialog = new ProgressDialog(OrdersActivity.this);
        progressDialog.setMessage("Please....");
        progressDialog.setTitle("T9 App");
        progressDialog.show();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_24);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
      //  recyclerView.setAdapter(new RecommendedAdapter());
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("total_tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    total_tables = Integer.parseInt(dataSnapshot.getValue(String.class));
                    for (int i = 1; i <= total_tables; i++) {
                        all_tables.add(i);
                    }
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            data.clear();
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try{
       SendNotification("test","working","jhl");}
        catch (Exception e)
        {

        }



    }

    public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.holder> {

        Context c;
        ArrayList<Cuisine> all = new ArrayList<>();

        @NonNull
        @Override
        public RecommendedAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.tables, parent, false);
            RecommendedAdapter.holder myHolder = new RecommendedAdapter.holder(listItem);
            return myHolder;

        }

        @Override
        public void onBindViewHolder(final RecommendedAdapter.holder holder, final int position) {
              int table = all_tables.get(position);
            holder.name.setText("Table no " + Integer.toString(table));
            if (data.contains(table)) {
                Glide.with(getApplicationContext()).load(R.drawable.table_occupied).into(holder.picture);
                FirebaseDatabase.getInstance().getReference().child(resturant_id).child("orders").child(Integer.toString(table)).child("pending").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
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
                        if (dataSnapshot.hasChildren()) {
                            Glide.with(getApplicationContext()).load(R.drawable.not_24).into(holder.not);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // Glide.with(getApplicationContext()).load(R.drawable.notification_active).into(holder.not);
                holder.not.setPadding(0, 0, 0, 0);
                //    Glide.with(getApplicationContext()).load(R.drawable.table_occupied).into(holder.picture);
                // holder.cardView.setBackgroundColor(Color.GREEN);
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(getApplicationContext(), DetailedOrderActtivity.class);
                        i.putExtra("table",  all_tables.get(position));
                        startActivity(i);
                    }
                });
            }


        }

        @Override
        public int getItemCount() {

            return all_tables.size();
        }

        class holder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView picture;
            ImageView not, wait;
            CardView cardView;

            public holder(@NonNull View itemView) {
                super(itemView);
                picture = (ImageView) itemView.findViewById(R.id.emptytable);
                name = (TextView) itemView.findViewById(R.id.tableno);
                not = (ImageView) itemView.findViewById(R.id.notification_present);
                wait = (ImageView) itemView.findViewById(R.id.waiting_prsent);
                cardView = itemView.findViewById(R.id.card_order);


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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.goto_notification) {
            startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            // finish();
        }
        if (item.getItemId() == R.id.goto_addcuisine) {
            startActivity(new Intent(getApplicationContext(), AdminHome.class));
            //finish();
        }
        else  if (item.getItemId() == R.id.go_logout)
        {
            SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences((OrdersActivity.this));
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(resturant_id);
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void SendNotification(String to, String title, String body) throws JSONException {
       RequestQueue mreRequestQueue= Volley.newRequestQueue(getApplicationContext());
        JSONObject main = new JSONObject();
        main.put("to", "/topics/"+to);
        JSONObject not = new JSONObject();
        not.put("title", title);
        not.put("body", body);
        main.put("notification", not);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", main, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }


        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("authorization", "key=AAAAwEFI5e4:APA91bFSQX3VcCztgTU7cgZ7SnM0XYDTH7wZXtQG4UyU5gJiiNX-6cDXxHJm9KgihoUCtxmxf74pdUYcPyutF0eNi7j7vmuUwo0a-UkY94wxXbpKy8iXg1w8PfJF9zGHmeJ5DGgXDAOy");
                map.put("content-type", "application/json");
                return map;
            }

        };
       // Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        mreRequestQueue.add(jsonObjectRequest);
    }

}

