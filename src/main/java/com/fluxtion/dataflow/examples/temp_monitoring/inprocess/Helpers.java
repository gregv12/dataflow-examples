package com.fluxtion.dataflow.examples.temp_monitoring.inprocess;


import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupBy;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public interface Helpers {

    DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    static GroupBy<String, MachineState> addContact(
            GroupBy<String, MachineState> machineStates,
            GroupBy<LocationCode, SupportContactEvent> supportContacts) {
        machineStates.toMap().forEach((id, state) ->
                state.setSupportContactEvent(supportContacts.toMap().get(state.getLocationCode()))
        );
        return machineStates;
    }

    static void prettyPrintAlarms(AlarmDeltaFilter alarmDeltaFilter) {
        String newAlarms = alarmDeltaFilter.getNewAlarms().values().stream()
                .map(m -> String.format("'%s@%s',  temp:'%.2f', avgTemp:'%.2f' %s",
                        m.getId(),
                        m.getLocationCode(),
                        m.getCurrentTemperature(),
                        m.getAvgTemperature(),
                        m.getSupportContactEvent()))
                .collect(Collectors.joining(", ", "New alarms: [", "]"));

        String alarmsToClear = alarmDeltaFilter.getAlarmsToClear().stream().collect(Collectors.joining(", ", "Alarms to clear[", "]"));
        String existingToClear = alarmDeltaFilter.getActiveAlarms().keySet().stream().collect(Collectors.joining(", ", "Current alarms[", "]"));
        System.out.println("ALARM UPDATE " + timeColonFormatter.format(LocalTime.now()));
        System.out.println(newAlarms);
        System.out.println(alarmsToClear);
        System.out.println(existingToClear);
        System.out.println("------------------------------------\n");
    }
}
