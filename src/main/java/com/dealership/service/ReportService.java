package com.dealership.service;

import com.dealership.model.InteractionEvent;
import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String, Object>> generateSalesReport(Map<String, String> filters);
    double generateSalesForecast(List<Double> pastSales, int n);
    List<InteractionEvent> generateCustomerInteractionReport(String customerId, Map<String, String> filters, CustomerService customerService);
    Map<String, Double> getSalesPercentageByModel(StockService stockService);
    List<Map<String, Object>> generateDetailedVehicleSalesReport();
}
