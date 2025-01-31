package com.fluxtion.dataflow.examples.temp_monitoring.inprocess;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = false, chain = true)
public class MachineState {

    private String id;
    private LocationCode locationCode;
    private double currentTemperature = Double.NaN;
    private double avgTemperature = Double.NaN;
    private double maxAvgTemperature;
    private double maxCurrentTemperature;
    private SupportContactEvent supportContactEvent;

    public MachineState(MachineProfileEvent machineProfileEvent) {
        this.id = machineProfileEvent.id();
        this.locationCode = machineProfileEvent.locationCode();
        this.maxCurrentTemperature = machineProfileEvent.maxTempAlarm();
        this.maxAvgTemperature = machineProfileEvent.maxAvgTempAlarm();
    }

    public Boolean outsideOperatingTemp() {
        return currentTemperature > maxCurrentTemperature || avgTemperature > maxAvgTemperature;
    }
}
