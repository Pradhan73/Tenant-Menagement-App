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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class UpdateTenant extends AppCompatActivity {

    PropertyStructure property_to_change;
    private EditText First, Last, Pon, Eml, PdnDus;
    private Button Up_tenant,delete_tenant;
    private ImageView tenant_dp;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String user_id;
    private ProgressBar progressBar;

    FirebaseStorage storage;
    StorageReference storageReference;

    private final int PICK_IMAGE_REQUEST = 22;
    private Uri tenant_image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tenant);

        Intent intent = getIntent();
        property_to_change = (PropertyStructure) intent.getSerializableExtra("property_to_change");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getUid();

        First = findViewById(R.id.first_name);
        Last = findViewById(R.id.last_name);
        Pon = findViewById(R.id.phone_no);
        Eml = findViewById(R.id.email_no);
        PdnDus = findViewById(R.id.pending_dues);
        Up_tenant = (Button)findViewById(R.id.update_tenant_details);
        delete_tenant = (Button)findViewById(R.id.delete_tenant);
        tenant_dp = (ImageView)findViewById(R.id.tenant_image);



        DocumentReference docRef = db.collection("users").document(user_id).collection("propertyList").document(property_to_change.getPropertyId()).collection("tenantInfo").document(property_to_change.getPropertyId());
                docRef
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                TenantStructure tenantStructure = new TenantStructure(document.getString("firstName"),document.getString("lastName"),document.getString("phone"),document.getString("email"),document.getString("pendingDues"),document.getString("tenantImage"));
//                                tenantStructure.setTenantId(document.getId());
                                placeInfoInPlace(tenantStructure);
                            } else {
                                Toast.makeText(UpdateTenant.this,"No Tenant Added",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(UpdateTenant.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        progressBar.setVisibility(View.INVISIBLE);
//                        for(DocumentSnapshot snapshot : task.getResult()) {
//                            TenantStructure  tenantStructure = new TenantStructure(snapshot.getString("firstName"),snapshot.getString("lastName"),snapshot.getString("phone"),snapshot.getString("email"),snapshot.getString("pendingDues"),snapshot.getString("tenantImage"));
//                            tenantStructure.setTenantId(snapshot.getId());
//                            placeInfoInPlace(tenantStructure);
////                            showTenant(tenantStructure);
//// snapshot.toObject(PropertyStructure.class);
//// new PropertyStructure(snapshot.getString("buildingType"),snapshot.getString("postalCode"),snapshot.getString("stateProvince"),snapshot.getString("city"),snapshot.getString("landmark"),snapshot.getString("houseName"),snapshot.getString("vacancy"),snapshot.getString("image_url"));
////                            propertyStructure.setPropertyId(snapshot.getId());
////                            property_item.add(propertyStructure);
//                        }
//                    } else {
//                        Toast.makeText(UpdateTenant.this,"No Property Added",Toast.LENGTH_LONG).show();
//                    }
//                }).addOnFailureListener(e -> Toast.makeText(UpdateTenant.this, "Fail to get the data.", Toast.LENGTH_SHORT).show());

        delete_tenant.setOnClickListener(v -> db.collection("users").document(user_id).collection("propertyList").document(property_to_change.getPropertyId()).collection("tenantInfo").document(property_to_change.getPropertyId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateTenant.this,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                        UpdateProperty();
                        Intent intent = new Intent(UpdateTenant.this,PropertyPage.class);
                        startActivity(intent);
                    }

                    private void UpdateProperty() {

                        DocumentReference propertyUpdate = db.collection("users").document(user_id).collection("propertyList").document(property_to_change.getPropertyId());
                        propertyUpdate
                                .update("vacancy", "Yes")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UpdateTenant.this,"Property Updated",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UpdateTenant.this,"Failed To Update Property",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateTenant.this,"Deletion Failed",Toast.LENGTH_SHORT).show();
                    }
                }));





        tenant_dp.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(UpdateTenant.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(UpdateTenant.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(UpdateTenant.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                }else {
                    ChoseImage();
                }
            }else {
                ChoseImage();
            }
        });

        Up_tenant.setOnClickListener(v -> {
            updateUploadImage();
        });





    }

    private void placeInfoInPlace(TenantStructure tenantStructure) {

        First.setText(tenantStructure.getFirstName());
        Last.setText(tenantStructure.getLastName());
        Eml.setText(tenantStructure.getEmail());
        Pon.setText(tenantStructure.getPhone());
        PdnDus.setText(tenantStructure.getPendingDues());
//        Glide.with(UpdateTenant.this).load(tenantStructure.getTenantImage()).into(tenant_dp);
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
            tenant_image_uri = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(),tenant_image_uri);
                tenant_dp.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void updateUploadImage()
    {
        if (tenant_image_uri != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Updating...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("tenants/"+ UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(tenant_image_uri)
                    .addOnSuccessListener(taskSnapshot -> {

                        // Image uploaded successfully
                        // Dismiss dialog
                        progressDialog.dismiss();
//                        Toast.makeText(UpdateHouseDetails.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                        update(taskSnapshot,First,Last,Pon,Eml,PdnDus);
                    })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(UpdateTenant.this,"Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(UpdateTenant.this,"Please Upload Image",Toast.LENGTH_SHORT).show();
        }
    }

    private void update(UploadTask.TaskSnapshot taskSnapshot, EditText first, EditText last, EditText pon, EditText eml, EditText pdnDus) {

        String first_na_me = First.getText().toString();
        String last_na_me = Last.getText().toString();
        String Ph_one = Eml.getText().toString();
        String Em_ail = Pon.getText().toString();
        String Pend_ing_dues = PdnDus.getText().toString();
        String te_nant_Dp = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString();

        TenantStructure tenantUpdate = new TenantStructure(first_na_me,last_na_me,Ph_one,Em_ail,Pend_ing_dues,te_nant_Dp);

        db.collection("users").document(user_id).collection("propertyList").document(property_to_change.getPropertyId()).collection("tenantInfo").document(property_to_change.getPropertyId())
                .set(tenantUpdate)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(UpdateTenant.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UpdateTenant.this,PropertyPage.class);
                    startActivity(intent);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateTenant.this,"Property Cannot Be Updated",Toast.LENGTH_LONG).show();
                    }
                });

    }



}