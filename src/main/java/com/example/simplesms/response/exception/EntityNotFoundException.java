package com.example.simplesms.response.exception;

public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException() {
        super(ErrorCode.COMMON_ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(String message) {
        super(message, ErrorCode.COMMON_ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue), ErrorCode.COMMON_ENTITY_NOT_FOUND);
    }
}
