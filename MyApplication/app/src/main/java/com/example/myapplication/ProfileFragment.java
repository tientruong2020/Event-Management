package com.example.myapplication;

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

import com.example.myapplication.Adapter.PhotoAdapter;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Event> myPhotoList;

    private RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdapterSaves;
    private List<Event> mySavedPosts;

    private ImageView optionToolBar;
    private CircleImageView userImageProfile;
//    private TextView userPosts;
//    private TextView userFollowers;
//    private TextView userFollowing;
    private TextView userFullName;
    private TextView userBio;
    private TextView userEmail;

    private ImageView userMyPictures;
    private ImageView userSavedPictures;

    private Button editProfile;

    private FirebaseUser firebaseUser;
    private String userProfileID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get profile ID from context to show profile
//        String dataTrans = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileID", "none");
//
//        if (dataTrans.equals("none")){
//            profileID = fFirebaseUser.getUid();
//        } else {
//            profileID = dataTrans;
//        }

        userProfileID = firebaseUser.getUid();

        optionToolBar = view.findViewById(R.id.profile_Options);
        userImageProfile = view.findViewById(R.id.profile_User_Image);
//        userFollowers = view.findViewById(R.id.followers);
//        userFollowing = view.findViewById(R.id.following);
//        userPosts = view.findViewById(R.id.posts);
        userFullName = view.findViewById(R.id.profile_User_Full_Name);
        userBio = view.findViewById(R.id.profile_User_Bio);
        userEmail = view.findViewById(R.id.profile_User_Email);
        userMyPictures = view.findViewById(R.id.profile_My_Pictures);
        userSavedPictures = view.findViewById(R.id.profile_Saved_Pictures);
        editProfile = view.findViewById(R.id.profile_Edit_Button);

        recyclerView = view.findViewById(R.id.profile_Recycler_View_Pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewSaves = view.findViewById(R.id.profile_Recycler_View_Saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPosts = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(), mySavedPosts);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        userInfo();
//        getFollowersAndFollowingCount();
//        getPostCount();
        myPhotos();
        getSavedPosts();

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

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        userMyPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        userSavedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
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

//    private void getPostCount() {
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
//    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPhotoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);

                    if (event.getEventPublisher().equals(userProfileID)) {
                        myPhotoList.add(event);
                    }
                }

                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSavedPosts() {
        final List<String> savedIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    savedIds.add(snapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        mySavedPosts.clear();

                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                            Event event = snapshot1.getValue(Event.class);
                            for (String id : savedIds) {
                                if (event.getEventID().equals(id)) {
                                    mySavedPosts.add(event);
                                }
                            }
                        }
                        postAdapterSaves.notifyDataSetChanged();
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

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userProfileID).exists()) {
                    editProfile.setText("following");
                } else {
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}