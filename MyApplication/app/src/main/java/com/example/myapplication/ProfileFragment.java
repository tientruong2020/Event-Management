package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Adapter.EventAdapter;
import com.example.myapplication.Adapter.SliderAdapter;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Author: Me Duc Thinh
 * Modified date: 09/05/2021
 * Description:
 * 1. Add drawable xml for profile/edit_profile/setting screen.
 * 2. Format ID XML and replace in JAVA code
 * 3. Rework with Google Sign in method
 *
 */

public class ProfileFragment extends Fragment {

    // Define Data Table
    private static final String TBL_EVENTS = "Events";
    private static final String TBL_USERS = "Users";
    private static final String TBL_JOINED_EVENTS = "JoinedEvents";
    // Define Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference eventsRef;
    private DatabaseReference usersRef;
    private DatabaseReference joinedEventsRef;

    // Define JAVA UI
    private View view;

    private RecyclerView recyclerViewAllEvents;
    private EventAdapter eventAdapterAllEvents;
    private ArrayList<Event> allEvents;

    private RecyclerView recyclerViewYourEvents;
    private EventAdapter eventAdapterYourEvents;
    private ArrayList<Event> yourEvents;

    private RecyclerView recyclerViewInvitation;
    private EventAdapter eventAdapterInvitation;
    private ArrayList<Event> invitation;

    private ImageView optionToolBar;
    private CircleImageView userImageProfile;
    private TextView userFullName;
    private TextView userBio;
    private TextView userEmail;

    private TextView userAllEvents;
    private TextView userYourEvents;
    private TextView userInvitation;

    private Button editProfile;

    private String userProfileID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        eventsRef = FirebaseDatabase.getInstance().getReference().child(TBL_EVENTS);
        usersRef = FirebaseDatabase.getInstance().getReference().child(TBL_USERS);
        joinedEventsRef = FirebaseDatabase.getInstance().getReference().child(TBL_JOINED_EVENTS);

        // Get profile ID from context to show profile
        String dataTrans = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("userProfileID", "none");

        if (dataTrans.equals("none")){
            userProfileID = currentUser.getUid();
        } else {
            userProfileID = dataTrans;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        // Bind Java to XML
        optionToolBar = view.findViewById(R.id.profile_Options);
        userImageProfile = view.findViewById(R.id.profile_User_Image);
        userFullName = view.findViewById(R.id.profile_User_Full_Name);
        userBio = view.findViewById(R.id.profile_User_Bio);
        userEmail = view.findViewById(R.id.profile_User_Email);
        editProfile = view.findViewById(R.id.profile_Edit_Button);

        userInfo();

        if (userProfileID.equals(currentUser.getUid())) {
            editProfile.setText("Edit profile");
        } else {
            checkFollowingStatus();
        }

        optionToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OptionActivity.class));
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();

                if (btnText.equals("Edit profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {
                    if (btnText.equals("follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid())
                                .child("following").child(userProfileID).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(userProfileID)
                                .child("followers").child(currentUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid())
                                .child("following").child(userProfileID).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(userProfileID)
                                .child("followers").child(currentUser.getUid()).removeValue();
                    }
                }
            }
        });

        // Bind Java to XML
        userAllEvents = view.findViewById(R.id.profile_All_Events);
        userYourEvents = view.findViewById(R.id.profile_Your_Events);
        userInvitation = view.findViewById(R.id.profile_Invitation);

        // Bind Java to XML + define recycler view for ALL_EVENTS
        recyclerViewAllEvents = view.findViewById(R.id.profile_Recycler_View_All_Events);
        recyclerViewAllEvents.setHasFixedSize(true);
        recyclerViewAllEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        allEvents = new ArrayList<Event>();
        eventAdapterAllEvents = new EventAdapter(getContext(), allEvents);
        recyclerViewAllEvents.setAdapter(eventAdapterAllEvents);

        // Bind Java to XML + define recycler view for YOUR_EVENTS
        recyclerViewYourEvents = view.findViewById(R.id.profile_Recycler_View_Your_Events);
        recyclerViewYourEvents.setHasFixedSize(true);
        recyclerViewYourEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        yourEvents = new ArrayList<Event>();
        eventAdapterYourEvents = new EventAdapter(getContext(), yourEvents);
        recyclerViewYourEvents.setAdapter(eventAdapterYourEvents);

        // Bind Java to XML + define recycler view for INVITATION
        recyclerViewInvitation = view.findViewById(R.id.profile_Recycler_View_Invitation);
        recyclerViewInvitation.setHasFixedSize(true);
        recyclerViewInvitation.setLayoutManager(new LinearLayoutManager(getContext()));
        invitation = new ArrayList<Event>();
        eventAdapterInvitation = new EventAdapter(getContext(), invitation);
        recyclerViewInvitation.setAdapter(eventAdapterInvitation);

        recyclerViewAllEvents.setVisibility(View.VISIBLE);
        recyclerViewYourEvents.setVisibility(View.GONE);
        recyclerViewInvitation.setVisibility(View.GONE);

        userAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAllEvents.setVisibility(View.VISIBLE);
                recyclerViewYourEvents.setVisibility(View.GONE);
                recyclerViewInvitation.setVisibility(View.GONE);
            }
        });

        userYourEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAllEvents.setVisibility(View.GONE);
                recyclerViewYourEvents.setVisibility(View.VISIBLE);
                recyclerViewInvitation.setVisibility(View.GONE);

            }
        });

        userInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewAllEvents.setVisibility(View.GONE);
                recyclerViewYourEvents.setVisibility(View.GONE);
                recyclerViewInvitation.setVisibility(View.VISIBLE);
            }
        });

        // getCountAllEvents();
        getAllEvents();
        // getCountYourEvents();
        getYourEvents();
        // getCountInvitation();
        getInvitation();

        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//        eventsRef = FirebaseDatabase.getInstance().getReference().child(TBL_EVENTS);
