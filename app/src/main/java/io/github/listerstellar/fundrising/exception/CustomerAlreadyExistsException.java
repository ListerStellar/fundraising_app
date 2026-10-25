package io.github.listerstellar.fundrising.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String username) {
        super("Customer (" + username + ") already exists");
    }
}
