package com.example.hrm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class VacantPropertyAdapter extends RecyclerView.Adapter<VacantPropertyAdapter.MyVacantViewHolder> {

    ArrayList<PropertyStructure> vacant_property_list;
    Context cxt;

    public VacantPropertyAdapter(Context cxt,ArrayList<PropertyStructure> vacant_property_list) {
        this.vacant_property_list = vacant_property_list;
        this.cxt = cxt;
    }

    @NonNull
    @NotNull
    @Override
    public VacantPropertyAdapter.MyVacantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(cxt).inflate(R.layout.vacantpropertypagecarditems, parent, false);
        return new MyVacantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VacantPropertyAdapter.MyVacantViewHolder holder, int position) {

        PropertyStructure propertyStructure = vacant_property_list.get(position);

        String image = propertyStructure.getImage_url();
//        Glide.with(cxt).load(vacant_property_list.get(position).getImage_url()).into(holder.house_image_card);
//        Uri image_uri = Uri.parse(image);

        Glide.with(cxt).load(image).into(holder.house_image_card);
        holder.house_name_card.setText(propertyStructure.getHouseName());
        String address = propertyStructure.getLandmark() + "," + propertyStructure.getCity() + "," + propertyStructure.getStateProvince() + "," + propertyStructure.getPostalCode();
        holder.address_card.setText(address);
        holder.buildingType_card.setText(propertyStructure.getBuildingType());

    }

    @Override
    public int getItemCount() {
        return vacant_property_list.size();
    }

    public class MyVacantViewHolder extends RecyclerView.ViewHolder {

        ImageView house_image_card;
        TextView house_name_card;
        TextView address_card;
        TextView buildingType_card;
        Button update_house;
        Button enter_tenant_details;

        public MyVacantViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            house_image_card = (ImageView) itemView.findViewById(R.id.property_card_image);
            house_name_card = (TextView)itemView.findViewById(R.id.property_card_house_name);
            address_card = (TextView)itemView.findViewById(R.id.property_card_address);
            buildingType_card = (TextView)itemView.findViewById(R.id.property_card_building_type);
            update_house = (Button)itemView.findViewById(R.id.update_property_button);
            enter_tenant_details = (Button)itemView.findViewById(R.id.tenantDetails_button);

            update_house.setOnClickListener(v -> {
                updateTheHouseDetails();
            });

            enter_tenant_details.setOnClickListener(v -> {
                updateTheTenantDetails();
            });

        }

        private void updateTheTenantDetails() {
            PropertyStructure property_to_change = vacant_property_list.get(getAdapterPosition());
            Intent intent = new Intent(cxt,TenantDetailsForm.class);
            intent.putExtra("property_to_change",property_to_change);
            cxt.startActivity(intent);
        }

        private void updateTheHouseDetails() {
            PropertyStructure property_to_change = vacant_property_list.get(getAdapterPosition());
            Intent intent = new Intent(cxt,UpdateHouseDetails.class);
            intent.putExtra("property_to_change",property_to_change);
            cxt.startActivity(intent);
        }


    }


//    @NonNull
//    @NotNull
//    @Override
//    public VacantPropertyAdapter.MyVacantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(cxt).inflate(R.layout.vacantpropertypagecarditems, parent, false);
//        return new VacantPropertyAdapter.MyVacantViewHolder(v);
//    }

//    @Override
//    public void onBindViewHolder(@NonNull @NotNull VacantPropertyAdapter.MyVacantViewHolder holder, int position) {
//
//        PropertyStructure propertyStructure = vacant_property_list.get(position);
//
////        String image_uri = propertyStructure.getImage_url();
////        Uri uri = Uri.parse(image_uri);
////        holder.house_image_card.setImageURI(uri);
//        Glide.with(cxt).load(vacant_property_list.get(position).getImage_url()).into(holder.house_image_card);
//        holder.house_name_card.setText(propertyStructure.getHouseName());
//        String address = propertyStructure.getLandmark() + "," + propertyStructure.getCity() + "," + propertyStructure.getStateProvince() + "," + propertyStructure.getPostalCode();
//        holder.address_card.setText(address);
//        holder.buildingType_card.setText(propertyStructure.getBuildingType());
//    }



//    @Override
//    public int getItemCount() {
//        return vacant_property_list.size();
//    }


//    public static class MyVacantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        ImageView house_image_card;
//        TextView house_name_card;
//        TextView address_card;
//        TextView buildingType_card;
//        Button update_house;
//        Button enter_tenant_details;
//
//
//        public MyVacantViewHolder(@NonNull @NotNull View itemView) {
//            super(itemView);
//            house_image_card = (ImageView) itemView.findViewById(R.id.property_card_image);
//            house_name_card = (TextView)itemView.findViewById(R.id.property_card_house_name);
//            address_card = (TextView)itemView.findViewById(R.id.property_card_address);
//            buildingType_card = (TextView)itemView.findViewById(R.id.property_card_building_type);
//            update_house = (Button)itemView.findViewById(R.id.update_property_button);
//            enter_tenant_details = (Button)itemView.findViewById(R.id.tenantDetails_button);
//
//
//            update_house.setOnClickListener(this);
//            enter_tenant_details.setOnClickListener(this);
//
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.tenantDetails_button:
//                    enterTenant();
//                    break;
//                case R.id.update_property_button:
//                    updateProperty();
//                    break;
//
//            }
//
//
//        }
//
//        private void enterTenant() {
//
//
//        }
//
//        private void updateProperty() {
//
////            PropertyStructure property_to_change = vacant_property_list.get(getAdapterPosition());
////            Intent intent = new Intent(cxt,UpdateHouseDetails.class);
////            intent.putExtra("property_to_change",property_to_change);
////            cxt.startActivity(intent);
//        }
}


