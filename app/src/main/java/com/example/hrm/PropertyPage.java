package com.example.hrm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PropertyPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<PropertyStructure> property_item;
    private PropertyPageAdapter propertyPageAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String user_id;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_page);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getUid();

        recyclerView = findViewById(R.id.recyclerview_properties);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PropertyPage.this));

        property_item = new ArrayList<>();
        propertyPageAdapter = new PropertyPageAdapter(property_item,this);

        recyclerView.setAdapter(propertyPageAdapter);

        progressBar = findViewById(R.id.progressbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("images/");

        db = FirebaseFirestore.getInstance();

        db.collection("users").document(user_id).collection("propertyList")
                .whereEqualTo("vacancy", "No")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            PropertyStructure propertyStructure = new PropertyStructure(snapshot.getString("buildingType"),snapshot.getString("postalCode"),snapshot.getString("stateProvince"),snapshot.getString("city"),snapshot.getString("landmark"),snapshot.getString("houseName"),snapshot.getString("vacancy"),snapshot.getString("image_url"));
// snapshot.toObject(PropertyStructure.class);
// new PropertyStructure(snapshot.getString("buildingType"),snapshot.getString("postalCode"),snapshot.getString("stateProvince"),snapshot.getString("city"),snapshot.getString("landmark"),snapshot.getString("houseName"),snapshot.getString("vacancy"),snapshot.getString("image_url"));
                            propertyStructure.setPropertyId(snapshot.getId());
                            property_item.add(propertyStructure);
                        }
                        propertyPageAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(PropertyPage.this,"No Property Added",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(PropertyPage.this, "Fail to get the data.", Toast.LENGTH_SHORT).show());

        findViewById(R.id.add_property_button).setOnClickListener(v -> {
            Intent intent = new Intent(PropertyPage.this,AddProperty.class);
            startActivity(intent);
        });

        findViewById(R.id.vacant_properties).setOnClickListener(v -> {
            Intent intent = new Intent(PropertyPage.this,VacantPropertyPage.class);
            startActivity(intent);
        });

    }


}