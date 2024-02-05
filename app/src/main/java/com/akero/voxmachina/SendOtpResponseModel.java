package com.akero.voxmachina;

public class SendOtpResponseModel {
    private String message;
    private int status;

    public SendOtpResponseModel(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Optional: Override toString for a better representation if required.
    @Override
    public String toString() {
        return "SendOtpResponseModel{" +
                "message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
