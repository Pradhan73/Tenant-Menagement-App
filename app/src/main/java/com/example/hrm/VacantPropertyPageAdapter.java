//package com.example.hrm;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//
//public class VacantPropertyPageAdapter extends RecyclerView.Adapter<VacantPropertyPageAdapter.MyVacantPropertyViewHolder>{
//
//    private ArrayList<PropertyStructure> vacant_list;
//    private Context context;
//
//    public VacantPropertyPageAdapter(Context context, ArrayList<PropertyStructure> vacantPropertyList) {
//        this.context = context;
//        this.vacant_list = vacantPropertyList;
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public VacantPropertyPageAdapter.MyVacantPropertyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.vacantpropertypagecarditems, parent, false);
//        return new VacantPropertyPageAdapter.MyVacantPropertyViewHolder(v);
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull VacantPropertyPageAdapter.MyVacantPropertyViewHolder holder, int position) {
//        PropertyStructure propertyStructure = vacant_list.get(position);
//
////        String image_uri = propertyStructure.getImage_url();
////        Uri uri = Uri.parse(image_uri);
////        holder.house_image_card.setImageURI(uri);
//        Glide.with(context).load(vacant_list.get(position).getImage_url()).into(holder.house_image_card);
//        holder.house_name_card.setText(propertyStructure.getHouseName());
//        String address = propertyStructure.getLandmark() + "," + propertyStructure.getCity() + "," + propertyStructure.getStateProvince() + "," + propertyStructure.getPostalCode();
//        holder.address_card.setText(address);
//        holder.buildingType_card.setText(propertyStructure.getBuildingType());
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return vacant_list.size();
//    }
//
//    public class MyVacantPropertyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        ImageView house_image_card;
//        TextView house_name_card;
//        TextView address_card;
//        TextView buildingType_card;
//        Button update_house;
//        Button enter_tenant_details;
//
//
//        public MyVacantPropertyViewHolder(@NonNull @NotNull View itemView) {
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
//            PropertyStructure property_to_change = vacant_list.get(getAdapterPosition());
//            Intent intent = new Intent(context,UpdateHouseDetails.class);
//            intent.putExtra("property_to_change",property_to_change);
//            context.startActivity(intent);
//        }
//    }
//}



