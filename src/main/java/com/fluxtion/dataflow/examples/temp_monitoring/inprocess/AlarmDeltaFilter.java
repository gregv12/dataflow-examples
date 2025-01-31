package com.fluxtion.dataflow.examples.temp_monitoring.inprocess;

import com.fluxtion.dataflow.runtime.annotations.AfterEvent;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@ToString
public class AlarmDeltaFilter {

    private transient final Set<String> alarmsToClear = new HashSet<>();
    private transient final Map<String, MachineState> activeAlarms = new HashMap<>();
    private transient final Map<String, MachineState> newAlarms = new HashMap<>();
    private boolean changed;

    public AlarmDeltaFilter updateActiveAlarms(Map<String, MachineState> updatedActiveAlarms) {
        changed = !activeAlarms.keySet().equals(updatedActiveAlarms.keySet());

        alarmsToClear.clear();
        alarmsToClear.addAll(activeAlarms.keySet());
        alarmsToClear.removeAll(updatedActiveAlarms.keySet());

        newAlarms.clear();
        newAlarms.putAll(updatedActiveAlarms);
        activeAlarms.keySet().forEach(newAlarms::remove);

        activeAlarms.clear();
        activeAlarms.putAll(updatedActiveAlarms);
        return this;
    }

    @AfterEvent
    public void purgerNewAlarms() {
        alarmsToClear.clear();
        newAlarms.clear();
        changed = false;
    }

    public static class Helpers {
    }
}
