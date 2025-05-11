package com.dealership.model;

public enum VehicleStatus {
    IN_STOCK("Stokta"),
    IN_SHOWROOM_DISPLAY("Showroom (Gösterim)"),
    IN_SHOWROOM_ORDER("Showroom (Sipariş)"),
    RESERVED("Rezerve Edilmiş"),
    SOLD("Satıldı");

    private final String displayName;
    VehicleStatus(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
    @Override public String toString() { return displayName; }
}
