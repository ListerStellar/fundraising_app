package io.github.listerstellar.fundrising.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, String entityId) {
        super(entityName + " (" + entityId + ") not found");
    }
}
