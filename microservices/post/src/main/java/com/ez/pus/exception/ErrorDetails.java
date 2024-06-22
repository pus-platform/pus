package com.ez.pus.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Class representing the details of an error.
 * This class is used to structure the information about an error that occurred.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    /**
     * The timestamp when the error occurred.
     */
    private LocalDateTime timeStamp;

    /**
     * The message describing the error.
     */
    private String message;

    /**
     * The path where the error occurred.
     */
    private String path;

    /**
     * The error code associated with the error.
     */
    private String errorCode;
}
