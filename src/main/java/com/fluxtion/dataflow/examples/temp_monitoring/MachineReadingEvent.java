package com.fluxtion.dataflow.examples.temp_monitoring;

//INPUT EVENTS
public record MachineReadingEvent(String id, double temp) {
}
