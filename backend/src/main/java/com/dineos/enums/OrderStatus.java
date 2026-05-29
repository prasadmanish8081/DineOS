package com.dineos.enums;

public enum OrderStatus {
    PLACED,
    ACCEPTED,
    PREPARING,
    READY,
    SERVED,
    COMPLETED,
    CANCELLED;

    public boolean isActive() {
        return this != COMPLETED && this != CANCELLED;
    }
}
