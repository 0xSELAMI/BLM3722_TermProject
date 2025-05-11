package com.dealership.model;

public enum InteractionEventType {
    INITIAL_VISIT("İlk Ziyaret"),
    STOCK_INQUIRY("Stok Sorgulama"),
    TEST_DRIVE_SCHEDULED("Test Sürüşü Planlandı"),
    TEST_DRIVE_COMPLETED("Test Sürüşü Tamamlandı"),
    QUOTE_REQUESTED("Fiyat Teklifi İstendi"),
    QUOTE_GIVEN("Fiyat Teklifi Verildi"),
    ORDER_PLACED("Sipariş Verildi"),
    VEHICLE_DELIVERED("Araç Teslim Edildi"),
    OTHER("Diğer");

    private final String displayName;
    InteractionEventType(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
    @Override public String toString() { return displayName; }
}
