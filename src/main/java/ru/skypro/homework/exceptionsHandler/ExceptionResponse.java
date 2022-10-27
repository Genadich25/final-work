package ru.skypro.homework.exceptionsHandler;

import java.time.LocalDateTime;

/**
 * Class of response to exception
 */
public class ExceptionResponse {

    private String message;

    private LocalDateTime dateTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}