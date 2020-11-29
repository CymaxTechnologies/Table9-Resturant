package com.example.resturantappadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.resturantappadmin.Models.FoodType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {
    ArrayList<Cuisine> data=new ArrayList<>();
    String resturant_id,resturant__name;
    ArrayList<FoodType> foodTypes=new ArrayList<>();
    ArrayList<FoodType> allFoodTYpes=new ArrayList<>();
    ExpandableListView expandableListView;
    CustomExpandableAdapter customExpandableAdapter;
    EditText editText;
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
        editText=(EditText)findViewById(R.id.search);

        expandableListView=(ExpandableListView)findViewById(R.id.expandablelistview);
        FirebaseDatabase.getInstance().getReference().child(resturant_id).child("cuisines").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot parent:dataSnapshot.getChildren())
                {
                    FoodType foodType=new FoodType();
                    foodType.setName(parent.getKey());

                    for(DataSnapshot child:parent.getChildren())
                    {
                        Cuisine c=child.getValue(Cuisine.class);
                        foodType.getCuisines().add(c);
                    }
                    foodTypes.add(foodType);

                }
                allFoodTYpes.addAll(foodTypes);
                customExpandableAdapter=new CustomExpandableAdapter(getApplicationContext(),foodTypes);
                expandableListView.setAdapter(customExpandableAdapter);
                expandAll();
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                      customExpandableAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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
                FirebaseMessaging.getInstance().unsubscribeFromTopic(resturant_id);

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomExpandableAdapter extends BaseExpandableListAdapter
    {
        Context context;
        ArrayList<FoodType> foodTypes;
        CustomExpandableAdapter(Context c,ArrayList<FoodType> l)
        {
            context=c;
            foodTypes=l;
        }
        @Override
        public int getGroupCount() {
            return foodTypes.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return foodTypes.get(groupPosition).getCuisines().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return foodTypes.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return foodTypes.get(groupPosition).getCuisines().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            FoodType foodType=foodTypes.get(groupPosition);
            if(convertView==null)
            {
                convertView= LayoutInflater.from(context).inflate(R.layout.expandablelistviewheader,null);
            }
            TextView textView=convertView.findViewById(R.id.foodtypename);
            textView.setText(foodType.getName().toUpperCase());

            return convertView;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Cuisine c=foodTypes.get(groupPosition).getCuisines().get(childPosition);
            final View itemView;
            if(groupPosition==foodTypes.size()-1&&childPosition==foodTypes.get(groupPosition).getCuisines().size()-1)
            {

            }
//            Toast.makeText(context,c.cousine_name,Toast.LENGTH_LONG).show();
            if(true)
            {
                itemView=LayoutInflater.from(context).inflate(R.layout.menuitem,null);
            }
            else
            {
                itemView=convertView;
            }
            if(groupPosition==foodTypes.size()-1&&childPosition==foodTypes.get(groupPosition).getCuisines().size()-1)
            {
                itemView.setPadding(0,0,0,100);
            }
            TextView name,description,availability;
            CardView cardView;
            final ImageView picture,veg_non,veg_non_out;
            FrameLayout frameLayout;
            final Button add,remove,text;
            RatingBar ratingBar;
            LinearLayout linearLayout=itemView.findViewById(R.id.edittextlinearlayout);
            frameLayout=itemView.findViewById(R.id.frame);
            veg_non_out=itemView.findViewById(R.id.veg_vector);
            veg_non=itemView.findViewById(R.id.veg_pic);
            name=(TextView)itemView.findViewById(R.id.cousine_name);
            description=(TextView)itemView.findViewById(R.id.description);
            availability=(TextView)itemView.findViewById((R.id.about));
            picture=(ImageView) itemView.findViewById(R.id.picture);
            add=(Button)itemView.findViewById(R.id.add);
            ratingBar=(RatingBar)itemView.findViewById(R.id.ratingBar);
            name.setText(c.cousine_name);
            cardView=itemView.findViewById(R.id.menu_item_card);
            text=itemView.findViewById(R.id.textadd);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AdminHome.this,"working",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(AdminHome.this,AddItem.class);
                    intent.putExtra("cuisine",c);
                    startActivity(intent);
                }
            });
            if(c.price.equals(c.discount_price))
            {
                description.setText("Rs: "+c.price);
            }
            else
            {
                description.setText("Rs: "+c.price+"   "+c.discount_price,TextView.BufferType.SPANNABLE);

                Spannable spannable = (Spannable) description.getText();
                spannable.setSpan(new StrikethroughSpan(), 4, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            availability.setText(c.about);
            ratingBar.setRating(5);
            ratingBar.setVisibility(View.GONE);
            if(!c.getPicture().equals(""))
            {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                Glide.with(getApplicationContext())
                        .load(c.getPicture()).apply(requestOptions)
                        .apply(requestOptions)

                        .into(picture);
                picture.setPadding(0,0,5,0);
                final View view=LayoutInflater.from(AdminHome.this).inflate(R.layout.enlarg_image_layout,null);

                final RequestOptions finalRequestOptions = requestOptions;
                final RequestOptions finalRequestOptions1 = requestOptions;
                picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView gifImageView = new ImageView(AdminHome.this);
                        //T
                        // oast.makeText(getApplicationContext(),c.getPicture(),Toast.LENGTH_LONG).show();
                        final View view=LayoutInflater.from(AdminHome.this).inflate(R.layout.enlarg_image_layout,null);
                        Glide.with(AdminHome.this).load(c.getPicture()).into(gifImageView);

                        AlertDialog.Builder share_dialog = new AlertDialog.Builder(AdminHome.this);
                        share_dialog.setView(view);

                        Window window = share_dialog.create().getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.BOTTOM;
                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);
                        share_dialog.show();
                        Glide.with(AdminHome.this)
                                .asBitmap()
                                .load(c.getPicture())

                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        ImageView imageView=(ImageView)view.findViewById(R.id.imageView);

                                        imageView.setImageBitmap(resource);
                                    }});

                        // imageView.setImageDrawable(picture.getDrawable());



                    }
                });




                name.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                veg_non_out.setVisibility(View.GONE);
                name.setWidth(220);
                if(c.veg_nonveg.equals("non_veg"))
                {

                    veg_non_out.setImageResource(R.drawable.nonveg_vector);
                }


                //linearLayout.setLayoutParams(layoutParams);
            }
            else
            {
                if(c.veg_nonveg.equals("non_veg"))
                {

                    veg_non_out.setImageResource(R.drawable.nonveg_vector);
                }
                frameLayout.setVisibility(View.GONE);
            }

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdminHome.this);
                    builder.setCancelable(false);
                    builder.setTitle("Alert!");
                    builder.setMessage("Are you sure ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child(resturant_id).child("cuisines").child(foodTypes.get(groupPosition).getName()).child(foodTypes.get(groupPosition).getCuisines().get(childPosition).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(),"Dish removed Succesfully",Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Error occured : "+task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                    }
                                    customExpandableAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No",null);
                    builder.show();
                }
            });

            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
        void filter(String s)
        {
            foodTypes.clear();
            if(s.equals(""))
            {
                foodTypes.addAll(allFoodTYpes);
            }
            else
            {
                for(FoodType f:allFoodTYpes)
                {
                    FoodType x = null;
                    if(f.getName().toLowerCase().contains(s.toLowerCase()))
                    {
                        foodTypes.add(f);
                        continue;
                    }
                    for(Cuisine c:f.getCuisines())
                    {
                        if(c.getCousine_name().toLowerCase().contains(s.toLowerCase()))
                        {
                            if(x==null)
                            {
                                x=new FoodType();
                                x.setName(c.getCousine_name());
                                x.getCuisines().add(c);

                            }
                            else
                            {
                                x.getCuisines().add(c);
                            }
                        }
                    }
                    if(x!=null)
                    {
                        foodTypes.add(x);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
    private void expandAll() {
        int count = customExpandableAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.expandGroup(i);
        }
    }
}