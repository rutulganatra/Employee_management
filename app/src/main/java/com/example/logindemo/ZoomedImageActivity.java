package com.example.logindemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class ZoomedImageActivity extends AppCompatActivity {


    PhotoView zoomImage;
    String image;
    ImageView closeBtn;
    private final String TAG ="ZoomedImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_image);
        zoomImage = findViewById(R.id.zoomImage);
        closeBtn = findViewById(R.id.closeBtn);
        image = getIntent().getStringExtra("image");
        Glide.with(this).load(image).override(400, 400).into(zoomImage);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked");
                Intent i = new Intent(ZoomedImageActivity.this,ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
