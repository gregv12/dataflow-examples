package com.fluxtion.dataflow.examples.temp_monitoring;

public record MachineProfileEvent(String id,
                                  LocationCode locationCode,
                                  double maxTempAlarm,
                                  double maxAvgTempAlarm) {
}
