package calitei.parking.api.error;

import java.util.Date;

public class ExceptionResponse {

    private final String message;
    private final Date timestamp;
    private final int httpCode;

    public ExceptionResponse(String message, Date timestamp, int httpCode) {
        super();
        this.message = message;
        this.timestamp = timestamp;
        this.httpCode = httpCode;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getHttpCode() {
        return httpCode;
    }

}