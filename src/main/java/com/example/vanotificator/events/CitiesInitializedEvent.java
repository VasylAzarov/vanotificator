package com.example.vanotificator.events;

import org.springframework.context.ApplicationEvent;

public class CitiesInitializedEvent extends ApplicationEvent {
    public CitiesInitializedEvent(Object source) {
        super(source);
    }
}