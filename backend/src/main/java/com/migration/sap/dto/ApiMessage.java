package com.migration.sap.dto;

/**
 * Replaces the BAPIRET2/add_message macro pattern from LZFG_MAT_SOTOP.txt lines 32-39.
 */
public class ApiMessage {

    private String type;
    private String id;
    private String number;
    private String message;

    public ApiMessage() {}

    public ApiMessage(String type, String id, String number, String message) {
        this.type = type;
        this.id = id;
        this.number = number;
        this.message = message;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
