package com.example.ourapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";//used for debugging - a static logtag with our class name

    private ArrayList<String> imagesNames = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();

    private ArrayList<String> ids = new ArrayList<>();

    private Context context;

    public RecyclerViewAdapter(ArrayList<String> imagesNames, ArrayList<String> images, Context context, ArrayList<String> ids) {
        this.imagesNames = imagesNames;
        this.images = images;
        this.context = context;
        this.ids = ids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder : called");

        Glide.with(context)
                .asBitmap()
                .load(images.get(position)) //image url
                .into(holder.circleImageView); //loading image into image view

        holder.teachersName.setText(imagesNames.get(position));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TutorsRating");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(ids.size() > 0){
                    if(snapshot.child(ids.get(position)).exists()){
                        String rate = snapshot.child(ids.get(position)).child("rating").getValue().toString();
                        holder.teachersRating.setText(rate);
                        holder.rvTeacherRating.setRating(Float.parseFloat(rate));
                    }
                }
                else {
                    holder.teachersRating.setText("0.0");
                    holder.rvTeacherRating.setRating(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked on: " + imagesNames.get(position));
                //Toast.makeText(context, "" + imagesNames.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, TeacherInfoActivity.class);
                intent.putExtra("tutorId", ids.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //if left 'return 0', you'll get a blank screen when starting the activity containing the rv
        return imagesNames.size(); //this tells the adapter how many items is in your list
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView teachersName;
        TextView teachersRating;
        RatingBar rvTeacherRating;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.recyclerViewCircularImage);
            teachersName = itemView.findViewById(R.id.rvTeacherName);
            teachersRating = itemView.findViewById(R.id.decimalRatingTextView);
            rvTeacherRating = itemView.findViewById(R.id.rvTeacherRating);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }

}
