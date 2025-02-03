package com.fluxtion.dataflow.examples.temp_monitoring.benchmark;

import com.fluxtion.dataflow.DataFlowCompiler;
import com.fluxtion.dataflow.examples.temp_monitoring.StreamProcessorBuilder;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.LocationCode;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineProfileEvent;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineReadingEvent;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.SupportContactEvent;
import com.fluxtion.dataflow.runtime.DataFlow;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.StringWriter;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class MonitoringCompiledBenchmark {

    private static final LongAdder counter = new LongAdder();

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 2)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void throughPut_FunctionalProcessor( Blackhole blackhole, TempMonitorJmhStata tempMonitorJmhStata) throws InterruptedException {
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

            tempMonitor = DataFlowCompiler.compile(StreamProcessorBuilder::buildMachineMonitoring);
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
            tempMonitor.addSink("alarmPublisher", MonitoringCompiledBenchmark::countUpdates);
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
