package com.example.logindemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.util.ArrayList;

public class AdptEmployee extends RecyclerView.Adapter<AdptEmployee.ViewHolder>{

    static ArrayList<EmployeeModel> list;
    Context context;
    private static String TAG ="AdptEmployee";
    static ArrayList<String> name = new ArrayList<>();
    static boolean delete = false;
    private SparseBooleanArray mSelectedItemsIds;
    DatabaseReference mDatabase;
    public AdptEmployee(ArrayList<EmployeeModel> list, Context context) {
        this.list = list;
        this.context = context;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_employee_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final EmployeeModel employeeModel = list.get(position);
        Log.d(TAG, "onBindViewHolder: "+position);
        holder.name.setText(employeeModel.getName());
        if(delete){
            holder.deleteCheckBox.setVisibility(View.VISIBLE);
        }
        else{
            holder.deleteCheckBox.setVisibility(View.GONE);
        }
        Glide.with(context).load(employeeModel.getUrl()).into(holder.profileImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetalisActivity.class);
                i.putExtra("imageUrl",employeeModel.getUrl());
                i.putExtra("name",employeeModel.getName());
                i.putExtra("email",employeeModel.getEmail());
                i.putExtra("contact",employeeModel.getContact());
                i.putExtra("designation",employeeModel.getDesignation());
                i.putExtra("address",employeeModel.getAddress());
                i.putExtra("gender",employeeModel.getGender());
                i.putExtra("salary",employeeModel.getSalary());
                i.putExtra("about",employeeModel.getDescription());
                context.startActivity(i);
            }
        });
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = list.get(holder.getAdapterPosition()).getUrl();
                Intent intent = new Intent(context, ZoomedImageActivity.class);
                intent.putExtra("image", imageUrl);
                context.startActivity(intent);
            }
        });
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UpdateActivity.class);
                i.putExtra("id",employeeModel.getId());
                i.putExtra("imageUrl",employeeModel.getUrl());
                i.putExtra("name",employeeModel.getName());
                i.putExtra("email",employeeModel.getEmail());
                i.putExtra("contact",employeeModel.getContact());
                i.putExtra("designation",employeeModel.getDesignation());
                i.putExtra("address",employeeModel.getAddress());
                i.putExtra("gender",employeeModel.getGender());
                i.putExtra("salary",employeeModel.getSalary());
                i.putExtra("about",employeeModel.getDescription());
                context.startActivity(i);
            }
        });
        holder.deleteCheckBox.setChecked(mSelectedItemsIds.get(position));
        holder.deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    name.add(list.get(position).getId());
                    Log.d(TAG, "onCheckedChanged: 1:-"+name.size()+" "+name);
                   // checkCheckBox(position, !mSelectedItemsIds.get(position));
                    if(name.size()==list.size()){
                        ProfileActivity.deleteChecked.setChecked(true);
                    }
                    Log.d(TAG, "onCheckedChanged: 2:-"+name.size()+" "+name);
                }
                else {
                    mSelectedItemsIds.put(position,false);
                    Log.d(TAG, "onCheckedChanged: 3:-"+name.size()+" "+name);
                    if(name.contains(list.get(position).getId())){
                        name.remove(list.get(position).getId());
                        Log.d(TAG, "onCheckedChanged: 4:-"+name.size()+" "+name);
                        ProfileActivity.deleteChecked.setChecked(false);
                    }
                    Log.d(TAG, "onCheckedChanged: 5:-"+name.size()+" "+name);
                }
            }
        });
        ProfileActivity.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < name.size(); i++) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("Employee");
                    mDatabase.child(name.get(i)).removeValue();
                    list.remove(i);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircularImageView profileImage;
        TextView name;
        Button editBtn;
        CheckBox deleteCheckBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage1);
            name = itemView.findViewById(R.id.employee_name);
            editBtn = itemView.findViewById(R.id.btn_update);
            deleteCheckBox = itemView.findViewById(R.id.deleteChecked);
        }
    }
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public void checkCheckBox(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, true);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }
}
