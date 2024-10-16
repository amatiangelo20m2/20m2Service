package com.ventimetriconsulting.branch.exception.customexceptions;

public class OrderNotFound extends RuntimeException {
    public OrderNotFound(String message) {
        super(message);
    }
}