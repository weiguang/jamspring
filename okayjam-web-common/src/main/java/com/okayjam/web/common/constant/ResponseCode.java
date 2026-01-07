package com.okayjam.web.common.constant;


/**
 * rms-controller
 *
 * @author JamChen
 *         2023/05/09 11:41
 **/
public enum ResponseCode {
    /**
     * 200 OK - 请求成功返回。
     */
    OK(0, "OK"),
    NOT_FOUND(404, "Not Found"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),

    DEFAULT_ERROR(1000, "Default Error");

    private static final ResponseCode[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;

    private final String reason;

    ResponseCode(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public static ResponseCode resolve(int statusCode) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (ResponseCode status : VALUES) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
