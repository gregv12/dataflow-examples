package com.fluxtion.dataflow.examples;

import com.fluxtion.dataflow.Fluxtion;
import com.fluxtion.dataflow.builder.DataFlowBuilder;
import com.fluxtion.dataflow.examples.temp_monitoring.*;
import com.fluxtion.dataflow.runtime.DataFlow;
import com.fluxtion.dataflow.runtime.flowfunction.aggregate.function.primitive.DoubleAverageFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupBy;
import com.fluxtion.dataflow.runtime.time.FixedRateTrigger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class JmhBenchamrk {

    private static final LongAdder counter = new LongAdder();

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 2)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void throughPut_No_BranchingProcessor( Blackhole blackhole, TempMonitorJmhStata tempMonitorJmhStata) throws InterruptedException {
//        priceLadderState.priceProcessorNoBranch.newPriceLadder(priceLadderState.nextPriceLadder());
        blackhole.consume(tempMonitorJmhStata.nextPriceLadder());
    }

    @State(Scope.Thread)
    public static class TempMonitorJmhStata {
        private final String[] MACHINE_IDS = new String[]{"server_GOOG", "server_AMZN", "server_MSFT", "server_TKM"};
        private final Random random = new Random();
        private DataFlow tempMonitor;

        @Setup(Level.Trial)
        public void doSetup() {
            System.out.println("Setup temp monitor");

            counter.reset();

            tempMonitor = Fluxtion.compile(c -> {

                var currentMachineTemp = DataFlowBuilder.groupBy(MachineReadingEvent::id, MachineReadingEvent::temp);

                var avgMachineTemp = DataFlowBuilder.subscribe(MachineReadingEvent.class)
                        .groupBySliding(MachineReadingEvent::id, MachineReadingEvent::temp, DoubleAverageFlowFunction::new, 1000, 4);

                var tempMonitor = DataFlowBuilder.groupBy(MachineProfileEvent::id)
                        .mapValues(MachineState::new)
                        .mapBi(DataFlowBuilder.groupBy(SupportContactEvent::locationCode), Helpers::addContact)
                        .innerJoin(currentMachineTemp, MachineState::setCurrentTemperature)
                        .innerJoin(avgMachineTemp, MachineState::setAvgTemperature)
                        .publishTriggerOverride(FixedRateTrigger.atMillis(1_000))
                        .filterValues(MachineState::outsideOperatingTemp)
                        .map(GroupBy::toMap)
                        .map(new AlarmDeltaFilter()::updateActiveAlarms)
                        .filter(AlarmDeltaFilter::isChanged)
                        .sink("alarmPublisher");
//                        .build();
            });
            tempMonitor.init();

            //set up machine locations
            tempMonitor.onEvent(new MachineProfileEvent("server_GOOG", LocationCode.USA_EAST_1, 70, 48));
            tempMonitor.onEvent(new MachineProfileEvent("server_AMZN", LocationCode.USA_EAST_1, 99.999, 65));
            tempMonitor.onEvent(new MachineProfileEvent("server_MSFT", LocationCode.USA_EAST_2, 92, 49.99));
            tempMonitor.onEvent(new MachineProfileEvent("server_TKM", LocationCode.USA_EAST_2, 102, 50.0001));

            //set up support contacts
            tempMonitor.onEvent(new SupportContactEvent("Jean", LocationCode.USA_EAST_1, "jean@fluxtion.com"));
            tempMonitor.onEvent(new SupportContactEvent("Tandy", LocationCode.USA_EAST_2, "tandy@fluxtion.com"));

            //setup output
            tempMonitor.addSink("alarmPublisher", JmhBenchamrk::countUpdates);
        }

        public Object nextPriceLadder() {
            String machineId = MACHINE_IDS[random.nextInt(MACHINE_IDS.length)];
            double temperatureReading = random.nextDouble() * 100;
            tempMonitor.onEvent(new MachineReadingEvent(machineId, temperatureReading));
            return tempMonitor;
        }

        @TearDown(Level.Iteration)
        public void doTearDown() {
            System.out.println("TearDown - updated alarms: " + counter.longValue());
        }
    }

    private static void countUpdates(Object o) {
        counter.increment();
    }
}
