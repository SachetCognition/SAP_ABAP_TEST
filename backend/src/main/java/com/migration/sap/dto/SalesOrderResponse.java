package com.migration.sap.dto;

import java.util.ArrayList;
import java.util.List;

public class SalesOrderResponse {

    private List<SalesOrderDTO> data;
    private int count;
    private List<ApiMessage> messages;

    public SalesOrderResponse() {
        this.data = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public SalesOrderResponse(List<SalesOrderDTO> data, int count, List<ApiMessage> messages) {
        this.data = data;
        this.count = count;
        this.messages = messages;
    }

    public List<SalesOrderDTO> getData() { return data; }
    public void setData(List<SalesOrderDTO> data) { this.data = data; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public List<ApiMessage> getMessages() { return messages; }
    public void setMessages(List<ApiMessage> messages) { this.messages = messages; }
}
