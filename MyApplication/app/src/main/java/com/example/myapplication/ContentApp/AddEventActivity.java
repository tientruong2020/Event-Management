package com.example.myapplication.ContentApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Author: Le Anh Tuan
 * Modified date: 08/05/2021
 * Description:
 * 1. Refactor this class Name.
 * 2. Design the XML.
 */
public class AddEventActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final String TBL_USERS = "Users";
    private static final String TBL_EVENTS = "Events";
    private static final String EVENT_IMAGES_FOLDER = "Event Images";

    // Java UI
    private Button backHomeBtn, btnAddEvent;
    private EditText edtAddEventName, edtAddEventDescription, edtAddEventPlace,
            edtAddEventStartDate, edtAddEventEndDate, edtAddEventCategory, edtAddEventType,
            edtAddEventLimit;
    private ImageView ivAddEventFirstImage, ivAddEventCategoryHelp, ivAddEventTypeHelp;
    private CheckBox cbAddEventLimit;

    // Alert Dialog Builder
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog informUser;

    private Uri imageUri;
    private String downloadUrl; // url to bind to Firebase realtime db

    private String eventName, eventDescription, eventPlace, eventStartDate, eventEndDate,
            eventCategory, eventType, eventLimit; // store the event info

    private DatabaseReference usersRef;             // get tbl_Users
    private DatabaseReference eventsRef;             // get tbl_Events
    private FirebaseAuth mAuth;                     // get current user
    private StorageReference postImagesReference;   // store the image in to Firebase Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // initialize Firebase objects
        mAuth = FirebaseAuth.getInstance();
        postImagesReference = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child(TBL_USERS);
        eventsRef = FirebaseDatabase.getInstance().getReference().child(TBL_EVENTS);

        // bind Java to XML
        backHomeBtn = findViewById(R.id.backHomeBtn);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        edtAddEventName = findViewById(R.id.edtAddEventName);
        edtAddEventDescription = findViewById(R.id.edtAddEventDescription);
        edtAddEventPlace = findViewById(R.id.edtAddEventPlace);
        edtAddEventStartDate = findViewById(R.id.edtAddEventStartDate);
        edtAddEventEndDate = findViewById(R.id.edtAddEventEndDate);
        edtAddEventCategory = findViewById(R.id.edtAddEventCategory);
        edtAddEventType = findViewById(R.id.edtAddEventType);
        edtAddEventLimit = findViewById(R.id.edtAddEventLimit);
        ivAddEventFirstImage = findViewById(R.id.ivAddEventFirstImage);
        ivAddEventCategoryHelp = findViewById(R.id.ivAddEventCategoryHelp);
        ivAddEventTypeHelp = findViewById(R.id.ivAddEventTypeHelp);
        cbAddEventLimit = findViewById(R.id.cbAddEventLimit);

        // disable edtAddEventLimit
        edtAddEventLimit.setClickable(false);

        // initialize Alert Dialog Builder
        alertDialogBuilder = new AlertDialog.Builder(AddEventActivity.this);

        helpUser();

        // disable edtAddEventLimit based on cbAddEventLimit status
        cbAddEventLimit.setOnClickListener(v ->
                edtAddEventLimit.setClickable(cbAddEventLimit.isChecked()));

        // user choose an Image
        ivAddEventFirstImage.setOnClickListener(v -> {
            // go to the gallery to let user choose an image
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        });

        btnAddEvent.setOnClickListener(v -> {
            if (cbAddEventLimit.isChecked() && isFieldFilled(edtAddEventLimit)) {
                eventLimit = edtAddEventLimit.getText().toString();
            } else if (cbAddEventLimit.isChecked() && !isFieldFilled(edtAddEventLimit)) {
                return;
            } else if (!cbAddEventLimit.isChecked()) {
                eventLimit = "no limit";
            }

            if (isFieldFilled(edtAddEventName) && isFieldFilled(edtAddEventDescription)
                    && isFieldFilled(edtAddEventPlace) && isFieldFilled(edtAddEventStartDate)
                    && isFieldFilled(edtAddEventEndDate) && isFieldFilled(edtAddEventCategory)
                    && isFieldFilled(edtAddEventType)) {
                eventName = edtAddEventName.getText().toString();
                eventDescription = edtAddEventDescription.getText().toString();
                eventPlace = edtAddEventPlace.getText().toString();
                eventStartDate = edtAddEventStartDate.getText().toString();
                eventEndDate = edtAddEventEndDate.getText().toString();
                eventCategory = edtAddEventCategory.getText().toString();
                eventType = edtAddEventType.getText().toString();

                if (imageUri == null) {
                    Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
                } else {
                    storeImageToFirebaseStorage();
                }
            }
        });

        backHomeBtn.setOnClickListener(view -> backHome());
    }

    /**
     * Redirect user to Home Activity
     */
    private void backHome() {
        Intent intent = new Intent(this, BottomNavbarActivity.class);
        startActivity(intent);
    }

    /**
     * Display Alert Dialog to help user when user click on help icon.
     */
    private void helpUser() {
        ivAddEventCategoryHelp.setOnClickListener(v -> {
            alertDialogBuilder.setMessage("This is the event category");
            informUser = alertDialogBuilder.create();
            informUser.show();
        });

        ivAddEventTypeHelp.setOnClickListener(v -> {
            alertDialogBuilder.setMessage("This is the event type");
            informUser = alertDialogBuilder.create();
            informUser.show();
        });
    }

    /**
     * Check if EditText field has been filled or not.
     * @param editText the checking EditText.
     * @return false if empty. Otherwise, true.
     */
    private boolean isFieldFilled(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Required field");
            return false;
        }
        return true;
    }

    /**
     * Display the chosen image
     * @param requestCode request code for going to Gallery
     * @param resultCode return resultCode from Gallery
     * @param data the chosen image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ivAddEventFirstImage.setImageURI(imageUri);
        }
    }

    /**
     * Upload the post image to FirebaseStorage with the format IMG_yyyyMMdd_HHmm.
     */
    @SuppressLint("SimpleDateFormat")
    private void storeImageToFirebaseStorage() {
        SimpleDateFormat sdfImageDate = new SimpleDateFormat("yyyyMMdd_HHmm");
        Date now = new Date();
        String strImageDate = sdfImageDate.format(now);

        String postedImageName = "IMG_" + strImageDate + ".jpg"; // IMG_20210506_2309.jpg

        StorageReference filePath = postImagesReference
                .child(EVENT_IMAGES_FOLDER)
                .child(mAuth.getCurrentUser().getUid())
                .child(imageUri.getLastPathSegment() + "_" + postedImageName);

        filePath.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Toast.makeText(this, "Image uploaded successfully to Storage", Toast.LENGTH_SHORT).show();

                filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = uri.toString();

                    saveEventInformationIntoDatabase();
                });

            } else {
                String message = task.getException().getMessage();
                Toast.makeText(this, "Cannot upload image to Storage: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Save the event information into the Firebase Realtime database
     */
    private void saveEventInformationIntoDatabase() {
        usersRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // get user full name and profile image
                    //TODO: kiểm tra lại lần nữa: tên các key trong database
                    String userFullName = snapshot.child("fullname").getValue().toString();
                    String userProfileImage = snapshot.child("profile_image").getValue().toString();
                    // TODO: lấy slogan của công ty nữa.

                    // save post information into database
                    HashMap<String, Object> postMap = new HashMap<>();
                    postMap.put("uid", mAuth.getCurrentUser().getUid());
                    postMap.put("userProfileImage", userProfileImage);
                    postMap.put("userFullname", userFullName);
                    postMap.put("postDate", getCurrentDate());
                    postMap.put("postTime", getCurrentTime());
                    postMap.put("eventName", eventName);
                    postMap.put("eventDescription", eventDescription);
                    postMap.put("eventPlace", eventPlace);
                    postMap.put("eventStartDate", eventStartDate);
                    postMap.put("eventEndDate", eventEndDate);
                    postMap.put("eventImage", downloadUrl);
                    postMap.put("eventCategory", eventCategory);
                    postMap.put("eventType", eventType);
                    postMap.put("eventLimit", eventLimit);


                    String postName = mAuth.getCurrentUser().getUid() + "_" + getCurrentDate() + "_" + getCurrentTime();

                    eventsRef.child(postName)
                            .updateChildren(postMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(AddEventActivity.this, BottomNavbarActivity.class));
                            finish();

                            Toast.makeText(AddEventActivity.this, "New post is updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(AddEventActivity.this, "Error update post: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * get the current date in the format: yyyy-MM-dd
     * @return the current date in String
     */
    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        return sdfDate.format(now);
    }

    /**
     * get the current in the format: HH:mm:ss
     * @return the current time in String
     */
    @SuppressLint("SimpleDateFormat")
    private String getCurrentTime() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        return sdfTime.format(now);
    }
}