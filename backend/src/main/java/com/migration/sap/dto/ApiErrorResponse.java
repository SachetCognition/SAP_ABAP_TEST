package com.migration.sap.dto;

import java.util.List;

/**
 * Generic error response DTO used by GlobalExceptionHandler.
 * Works for both MaterialStock and SalesOrder error responses.
 */
public class ApiErrorResponse {

    private int count;
    private List<ApiMessage> messages;

    public ApiErrorResponse() {}

    public ApiErrorResponse(int count, List<ApiMessage> messages) {
        this.count = count;
        this.messages = messages;
    }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public List<ApiMessage> getMessages() { return messages; }
    public void setMessages(List<ApiMessage> messages) { this.messages = messages; }
}
