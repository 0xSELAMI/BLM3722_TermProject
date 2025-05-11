package com.dealership.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InteractionEvent {
    private String eventId;
    private String customerIdFk;
    private InteractionEventType eventType;
    private LocalDateTime eventDate;
    private String vehicleChassisNumberFk;
    private String details;

    public InteractionEvent(String eventId, String customerIdFk, InteractionEventType eventType, LocalDateTime eventDate, String vehicleChassisNumberFk, String details) {
        this.eventId = eventId;
        this.customerIdFk = customerIdFk;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.vehicleChassisNumberFk = vehicleChassisNumberFk;
        this.details = details;
    }

    public String getEventId() { return eventId; }
    public String getCustomerIdFk() { return customerIdFk; }
    public InteractionEventType getEventType() { return eventType; }
    public LocalDateTime getEventDate() { return eventDate; }
    public String getVehicleChassisNumberFk() { return vehicleChassisNumberFk; }
    public String getDetails() { return details; }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Etkileşim [ID=" + eventId+ ", Tür=" + eventType.getDisplayName() + ", Tarih=" + eventDate.format(formatter) +
               (vehicleChassisNumberFk != null ? ", Araç Şase No=" + vehicleChassisNumberFk : "") +
               ", Detay=" + details + "]";
    }
}
