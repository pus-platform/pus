package com.ez.pus.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Custom exception handling class for the application.
 */
public class CustomExceptionHandling extends RuntimeException {

    /**
     * Exception thrown when a resource is not found.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResourceNotFoundException extends RuntimeException{
        private String resourceName;
        private String fieldName;
        private Object fieldValue;

        /**
         * Constructor for the ResourceNotFoundException.
         *
         * @param resourceName The name of the resource.
         * @param fieldName The name of the field.
         * @param fieldValue The value of the field.
         */
        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s not found with '%s': %s", resourceName, fieldName, fieldValue));
            this.resourceName = resourceName;
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }
    }

    /**
     * Exception thrown when an email already exists.
     */
    public static class EmailAlreadyExistsException extends RuntimeException{
        /**
         * Constructor for the EmailAlreadyExistsException.
         *
         * @param message The exception message.
         */
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a username already exists.
     */
    public static class UserNameAlreadyExistsException extends RuntimeException{
        /**
         * Constructor for the UserNameAlreadyExistsException.
         *
         * @param message The exception message.
         */
        public UserNameAlreadyExistsException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an invalid argument is provided.
     */
    public static class InvalidArgumentException extends RuntimeException {
        /**
         * Constructor for the InvalidArgumentException.
         *
         * @param message The exception message.
         */
        public InvalidArgumentException(String message) {
            super(message);
        }
    }

}
