package com.HiveGroup.HiveRH.Common.Utils.Exceptions;

import lombok.Getter;

import java.time.Instant;
import java.util.NoSuchElementException;

@Getter
public class EntityNotFoundException extends NoSuchElementException {
    private final Instant timestamp;
    private final String entity;

    public EntityNotFoundException(String message, String entity) {
        super(message);
        this.entity = entity;
        this.timestamp = Instant.now();
    }
}
