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

public class PropertyPageAdapter extends RecyclerView.Adapter<PropertyPageAdapter.MyPropertyViewHolder> {

    private ArrayList<PropertyStructure> propertyItem;
    private Context context;

    public PropertyPageAdapter(ArrayList<PropertyStructure> propertyItem, Context context) {
        this.propertyItem = propertyItem;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public PropertyPageAdapter.MyPropertyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.propertyitemscardview, parent, false);
        return new MyPropertyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PropertyPageAdapter.MyPropertyViewHolder holder, int position) {

        PropertyStructure propertyStructure = propertyItem.get(position);

        String image = propertyStructure.getImage_url();
        // "https://firebasestorage.googleapis.com/v0/b/hrm-app-48134.appspot.com/o/images%2F15bb3df8-bd12-426f-80d4-6dc3171cd68d?alt=media&token=929bf248-66a8-43e3-9538-1e59172f5ffd";
//        Uri image_uri = Uri.parse(image);
//        Uri image_uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/hrm-app-48134.appspot.com/o/images%2F7b40c1e0-8adc-4566-a1b1-119de89d3c9d?alt=media&token=be9e3937-433f-48f5-9566-65435e8cda6c");
        Glide.with(context).load(image).into(holder.house_image_card);

        holder.house_name_card.setText(propertyStructure.getHouseName());
        String address = propertyStructure.getLandmark() + ", " + propertyStructure.getCity() + ", " + propertyStructure.getStateProvince() + ", " + propertyStructure.getPostalCode();
        holder.address_card.setText(address);
        holder.buildingType_card.setText(propertyStructure.getBuildingType());
    }

    @Override
    public int getItemCount() {
        return propertyItem.size();
    }

    public class MyPropertyViewHolder extends RecyclerView.ViewHolder {

        ImageView house_image_card;
        TextView house_name_card;
        TextView address_card;
        TextView buildingType_card;
        Button update_property;
        Button view_tenant;

        public MyPropertyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            house_image_card = itemView.findViewById(R.id.property_card_image);
            house_name_card = itemView.findViewById(R.id.property_card_house_name);
            address_card = itemView.findViewById(R.id.property_card_address);
            buildingType_card = itemView.findViewById(R.id.property_card_building_type);
            update_property = (Button)itemView.findViewById(R.id.update_property_button);
            view_tenant = (Button)itemView.findViewById(R.id.tenantDetails_button);

            update_property.setOnClickListener(v -> {
                updateTheProperty();
            });

            view_tenant.setOnClickListener(v -> {
                seeTenantInfo();
            });

        }

        private void seeTenantInfo() {
            PropertyStructure property_to_change = propertyItem.get(getAdapterPosition());
            Intent intent = new Intent(context,DisplayTenantDetails.class);
            intent.putExtra("property_to_change",property_to_change);
            context.startActivity(intent);
        }

        private void updateTheProperty() {
            PropertyStructure property_to_change = propertyItem.get(getAdapterPosition());
            Intent intent = new Intent(context,UpdateHouseDetails.class);
            intent.putExtra("property_to_change",property_to_change);
            context.startActivity(intent);
        }

    }


//    public PropertyPageAdapter(Context context, ArrayList<PropertyStructure> propertyList) {
//        this.context = context;
//        this.propertyList = propertyList;
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public MyPropertyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.propertyitemscardview, parent, false);
//        return new MyPropertyViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull PropertyPageAdapter.MyPropertyViewHolder holder, int position) {
//
//        PropertyStructure propertyStructure = propertyList.get(position);
//
////        String image_uri = propertyStructure.getImage_url();
////        Uri uri = Uri.parse(image_uri);
////        holder.house_image_card.setImageURI(uri);
//        Glide.with(context).load(propertyList.get(position).getImage_url()).into(holder.house_image_card);
//        holder.house_name_card.setText(propertyStructure.getHouseName());
//        String address = propertyStructure.getLandmark() + ", " + propertyStructure.getCity() + ", " + propertyStructure.getStateProvince() + ", " + propertyStructure.getPostalCode();
//        holder.address_card.setText(address);
//        holder.buildingType_card.setText(propertyStructure.getBuildingType());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return propertyList.size();
//    }
//
//    public static class MyPropertyViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView house_image_card;
//        TextView house_name_card;
//        TextView address_card;
//        TextView buildingType_card;
//
//        public MyPropertyViewHolder(@NonNull @NotNull View itemView) {
//            super(itemView);
//            house_image_card = itemView.findViewById(R.id.property_card_image);
//            house_name_card = itemView.findViewById(R.id.property_card_house_name);
//            address_card = itemView.findViewById(R.id.property_card_address);
//            buildingType_card = itemView.findViewById(R.id.property_card_building_type);
//
//            itemView.findViewById(R.id.update_property_button).setOnClickListener(v -> {
////                PropertyStructure property_to_change = propertyList.get(getAdapterPosition());
////                Intent intent = new Intent(context,UpdateHouseDetails.class);
////                intent.putExtra("property_to_change",property_to_change);
////                context.startActivity(intent);
//            });
//        }
//
//    }
}
