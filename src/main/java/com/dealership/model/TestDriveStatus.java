package com.dealership.model;

public enum TestDriveStatus {
    SCHEDULED("Planlandı"),
    COMPLETED("Tamamlandı"),
    CANCELLED("İptal Edildi");

    private final String displayName;
    TestDriveStatus(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
    @Override public String toString() { return displayName; }
}
