package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Me Duc Thinh
 * Modified date: 08/05/2021
 * Description:
 * 1. Create package: Adapter -> UserAdapter.
 * 2. Create package: Model -> User
 * 3. Add action see profile & edit profile to two class: ProfileFragment & EditProfileActivity
 * 4. Add action search user and see friend profile to class: SearchFragment
 * 5. Design the XML: activity_edit_profile, fragment_profile, fragment_search.
 * 6. Add some activity & user_permission to AndroidManifest.xml
 * 7. Add some dependencies to app build.gradle
 */

public class SearchFragment extends Fragment {

    // Define RecyclerView to list user allow search user's fullname action
    private RecyclerView recyclerViewUser;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    // Define RecyclerView to list event allow search event's #hastag action
//    private RecyclerView recyclerViewTags;
//    private List<String> mHashTags;
//    private List<String> mHashTagsCount;
//    private TagAdapter tagAdapter;

    // Define search bar
    private SocialAutoCompleteTextView searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerViewUser = view.findViewById(R.id.recycler_view_users);
        // Optimize performance when scrolling
        recyclerViewUser.setHasFixedSize(true);
        // Attach layout manager to the RecyclerView
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext()));

//        recyclerViewTags = view.findViewById(R.id.recycler_view_tags);
//        // Optimize performance when scrolling
//        recyclerViewTags.setHasFixedSize(true);
//        // Attach layout manager to the RecyclerView
//        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

//        mHashTags = new ArrayList<>();
//        mHashTagsCount = new ArrayList<>();
//        tagAdapter = new TagAdapter(getContext() , mHashTags , mHashTagsCount);
//        recyclerViewTags.setAdapter(tagAdapter);

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext() , mUsers , true);
        recyclerViewUser.setAdapter(userAdapter);

        searchBar = view.findViewById(R.id.search_bar);

        readUsers();
//        readTags();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mUsers.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                filter(s.toString());
            }
        });

        return view;
    }

//    private void readTags() {
//
//        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mHashTags.clear();
//                mHashTagsCount.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    mHashTags.add(snapshot.getKey());
//                    mHashTagsCount.add(snapshot.getChildrenCount() + "");
//                }
//
//                tagAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(searchBar.getText().toString())){
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchUser (String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("userFullName").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void filter (String text) {
//        List<String> mSearchTags = new ArrayList<>();
//        List<String> mSearchTagsCount = new ArrayList<>();
//
//        for (String s : mHashTags) {
//            if (s.toLowerCase().contains(text.toLowerCase())){
//                mSearchTags.add(s);
//                mSearchTagsCount.add(mHashTagsCount.get(mHashTags.indexOf(s)));
//            }
//        }
//
//        tagAdapter.filter(mSearchTags , mSearchTagsCount);
//    }
}