//        usersRef = FirebaseDatabase.getInstance().getReference().child(TBL_USERS);
//
//        // Get profile ID from context to show profile
//        String dataTrans = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("userProfileID", "none");
//
//        if (dataTrans.equals("none")){
//            userProfileID = currentUser.getUid();
//        } else {
//            userProfileID = dataTrans;
//            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
//        }
//
//        // Bind Java to XML
//        optionToolBar = view.findViewById(R.id.profile_Options);
//        userImageProfile = view.findViewById(R.id.profile_User_Image);
//        userFullName = view.findViewById(R.id.profile_User_Full_Name);
//        userBio = view.findViewById(R.id.profile_User_Bio);
//        userEmail = view.findViewById(R.id.profile_User_Email);
//        editProfile = view.findViewById(R.id.profile_Edit_Button);
//
//        userInfo();
//
//        if (userProfileID.equals(currentUser.getUid())) {
//            editProfile.setText("Edit profile");
//        } else {
//            checkFollowingStatus();
//        }
//
//        optionToolBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getContext(), OptionActivity.class));
//            }
//        });
//
//        editProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String btnText = editProfile.getText().toString();
//
//                if (btnText.equals("Edit profile")) {
//                    startActivity(new Intent(getContext(), EditProfileActivity.class));
//                } else {
//                    if (btnText.equals("follow")) {
//                        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid())
//                                .child("following").child(userProfileID).setValue(true);
//
//                        FirebaseDatabase.getInstance().getReference().child("Follow").child(userProfileID)
//                                .child("followers").child(currentUser.getUid()).setValue(true);
//                    } else {
//                        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid())
//                                .child("following").child(userProfileID).removeValue();
//
//                        FirebaseDatabase.getInstance().getReference().child("Follow").child(userProfileID)
//                                .child("followers").child(currentUser.getUid()).removeValue();
//                    }
//                }
//            }
//        });
//
//        // Bind Java to XML
//        userAllEvents = view.findViewById(R.id.profile_All_Events);
//        userYourEvents = view.findViewById(R.id.profile_Your_Events);
//        userInvitation = view.findViewById(R.id.profile_Invitation);
//
//        // Bind Java to XML + define recycler view for ALL_EVENTS
//        recyclerViewAllEvents = view.findViewById(R.id.profile_Recycler_View_All_Events);
//        recyclerViewAllEvents.setHasFixedSize(true);
//        recyclerViewAllEvents.setLayoutManager(new LinearLayoutManager(getContext()));
//        allEvents = new ArrayList<Event>();
//        eventAdapterAllEvents = new EventAdapter(getContext(), allEvents);
//        recyclerViewAllEvents.setAdapter(eventAdapterAllEvents);
//
//        // Bind Java to XML + define recycler view for YOUR_EVENTS
//        recyclerViewYourEvents = view.findViewById(R.id.profile_Recycler_View_Your_Events);
//        recyclerViewYourEvents.setHasFixedSize(true);
//        recyclerViewYourEvents.setLayoutManager(new LinearLayoutManager(getContext()));
//        yourEvents = new ArrayList<Event>();
//        eventAdapterYourEvents = new EventAdapter(getContext(), yourEvents);
//        recyclerViewYourEvents.setAdapter(eventAdapterYourEvents);
//
//        // Bind Java to XML + define recycler view for INVITATION
//        recyclerViewInvitation = view.findViewById(R.id.profile_Recycler_View_Invitation);
//        recyclerViewInvitation.setHasFixedSize(true);
//        recyclerViewInvitation.setLayoutManager(new LinearLayoutManager(getContext()));
//        invitation = new ArrayList<Event>();
//        eventAdapterInvitation = new EventAdapter(getContext(), invitation);
//        recyclerViewInvitation.setAdapter(eventAdapterInvitation);
//
//        recyclerViewAllEvents.setVisibility(View.VISIBLE);
//        recyclerViewYourEvents.setVisibility(View.GONE);
//        recyclerViewInvitation.setVisibility(View.GONE);
//
//        userAllEvents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerViewAllEvents.setVisibility(View.VISIBLE);
//                recyclerViewYourEvents.setVisibility(View.GONE);
//                recyclerViewInvitation.setVisibility(View.GONE);
//            }
//        });
//
//        userYourEvents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerViewAllEvents.setVisibility(View.GONE);
//                recyclerViewYourEvents.setVisibility(View.VISIBLE);
//                recyclerViewInvitation.setVisibility(View.GONE);
//
//            }
//        });
//
//        userInvitation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerViewAllEvents.setVisibility(View.GONE);
//                recyclerViewYourEvents.setVisibility(View.GONE);
//                recyclerViewInvitation.setVisibility(View.VISIBLE);
//            }
//        });
//
//        //        getCountAllEvents();
//        getAllEvents();
//        //        getCountYourEvents();
//        getYourEvents();
//        //        getCountInvitation();
//        getInvitation();
//    }

    private void userInfo() {
        usersRef.child(userProfileID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Picasso.get().load(user.getUserImageUrl()).into(userImageProfile);
                userFullName.setText(user.getUserFullName());
                userEmail.setText(user.getUserEmail());
                userBio.setText(user.getUserBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Get count to show
    private void getCountAllEvents() {
//        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int counter = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Event event = snapshot.getValue(Event.class);
//
//                    if (event.getEventPublisher().equals(userProfileID)) counter ++;
//                }
//
//                userPosts.setText(String.valueOf(counter));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getCountYourEvents() {
//        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int counter = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Event event = snapshot.getValue(Event.class);
//
//                    if (event.getEventPublisher().equals(userProfileID)) counter ++;
//                }
//
//                userPosts.setText(String.valueOf(counter));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void getCountInvitation() {
//        FirebaseDatabase.getInstance().getReference().child("Invitation").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int counter = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Event event = snapshot.getValue(Event.class);
//
//                    if (event.getEventPublisher().equals(userProfileID)) counter ++;
//                }
//
//                userPosts.setText(String.valueOf(counter));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    // Get info => setAdapter to show in recycler view
    private void getAllEvents() {

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allEvents.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event eachEvent = snapshot.getValue(Event.class);
                    allEvents.add(eachEvent);
                }

                eventAdapterAllEvents.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getYourEvents() {

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                yourEvents.clear();

                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                    Event eachEvent = snapshot1.getValue(Event.class);
                        yourEvents.add(eachEvent);
                }

                eventAdapterYourEvents.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getInvitation() {

    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(currentUser.getUid()).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userProfileID).exists()) {
                    editProfile.setText("Following");
                } else {
                    editProfile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}