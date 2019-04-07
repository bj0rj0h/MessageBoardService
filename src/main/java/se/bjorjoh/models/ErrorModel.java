package se.bjorjoh.models;

public class ErrorModel {

    private String message;
    private String timestamp;

    public ErrorModel(String message,String timestamp){
        this.message=message;
        this.timestamp=timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
