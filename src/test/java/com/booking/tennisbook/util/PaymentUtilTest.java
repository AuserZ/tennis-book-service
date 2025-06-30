// package com.booking.tennisbook.util;

// import com.booking.tennisbook.dto.payment.*;
// import com.booking.tennisbook.repository.PaymentMethodRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.http.MediaType;
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
// import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
// import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
// import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
// import reactor.core.publisher.Mono;

// import java.util.Collections;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// public class PaymentUtilTest {
//     @Mock
//     private PaymentMethodRepository paymentMethodRepository;
//     @Mock
//     private WebClient webClient;
//     @Mock
//     private WebClient.RequestBodyUriSpec requestBodyUriSpec;
//     @Mock
//     private WebClient.RequestHeadersSpec requestHeadersSpec;
//     @Mock
//     private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//     @Mock
//     private WebClient.ResponseSpec responseSpec;
//     @InjectMocks
//     private PaymentUtil paymentUtil;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         paymentUtil = new PaymentUtil(paymentMethodRepository);
//         // Inject the mocked webClient
//         TestUtils.setField(paymentUtil, "webClient", webClient);
//     }

//     @Test
//     void testProcessPayment_success() throws Exception {
//         DokuPaymentRequest request = new DokuPaymentRequest();
//         // Build a realistic PaymentDokuResponse
//         OrderData orderData = new OrderData("100000", "INV-123456", "IDR", "SESSION-1");
//         PaymentData paymentData = new PaymentData(
//                 java.util.Arrays.asList("VIRTUAL_ACCOUNT_BCA", "EMONEY_OVO"),
//                 10,
//                 "token-123",
//                 "https://payment.doku.com/redirect",
//                 "2024-12-31T23:59:59+07:00"
//         );
//         OriginData originData = new OriginData("TennisBook", "BookingSystem", "v1", "web");
//         AdditionalInfo additionalInfo = new AdditionalInfo(originData);
//         HeadersData headersData = new HeadersData("req-123", "sig-abc", "2024-06-01T12:00:00+07:00", "client-001");
//         PaymentResponseData responseData = new PaymentResponseData(
//                 orderData,
//                 paymentData,
//                 additionalInfo,
//                 123456789L,
//                 headersData
//         );
//         PaymentDokuResponse expectedResponse = new PaymentDokuResponse(
//                 java.util.Collections.singletonList("SUCCESS"),
//                 responseData
//         );

//         // Mock fetchAccessToken
//         TestUtils.setField(paymentUtil, "dokuClientId", "test-client-id");
//         TestUtils.setField(paymentUtil, "dokuClientSecret", "test-client-secret");
//         TestUtils.setField(paymentUtil, "dokuPaymentApi", "http://mock-api");
//         TestUtils.setField(paymentUtil, "dokuAuthApi", "http://mock-auth");

//         // Mock WebClient for executePaymentRequest
//         when(webClient.post()).thenReturn(requestBodyUriSpec);
//         when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
//         when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
//         when(requestBodyUriSpec.bodyValue(anyString())).thenReturn(requestHeadersSpec);
//         when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//         when(responseSpec.bodyToMono(eq(PaymentDokuResponse.class))).thenReturn(Mono.just(expectedResponse));

//         // Mock WebClient for fetchAccessToken
//         when(requestBodyUriSpec.bodyValue(anyString())).thenReturn(requestHeadersSpec);
//         when(responseSpec.bodyToMono(eq(Object.class))).thenReturn(Mono.just(java.util.Collections.singletonMap("access_token", "mock-token")));

//         PaymentDokuResponse response = paymentUtil.processPayment(request);
//         System.out.print(response);
//         assertNotNull(response);
//         assertEquals("SUCCESS", response.getMessage().get(0));
//         assertNotNull(response.getResponse());
//         assertEquals("100000", response.getResponse().getOrder().getAmount());
//         assertEquals("INV-123456", response.getResponse().getOrder().getInvoiceNumber());
//         assertEquals("IDR", response.getResponse().getOrder().getCurrency());
//         assertEquals("SESSION-1", response.getResponse().getOrder().getSessionId());
//         assertEquals("token-123", response.getResponse().getPayment().getTokenId());
//         assertEquals("https://payment.doku.com/redirect", response.getResponse().getPayment().getUrl());
//         assertEquals("TennisBook", response.getResponse().getAdditionalInfo().getOrigin().getProduct());
//         assertEquals("req-123", response.getResponse().getHeaders().getRequestId());
//         assertEquals(123456789L, response.getResponse().getUuid());
//     }
// }

// // TestUtils for reflection field injection
// class TestUtils {
//     static void setField(Object target, String fieldName, Object value) {
//         try {
//             java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
//             field.setAccessible(true);
//             field.set(target, value);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }
// } 