package com.example.logindemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.gesture.GestureLibraries;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.mikhaellopez.circularimageview.CircularImageView;

public class DetalisActivity extends AppCompatActivity {

    CircularImageView profileImage;
    TextView tvname,tvemail,tvDesignation,tvContact,tvAddress,tvGender,tvSalary,tvAbout;
    ImageView backbtn;
    AppBarLayout mAppBar;
    CollapsingToolbarLayout toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalis);
        init();
        Intent i = getIntent();
        final String url = i.getStringExtra("imageUrl");
        String name1 = i.getStringExtra("name");
        String email = i.getStringExtra("email");
        String cont = i.getStringExtra("contact");
        String designation = i.getStringExtra("designation");
        String address = i.getStringExtra("address");
        String gender = i.getStringExtra("gender");
        Double salary = i.getDoubleExtra("salary",0);
        String about = i.getStringExtra("about");
        Glide.with(this).load(url).into(profileImage);
        tvname.setText(name1);
        tvemail.setText(email);
        tvDesignation.setText(designation);
        tvContact.setText(cont);
        tvAddress.setText(address);
        tvGender.setText(gender);
        tvSalary.setText(salary.toString());
        tvAbout.setText(about);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalisActivity.this, ZoomedImageActivity.class);
                intent.putExtra("image", url);
                startActivity(intent);
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalisActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    profileImage.setVisibility(View.GONE);
                }
                else if (isShow) {
                    isShow = false;
                    profileImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void init(){
        tvname = findViewById(R.id.name);
        profileImage = findViewById(R.id.profileImage);
        tvemail = findViewById(R.id.email);
        tvDesignation = findViewById(R.id.designation);
        tvContact = findViewById(R.id.contact);
        tvAddress = findViewById(R.id.address);
        tvGender = findViewById(R.id.gender);
        tvSalary = findViewById(R.id.salary);
        tvAbout = findViewById(R.id.about);
        backbtn = findViewById(R.id.backBtn);
        mAppBar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.collapsingToolbar);
    }
}
