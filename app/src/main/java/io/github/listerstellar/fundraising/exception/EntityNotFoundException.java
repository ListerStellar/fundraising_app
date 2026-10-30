package io.github.listerstellar.fundraising.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, String entityId) {
        super(entityName + " (" + entityId + ") not found");
    }
}
