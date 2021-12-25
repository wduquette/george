package com.wjduquette.george.model;

import com.wjduquette.george.model.Interrupt;

public class InterruptException extends RuntimeException {
    private final Interrupt interrupt;
    public InterruptException(Interrupt interrupt) {
        super(interrupt.toString());
        this.interrupt = interrupt;
    }

    public Interrupt get() {
        return interrupt;
    }
}
