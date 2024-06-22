package com.ez.pus.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class CustomExceptionHandling extends RuntimeException {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ResourceNotFoundException extends RuntimeException{
        private String resourceName;
        private String fieldName;
        private Object fieldValue;

        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s not found with '%s': %s", resourceName, fieldName, fieldValue));
            this.resourceName = resourceName;
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }
    }

    public static class EmailAlreadyExistsException extends RuntimeException{
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class UserNameAlreadyExistsException extends RuntimeException{
        public UserNameAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidArgumentException extends RuntimeException {
        public InvalidArgumentException(String message) {
            super(message);
        }
    }

}
