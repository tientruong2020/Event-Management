package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventDetailActivity;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    // Define Data Table
    private static final String TBL_EVENTS = "Events";
    private static final String TBL_USERS = "Users";
    // Define Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;
    private DatabaseReference eventsRef;

    private Context mContext;
    private ArrayList<Event> mEvents;

    public EventAdapter (Context mContext, ArrayList<Event> list){
        this.mContext = mContext;
        this.mEvents = list;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child(TBL_USERS);
        eventsRef = FirebaseDatabase.getInstance().getReference().child(TBL_EVENTS);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        String currentUserId;

        // Define Java
        public CircleImageView allEvents_Item_Image;
        public TextView allEvents_Item_Title;
        public TextView allEvents_Item_StartTime;
        public TextView allEvents_Item_EndTime;
        public TextView allEvents_Item_Address;

        public ViewHolder (@NonNull View itemView) {
            super(itemView);

            // Bind XML to JAVA
            allEvents_Item_Image = (CircleImageView) itemView.findViewById(R.id.image_all_events_item);
            allEvents_Item_Title = (TextView) itemView.findViewById(R.id.title_all_events_item);
            allEvents_Item_StartTime = (TextView) itemView.findViewById(R.id.start_time_all_events_item);
            allEvents_Item_EndTime = (TextView) itemView.findViewById(R.id.end_time_all_events_item);
            allEvents_Item_Address = (TextView) itemView.findViewById(R.id.address_all_events_item);

            currentUserId = currentUser.getUid();

            // user click on an event item, redirect to EventDetailActivity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    // Get connect to XML
    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_events_item, parent, false);
        return new ViewHolder(view);
    }

    // Create multiple CardView and setTag for each card
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {

        final Event event = mEvents.get(position);
        holder.itemView.setTag(mEvents.get(position));

        // Set Properties for each card view
        if(event.getImgUri_list().get(0).equalsIgnoreCase("default")) {
            holder.allEvents_Item_Image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get().load(event.getImgUri_list().get(0)).placeholder(R.mipmap.ic_launcher).into(holder.allEvents_Item_Image);
        }
        holder.allEvents_Item_Title.setText(event.getEvent_name());
        holder.allEvents_Item_StartTime.setText(event.getStart_date());
        holder.allEvents_Item_EndTime.setText(event.getEnd_date());
        holder.allEvents_Item_Address.setText(event.getPlace());

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
