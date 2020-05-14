package com.example.logindemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    public Boolean delete = false;
    FirebaseAuth mAuth;
    ImageView logoutBtn;
    FloatingActionButton fab;
    RecyclerView employeeList;
    ArrayList<EmployeeModel> list;
    AdptEmployee adptEmployee;
    Toolbar toolbar;
    static CheckBox deleteChecked;
    static Button deleteBtn;
    static Boolean checked=false;
    ProgressDialog progrss;
    DatabaseReference mDatabase;
    static TextView info;
    MenuItem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Employee");
        progrss.show();
        progrss.setMessage(getResources().getString(R.string.msg_loading));
        dr.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progrss.dismiss();
                if(dataSnapshot.exists()){
                    for (DataSnapshot dss : dataSnapshot.getChildren()){
                        EmployeeModel em = dss.getValue(EmployeeModel.class);
                        list.add(em);
                    }
                    setAdptEmployee();
                }
                if(list.size()==0)
                {
                    info.setVisibility(View.VISIBLE);
                    item.setVisible(false);
                    progrss.dismiss();
                }
                else
                {
                    info.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logoutBtn:
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.fab:
                Intent intent1 = new Intent(ProfileActivity.this, DataActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
        }
    }
    private void init() {
        logoutBtn = findViewById(R.id.logoutBtn);
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        employeeList = findViewById(R.id.employeeslist);
        list = new ArrayList<>();
        deleteChecked = findViewById(R.id.deleteChecked);
        deleteBtn = findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(this);
        progrss = new ProgressDialog(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        mAuth = FirebaseAuth.getInstance();
        logoutBtn.setOnClickListener(this);
        info = findViewById(R.id.tv_info);
        info.setVisibility(View.GONE);
        deleteChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteChecked.isChecked()) {
                    for (int i = 0; i < list.size(); i++)
                        adptEmployee.checkCheckBox(i, true);
                }
                else {
                    adptEmployee.removeSelection();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        item = menu.findItem(R.id.actionDelete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionDelete:
                if(delete)
                {
                    adptEmployee.delete=false;
                    delete=false;
                    deleteBtn.setVisibility(View.GONE);
                    deleteChecked.setVisibility(View.GONE);
                    adptEmployee.notifyDataSetChanged();
                }
                else
                {
                    delete=true;
                    adptEmployee.delete = true;
                    deleteBtn.setVisibility(View.VISIBLE);
                    deleteChecked.setVisibility(View.VISIBLE);
                }
                adptEmployee.notifyDataSetChanged();
                return true;
            default:
                    return super.onOptionsItemSelected(item);
        }

    }
    private void setAdptEmployee(){
        adptEmployee = new AdptEmployee(list,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        employeeList.setLayoutManager(layoutManager);
        employeeList.setAdapter(adptEmployee);
    }
}
