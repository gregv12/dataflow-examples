package com.fluxtion.dataflow.examples.temp_monitoring.inprocess;

//INPUT EVENTS
public record MachineReadingEvent(String id, double temp) {
}
