package com.fluxtion.dataflow.examples.frontpage;

import com.fluxtion.dataflow.builder.DataFlowBuilder;
import com.fluxtion.dataflow.runtime.DataFlow;
import com.fluxtion.dataflow.runtime.flowfunction.helpers.Aggregates;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WindowExample {
    record CarTracker(String id, double speed) {}

    public static void main(String[] args) {
        //calculate average speed, sliding window 5 buckets of 200 millis
        DataFlow averageCarSpeed = DataFlowBuilder.subscribe(CarTracker::speed)
                .slidingAggregate(Aggregates.doubleAverageFactory(), 200, 5)
                .sink("average car speed")
                .build();

        //register an output sink
        averageCarSpeed.addSink("average car speed", System.out::println);

        //send data from an unbounded real-time feed
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> averageCarSpeed.onEvent(new CarTracker("car-reg", new Random().nextDouble(100))),
                100, 100, TimeUnit.MILLISECONDS);
    }
}
