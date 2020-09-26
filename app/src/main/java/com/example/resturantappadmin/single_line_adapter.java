package com.example.resturantappadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class single_line_adapter extends RecyclerView.Adapter<single_line_adapter.holder> {
    ArrayList<Cuisine> data;
    ArrayList<Integer> count;
    single_line_adapter(ArrayList<Cuisine> data,ArrayList<Integer> count)
    {
        this.data=data;
        this.count=count;
    }
    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.singleeditttext,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
              String s=Integer.toString(position+1)+". "+data.get(position).getCousine_name()+"   X  "+Integer.toString(count.get(position));
              holder.x.setText(s);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class holder extends RecyclerView.ViewHolder{
        TextView x;
        public holder(@NonNull View itemView) {
            super(itemView);
            x=itemView.findViewById(R.id.item_count);
        }
    }
}
