package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Adapter.SliderAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import com.example.myapplication.Model.Event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Author: Le Anh Tuan
 * Modified Date: 17/5/2021
 * Description: Display all events
 */
public class HomeFragment extends Fragment {

    private static final String TBL_EVENTS = "Events";

    // get the recycler view
    private RecyclerView rvAllEvents;
    private View view;

    // reference to TBL_EVENTS
    private DatabaseReference eventsRef;

    public HomeFragment() {
        // required empty constructor
    }

    /**
     * Inflate the corresponding layout.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    /**
     * Display all events.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eventsRef = FirebaseDatabase.getInstance().getReference().child(TBL_EVENTS);

        // handles recycler view initialization
        rvAllEvents = view.findViewById(R.id.rvAllEvents);
        rvAllEvents.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvAllEvents.setLayoutManager(linearLayoutManager);

        displayAllEvents();
    }

    private void displayAllEvents() {
        Query sortEventsInDescendingOrder = eventsRef.orderByChild("createdAt"); // newest first

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(sortEventsInDescendingOrder, Event.class)
                .build();

        FirebaseRecyclerAdapter<Event, EventsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Event, EventsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull EventsViewHolder holder,
                                                    int position, @NonNull @NotNull Event model) {

                        String uid = model.getUid();
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                String userProfileImage = snapshot.child("userImageUrl").getValue().toString();
                                String userBio = snapshot.child("userBio").getValue().toString();
                                String userFullName = snapshot.child("userFullName").getValue().toString();

                                Picasso.get().load(userProfileImage).into(holder.civUserProfileImage);
                                holder.txtUserFullName.setText(userFullName);
                                holder.txtUserBio.setText(userBio);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                        });

                        final String eventIds = getRef(position).getKey();

                        eventsRef.child(eventIds).child("ImgUri_list").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                ArrayList<String> allImagesUri = (ArrayList<String>) snapshot.getValue();
                                holder.sliderView.setSliderAdapter(new SliderAdapter(allImagesUri));
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                        });

                        holder.txtEventName.setText(model.getEvent_name());

                        String[] splitStartString = model.getStart_date().split(" ");
                        String[] splitEndString = model.getEnd_date().split(" ");

                        String startDate = splitStartString[1] + " " + getResources().getString(R.string.date) + " " + splitStartString[0];
                        String endDate = splitEndString[1] + " "  + getResources().getString(R.string.date) + " " + splitEndString[0];

                        holder.txtEventStartDate.setText(startDate);
                        holder.txtEventEndDate.setText(endDate);
                        holder.txtEventPlace.setText(model.getPlace());
                    }

                    @NonNull
                    @Override
                    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_event_layout, parent, false);
                        EventsViewHolder viewHolder = new EventsViewHolder(view);
                        return viewHolder;
                    }
                };
        rvAllEvents.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    protected static class EventsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civUserProfileImage;
        private TextView txtUserFullName, txtUserBio, txtEventName, txtEventStartDate, txtEventEndDate, txtEventPlace;
        private ImageView ivDropLike, ivJoinEvent, ivEventLocation;
        private SliderView sliderView;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            civUserProfileImage = itemView.findViewById(R.id.civUserProfileImage);
            txtUserFullName = itemView.findViewById(R.id.txtUserFullName);
            txtUserBio = itemView.findViewById(R.id.txtUserBio);
            txtEventName = itemView.findViewById(R.id.txtEventName);
            txtEventStartDate = itemView.findViewById(R.id.txtEventStartDate);
            txtEventEndDate = itemView.findViewById(R.id.txtEventEndDate);
            txtEventPlace = itemView.findViewById(R.id.txtEventPlace);
            ivDropLike = itemView.findViewById(R.id.ivDropLike);
            ivJoinEvent = itemView.findViewById(R.id.ivJoinEvent);
            ivEventLocation = itemView.findViewById(R.id.ivEventLocation);
            sliderView = itemView.findViewById(R.id.imageSlider);
        }
    }
}