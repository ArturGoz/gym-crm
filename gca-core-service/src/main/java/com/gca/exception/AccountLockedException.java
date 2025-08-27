package com.gca.exception;

public class AccountLockedException extends RuntimeException {
    private static final String MESSAGE = "Account locked due to too many failed login attempts";

    public AccountLockedException() {
        super(MESSAGE);
    }
}
