package com.dealership.model;

public enum OrderStatus {
    PLACED("Sipariş Verildi"),
    PAYMENT_PENDING("Ödeme Bekleniyor"),
    PAID("Ödendi"),
    DELIVERY_SCHEDULED("Teslimat Planlandı"),
    DELIVERED("Teslim Edildi"),
    CANCELLED("İptal Edildi");

    private final String displayName;
    OrderStatus(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
    @Override public String toString() { return displayName; }
}
