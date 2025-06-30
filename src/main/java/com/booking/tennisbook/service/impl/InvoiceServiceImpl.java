package com.booking.tennisbook.service.impl;

import com.booking.tennisbook.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private static final String DEFAULT_PREFIX = "INV";
    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final String INVOICE_FORMAT = "%s-%s-%04d";
    
    // Atomic counter to ensure thread-safe incrementing
    private final AtomicInteger dailyCounter = new AtomicInteger(1);
    private String lastDate = "";
    
    @Override
    public String generateInvoiceNumber() {
        return generateInvoiceNumber(DEFAULT_PREFIX);
    }
    
    @Override
    public String generateInvoiceNumber(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        
        // Reset counter if it's a new day
        if (!currentDate.equals(lastDate)) {
            dailyCounter.set(1);
            lastDate = currentDate;
            logger.debug("Reset daily counter for new date: {}", currentDate);
        }
        
        // Get the next sequence number
        int sequenceNumber = dailyCounter.getAndIncrement();
        
        // Generate invoice number
        String invoiceNumber = String.format(INVOICE_FORMAT, prefix, currentDate, sequenceNumber);
        
        logger.debug("Generated invoice number: {}", invoiceNumber);
        return invoiceNumber;
    }
} 