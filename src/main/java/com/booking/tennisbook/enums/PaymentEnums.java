package com.booking.tennisbook.enums;

/**
 * Global enums for payment-related constants
 */
public class PaymentEnums {

    /**
     * Payment types supported by Doku
     */
    public enum PaymentType {
        SALE("SALE"),
        AUTHORIZE("AUTHORIZE");

        private final String value;

        PaymentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Payment method types supported by Doku
     */
    public enum PaymentMethodType {
        // Virtual Accounts
        VIRTUAL_ACCOUNT_BCA("VIRTUAL_ACCOUNT_BCA"),
        VIRTUAL_ACCOUNT_BANK_MANDIRI("VIRTUAL_ACCOUNT_BANK_MANDIRI"),
        VIRTUAL_ACCOUNT_BANK_SYARIAH_MANDIRI("VIRTUAL_ACCOUNT_BANK_SYARIAH_MANDIRI"),
        VIRTUAL_ACCOUNT_DOKU("VIRTUAL_ACCOUNT_DOKU"),
        VIRTUAL_ACCOUNT_BRI("VIRTUAL_ACCOUNT_BRI"),
        VIRTUAL_ACCOUNT_BNI("VIRTUAL_ACCOUNT_BNI"),
        VIRTUAL_ACCOUNT_BANK_PERMATA("VIRTUAL_ACCOUNT_BANK_PERMATA"),
        VIRTUAL_ACCOUNT_BANK_CIMB("VIRTUAL_ACCOUNT_BANK_CIMB"),
        VIRTUAL_ACCOUNT_BANK_DANAMON("VIRTUAL_ACCOUNT_BANK_DANAMON"),
        VIRTUAL_ACCOUNT_BNC("VIRTUAL_ACCOUNT_BNC"),
        VIRTUAL_ACCOUNT_BTN("VIRTUAL_ACCOUNT_BTN"),
        VIRTUAL_ACCOUNT_MAYBANK("VIRTUAL_ACCOUNT_MAYBANK"),
        VIRTUAL_ACCOUNT_SINARMAS("VIRTUAL_ACCOUNT_SINARMAS"),

        // Online to Offline
        ONLINE_TO_OFFLINE_ALFA("ONLINE_TO_OFFLINE_ALFA"),
        ONLINE_TO_OFFLINE_INDOMARET("ONLINE_TO_OFFLINE_INDOMARET"),

        // Credit Cards
        CREDIT_CARD("CREDIT_CARD"),

        // Direct Debit
        DIRECT_DEBIT_CIMB("DIRECT_DEBIT_CIMB"),
        DIRECT_DEBIT_BRI("DIRECT_DEBIT_BRI"),
        DIRECT_DEBIT_ALLO("DIRECT_DEBIT_ALLO"),

        // E-Money
        EMONEY_OVO("EMONEY_OVO"),
        EMONEY_DOKU("EMONEY_DOKU"),
        EMONEY_LINKAJA("EMONEY_LINKAJA"),
        EMONEY_SHOPEE_PAY("EMONEY_SHOPEE_PAY"),
        EMONEY_DANA("EMONEY_DANA"),
        EMONEY_SHOPEEPAY("EMONEY_SHOPEEPAY"),

        // E-Pay
        EPAY_BRI("EPAY_BRI"),

        // Peer to Peer
        PEER_TO_PEER_KREDIVO("PEER_TO_PEER_KREDIVO"),
        PEER_TO_PEER_INDODANA("PEER_TO_PEER_INDODANA"),
        PEER_TO_PEER_BRI_CERIA("PEER_TO_PEER_BRI_CERIA"),
        PEER_TO_PEER_AKULAKU("PEER_TO_PEER_AKULAKU"),

        // Other Payment Methods
        JENIUS_PAY("JENIUS_PAY"),
        OCTO_CLICKS("OCTO_CLICKS"),
        PERMATA_NET("PERMATA_NET"),
        KLIKPAY_BCA("KLIKPAY_BCA"),
        DANAMON_ONLINE_BANKING("DANAMON_ONLINE_BANKING"),
        QRIS("QRIS");

        private final String value;

        PaymentMethodType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * Get enum from string value
         */
        public static PaymentMethodType fromValue(String value) {
            for (PaymentMethodType type : PaymentMethodType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown payment method type: " + value);
        }
    }

    /**
     * Payment status enums
     */
    public enum PaymentStatus {
        PENDING("PENDING"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED"),
        REFUNDED("REFUNDED"),
        EXPIRED("EXPIRED"),
        CANCELLED("CANCELLED");

        private final String value;

        PaymentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Currency types
     */
    public enum Currency {
        IDR("IDR"),
        USD("USD"),
        EUR("EUR");

        private final String value;

        Currency(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Default payment configuration
     */
    public static class PaymentDefaults {
        public static final int DEFAULT_PAYMENT_DUE_DATE = 10; // 60 minutes
        public static final PaymentType DEFAULT_PAYMENT_TYPE = PaymentType.SALE;
        public static final Currency DEFAULT_CURRENCY = Currency.IDR;
    }
} 