package com.dealership.service;

import com.dealership.model.Quote;
import java.time.LocalDate;
import java.util.List;

public interface QuoteService {
    Quote createQuote(String customerId, String vehicleChassisNumber, double price, LocalDate validUntil);
    Quote findQuoteById(String quoteId);
    List<Quote> getAllQuotes();
}
