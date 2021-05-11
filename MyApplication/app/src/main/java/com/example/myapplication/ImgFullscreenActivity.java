package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.IOException;

public class ImgFullscreenActivity extends AppCompatActivity {
    private ImageView fullScreenImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_fullscreen);
        fullScreenImg = findViewById(R.id.imgFullscreen);
        Intent callingActivityIntent = getIntent();
        if (callingActivityIntent !=null){
            Uri uri = (Uri) callingActivityIntent.getParcelableExtra("imgUri");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fullScreenImg.setImageBitmap(bitmap);
        }
    }
}