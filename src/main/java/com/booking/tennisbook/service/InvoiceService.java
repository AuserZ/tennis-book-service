package com.booking.tennisbook.service;

public interface InvoiceService {
    /**
     * Generates a unique invoice number
     * @return A unique invoice number string
     */
    String generateInvoiceNumber();
    
    /**
     * Generates an invoice number with a specific prefix
     * @param prefix The prefix to use for the invoice number
     * @return A unique invoice number string with the specified prefix
     */
    String generateInvoiceNumber(String prefix);
} 