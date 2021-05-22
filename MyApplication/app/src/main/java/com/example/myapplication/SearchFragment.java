package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.Model.Event;
import com.example.myapplication.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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

    // reference to Events node in Database (Tuan)
    private static final String NODE_EVENTS = "Events";
    private DatabaseReference eventsRef;

    // Define RecyclerView to list user allow search user's full name action
    private RecyclerView recyclerViewUser;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    // search event results recycler view (Tuan)
    private RecyclerView rvSearchEventResults;

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

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext() , mUsers , true);
        recyclerViewUser.setAdapter(userAdapter);

        searchBar = view.findViewById(R.id.search_bar);

        // reference to the Events node in Database (Tuan)
        eventsRef = FirebaseDatabase.getInstance().getReference().child(NODE_EVENTS);

        // binding the Recycler View (Tuan)
        rvSearchEventResults = view.findViewById(R.id.rvSearchEventResults);

        // set some properties for search event Recycler (Tuan)
        rvSearchEventResults.setHasFixedSize(true);
        rvSearchEventResults.setLayoutManager(new LinearLayoutManager(getContext()));

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
                searchEvents(s.toString()); // Tuan
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

    /** Modified by Tuan
     * Search event based on event name
     * @param eventName the passed in event name
     */
    private void searchEvents(String eventName) {
        Query searchEventQuery = eventsRef.orderByChild("event_name").startAt(eventName).endAt(eventName + "\uf8ff");

        FirebaseRecyclerOptions<Event> options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(searchEventQuery, Event.class)
                .build();

        FirebaseRecyclerAdapter<Event, FindEventHolder> firebaseAdapter =
                new FirebaseRecyclerAdapter<Event, FindEventHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SearchFragment.FindEventHolder holder, int position, @NonNull Event model) {

                        final String clickedEventId = getRef(position).getKey();

                        holder.txtSearchEventName.setText(model.getEvent_name());
                        holder.txtSearchEventDescription.setText(model.getDescription());

                        ArrayList<String> allImagesUri = model.getImgUri_list();
                        Picasso.get().load(allImagesUri.get(0)).into(holder.civSearchEventImage);

                        holder.itemView.setOnClickListener(v -> {
                            // changing the activity and send the user ID along with the intent
                            Intent clickPostIntent = new Intent(getContext(), ClickEventActivity.class);
                            clickPostIntent.putExtra("EventKey", clickedEventId);
                            startActivity(clickPostIntent);
                        });

                    }

                    @NonNull
                    @Override
                    public FindEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_search_event_result_layout, parent, false);
                        SearchFragment.FindEventHolder viewHolder = new SearchFragment.FindEventHolder(view);
                        return viewHolder;
                    }
                };
        rvSearchEventResults.setAdapter(firebaseAdapter);
        firebaseAdapter.startListening();
    }

    /**
     * Modified by Tuan
     */
    protected static class FindEventHolder extends RecyclerView.ViewHolder {
        private TextView txtSearchEventName, txtSearchEventDescription;
        private CircleImageView civSearchEventImage;

        public FindEventHolder(@NonNull View itemView) {
            super(itemView);

            txtSearchEventName = itemView.findViewById(R.id.txtSearchEventName);
            txtSearchEventDescription = itemView.findViewById(R.id.txtSearchEventDescription);
            civSearchEventImage = itemView.findViewById(R.id.civSearchEventImage);
        }
    }
}