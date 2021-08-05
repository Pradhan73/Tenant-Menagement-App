package com.example.hrm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayTenantDetails extends AppCompatActivity {

    PropertyStructure property_to_change;
    private TextView name,em_ail,ph_one,pending_dues;
    private ImageView tenant_image;
    private Button update_tenant;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String user_id;
    private ProgressBar progressBar;

    FirebaseStorage storage;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tenant_details);

        progressBar = findViewById(R.id.progressbar);

        property_to_change = (PropertyStructure) getIntent().getSerializableExtra("property_to_change");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getUid();


        name = findViewById(R.id.fill_name);
        em_ail = findViewById(R.id.fill_email);
        ph_one = findViewById(R.id.fill_phone);
        pending_dues = findViewById(R.id.fill_pending_dues);
        tenant_image = (ImageView) findViewById(R.id.tenant_image);
        update_tenant = (Button) findViewById(R.id.update_tenant);

        update_tenant.setOnClickListener(v -> {
            Intent intent = new Intent(this,UpdateTenant.class);
            intent.putExtra("property_to_change",property_to_change);
            startActivity(intent);
        });


        db.collection("users").document(user_id).collection("propertyList").document(property_to_change.getPropertyId()).collection("tenantInfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            TenantStructure  tenantStructure = new TenantStructure(snapshot.getString("firstName"),snapshot.getString("lastName"),snapshot.getString("phone"),snapshot.getString("email"),snapshot.getString("pendingDues"),snapshot.getString("tenantImage"));
                            showTenant(tenantStructure);
// snapshot.toObject(PropertyStructure.class);
// new PropertyStructure(snapshot.getString("buildingType"),snapshot.getString("postalCode"),snapshot.getString("stateProvince"),snapshot.getString("city"),snapshot.getString("landmark"),snapshot.getString("houseName"),snapshot.getString("vacancy"),snapshot.getString("image_url"));
//                            propertyStructure.setPropertyId(snapshot.getId());
//                            property_item.add(propertyStructure);
                        }
                    } else {
                        Toast.makeText(DisplayTenantDetails.this,"No Property Added",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(DisplayTenantDetails.this, "Fail to get the data.", Toast.LENGTH_SHORT).show());


    }

    private void showTenant(TenantStructure tenantStructure) {

        String Name = tenantStructure.getFirstName() + " " + tenantStructure.getLastName();
        String Email = tenantStructure.getEmail();
        String Phone = tenantStructure.getPhone();
        String PendingDues = tenantStructure.getPendingDues();

        name.setText(Name);
        em_ail.setText(Email);
        ph_one.setText(Phone);
        pending_dues.setText(PendingDues);
        Glide.with(DisplayTenantDetails.this).load(tenantStructure.getTenantImage()).into(tenant_image);

    }


}