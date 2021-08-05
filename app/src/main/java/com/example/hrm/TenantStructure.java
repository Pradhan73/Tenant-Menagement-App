package com.example.hrm;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class TenantStructure implements Serializable {

    String firstName,lastName,phone,email,pendingDues,tenantImage;
    @Exclude String tenantId;

    public TenantStructure(String firstName, String lastName, String phone, String email, String pendingDues, String tenantImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.pendingDues = pendingDues;
        this.tenantImage = tenantImage;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantImage() {
        return tenantImage;
    }

    public void setTenantImage(String tenantImage) {
        this.tenantImage = tenantImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPendingDues() {
        return pendingDues;
    }

    public void setPendingDues(String pendingDues) {
        this.pendingDues = pendingDues;
    }
}
