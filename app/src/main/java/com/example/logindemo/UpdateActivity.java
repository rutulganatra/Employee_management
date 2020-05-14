package com.example.logindemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;

public class UpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static String TAG = "UpdateActivity";
    TextInputLayout edtName,edtemail,edtContact,edtAddress,edtSalary,edtAbout;
    RadioGroup rgGender;
    Spinner designation;
    RadioButton rbMale,rbFemale;
    MaterialButton mbUpdate;
    TextView tvDesignation;
    CircularImageView employeeImage;
    private DatabaseReference mDatabase;
    StorageReference mStorageRef;
    ImageView backBtn;
    EmployeeModel employeeModel;
    Uri imguri;
    String item="Select your designation",gender,id,email,url;
    String name,cont,desig,address,about;
    ProgressDialog progressBar;
    Double salary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        init();


    }
    private void init(){
        edtName = findViewById(R.id.edtname);
        edtemail = findViewById(R.id.edtEmail);
        edtContact = findViewById(R.id.edtContact);
        edtAddress = findViewById(R.id.edtAddress);
        edtSalary = findViewById(R.id.edtSalary);
        edtAbout = findViewById(R.id.edtAbout);
        designation = findViewById(R.id.spinner_designation);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.male);
        rbFemale = findViewById(R.id.female);
        mbUpdate = findViewById(R.id.mbUpdate);
        tvDesignation = findViewById(R.id.tvDesignation);
        employeeImage = findViewById(R.id.EmployeeImage);
        backBtn = findViewById(R.id.backBtn);
        ArrayAdapter<CharSequence> desigationAdapter= ArrayAdapter.createFromResource(this,R.array.designation, android.R.layout.simple_list_item_1);
        desigationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designation.setAdapter(desigationAdapter);
        designation.setOnItemSelectedListener(UpdateActivity.this);
        rgGender.setOnCheckedChangeListener(UpdateActivity.this);
        employeeImage.setOnClickListener(this);
        mbUpdate.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Updating...");
        progressBar.setContentView(R.layout.activity_data);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        url = i.getStringExtra("imageUrl");
        cont = i.getStringExtra("contact");
        desig = i.getStringExtra("designation");
        address = i.getStringExtra("address");
        gender = i.getStringExtra("gender");
        salary =i.getDoubleExtra("salary",0);
        about = i.getStringExtra("about");
        edtName.getEditText().setText(name);
        edtemail.getEditText().setText(email);
        edtSalary.getEditText().setText(salary.toString());
        employeeImage.setImageURI(Uri.parse(url));
        edtContact.getEditText().setText(cont);
        edtAddress.getEditText().setText(address);
        edtAbout.getEditText().setText(about);
        designation.setSelection(desigationAdapter.getPosition(desig));
        Glide.with(getApplicationContext()).load(url).into(employeeImage);
        if(gender.equals("Male")){
            rbMale.setChecked(true);
        }
        else {
            rbMale.setChecked(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(rgGender.getCheckedRadioButtonId() == R.id.male){
            gender = rbMale.getText().toString();
        }
        else {
            gender = rbFemale.getText().toString();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                Intent i = new Intent(UpdateActivity.this,ProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.EmployeeImage:
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
                CropImage.activity(imguri).start(UpdateActivity.this);
                finishActivity(0);
                break;
            case R.id.mbUpdate:
                if(isValidate()) {
                    updateData(id);
                }
                break;
        }
    }

    private void updateData(String updateId) {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabase = FirebaseDatabase.getInstance().getReference("Employee").child(updateId);
        FileUploader();
        employeeModel = new EmployeeModel();
        employeeModel.setId(updateId);
        employeeModel.setUrl(imguri.toString());
        employeeModel.setName(edtName.getEditText().getText().toString());
        employeeModel.setEmail(edtemail.getEditText().getText().toString());
        employeeModel.setAddress(edtAddress.getEditText().getText().toString());
        employeeModel.setDesignation(item);
        employeeModel.setGender(gender);
        employeeModel.setSalary(Double.parseDouble(edtSalary.getEditText().getText().toString()));
        employeeModel.setContact(edtContact.getEditText().getText().toString());
        employeeModel.setDescription(edtAbout.getEditText().getText().toString());
        mDatabase.setValue(employeeModel);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imguri = resultUri;
                employeeImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void FileUploader(){
        if(imguri!=null){
            progressBar.show();
            StorageReference storageReference = mStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));
            storageReference.putFile(imguri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(UpdateActivity.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(UpdateActivity.this,ProfileActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            progressBar.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
        else {
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();

        }
    }
    private boolean isValidate() {
        boolean valid = true;
        if (imguri == null){
            showToast(getResources().getString(R.string.error_msg_image));
            valid=false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(edtemail.getEditText().getText().toString().trim()).matches()){
            edtemail.setError("Same data cannot be updated");
            valid = false;
        }
        else {
            edtemail.setError(null);
        }
        if (edtName.getEditText().getText().toString().isEmpty()){
            edtName.setError(getResources().getString(R.string.error_msg_name));
            valid = false;
        }
        else {
            edtName.setError(null);
        }
        if (edtSalary.getEditText().getText().toString().isEmpty()){
            edtSalary.setError(getResources().getString(R.string.error_msg_salary));
            valid = false;
        }
        else {
            edtSalary.setError(null);
        }
        if (edtAddress.getEditText().getText().toString().isEmpty()){
            edtAddress.setError(getResources().getString(R.string.error_msg_address));
            valid = false;
        }
        else {
            edtAddress.setError(null);
        }
        if (!Patterns.PHONE.matcher(edtContact.getEditText().getText().toString().trim()).matches()){
            edtContact.setError(getResources().getString(R.string.error_msg_contact));
            valid = false;
        }
        else {
            edtContact.setError(null);
        }
        if(rgGender.getCheckedRadioButtonId()  == -1){
            showToast(getResources().getString(R.string.error_msg_gender));
            valid = false;
        }
        if (item.equals("Select your designation")){
            showToast(getResources().getString(R.string.error_msg_desgination));
            valid = false;
        }
        if (edtAbout.getEditText().getText().toString().isEmpty()){
            edtAbout.setError(getResources().getString(R.string.error_msg_about));
            valid = false;
        }
        else {
            edtAbout.setError(null);
        }
        return valid;
    }
    private void showToast(String msg){
        Toast.makeText(UpdateActivity.this,msg,Toast.LENGTH_LONG).show();
    }
}
