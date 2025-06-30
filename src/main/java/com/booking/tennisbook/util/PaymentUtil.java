package com.booking.tennisbook.util;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.booking.tennisbook.dto.payment.CustomerDetails;
import com.booking.tennisbook.dto.payment.DokuPaymentRequest;
import com.booking.tennisbook.dto.payment.LineItemsDoku;
import com.booking.tennisbook.dto.payment.OrderDoku;
import com.booking.tennisbook.dto.payment.PaymentDoku;
import com.booking.tennisbook.dto.payment.PaymentDokuResponse;
import com.booking.tennisbook.model.Booking;
import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.model.User;
import com.booking.tennisbook.repository.PaymentMethodRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentUtil {
    Logger logger = LoggerFactory.getLogger(PaymentUtil.class);

    private final PaymentMethodRepository paymentMethodRepository;

    @Value("${doku.payment.api}")
    private String dokuPaymentApi;
    @Value("${doku.auth.api}")
    private String dokuAuthApi;
    @Value("${doku.client.id}")
    private String dokuClientId;
    @Value("${doku.client.secret}")
    private String dokuClientSecret;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PaymentUtil(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public DokuPaymentRequest buildDokuRequest(Booking booking, User user){
        
        logger.info("[START] Building DOKU payment request for booking ID: {}, user ID: {}", booking.getId(), user.getId());
        DokuPaymentRequest dokuPaymentRequest = new DokuPaymentRequest();

        logger.info("[START] Building order data for booking ID: {}", booking.getId());
        OrderDoku orderDoku = new OrderDoku();
        orderDoku.setAmount(booking.getTotalPrice());
        orderDoku.setInvoiceNumber(invoiceBuilder(booking));
        orderDoku.setCurrency("IDR");
        logger.info("[END] Order data built - Amount: {}, Invoice: {}, Currency: {}", 
                   booking.getTotalPrice(), orderDoku.getInvoiceNumber(), orderDoku.getCurrency());

        // logger.info("[START] Building line items for session ID: {}", booking.getSession().getId());
        // List<LineItemsDoku> lineItems = new ArrayList<>();
        // LineItemsDoku bookingSession = new LineItemsDoku(booking.getSession().getId(), booking.getSession().getCoach().getName(), booking.getSession().getTennisField().getName(), booking.getSession().getStartTime(), booking.getSession().getEndTime(), booking.getSession().getDate(),booking.getSession().getType(), booking.getParticipants(), booking.getTotalPrice(), "service");
        // logger.info("[END] Line items built - Session: {}, Coach: {}, Field: {}, Type: {}, Participants: {}", 
        //            booking.getSession().getId(), booking.getSession().getCoach().getName(), 
        //            booking.getSession().getTennisField().getName(), booking.getSession().getType(), 
        //            booking.getParticipants());

        // lineItems.add(bookingSession);
        // orderDoku.setLineItems(lineItems);
        logger.info("[END] Order building completed");

        logger.info("[START] Building payment configuration");
        PaymentDoku paymentDoku = new PaymentDoku();
        paymentDoku.setPayment_due_date(10);
        paymentDoku.setType("SALE");

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
        List<String> paymentMethodTypes = paymentMethods.stream()
                .filter(method -> method.getPaymentMethodType() != null)
                .map(method -> method.getPaymentMethodType().name())
                .toList();

        paymentDoku.setPayment_method_types(paymentMethodTypes);
        logger.info("[END] Payment configuration built - Due date: {}, Type: {}, Available methods: {}", 
                   paymentDoku.getPayment_due_date(), paymentDoku.getType(), paymentMethodTypes.size());

        logger.info("[START] Building customer details for user: {}", user.getEmail());
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setId(user.getId());
        customerDetails.setEmail(user.getEmail());
        customerDetails.setName(user.getName());
        logger.info("[END] Customer details built - ID: {}, Email: {}, Name: {}", 
                   customerDetails.getId(), customerDetails.getEmail(), customerDetails.getName());

        dokuPaymentRequest.setOrder(orderDoku);
        dokuPaymentRequest.setPayment(paymentDoku);
        dokuPaymentRequest.setCustomer(customerDetails);
        
        logger.info("[END] DOKU payment request built successfully for booking ID: {}", booking.getId());

        return dokuPaymentRequest;
    }

    // Commented out the old processPayment method
    /*
    public PaymentDokuResponse processPayment(DokuPaymentRequest paymentRequest) {
        long startTime = System.currentTimeMillis();
        logger.info("[START] Processing DOKU payment request");
        
        try {
            logger.debug("Serializing payment request to JSON");
            String minifiedJson = objectMapper.writeValueAsString(paymentRequest);
            logger.debug("Payment request JSON: {}", minifiedJson);
            
            logger.info("Fetching DOKU access token");
            String accessToken = fetchAccessToken();
            logger.debug("Access token retrieved successfully");
            
            logger.info("Generating timestamp for request");
            String timestamp = generateTimestamp();
            logger.debug("Timestamp generated: {}", timestamp);
            
            logger.info("Creating request signature");
            String signature = createSignature(minifiedJson, accessToken, timestamp);
            logger.debug("Signature created successfully");
            
            logger.info("Executing payment request to DOKU API");
            PaymentDokuResponse response = executePaymentRequest(minifiedJson, accessToken, timestamp, signature);
            
            long endTime = System.currentTimeMillis();
            logger.info("[END] DOKU payment processed successfully in {}ms", (endTime - startTime));
            logger.debug("Payment response: {}", response);
            
            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[ERROR] Failed to process DOKU payment after {}ms", (endTime - startTime), e);
            throw new RuntimeException("Failed to process DOKU payment", e);
        }
    }
    */

    public PaymentDokuResponse processPaymentCheckout(DokuPaymentRequest paymentRequest) {
        long startTime = System.currentTimeMillis();
        logger.info("[START] Processing DOKU Checkout payment request");
        
        try {
            logger.debug("Serializing payment request to JSON");
            String minifiedJson = objectMapper.writeValueAsString(paymentRequest);
            logger.debug("Payment request JSON: {}", minifiedJson);
            
            logger.info("Generating timestamp for request");
            String timestamp = generateTimestamp();
            logger.debug("Timestamp generated: {}", timestamp);
            
            logger.info("Creating request signature");
            String signature = createCheckoutSignature(minifiedJson, timestamp);
            logger.debug("Signature created successfully");
            
            logger.info("Executing payment request to DOKU Checkout API");
            PaymentDokuResponse response = executeCheckoutPaymentRequest(minifiedJson, timestamp, signature);
            
            long endTime = System.currentTimeMillis();
            logger.info("[END] DOKU Checkout payment processed successfully in {}ms", (endTime - startTime));
            logger.debug("Payment response: {}", response);
            
            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[ERROR] Failed to process DOKU Checkout payment after {}ms", (endTime - startTime), e);
            throw new RuntimeException("Failed to process DOKU Checkout payment", e);
        }
    }

    private String createCheckoutSignature(String requestBody, String timestamp) {
        logger.debug("Creating DOKU Checkout signature");
        
        // According to DOKU Checkout documentation, signature is HMACSHA256
        // The signature should be created using the request body and timestamp
        String stringToSign = requestBody + timestamp;
        return "HMACSHA256=" + hmacSha256Base64(stringToSign, dokuClientSecret);
    }

    private PaymentDokuResponse executeCheckoutPaymentRequest(String requestBody, String timestamp, String signature) {
        logger.debug("Executing DOKU Checkout payment request");
        
        return webClient.post()
                .uri(dokuPaymentApi)
                .header("Client-Id", dokuClientId)
                .header("Request-Id", generateRequestId())
                .header("Request-Timestamp", timestamp)
                .header("Signature", signature)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PaymentDokuResponse.class)
                .block();
    }

    private static String hmacSha256Base64(String data, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(keySpec);
            byte[] macData = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(macData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }

    private String generateTimestamp() {
        return ZonedDateTime.now(ZoneId.of("Asia/Jakarta"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private String createSignature(String minifiedJson, String accessToken, String timestamp) {
        String endpointPath = "/checkout/v1/payment";
        String httpMethod = "POST";
        String bodyHash = sha256Hex(minifiedJson);
        String stringToSign = String.format("%s:%s:%s:%s:%s",
                httpMethod,
                endpointPath,
                accessToken,
                bodyHash,
                timestamp
        );
        return hmacSha512Base64(stringToSign, dokuClientSecret);
    }

    private PaymentDokuResponse executePaymentRequest(String minifiedJson, String accessToken, String timestamp, String signature) {
        return webClient.post()
                .uri(dokuPaymentApi)
                .header("Authorization", "Bearer " + accessToken)
                .header("X-TIMESTAMP", timestamp)
                .header("X-SIGNATURE", signature)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Client-Id", dokuClientId)
                .header("Request-Id", generateRequestId())
                .bodyValue(minifiedJson)
                .retrieve()
                .bodyToMono(PaymentDokuResponse.class)
                .block();
    }

    private String generateRequestId(){
        return UUID.randomUUID().toString();
    }

    private String fetchAccessToken() throws Exception {
        logger.debug("Starting DOKU access token fetch");
        
        // DOKU uses Basic Auth with clientId:clientSecret base64 encoded
        String basicAuth = Base64.getEncoder().encodeToString((dokuClientId + ":" + dokuClientSecret).getBytes(StandardCharsets.UTF_8));
        Map<String, String> body = Map.of("grantType", "client_credentials");
        
        logger.debug("Making authentication request to DOKU auth API: {}", dokuAuthApi);
        
        // DOKU expects application/json
        Object rawResponse = webClient.post()
                .uri(dokuAuthApi)
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(objectMapper.writeValueAsString(body))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
                
        if (!(rawResponse instanceof Map<?, ?> response)) {
            logger.error("Unexpected response type from DOKU auth API: {}", rawResponse.getClass().getSimpleName());
            throw new RuntimeException("Failed to fetch DOKU access token: unexpected response");
        }
        
        Object token = response.get("access_token");
        if (token == null) {
            logger.error("Missing access_token in DOKU auth response: {}", response);
            throw new RuntimeException("Failed to fetch DOKU access token: missing access_token");
        }
        
        logger.debug("Successfully retrieved DOKU access token");
        return token.toString();
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private static String hmacSha512Base64(String data, String secret) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(macData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA512", e);
        }
    }

    public String invoiceBuilder(Booking booking) {
        logger.debug("Building invoice number for booking ID: {}", booking.getId());
        
        StringBuilder invoiceNumber = new StringBuilder();

        invoiceNumber.append("INV-");
        invoiceNumber.append(booking.getId() + "-");
        invoiceNumber.append(booking.getSession().getType() + "-");
        invoiceNumber.append(booking.getSession().getId());

        String result = invoiceNumber.toString();
        logger.debug("Generated invoice number: {} for booking ID: {}", result, booking.getId());
        return result;
    }

    public String sessionTypeConverter(String type) throws Exception {
        logger.debug("Converting session type: {}", type);
        
        if (isEmpty(type)) {
            logger.error("Session type is empty or null");
            throw new Exception("Session type cannot be empty");
        }

        String result = type.equals("Private") ? "PRV" : "PBL";
        logger.debug("Converted session type from '{}' to '{}'", type, result);
        return result;
    }
}
