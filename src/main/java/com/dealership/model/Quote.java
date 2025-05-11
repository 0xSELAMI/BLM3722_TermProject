package com.dealership.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Quote {
    private String quoteId;
    private String customerIdFk;
    private String vehicleChassisNumberFk;
    private LocalDateTime quoteDate;
    private double price;
    private LocalDate validUntil;

    public Quote(String quoteId, String customerIdFk, String vehicleChassisNumberFk, LocalDateTime quoteDate, double price, LocalDate validUntil) {
        this.quoteId = quoteId;
        this.customerIdFk = customerIdFk;
        this.vehicleChassisNumberFk = vehicleChassisNumberFk;
        this.quoteDate = quoteDate;
        this.price = price;
        this.validUntil = validUntil;
    }

    public String getQuoteId() { return quoteId; }
    public String getCustomerIdFk() { return customerIdFk; }
    public String getVehicleChassisNumberFk() { return vehicleChassisNumberFk; }
    public LocalDateTime getQuoteDate() { return quoteDate; }
    public double getPrice() { return price; }
    public LocalDate getValidUntil() { return validUntil; }

    @Override
    public String toString() {
        return "Fiyat Teklifi [ID=" + quoteId + ", Müşteri ID=" + customerIdFk +
               (vehicleChassisNumberFk != null ? ", Araç Şase No=" + vehicleChassisNumberFk : "") +
               ", Tarih=" + quoteDate.format(DateTimeFormatter.ISO_DATE) +
               ", Fiyat=" + price +
               ", Geçerlilik=" + validUntil.format(DateTimeFormatter.ISO_DATE) + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quote quote = (Quote) obj;
        return quoteId != null ? quoteId.equals(quote.quoteId) : quote.quoteId == null;
    }

    @Override
    public int hashCode() {
        return quoteId != null ? quoteId.hashCode() : 0;
    }
}
