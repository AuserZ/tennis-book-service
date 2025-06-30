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

import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
    @Value("${doku.callback.url}")
    private String dokuCallbackUrl;
    @Value("${doku.callback.url.cancel}")
    private String dokuCallbackUrlCancel;
    @Value("${doku.callback.url.result}")
    private String dokuCallbackUrlResult;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PaymentUtil(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public DokuPaymentRequest buildDokuRequest(Booking booking, User user) {
        logger.info("[START] Building DOKU payment request for booking ID: {}, user ID: {}", booking.getId(),
                user.getId());

        DokuPaymentRequest dokuPaymentRequest = new DokuPaymentRequest();

        logger.info("[START] Building order data for booking ID: {}", booking.getId());
        OrderDoku orderDoku = new OrderDoku();
        orderDoku.setAmount(booking.getTotalPrice().intValue());
        orderDoku.setInvoice_number(invoiceBuilder(booking));
        orderDoku.setCurrency("IDR");
        
        logger.info("[END] Order data built - Amount: {}, Invoice: {}, Currency: {}",
                booking.getTotalPrice(), orderDoku.getInvoice_number(), orderDoku.getCurrency());

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

    public PaymentDokuResponse processPaymentCheckout(DokuPaymentRequest paymentRequest) {
        long startTime = System.currentTimeMillis();
        logger.info("[START] Processing DOKU Checkout payment request");

        try {
            String minifiedJson = objectMapper.writeValueAsString(paymentRequest);
            String requestId = generateRequestId();
            String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            String signature = createCheckoutSignature(dokuClientId, requestId, timestamp, minifiedJson);

            String responseBody = webClient.post()
                    .uri(dokuPaymentApi)
                    .header("Client-Id", dokuClientId)
                    .header("Request-Id", requestId)
                    .header("Request-Timestamp", timestamp)
                    .header("Signature", signature)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(paymentRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            long endTime = System.currentTimeMillis();
            logger.info("[END] DOKU Checkout payment processed in {}ms", (endTime - startTime));
            logger.info("Raw response: {}", responseBody);

            if (responseBody == null || responseBody.isBlank()) {
                logger.error("DOKU returned an empty response body!");
                throw new RuntimeException("DOKU returned an empty response body!");
            }

            try {
                return objectMapper.readValue(responseBody, PaymentDokuResponse.class);
            } catch (Exception parseEx) {
                logger.error("Jackson parsing error: ", parseEx);
                logger.error("DOKU error or unexpected response: {}", responseBody);
                throw new RuntimeException("DOKU error or unexpected response: " + responseBody);
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[ERROR] Failed to process DOKU Checkout payment after {}ms", (endTime - startTime), e);
            throw new RuntimeException("Exception: " + e.getMessage(), e);
        }
    }

    private String createCheckoutSignature(String clientId, String requestId, String timestamp, String requestBody) {
        try {
            // 1. Digest: Base64(SHA256(requestBody))
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(requestBody.getBytes(StandardCharsets.UTF_8));
            String digestBase64 = Base64.getEncoder().encodeToString(hash);

            // 2. Build stringToSign as per DOKU spec
            String stringToSign = "Client-Id:" + clientId + "\n" +
                    "Request-Id:" + requestId + "\n" +
                    "Request-Timestamp:" + timestamp + "\n" +
                    "Request-Target:/checkout/v1/payment\n" +
                    "Digest:" + digestBase64;

            // 3. HMACSHA256 and Base64 encode
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(dokuClientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(keySpec);
            byte[] macData = sha256Hmac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String signatureBase64 = Base64.getEncoder().encodeToString(macData);
            return "HMACSHA256=" + signatureBase64;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DOKU Checkout signature", e);
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
                timestamp);
        return hmacSha512Base64(stringToSign, dokuClientSecret);
    }

    private PaymentDokuResponse executePaymentRequest(String minifiedJson, String accessToken, String timestamp,
            String signature) {
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

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    private String fetchAccessToken() throws Exception {
        logger.debug("Starting DOKU access token fetch");

        // DOKU uses Basic Auth with clientId:clientSecret base64 encoded
        String basicAuth = Base64.getEncoder()
                .encodeToString((dokuClientId + ":" + dokuClientSecret).getBytes(StandardCharsets.UTF_8));
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
                if (hex.length() == 1)
                    hexString.append('0');
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
        invoiceNumber.append(sessionTypeConverter(booking.getSession().getType()) + "-");
        invoiceNumber.append(booking.getSession().getId());
        invoiceNumber.append(UUID.randomUUID());

        String result = invoiceNumber.toString();
        logger.debug("Generated invoice number: {} for booking ID: {}", result, booking.getId());
        return result;
    }

    public String sessionTypeConverter(String type) {
        logger.debug("Converting session type: {}", type);

        String result = type.equals("Private") ? "PRV" : "PBL";
        logger.debug("Converted session type from '{}' to '{}'", type, result);
        return result;
    }
}
