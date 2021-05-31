package com.example.myapplication.ContentApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.Comment;
import com.example.myapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    private static final String TBL_COMMENT = "Comments";
    private static final String TBL_USERS = "Users";
    private static final String UID = FirebaseAuth.getInstance().getUid();
    private String EventKey;
    private MaterialEditText edCommentContent;
    private Button btnBackToHome, btnComment;
    private RecyclerView rcvComment;
    private DatabaseReference mComment;
    private DatabaseReference mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commemt);
        Intent parentIntent = getIntent();
        EventKey = parentIntent.getStringExtra("EventId");
        edCommentContent = findViewById(R.id.etCommentContent);
        btnBackToHome = findViewById(R.id.btnCommentToHome);
        btnComment = findViewById(R.id.btnComment);
        rcvComment = findViewById(R.id.rcvComment);
        mComment = FirebaseDatabase.getInstance().getReference(TBL_COMMENT);
        mUser = FirebaseDatabase.getInstance().getReference(TBL_USERS);
        rcvComment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvComment.setLayoutManager(linearLayoutManager);
        getCommentOfEvent();


        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = edCommentContent.getText().toString();
                if(TextUtils.isEmpty(commentContent)){
                    Toast.makeText(CommentActivity.this,"Comment is Empty!!!",Toast.LENGTH_SHORT).show();
                }else {
                    Date date = new Date();
                    long timestamp = new Timestamp(date.getTime()).getTime();
                    String key = mComment.child(EventKey).push().getKey();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("UID",UID);
                    hashMap.put("Content",commentContent);
                    hashMap.put("createdAt",timestamp);
                    mComment.child(EventKey).child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                edCommentContent.setText("");
                                Toast.makeText(CommentActivity.this,"You Added a Comment for this Event!!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommentActivity.this, BottomNavbarActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getCommentOfEvent(){
        Query query = mComment.child(EventKey).orderByChild("createdAt");
        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(query,Comment.class)
                .build();
        FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                String uid = model.getUID();
                DatabaseReference usersRef = mUser.child(UID);
                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userProfileImage = snapshot.child("userImageUrl").getValue().toString();
                        String userFullName = snapshot.child("userFullName").getValue().toString();

                        Picasso.get().load(userProfileImage).into(holder.circleImageView);
                        holder.txtUserFullName.setText(userFullName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                String commentContent = model.getContent();
                long timestamp = model.getCreatedAt();
                Timestamp ts= new Timestamp(timestamp);
                Date date = new Date(ts.getTime());

                holder.txtCommentContent.setText(commentContent);
                holder.txtCommentTime.setText(date.toString());
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_comment_layout,parent,false);
                CommentViewHolder viewHolder = new CommentViewHolder(view);
                return  viewHolder;
            }
        };
        rcvComment.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    private static class CommentViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView txtUserFullName,txtCommentContent, txtCommentTime;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.civComentUserImage);
            txtUserFullName = itemView.findViewById(R.id.txtCommentedUserFullName);
            txtCommentContent = itemView.findViewById(R.id.txtCommentedContent);
            txtCommentTime = itemView.findViewById(R.id.txtCommentedTime);
        }
    }
}