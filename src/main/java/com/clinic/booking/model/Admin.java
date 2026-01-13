package com.clinic.booking.model;

/**
 * 管理者エンティティ
 */
public class Admin {
    private Integer adminId;
    private String adminIdString;
    private String password;

    // Getters and Setters
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminIdString() {
        return adminIdString;
    }

    public void setAdminIdString(String adminIdString) {
        this.adminIdString = adminIdString;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
