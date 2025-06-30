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
    }

    public DokuPaymentRequest buildDokuRequest(Booking booking, User user){
        
        logger.info("[START] Build Doku Request");
        DokuPaymentRequest dokuPaymentRequest = new DokuPaymentRequest();

        logger.info("[START] Build Order");
        OrderDoku orderDoku = new OrderDoku();
        orderDoku.setAmount(booking.getTotalPrice());
        orderDoku.setInvoiceNumber(invoiceBuilder(booking));
        orderDoku.setCurrency("IDR");

        logger.info("[START] Build Line Items");
        List<LineItemsDoku> lineItems = new ArrayList<>();
        LineItemsDoku bookingSession = new LineItemsDoku(booking.getSession().getId(), booking.getSession().getCoach().getName(), booking.getSession().getTennisField().getName(), booking.getSession().getStartTime(), booking.getSession().getEndTime(), booking.getSession().getDate(),booking.getSession().getType(), booking.getParticipants(), booking.getTotalPrice(), "service");
        logger.info("[END] Build Line Items");

        lineItems.add(bookingSession);
        orderDoku.setLineItems(lineItems);
        logger.info("[END] Build Order");

        logger.info("[START] Build Payment");
        PaymentDoku paymentDoku = new PaymentDoku();
        paymentDoku.setPayment_due_date(10);
        paymentDoku.setType("SALE");

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
        List<String> paymentMethodTypes = paymentMethods.stream()
                .filter(method -> method.getPaymentMethodType() != null)
                .map(method -> method.getPaymentMethodType().name())
                .toList();

        paymentDoku.setPayment_method_types(paymentMethodTypes);
        logger.info("[END] Build Payment");

        logger.info("[START] Build Customer");
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setId(user.getId());
        customerDetails.setEmail(user.getEmail());
        customerDetails.setName(user.getName());
        logger.info("[END] Build Customer");

        dokuPaymentRequest.setOrder(orderDoku);
        dokuPaymentRequest.setPayment(paymentDoku);
        dokuPaymentRequest.setCustomer(customerDetails);
        
        logger.info("[End] Build Doku Request");

        return dokuPaymentRequest;
    }

    public PaymentDokuResponse processPayment(DokuPaymentRequest paymentRequest) {
        try {
            String minifiedJson = objectMapper.writeValueAsString(paymentRequest);
            String accessToken = fetchAccessToken();
            String timestamp = generateTimestamp();
            String signature = createSignature(minifiedJson, accessToken, timestamp);
            PaymentDokuResponse response = executePaymentRequest(minifiedJson, accessToken, timestamp, signature);
            return response;
        } catch (Exception e) {
            logger.error("Error processing DOKU payment", e);
            throw new RuntimeException("Failed to process DOKU payment", e);
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
        // DOKU uses Basic Auth with clientId:clientSecret base64 encoded
        String basicAuth = Base64.getEncoder().encodeToString((dokuClientId + ":" + dokuClientSecret).getBytes(StandardCharsets.UTF_8));
        Map<String, String> body = Map.of("grantType", "client_credentials");
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
            throw new RuntimeException("Failed to fetch DOKU access token: unexpected response");
        }
        Object token = response.get("access_token");
        if (token == null) {
            throw new RuntimeException("Failed to fetch DOKU access token: missing access_token");
        }
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
        StringBuilder invoiceNumber = new StringBuilder();

        invoiceNumber.append("INV-");
        invoiceNumber.append(booking.getId() + "-");
        invoiceNumber.append(booking.getSession().getType() + "-");
        invoiceNumber.append(booking.getSession().getId());

        return invoiceNumber.toString();
    }

    public String sessionTypeConverter(String type) throws Exception {
        if (isEmpty(type))
            throw new Exception();

        return type.equals("Private") ? "PRV" : "PBL";
    }
}
