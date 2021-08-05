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

public class VacantPropertyPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<PropertyStructure> vacant_property_item;
    private VacantPropertyAdapter vacantPropertyAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String user_id;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacant_property_page);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getUid();

        recyclerView = findViewById(R.id.recyclerview_properties);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(VacantPropertyPage.this));

        vacant_property_item = new ArrayList<>();
        vacantPropertyAdapter = new VacantPropertyAdapter(this, vacant_property_item);

        recyclerView.setAdapter(vacantPropertyAdapter);

        progressBar = findViewById(R.id.progressbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("images/");

        db = FirebaseFirestore.getInstance();



        db.collection("users").document(user_id).collection("propertyList")
                .whereEqualTo("vacancy", "Yes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        for(DocumentSnapshot documentSnapshot : task.getResult()) {
                            PropertyStructure propertyStructure = new PropertyStructure(documentSnapshot.getString("buildingType"),documentSnapshot.getString("postalCode"),documentSnapshot.getString("stateProvince"),documentSnapshot.getString("city"),documentSnapshot.getString("landmark"),documentSnapshot.getString("houseName"),documentSnapshot.getString("vacancy"),documentSnapshot.getString("image_url"));
//documentSnapshot.toObject(PropertyStructure.class);
// new PropertyStructure(documentSnapshot.getString("buildingType"),documentSnapshot.getString("postalCode"),documentSnapshot.getString("stateProvince"),documentSnapshot.getString("city"),documentSnapshot.getString("landmark"),documentSnapshot.getString("houseName"),documentSnapshot.getString("vacancy"),documentSnapshot.getString("image_url"));
                            propertyStructure.setPropertyId(documentSnapshot.getId());
                            vacant_property_item.add(propertyStructure);
                        }
                        vacantPropertyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(VacantPropertyPage.this,"No Property Added",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(VacantPropertyPage.this, "Fail to get the data.", Toast.LENGTH_SHORT).show());
//        db.collection("users").document(user_id).collection("propertyList")
//                .whereEqualTo("vacancy", "Yes")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            progressBar.setVisibility(View.GONE);
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                PropertyStructure c = d.toObject(PropertyStructure.class);
//                                c.setPropertyId(d.getId());
//                                vacant_property_item.add(c);
//                            }
//                            vacantPropertyPageAdapter.notifyDataSetChanged();
//                        } else {
//                            Toast.makeText(VacantPropertyPage.this, "No data found in Database", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // if we do not get any data or any error we are displaying
//                // a toast message that we do not get any data
//                Toast.makeText(VacantPropertyPage.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
//            }
//        });



        findViewById(R.id.not_vacant_properties).setOnClickListener(v -> {
            Intent intent = new Intent(VacantPropertyPage.this,PropertyPage.class);
            startActivity(intent);
        });


        findViewById(R.id.add_property_button).setOnClickListener(v -> {
            Intent intent = new Intent(this,AddProperty.class);
            startActivity(intent);
        });
    }
}