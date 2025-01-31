package com.fluxtion.dataflow.examples.temp_monitoring;

import com.fluxtion.dataflow.builder.DataFlowBuilder;
import com.fluxtion.dataflow.builder.flowfunction.FlowBuilder;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.*;
import com.fluxtion.dataflow.runtime.flowfunction.aggregate.function.primitive.DoubleAverageFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupBy;
import com.fluxtion.dataflow.runtime.time.FixedRateTrigger;


/**
 * Build a rela time event processor that consumes event streams to monitor and alarm machines in a data centre. </br>
 * <br>
 * Monitors each machine for an average or current temperature breach in a sliding window of 4 seconds with a bucket size of 1 second
 * readings are produced randomly every mircosecond the aggregation handles all combining values within a window and dropping
 * values that have expired.<br>
 * <br>
 * Alarm status is published on any change to the alarm state, i.e. new alarms or cleared old alarms<br>
 * <br>
 * Each machine can have its own temperature alarm profile updated by event MachineProfile<br>
 * <br>
 * Notifies a support contact in the correct location where the breach has occurred. The contact lookup is built up
 * through events:
 * <ul>
 *     <li>MachineLocation = Machine id -> location</li>
 *     <li>SupportContact = location -> contact details</li>
 * </ul>
 * <br>
 * <br>
 * <p>
 * A sink is available for the host application to consume the alarm output, in this case a pretty print consumer<br>
 * <br>
 **/

public interface StreamProcessorBuilder {

    static FlowBuilder<AlarmDeltaFilter> buildMachineMonitoring() {

        //create a stream machine temps grouped by machine id
        var currentMachineTemp = DataFlowBuilder.groupBy(
                MachineReadingEvent::id, MachineReadingEvent::temp);

        //create a stream of averaged machine sliding temps,
        //with a 4-second window and 1 second buckets grouped by machine id
        var avgMachineTemp = DataFlowBuilder.subscribe(MachineReadingEvent.class)
                .groupBySliding(
                        MachineReadingEvent::id,
                        MachineReadingEvent::temp,
                        DoubleAverageFlowFunction::new,
                        1000,
                        4);

        //join machine profiles with contacts and then with readings.
        //Publish alarms with stateful user function
        var tempMonitor = DataFlowBuilder.groupBy(MachineProfileEvent::id)
                .mapValues(MachineState::new)
                .mapBi(
                        DataFlowBuilder.groupBy(SupportContactEvent::locationCode),
                        Helpers::addContact)
                .innerJoin(currentMachineTemp, MachineState::setCurrentTemperature)
                .innerJoin(avgMachineTemp, MachineState::setAvgTemperature)
                .publishTriggerOverride(FixedRateTrigger.atMillis(1_000))
                .filterValues(MachineState::outsideOperatingTemp)
                .map(GroupBy::toMap)
                .map(new AlarmDeltaFilter()::updateActiveAlarms)
                .filter(AlarmDeltaFilter::isChanged)
                .sink("alarmPublisher");

        return tempMonitor;
    }
}
