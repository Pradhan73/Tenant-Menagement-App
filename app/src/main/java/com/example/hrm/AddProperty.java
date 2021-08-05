
package com.example.hrm;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class AddProperty extends AppCompatActivity {

    private EditText add_building_type, add_postal_code, add_state_province, add_city,add_landmark, add_house_name,add_vacancy;
    private Button submit, add_upload;
    private ImageView add_image;

    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String current_user_id;

    private Uri house_image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        add_building_type = findViewById(R.id.building_type);
        add_postal_code = findViewById(R.id.postal_code);
        add_state_province = findViewById(R.id.state_province);
        add_city = findViewById(R.id.city);
        add_landmark = findViewById(R.id.landmark);
        add_house_name = findViewById(R.id.house_name);
        add_vacancy = findViewById(R.id.vacant);
        submit = findViewById(R.id.submit_button);
        add_image = findViewById(R.id.house_image);
        add_upload = findViewById(R.id.upload_image);

        //firebase and storage instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();

        submit.setOnClickListener(v -> uploadImage());

        add_image.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(AddProperty.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddProperty.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(AddProperty.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

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
    private void uploadImage()
    {
        if (house_image_uri != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Your Property...");
            progressDialog.show();

            // Defining the child of storageReference
            ref = storageReference.child("images/"+ UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            //                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
//                            if(task.isSuccessful()) {
//                                addDataToFirestore(task,add_building_type,add_postal_code,add_state_province,add_city,add_landmark,add_house_name,add_vacancy);
//                            }else {
//                                Toast.makeText(AddProperty.this,"Failed " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    })
            ref.putFile(house_image_uri)
                    .addOnSuccessListener(taskSnapshot -> {

                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
//                                Toast.makeText(AddProperty.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                                addDataToFirestore(taskSnapshot,add_building_type,add_postal_code,add_state_province,add_city,add_landmark,add_house_name,add_vacancy);
                            })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast.makeText(AddProperty.this,"Failed " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setProgress((int) progress);
                            });
        }else {
            Toast.makeText(AddProperty.this,"Please Select An Image",Toast.LENGTH_SHORT).show();
        }
    }

//    private void addDataToFirestore(Task<UploadTask.TaskSnapshot> task, EditText add_building_type, EditText add_postal_code, EditText add_state_province, EditText add_city, EditText add_landmark, EditText add_house_name, EditText add_vacancy) {
//
//        final Uri[] download_uri = new Uri[1];
////        if(task != null) {
////            download_uri = task.getResult().getDownloadUrl();
////        }else {
////            download_uri = house_image_uri;
////        }
//
//        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                download_uri[0] = uri;
////                Toast.makeText(getBaseContext(), "Upload success! URL - " + downloadUrl.toString() , Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        String image_url = download_uri[0].toString();
//        String buildingType = add_building_type.getText().toString();
//        String postalCode = add_postal_code.getText().toString();
//        String stateProvince = add_state_province.getText().toString();
//        String city = add_city.getText().toString();
//        String landmark = add_landmark.getText().toString();
//        String houseName = add_house_name.getText().toString();
//        String vacancy = add_vacancy.getText().toString();
//
//
//        PropertyStructure propertyStructure = new PropertyStructure(buildingType,postalCode,stateProvince,city,landmark,houseName, vacancy,image_url);
////
//        db.collection("users").document(current_user_id).collection("propertyList")
//                .add(propertyStructure)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(AddProperty.this, "Added Your Property", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(AddProperty.this,PropertyPage.class);
//                    startActivity(intent);
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(AddProperty.this,"Property Cannot Be Added",Toast.LENGTH_LONG).show();
//                    }
//                });
//    }

    private void addDataToFirestore(UploadTask.TaskSnapshot taskSnapshot, EditText add_building_type, EditText add_postal_code, EditText add_state_province, EditText add_city, EditText add_landmark, EditText add_house_name, EditText add_vacancy) {


        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String image_url = uri.toString();

                String buildingType = add_building_type.getText().toString();
                String postalCode = add_postal_code.getText().toString();
                String stateProvince = add_state_province.getText().toString();
                String city = add_city.getText().toString();
                String landmark = add_landmark.getText().toString();
                String houseName = add_house_name.getText().toString();
                String vacancy = add_vacancy.getText().toString();

                PropertyStructure propertyStructure = new PropertyStructure(buildingType,postalCode,stateProvince,city,landmark,houseName, vacancy,image_url);

                db.collection("users").document(current_user_id).collection("propertyList")
                        .add(propertyStructure)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(AddProperty.this, "Added Your Property", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddProperty.this,PropertyPage.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProperty.this,"Property Cannot Be Added",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
                //house_image_uri.toString();
        //Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString();
//        String buildingType = add_building_type.getText().toString();
//        String postalCode = add_postal_code.getText().toString();
//        String stateProvince = add_state_province.getText().toString();
//        String city = add_city.getText().toString();
//        String landmark = add_landmark.getText().toString();
//        String houseName = add_house_name.getText().toString();
//        String vacancy = add_vacancy.getText().toString();
//
//        PropertyStructure propertyStructure = new PropertyStructure(buildingType,postalCode,stateProvince,city,landmark,houseName, vacancy,image_url);
//
//        db.collection("users").document(current_user_id).collection("propertyList")
//                .add(propertyStructure)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(AddProperty.this, "Added Your Property", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(AddProperty.this,PropertyPage.class);
//                    startActivity(intent);
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(AddProperty.this,"Property Cannot Be Added",Toast.LENGTH_LONG).show();
//                    }
//                });

    }


}



