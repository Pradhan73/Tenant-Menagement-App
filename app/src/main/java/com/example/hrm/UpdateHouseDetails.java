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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class UpdateHouseDetails extends AppCompatActivity {

    private EditText building_type;
    private EditText postal_code;
    private EditText state_province;
    private EditText ci_ty;
    private EditText land_mark;
    private EditText house_name;
    private EditText vac_ancy;
    private Button update_button;
    private Button delete_button;
    private ImageView add_image;

    private FirebaseFirestore db;

    private PropertyStructure property_to_change;

    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri house_image_uri;
    String current_user_id;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_house_details);


        property_to_change = (PropertyStructure) getIntent().getSerializableExtra("property_to_change");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();

        building_type = findViewById(R.id.building_type);
        postal_code = findViewById(R.id.postal_code);
        state_province = findViewById(R.id.state_province);
        ci_ty = findViewById(R.id.city);
        land_mark = findViewById(R.id.landmark);
        house_name = findViewById(R.id.house_name);
        vac_ancy = findViewById(R.id.vacant);
        update_button = findViewById(R.id.house_update_button);
        delete_button = findViewById(R.id.delete_property);
        add_image = findViewById(R.id.house_image);

        building_type.setText(property_to_change.getBuildingType());
        postal_code.setText(property_to_change.getPostalCode());
        state_province.setText(property_to_change.getStateProvince());
        ci_ty.setText(property_to_change.getCity());
        land_mark.setText(property_to_change.getLandmark());
        house_name.setText(property_to_change.getHouseName());
        vac_ancy.setText(property_to_change.getVacancy());
//        Glide.with(UpdateHouseDetails.this).load(property_to_change.getImage_url()).into(add_image);

        update_button.setOnClickListener(v -> updateUploadImage());


        delete_button.setOnClickListener(v -> db.collection("users").document(current_user_id).collection("propertyList").document(property_to_change.getPropertyId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateHouseDetails.this,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateHouseDetails.this,PropertyPage.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateHouseDetails.this,"Deletion Failed",Toast.LENGTH_SHORT).show();
                    }
                }));

        add_image.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(UpdateHouseDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(UpdateHouseDetails.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(UpdateHouseDetails.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                }else {
                    ChoseImage();
                }
            }else {
                ChoseImage();
            }
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
                add_image.setImageBitmap(bitmap);
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
        if (house_image_uri != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Updating...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(house_image_uri)
                    .addOnSuccessListener(taskSnapshot -> {

                        // Image uploaded successfully
                        // Dismiss dialog
                        progressDialog.dismiss();
//                        Toast.makeText(UpdateHouseDetails.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                        update(taskSnapshot,building_type,postal_code,state_province,ci_ty,land_mark,house_name,vac_ancy);
                    })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(UpdateHouseDetails.this,"Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int)progress + "%");
                            });
        }
    }


    private void update(UploadTask.TaskSnapshot taskSnapshot, EditText add_building_type, EditText add_postal_code, EditText add_state_province, EditText add_city, EditText add_landmark, EditText add_house_name, EditText add_vacancy) {

        String image_url = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString();
        String buildingType = add_building_type.getText().toString();
        String postalCode = add_postal_code.getText().toString();
        String stateProvince = add_state_province.getText().toString();
        String city = add_city.getText().toString();
        String landmark = add_landmark.getText().toString();
        String houseName = add_house_name.getText().toString();
        String vacancy = add_vacancy.getText().toString();

        PropertyStructure propertyStructure = new PropertyStructure(buildingType,postalCode,stateProvince,city,landmark,houseName, vacancy,image_url);

        db.collection("users").document(current_user_id).collection("propertyList").document(property_to_change.getPropertyId())
                .set(propertyStructure)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(UpdateHouseDetails.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UpdateHouseDetails.this,PropertyPage.class);
                    startActivity(intent);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateHouseDetails.this,"Property Cannot Be Updated",Toast.LENGTH_LONG).show();
                    }
                });

    }

}