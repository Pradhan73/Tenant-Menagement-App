package com.example.hrm;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class PropertyStructure implements Serializable {

    String buildingType,postalCode,stateProvince,city,landmark,houseName,vacancy,image_url;
    @Exclude String propertyId;

    public PropertyStructure(String buildingType,String postalCode,String stateProvince,String city,String landmark,String houseName,String vacancy,String image_url) {

        this.buildingType =buildingType;
        this.postalCode = postalCode;
        this.stateProvince  = stateProvince;
        this.city = city;
        this.landmark = landmark;
        this.houseName = houseName;
        this.vacancy = vacancy;
        this.image_url = image_url;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
