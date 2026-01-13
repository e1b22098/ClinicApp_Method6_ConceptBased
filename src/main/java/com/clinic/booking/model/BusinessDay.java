package com.clinic.booking.model;

import java.time.LocalDate;

/**
 * 営業日エンティティ
 */
public class BusinessDay {
    private Integer businessDayId;
    private LocalDate businessDate;
    private Boolean isAccepting;
    private String timeSlots; // JSON形式で時間枠を保存（例: "09:00,10:00,11:00,14:00,15:00,16:00"）

    // Getters and Setters
    public Integer getBusinessDayId() {
        return businessDayId;
    }

    public void setBusinessDayId(Integer businessDayId) {
        this.businessDayId = businessDayId;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public Boolean getIsAccepting() {
        return isAccepting;
    }

    public void setIsAccepting(Boolean isAccepting) {
        this.isAccepting = isAccepting;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(String timeSlots) {
        this.timeSlots = timeSlots;
    }
}
