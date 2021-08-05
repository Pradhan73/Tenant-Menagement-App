package com.example.hrm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class TenantDetailsForm extends AppCompatActivity {

    EditText first_name,last_name,pho_ne,em_ail,pending_dues;
    ImageView tenant_image;
    Button enter_tenant;
    PropertyStructure property_to_change;

    private final int PICK_IMAGE_REQUEST = 22;
    private Uri house_image_uri;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_details_form);

        property_to_change = (PropertyStructure) getIntent().getSerializableExtra("property_to_change");

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        pho_ne = findViewById(R.id.phone_no);
        em_ail = findViewById(R.id.email_no);
        pending_dues = findViewById(R.id.pending_dues);
        tenant_image = findViewById(R.id.tenant_image);
        enter_tenant = findViewById(R.id.enter_tenant_details);

        //firebase and storage instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();

        tenant_image.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(TenantDetailsForm.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(TenantDetailsForm.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(TenantDetailsForm.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                }else {
                    ChoseImage();
                }
            }else {
                ChoseImage();
            }
        });

        enter_tenant.setOnClickListener(v -> {
            uploadTenantImage();
        });

    }

    private void ChoseImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Get the Uri of data
            house_image_uri = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(),house_image_uri);
                tenant_image.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    // UploadImage method
    private void uploadTenantImage()
    {
        if (house_image_uri != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Tenant...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("tenants/"+ UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(house_image_uri)
                    .addOnSuccessListener(taskSnapshot -> {

                        // Image uploaded successfully
                        // Dismiss dialog
                        progressDialog.dismiss();
//                                Toast.makeText(AddProperty.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                        addDataToFirestore(taskSnapshot,first_name,last_name,pho_ne,em_ail,pending_dues,tenant_image);
                    })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(TenantDetailsForm.this,"Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int)progress + "%");
                            });
        }else {
            Toast.makeText(TenantDetailsForm.this,"Please Select An Image",Toast.LENGTH_SHORT).show();
        }
    }

    private void addDataToFirestore(UploadTask.TaskSnapshot taskSnapshot, EditText first_name, EditText last_name, EditText pho_ne, EditText em_ail, EditText pending_dues, ImageView tenant_image) {

        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String tenantImage = uri.toString();

                String firstName = first_name.getText().toString();
                String lastName = last_name.getText().toString();
                String phone = pho_ne.getText().toString();
                String email = em_ail.getText().toString();
                String pendingDues = pending_dues.getText().toString();

                TenantStructure tenantStructure = new TenantStructure(firstName,lastName,phone,email,pendingDues,tenantImage);

                db.collection("users").document(current_user_id).collection("propertyList").document(property_to_change.getPropertyId()).collection("tenantInfo").document(property_to_change.getPropertyId())
                        .set(tenantStructure)
                        .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(TenantDetailsForm.this, "Added Tenant Successfully", Toast.LENGTH_LONG).show();
                            updateVacancy();
                            Intent intent = new Intent(TenantDetailsForm.this,PropertyPage.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TenantDetailsForm.this,"Property Cannot Be Added",Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
//        String tenantImage = house_image_uri.toString();
//        String firstName = first_name.getText().toString();
//        String lastName = last_name.getText().toString();
//        String phone = pho_ne.getText().toString();
//        String email = em_ail.getText().toString();
//        String pendingDues = pending_dues.getText().toString();
//
//        TenantStructure tenantStructure = new TenantStructure(firstName,lastName,phone,email,pendingDues,tenantImage);
//
//        db.collection("users").document(current_user_id).collection("propertyList").document(property_to_change.getPropertyId()).collection("tenantInfo").document(property_to_change.getPropertyId())
//                .set(tenantStructure)
//                .addOnSuccessListener(documentReference -> {
////                    Toast.makeText(TenantDetailsForm.this, "Added Tenant Successfully", Toast.LENGTH_LONG).show();
//                    updateVacancy();
//                    Intent intent = new Intent(TenantDetailsForm.this,PropertyPage.class);
//                    startActivity(intent);
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(TenantDetailsForm.this,"Property Cannot Be Added",Toast.LENGTH_LONG).show();
//                    }
//                });
    }

    private void updateVacancy() {

        DocumentReference propertyUpdate = db.collection("users").document(current_user_id).collection("propertyList").document(property_to_change.getPropertyId());
        propertyUpdate
                .update("vacancy", "No")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TenantDetailsForm.this,"Property Updated",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TenantDetailsForm.this,"Failed To Update Property",Toast.LENGTH_LONG).show();
                    }
                });
    }

}


