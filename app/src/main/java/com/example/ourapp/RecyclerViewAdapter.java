package com.example.ourapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  implements Filterable {

    //used for debugging - a static logtag with our class name
    private static final String TAG = "RecyclerViewAdapter";
    private static final String DEFAULT_RATING_STRING = "0.0";
    private static final int DEFAULT_RATING_INT = 0;

    //float prevStudentRate;

    private ArrayList<String> imagesNames;
    private ArrayList<String> images;

    //copy of the imagesNames list
    private ArrayList<String> imagesNamesFull;

    private ArrayList<String> ids;

    private Context context;

    public RecyclerViewAdapter(ArrayList<String> imagesNames, ArrayList<String> images, Context context, ArrayList<String> ids) {
        this.imagesNames = imagesNames;
        this.images = images;
        this.context = context;
        this.ids = ids;
        imagesNamesFull= new ArrayList<>(imagesNames);
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

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TutorsRating");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(ids.size() > 0) {
                    if(snapshot.child(ids.get(position)).exists()){
                        String rate = snapshot.child(ids.get(position)).child("rating").getValue().toString();
                        holder.teachersRating.setText(rate);
                        holder.rvTeacherRating.setRating(Float.parseFloat(rate));

                        databaseReference.removeEventListener(this);
                    }
                    else {
                        holder.teachersRating.setText(DEFAULT_RATING_STRING);
                        holder.rvTeacherRating.setRating(DEFAULT_RATING_INT);

                        databaseReference.removeEventListener(this);
                    }
                }
                else {
                    holder.teachersRating.setText(DEFAULT_RATING_STRING);
                    holder.rvTeacherRating.setRating(DEFAULT_RATING_INT);

                    databaseReference.removeEventListener(this);
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


                Intent intent = new Intent(context, TeacherInfoActivity.class);
                intent.putExtra("tutorId", ids.get(position));

                context.startActivity(intent);
            }
        });
    } //  end onBindViewHolder method



    @Override
    public int getItemCount() {
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

    } // end ViewHolder class


    // filter for the search
    @Override
    public Filter getFilter() {
        return namesFilter;
    }


    private Filter namesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(imagesNamesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String item : imagesNamesFull) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }


        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            imagesNames.clear();
            imagesNames.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

}
