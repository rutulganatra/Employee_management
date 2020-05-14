package com.example.logindemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.legacy.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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



public class DataActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static String TAG ="DataActivity";
    Spinner designation;
    TextInputLayout edtName,edtemail,edtContact,edtAddress,edtSalary,edtAbout;
    RadioGroup rgGender;
    RadioButton rbMale,rbFemale;
    MaterialButton mbAdd;
    String item="Select your designation",gender;
    CircularImageView employeeImage;
    ImageView backBtn;
    private DatabaseReference mDatabase;
    EmployeeModel employeeModel;
    StorageReference mStorageRef;
    Uri imguri;
    String imageUrl;
    TextView tvDesignation;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        init();
        verifyPermission();
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
        mbAdd = findViewById(R.id.mbAdd);
        tvDesignation = findViewById(R.id.tvDesignation);
        employeeImage = findViewById(R.id.EmployeeImage);
        backBtn = findViewById(R.id.backBtn);
        ArrayAdapter<CharSequence> desigationAdapter= ArrayAdapter.createFromResource(this,R.array.designation, android.R.layout.simple_list_item_1);
        desigationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designation.setAdapter(desigationAdapter);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        employeeModel = new EmployeeModel();
        mDatabase = FirebaseDatabase.getInstance().getReference("Employee");
        mDatabase.orderByChild("name");
        designation.setSelection(0,false);
        designation.setOnItemSelectedListener(DataActivity.this);
        rgGender.setOnCheckedChangeListener(DataActivity.this);
        employeeImage.setOnClickListener(this);
        mbAdd.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Uploading...");
        progressBar.setContentView(R.layout.activity_data);
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

    private void insertData(String imageUrl){
        String id = mDatabase.push().getKey();
        employeeModel.setId(id);
        employeeModel.setUrl(imageUrl);
        employeeModel.setName(edtName.getEditText().getText().toString());
        employeeModel.setEmail(edtemail.getEditText().getText().toString());
        employeeModel.setDesignation(item);
        employeeModel.setAddress(edtAddress.getEditText().getText().toString());
        employeeModel.setGender(gender);
        employeeModel.setSalary(Double.parseDouble(edtSalary.getEditText().getText().toString()));
        employeeModel.setDescription(edtAbout.getEditText().getText().toString());
        employeeModel.setContact(edtContact.getEditText().getText().toString());
        mDatabase.child(id).setValue(employeeModel);
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void FileUploader(){
        progressBar.show();
        if(imguri!=null){
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));
            storageReference.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressBar.dismiss();
                            imageUrl = String.valueOf(uri);
                            insertData(imageUrl);
                            showToast("Data inserted successfully");
                            Intent i = new Intent(DataActivity.this,ProfileActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                        }
                    });
                }
            });
        }
        else {
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mbAdd:
                if (isValidate()) {
                    FileUploader();
                }
                break;
            case R.id.EmployeeImage:

                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
                CropImage.activity(imguri).start(DataActivity.this);
                finishActivity(0);
                break;
            case R.id.backBtn:
                Intent intent = new Intent(DataActivity.this,ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    private void verifyPermission(){
        String[] permissions ={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) != PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[1]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DataActivity.this,permissions,0);
        }
    }

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

    private boolean isValidate() {
        boolean valid = true;
        if (imguri == null){
            showToast(getResources().getString(R.string.error_msg_image));
            valid=false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(edtemail.getEditText().getText().toString().trim()).matches()){
            edtemail.setError(getResources().getString(R.string.error_msg_email));
            valid = false;
        }else{edtemail.setError(null);}
        if (edtName.getEditText().getText().toString().isEmpty()){
            edtName.setError(getResources().getString(R.string.error_msg_name));
            valid = false;
        }else{edtName.setError(null);}
        if (edtSalary.getEditText().getText().toString().isEmpty()){
            edtSalary.setError(getResources().getString(R.string.error_msg_salary));
            valid = false;
        }else{edtSalary.setError(null);}
        if (edtAddress.getEditText().getText().toString().isEmpty()){
            edtAddress.setError(getResources().getString(R.string.error_msg_address));
            valid = false;
        }else{edtAddress.setError(null);}
        if (!Patterns.PHONE.matcher(edtContact.getEditText().getText().toString().trim()).matches()){
            edtContact.setError(getResources().getString(R.string.error_msg_contact));
            valid = false;
        }else{edtContact.setError(null);}
        if(rgGender.getCheckedRadioButtonId()  == -1){
            showToast(getResources().getString(R.string.error_msg_gender));
            valid = false;
        }
        if(designation.getSelectedItemId()==0){
            showToast(getResources().getString(R.string.error_msg_desgination));
            tvDesignation.setError(getResources().getString(R.string.error_msg_desgination));
            valid = false;
        }else{tvDesignation.setError(null);}
        if (edtAbout.getEditText().getText().toString().isEmpty()){
            edtAbout.setError(getResources().getString(R.string.error_msg_about));
            valid = false;
        }else{edtAbout.setError(null);}
        return valid;
    }

    private void showToast(String msg){
        Toast.makeText(DataActivity.this,msg,Toast.LENGTH_LONG).show();
    }
}
