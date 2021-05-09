package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
    // Java UI
    private RecyclerView recyclerViewAllEvents;
//    private EventAdapter eventAdapterAllEvents;
//    private List<Event> allEvents;

    private RecyclerView recyclerViewYourEvents;
//    private EventAdapter eventAdapterYourEvents;
//    private List<Event> yourEvents;

    private RecyclerView recyclerViewInvitation;
//    private EventAdapter eventAdapterInvitation;
//    private List<Event> invitation;

    private ImageView optionToolBar;
    private CircleImageView userImageProfile;
    private TextView userFullName;
    private TextView userBio;
    private TextView userEmail;

    private TextView userAllEvents;
    private TextView userYourEvents;
    private TextView userInvitation;

    private Button editProfile;

    private FirebaseUser firebaseUser;
    private String userProfileID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get profile ID from context to show profile
        String dataTrans = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("userProfileID", "none");

        if (dataTrans.equals("none")){
            userProfileID = firebaseUser.getUid();
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
        userAllEvents = view.findViewById(R.id.profile_All_Events);
        userYourEvents = view.findViewById(R.id.profile_Your_Events);
        userInvitation = view.findViewById(R.id.profile_Invitation);
        editProfile = view.findViewById(R.id.profile_Edit_Button);

        recyclerViewAllEvents = view.findViewById(R.id.profile_Recycler_View_All_Events);
        recyclerViewAllEvents.setHasFixedSize(true);
        recyclerViewAllEvents.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        allEvents = new ArrayList<>();
//        eventAdapterAllEvents = new EventAdapter(getContext(), allEvents);
//        recyclerViewAllEvents.setAdapter(eventAdapterAllEvents);

        recyclerViewYourEvents = view.findViewById(R.id.profile_Recycler_View_Your_Events);
        recyclerViewYourEvents.setHasFixedSize(true);
        recyclerViewYourEvents.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        yourEvents = new ArrayList<>();
//        eventAdapterYourEvents = new EventAdapter(getContext(), yourEvents);
//        recyclerViewYourEvents.setAdapter(eventAdapterYourEvents);

        recyclerViewInvitation = view.findViewById(R.id.profile_Recycler_View_Invitation);
        recyclerViewInvitation.setHasFixedSize(true);
        recyclerViewInvitation.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        invitation = new ArrayList<>();
//        eventAdapterInvitation = new EventAdapter(getContext(), invitation);
//        recyclerViewInvitation.setAdapter(eventAdapterInvitation);

        userInfo();
        getCountAllEvents();
        getAllEvents();
        getCountYourEvents();
        getYourEvents();
        getCountInvitation();
        getInvitation();

        if (userProfileID.equals(firebaseUser.getUid())) {
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
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(userProfileID).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(userProfileID)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(userProfileID).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(userProfileID)
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });

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

//        followers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), FollowersActivity.class);
//                intent.putExtra("id", profileID);
//                intent.putExtra("title", "followers");
//                startActivity(intent);
//            }
//        });

//        following.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), FollowersActivity.class);
//                intent.putExtra("id", profileID);
//                intent.putExtra("title", "followings");
//                startActivity(intent);
//            }
//        });

        return view;
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userProfileID).addValueEventListener(new ValueEventListener() {
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

//    private void getFollowersAndFollowingCount() {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(userProfileID);
//
//        ref.child("followers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userFollowers.setText("" + dataSnapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        ref.child("following").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userFollowing.setText("" + dataSnapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

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

    private void getAllEvents() {

    }

    private void getYourEvents() {

    }

    private void getInvitation() {

    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").addValueEventListener(new ValueEventListener() {
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