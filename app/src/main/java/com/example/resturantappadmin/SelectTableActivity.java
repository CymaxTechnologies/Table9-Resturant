package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectTableActivity extends AppCompatActivity {
    String table,resturant_id;
    ArrayList<String> list=new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_table);
        resturant_id=(String)getIntent().getStringExtra("resturant_id");
        recyclerView=(RecyclerView)findViewById(R.id.table_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(SelectTableActivity.this,4));
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
                          list.remove(d.getValue(String.class));
                       }
                       recyclerView.setAdapter(new adapter());
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
    class adapter extends RecyclerView.Adapter<adapter.holder>
    {
        @NonNull
        @Override
        public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.table_no_card,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull holder holder, final int position) {
            holder.table.setText(list.get(position));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE: {

                                    Intent data = new Intent();

//---set the data to pass back---
                                    data.setData(Uri.parse(list.get(position)));
                                    setResult(RESULT_OK, data);
//---close the activity---
                                    finish();
                                    break;
                                }

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectTableActivity.this);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class  holder extends RecyclerView.ViewHolder
        {
                 TextView table;
                 CardView cardView;
            public holder(@NonNull View itemView) {
                super(itemView);
                table=itemView.findViewById(R.id.table_no_text);
                cardView=itemView.findViewById(R.id.free_table_card);

            }
        }
    }
}