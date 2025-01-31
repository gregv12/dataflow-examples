
package com.fluxtion.dataflow.examples.temp_monitoring.benchmark.generated;

import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.AlarmDeltaFilter;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.Helpers;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineProfileEvent;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineReadingEvent;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineState;
import com.fluxtion.dataflow.examples.temp_monitoring.inprocess.SupportContactEvent;
import com.fluxtion.dataflow.runtime.CloneableDataFlow;
import com.fluxtion.dataflow.runtime.DataFlow;
import com.fluxtion.dataflow.runtime.annotations.ExportService;
import com.fluxtion.dataflow.runtime.annotations.OnEventHandler;
import com.fluxtion.dataflow.runtime.audit.Auditor;
import com.fluxtion.dataflow.runtime.audit.EventLogManager;
import com.fluxtion.dataflow.runtime.audit.NodeNameAuditor;
import com.fluxtion.dataflow.runtime.callback.CallbackDispatcherImpl;
import com.fluxtion.dataflow.runtime.callback.ExportFunctionAuditEvent;
import com.fluxtion.dataflow.runtime.callback.InternalEventProcessor;
import com.fluxtion.dataflow.runtime.context.DataFlowContext;
import com.fluxtion.dataflow.runtime.event.Event;
import com.fluxtion.dataflow.runtime.flowfunction.aggregate.function.AggregateIdentityFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.aggregate.function.primitive.DoubleAverageFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.function.BinaryMapFlowFunction.BinaryMapToRefFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.function.FilterFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.function.MapFlowFunction.MapRef2RefFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.function.PushFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupBy;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupBy.EmptyGroupBy;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupByFilterFlowFunctionWrapper;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupByFlowFunctionWrapper;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupByMapFlowFunction;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.GroupByTimedSlidingWindow;
import com.fluxtion.dataflow.runtime.flowfunction.groupby.InnerJoin;
import com.fluxtion.dataflow.runtime.flowfunction.helpers.Mappers;
import com.fluxtion.dataflow.runtime.flowfunction.helpers.Tuples.MapTuple;
import com.fluxtion.dataflow.runtime.input.EventFeed;
import com.fluxtion.dataflow.runtime.input.SubscriptionManager;
import com.fluxtion.dataflow.runtime.input.SubscriptionManagerNode;
import com.fluxtion.dataflow.runtime.lifecycle.BatchHandler;
import com.fluxtion.dataflow.runtime.lifecycle.Lifecycle;
import com.fluxtion.dataflow.runtime.node.DefaultEventHandlerNode;
import com.fluxtion.dataflow.runtime.node.ForkedTriggerTask;
import com.fluxtion.dataflow.runtime.node.MutableDataFlowContext;
import com.fluxtion.dataflow.runtime.output.SinkDeregister;
import com.fluxtion.dataflow.runtime.output.SinkPublisher;
import com.fluxtion.dataflow.runtime.output.SinkRegistration;
import com.fluxtion.dataflow.runtime.service.ServiceListener;
import com.fluxtion.dataflow.runtime.service.ServiceRegistryNode;
import com.fluxtion.dataflow.runtime.time.Clock;
import com.fluxtion.dataflow.runtime.time.ClockStrategy.ClockStrategyEvent;
import com.fluxtion.dataflow.runtime.time.FixedRateTrigger;
import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 *
 *
 * <pre>
 * generation time                 : Not available
 * eventProcessorGenerator version : ${generator_version_information}
 * api version                     : ${api_version_information}
 * </pre>
 *
 * Event classes supported:
 *
 * <ul>
 *   <li>com.fluxtion.dataflow.builder.generation.model.ExportFunctionMarker
 *   <li>com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineProfileEvent
 *   <li>com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineReadingEvent
 *   <li>com.fluxtion.dataflow.examples.temp_monitoring.inprocess.SupportContactEvent
 *   <li>com.fluxtion.dataflow.runtime.output.SinkDeregister
 *   <li>com.fluxtion.dataflow.runtime.output.SinkRegistration
 *   <li>com.fluxtion.dataflow.runtime.time.ClockStrategy.ClockStrategyEvent
 *   <li>java.lang.Object
 * </ul>
 *
 * @author Greg Higgins
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Processor
        implements CloneableDataFlow<Processor>,
        /*--- @ExportService start ---*/
        @ExportService ServiceListener,
        /*--- @ExportService end ---*/
        DataFlow,
        InternalEventProcessor,
        BatchHandler {

    //Node declarations
    private final transient AlarmDeltaFilter alarmDeltaFilter_21 = new AlarmDeltaFilter();
    private final transient CallbackDispatcherImpl callbackDispatcher = new CallbackDispatcherImpl();
    public final transient Clock clock = new Clock();
    private final transient FixedRateTrigger fixedRateTrigger_26 = new FixedRateTrigger(clock, 1000);
    private final transient FixedRateTrigger fixedRateTrigger_66 = new FixedRateTrigger(clock, 1000);
    private final transient GroupByFilterFlowFunctionWrapper groupByFilterFlowFunctionWrapper_18 =
            new GroupByFilterFlowFunctionWrapper(MachineState::outsideOperatingTemp);
    private final transient GroupByFlowFunctionWrapper groupByFlowFunctionWrapper_0 =
            new GroupByFlowFunctionWrapper<>(
                    MachineReadingEvent::id, MachineReadingEvent::temp, AggregateIdentityFlowFunction::new);
    private final transient GroupByFlowFunctionWrapper groupByFlowFunctionWrapper_3 =
            new GroupByFlowFunctionWrapper<>(
                    MachineProfileEvent::id, Mappers::identity, AggregateIdentityFlowFunction::new);
    private final transient GroupByFlowFunctionWrapper groupByFlowFunctionWrapper_7 =
            new GroupByFlowFunctionWrapper<>(
                    SupportContactEvent::locationCode, Mappers::identity, AggregateIdentityFlowFunction::new);
    private final transient GroupByMapFlowFunction groupByMapFlowFunction_5 =
            new GroupByMapFlowFunction(MachineState::new);
    private final transient InnerJoin innerJoin_10 = new InnerJoin();
    private final transient InnerJoin innerJoin_14 = new InnerJoin();
    private final transient MapTuple mapTuple_116 = new MapTuple<>(MachineState::setAvgTemperature);
    private final transient GroupByMapFlowFunction groupByMapFlowFunction_16 =
            new GroupByMapFlowFunction(mapTuple_116::mapTuple);
    private final transient MapTuple mapTuple_119 =
            new MapTuple<>(MachineState::setCurrentTemperature);
    private final transient GroupByMapFlowFunction groupByMapFlowFunction_12 =
            new GroupByMapFlowFunction(mapTuple_119::mapTuple);
    public final transient NodeNameAuditor nodeNameLookup = new NodeNameAuditor();
    private final transient SubscriptionManagerNode subscriptionManager =
            new SubscriptionManagerNode();
    private final transient MutableDataFlowContext context =
            new MutableDataFlowContext(
                    nodeNameLookup, callbackDispatcher, subscriptionManager, callbackDispatcher);
    private final transient SinkPublisher alarmPublisher = new SinkPublisher<>("alarmPublisher");
    private final transient DefaultEventHandlerNode handlerMachineProfileEvent =
            new DefaultEventHandlerNode<>(
                    2147483647,
                    "",
                    com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineProfileEvent.class,
                    "handlerMachineProfileEvent",
                    context);
    private final transient DefaultEventHandlerNode handlerMachineReadingEvent =
            new DefaultEventHandlerNode<>(
                    2147483647,
                    "",
                    com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineReadingEvent.class,
                    "handlerMachineReadingEvent",
                    context);
    private final transient GroupByTimedSlidingWindow groupByTimedSlidingWindow_2 =
            new GroupByTimedSlidingWindow<>(
                    handlerMachineReadingEvent,
                    DoubleAverageFlowFunction::new,
                    MachineReadingEvent::id,
                    MachineReadingEvent::temp,
                    1000,
                    4);
    private final transient DefaultEventHandlerNode handlerSupportContactEvent =
            new DefaultEventHandlerNode<>(
                    2147483647,
                    "",
                    com.fluxtion.dataflow.examples.temp_monitoring.inprocess.SupportContactEvent.class,
                    "handlerSupportContactEvent",
                    context);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_1 =
            new MapRef2RefFlowFunction<>(
                    handlerMachineReadingEvent, groupByFlowFunctionWrapper_0::aggregate);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_4 =
            new MapRef2RefFlowFunction<>(
                    handlerMachineProfileEvent, groupByFlowFunctionWrapper_3::aggregate);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_6 =
            new MapRef2RefFlowFunction<>(mapRef2RefFlowFunction_4, groupByMapFlowFunction_5::mapValues);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_8 =
            new MapRef2RefFlowFunction<>(
                    handlerSupportContactEvent, groupByFlowFunctionWrapper_7::aggregate);
    private final transient BinaryMapToRefFlowFunction binaryMapToRefFlowFunction_9 =
            new BinaryMapToRefFlowFunction<>(
                    mapRef2RefFlowFunction_6, mapRef2RefFlowFunction_8, Helpers::addContact);
    private final transient BinaryMapToRefFlowFunction binaryMapToRefFlowFunction_11 =
            new BinaryMapToRefFlowFunction<>(
                    binaryMapToRefFlowFunction_9, mapRef2RefFlowFunction_1, innerJoin_10::join);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_13 =
            new MapRef2RefFlowFunction<>(
                    binaryMapToRefFlowFunction_11, groupByMapFlowFunction_12::mapValues);
    private final transient BinaryMapToRefFlowFunction binaryMapToRefFlowFunction_15 =
            new BinaryMapToRefFlowFunction<>(
                    mapRef2RefFlowFunction_13, groupByTimedSlidingWindow_2, innerJoin_14::join);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_17 =
            new MapRef2RefFlowFunction<>(
                    binaryMapToRefFlowFunction_15, groupByMapFlowFunction_16::mapValues);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_19 =
            new MapRef2RefFlowFunction<>(
                    mapRef2RefFlowFunction_17, groupByFilterFlowFunctionWrapper_18::filterValues);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_20 =
            new MapRef2RefFlowFunction<>(mapRef2RefFlowFunction_19, GroupBy<Object, Object>::toMap);
    private final transient MapRef2RefFlowFunction mapRef2RefFlowFunction_22 =
            new MapRef2RefFlowFunction<>(
                    mapRef2RefFlowFunction_20, alarmDeltaFilter_21::updateActiveAlarms);
    private final transient FilterFlowFunction filterFlowFunction_23 =
            new FilterFlowFunction<>(mapRef2RefFlowFunction_22, AlarmDeltaFilter::isChanged);
    private final transient PushFlowFunction pushFlowFunction_24 =
            new PushFlowFunction<>(filterFlowFunction_23, alarmPublisher::publish);
    public final transient ServiceRegistryNode serviceRegistry = new ServiceRegistryNode();
    private final transient ExportFunctionAuditEvent functionAudit = new ExportFunctionAuditEvent();
    //Dirty flags
    private boolean initCalled = false;
    private boolean processing = false;
    private boolean buffering = false;
    private final transient IdentityHashMap<Object, BooleanSupplier> dirtyFlagSupplierMap =
            new IdentityHashMap<>(21);
    private final transient IdentityHashMap<Object, Consumer<Boolean>> dirtyFlagUpdateMap =
            new IdentityHashMap<>(21);

    private boolean isDirty_binaryMapToRefFlowFunction_9 = false;
    private boolean isDirty_binaryMapToRefFlowFunction_11 = false;
    private boolean isDirty_binaryMapToRefFlowFunction_15 = false;
    private boolean isDirty_clock = false;
    private boolean isDirty_filterFlowFunction_23 = false;
    private boolean isDirty_fixedRateTrigger_26 = false;
    private boolean isDirty_fixedRateTrigger_66 = false;
    private boolean isDirty_groupByTimedSlidingWindow_2 = false;
    private boolean isDirty_handlerMachineProfileEvent = false;
    private boolean isDirty_handlerMachineReadingEvent = false;
    private boolean isDirty_handlerSupportContactEvent = false;
    private boolean isDirty_mapRef2RefFlowFunction_1 = false;
    private boolean isDirty_mapRef2RefFlowFunction_4 = false;
    private boolean isDirty_mapRef2RefFlowFunction_6 = false;
    private boolean isDirty_mapRef2RefFlowFunction_8 = false;
    private boolean isDirty_mapRef2RefFlowFunction_13 = false;
    private boolean isDirty_mapRef2RefFlowFunction_17 = false;
    private boolean isDirty_mapRef2RefFlowFunction_19 = false;
    private boolean isDirty_mapRef2RefFlowFunction_20 = false;
    private boolean isDirty_mapRef2RefFlowFunction_22 = false;
    private boolean isDirty_pushFlowFunction_24 = false;

    //Forked declarations

    //Filter constants

    //unknown event handler
    private Consumer unKnownEventHandler = (e) -> {};

    public Processor(Map<Object, Object> contextMap) {
        if (context != null) {
            context.replaceMappings(contextMap);
        }
        binaryMapToRefFlowFunction_9.setDataFlowContext(context);
        binaryMapToRefFlowFunction_9.setDefaultValue(new EmptyGroupBy());
        binaryMapToRefFlowFunction_11.setDataFlowContext(context);
        binaryMapToRefFlowFunction_11.setDefaultValue(new EmptyGroupBy());
        binaryMapToRefFlowFunction_15.setDataFlowContext(context);
        binaryMapToRefFlowFunction_15.setDefaultValue(new EmptyGroupBy());
        filterFlowFunction_23.setDataFlowContext(context);
        mapRef2RefFlowFunction_1.setDataFlowContext(context);
        mapRef2RefFlowFunction_1.setDefaultValue(new EmptyGroupBy());
        mapRef2RefFlowFunction_4.setDataFlowContext(context);
        mapRef2RefFlowFunction_4.setDefaultValue(new EmptyGroupBy());
        mapRef2RefFlowFunction_6.setDataFlowContext(context);
        mapRef2RefFlowFunction_8.setDataFlowContext(context);
        mapRef2RefFlowFunction_8.setDefaultValue(new EmptyGroupBy());
        mapRef2RefFlowFunction_13.setDataFlowContext(context);
        mapRef2RefFlowFunction_17.setDataFlowContext(context);
        mapRef2RefFlowFunction_17.setPublishTriggerOverrideNode(fixedRateTrigger_66);
        mapRef2RefFlowFunction_19.setDataFlowContext(context);
        mapRef2RefFlowFunction_20.setDataFlowContext(context);
        mapRef2RefFlowFunction_22.setDataFlowContext(context);
        pushFlowFunction_24.setDataFlowContext(context);
        groupByTimedSlidingWindow_2.setDataFlowContext(context);
        groupByTimedSlidingWindow_2.rollTrigger = fixedRateTrigger_26;
        context.setClock(clock);
        alarmPublisher.setDataFlowContext(context);
        serviceRegistry.setDataFlowContext(context);
        //node auditors
        initialiseAuditor(clock);
        initialiseAuditor(nodeNameLookup);
        initialiseAuditor(serviceRegistry);
        if (subscriptionManager != null) {
            subscriptionManager.setSubscribingEventProcessor(this);
        }
        if (context != null) {
            context.setEventProcessorCallback(this);
        }
    }

    public Processor() {
        this(null);
    }

    @Override
    public void init() {
        initCalled = true;
        auditEvent(Lifecycle.LifecycleEvent.Init);
        //initialise dirty lookup map
        isDirty("test");
        clock.init();
        fixedRateTrigger_26.init();
        fixedRateTrigger_66.init();
        handlerMachineProfileEvent.init();
        handlerMachineReadingEvent.init();
        groupByTimedSlidingWindow_2.initialiseEventStream();
        handlerSupportContactEvent.init();
        mapRef2RefFlowFunction_1.initialiseEventStream();
        mapRef2RefFlowFunction_4.initialiseEventStream();
        mapRef2RefFlowFunction_6.initialiseEventStream();
        mapRef2RefFlowFunction_8.initialiseEventStream();
        binaryMapToRefFlowFunction_9.initialiseEventStream();
        binaryMapToRefFlowFunction_11.initialiseEventStream();
        mapRef2RefFlowFunction_13.initialiseEventStream();
        binaryMapToRefFlowFunction_15.initialiseEventStream();
        mapRef2RefFlowFunction_17.initialiseEventStream();
        mapRef2RefFlowFunction_19.initialiseEventStream();
        mapRef2RefFlowFunction_20.initialiseEventStream();
        mapRef2RefFlowFunction_22.initialiseEventStream();
        filterFlowFunction_23.initialiseEventStream();
        pushFlowFunction_24.initialiseEventStream();
        afterEvent();
    }

    @Override
    public void start() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before start()");
        }
        processing = true;
        auditEvent(Lifecycle.LifecycleEvent.Start);

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void startComplete() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before startComplete()");
        }
        processing = true;
        auditEvent(Lifecycle.LifecycleEvent.StartComplete);

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void stop() {
        if (!initCalled) {
            throw new RuntimeException("init() must be called before stop()");
        }
        processing = true;
        auditEvent(Lifecycle.LifecycleEvent.Stop);

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void tearDown() {
        initCalled = false;
        auditEvent(Lifecycle.LifecycleEvent.TearDown);
        serviceRegistry.tearDown();
        nodeNameLookup.tearDown();
        clock.tearDown();
        handlerSupportContactEvent.tearDown();
        handlerMachineReadingEvent.tearDown();
        handlerMachineProfileEvent.tearDown();
        subscriptionManager.tearDown();
        afterEvent();
    }

    @Override
    public void setContextParameterMap(Map<Object, Object> newContextMapping) {
        context.replaceMappings(newContextMapping);
    }

    @Override
    public void addContextParameter(Object key, Object value) {
        context.addMapping(key, value);
    }

    //EVENT DISPATCH - START
    @Override
    @OnEventHandler(failBuildIfMissingBooleanReturn = false)
    public void onEvent(Object event) {
        if (buffering) {
            triggerCalculation();
        }
        if (processing) {
            callbackDispatcher.queueReentrantEvent(event);
        } else {
            processing = true;
            onEventInternal(event);
            callbackDispatcher.dispatchQueuedCallbacks();
            processing = false;
        }
    }

    @Override
    public void onEventInternal(Object event) {
        if (event
                instanceof com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineProfileEvent) {
            MachineProfileEvent typedEvent = (MachineProfileEvent) event;
            handleEvent(typedEvent);
        } else if (event
                instanceof com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineReadingEvent) {
            MachineReadingEvent typedEvent = (MachineReadingEvent) event;
            handleEvent(typedEvent);
        } else if (event
                instanceof com.fluxtion.dataflow.examples.temp_monitoring.inprocess.SupportContactEvent) {
            SupportContactEvent typedEvent = (SupportContactEvent) event;
            handleEvent(typedEvent);
        } else if (event instanceof com.fluxtion.dataflow.runtime.output.SinkDeregister) {
            SinkDeregister typedEvent = (SinkDeregister) event;
            handleEvent(typedEvent);
        } else if (event instanceof com.fluxtion.dataflow.runtime.output.SinkRegistration) {
            SinkRegistration typedEvent = (SinkRegistration) event;
            handleEvent(typedEvent);
        } else if (event
                instanceof com.fluxtion.dataflow.runtime.time.ClockStrategy.ClockStrategyEvent) {
            ClockStrategyEvent typedEvent = (ClockStrategyEvent) event;
            handleEvent(typedEvent);
        } else if (event instanceof java.lang.Object) {
            Object typedEvent = (Object) event;
            handleEvent(typedEvent);
        }
    }

    public void handleEvent(MachineProfileEvent typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_26) {
            groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
        }
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_66) {
            mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
        }
        isDirty_handlerMachineProfileEvent = handlerMachineProfileEvent.onEvent(typedEvent);
        if (isDirty_handlerMachineProfileEvent) {
            mapRef2RefFlowFunction_4.inputUpdated(handlerMachineProfileEvent);
        }
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_4()) {
            isDirty_mapRef2RefFlowFunction_4 = mapRef2RefFlowFunction_4.map();
            if (isDirty_mapRef2RefFlowFunction_4) {
                mapRef2RefFlowFunction_6.inputUpdated(mapRef2RefFlowFunction_4);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_6()) {
            isDirty_mapRef2RefFlowFunction_6 = mapRef2RefFlowFunction_6.map();
            if (isDirty_mapRef2RefFlowFunction_6) {
                binaryMapToRefFlowFunction_9.inputUpdated(mapRef2RefFlowFunction_6);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_9()) {
            isDirty_binaryMapToRefFlowFunction_9 = binaryMapToRefFlowFunction_9.map();
            if (isDirty_binaryMapToRefFlowFunction_9) {
                binaryMapToRefFlowFunction_11.inputUpdated(binaryMapToRefFlowFunction_9);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_11()) {
            isDirty_binaryMapToRefFlowFunction_11 = binaryMapToRefFlowFunction_11.map();
            if (isDirty_binaryMapToRefFlowFunction_11) {
                mapRef2RefFlowFunction_13.inputUpdated(binaryMapToRefFlowFunction_11);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_13()) {
            isDirty_mapRef2RefFlowFunction_13 = mapRef2RefFlowFunction_13.map();
            if (isDirty_mapRef2RefFlowFunction_13) {
                binaryMapToRefFlowFunction_15.inputUpdated(mapRef2RefFlowFunction_13);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }

    public void handleEvent(MachineReadingEvent typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_26) {
            groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
        }
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_66) {
            mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
        }
        isDirty_handlerMachineReadingEvent = handlerMachineReadingEvent.onEvent(typedEvent);
        if (isDirty_handlerMachineReadingEvent) {
            groupByTimedSlidingWindow_2.inputUpdated(handlerMachineReadingEvent);
            mapRef2RefFlowFunction_1.inputUpdated(handlerMachineReadingEvent);
        }
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_1()) {
            isDirty_mapRef2RefFlowFunction_1 = mapRef2RefFlowFunction_1.map();
            if (isDirty_mapRef2RefFlowFunction_1) {
                binaryMapToRefFlowFunction_11.input2Updated(mapRef2RefFlowFunction_1);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_11()) {
            isDirty_binaryMapToRefFlowFunction_11 = binaryMapToRefFlowFunction_11.map();
            if (isDirty_binaryMapToRefFlowFunction_11) {
                mapRef2RefFlowFunction_13.inputUpdated(binaryMapToRefFlowFunction_11);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_13()) {
            isDirty_mapRef2RefFlowFunction_13 = mapRef2RefFlowFunction_13.map();
            if (isDirty_mapRef2RefFlowFunction_13) {
                binaryMapToRefFlowFunction_15.inputUpdated(mapRef2RefFlowFunction_13);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }

    public void handleEvent(SupportContactEvent typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_26) {
            groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
        }
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_66) {
            mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
        }
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        isDirty_handlerSupportContactEvent = handlerSupportContactEvent.onEvent(typedEvent);
        if (isDirty_handlerSupportContactEvent) {
            mapRef2RefFlowFunction_8.inputUpdated(handlerSupportContactEvent);
        }
        if (guardCheck_mapRef2RefFlowFunction_8()) {
            isDirty_mapRef2RefFlowFunction_8 = mapRef2RefFlowFunction_8.map();
            if (isDirty_mapRef2RefFlowFunction_8) {
                binaryMapToRefFlowFunction_9.input2Updated(mapRef2RefFlowFunction_8);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_9()) {
            isDirty_binaryMapToRefFlowFunction_9 = binaryMapToRefFlowFunction_9.map();
            if (isDirty_binaryMapToRefFlowFunction_9) {
                binaryMapToRefFlowFunction_11.inputUpdated(binaryMapToRefFlowFunction_9);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_11()) {
            isDirty_binaryMapToRefFlowFunction_11 = binaryMapToRefFlowFunction_11.map();
            if (isDirty_binaryMapToRefFlowFunction_11) {
                mapRef2RefFlowFunction_13.inputUpdated(binaryMapToRefFlowFunction_11);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_13()) {
            isDirty_mapRef2RefFlowFunction_13 = mapRef2RefFlowFunction_13.map();
            if (isDirty_mapRef2RefFlowFunction_13) {
                binaryMapToRefFlowFunction_15.inputUpdated(mapRef2RefFlowFunction_13);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }

    public void handleEvent(SinkDeregister typedEvent) {
        auditEvent(typedEvent);
        switch (typedEvent.filterString()) {
            //Event Class:[com.fluxtion.dataflow.runtime.output.SinkDeregister] filterString:[alarmPublisher]
            case ("alarmPublisher"):
                handle_SinkDeregister_alarmPublisher(typedEvent);
                afterEvent();
                return;
        }
        //Default, no filter methods
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_26) {
            groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
        }
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_66) {
            mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
        }
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }

    public void handleEvent(SinkRegistration typedEvent) {
        auditEvent(typedEvent);
        switch (typedEvent.filterString()) {
            //Event Class:[com.fluxtion.dataflow.runtime.output.SinkRegistration] filterString:[alarmPublisher]
            case ("alarmPublisher"):
                handle_SinkRegistration_alarmPublisher(typedEvent);
                afterEvent();
                return;
        }
        //Default, no filter methods
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_26) {
            groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
        }
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_66) {
            mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
        }
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }

    public void handleEvent(ClockStrategyEvent typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        isDirty_clock = true;
        clock.setClockStrategy(typedEvent);
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_26) {
            groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
        }
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.setClockStrategy(typedEvent);
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_66) {
            mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
        }
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.setClockStrategy(typedEvent);
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }

    public void handleEvent(Object typedEvent) {
        auditEvent(typedEvent);
        //Default, no filter methods
        isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_26) {
            groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
        }
        isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
        if (isDirty_fixedRateTrigger_66) {
            mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
        }
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }
    //EVENT DISPATCH - END

    //FILTERED DISPATCH - START
    private void handle_SinkDeregister_alarmPublisher(SinkDeregister typedEvent) {
        alarmPublisher.unregisterSink(typedEvent);
    }

    private void handle_SinkRegistration_alarmPublisher(SinkRegistration typedEvent) {
        alarmPublisher.sinkRegistration(typedEvent);
    }
    //FILTERED DISPATCH - END

    //EXPORTED SERVICE FUNCTIONS - START
    @Override
    public void deRegisterService(com.fluxtion.dataflow.runtime.service.Service<?> arg0) {
        beforeServiceCall(
                "public void com.fluxtion.dataflow.runtime.service.ServiceRegistryNode.deRegisterService(com.fluxtion.dataflow.runtime.service.Service<?>)");
        ExportFunctionAuditEvent typedEvent = functionAudit;
        serviceRegistry.deRegisterService(arg0);
        afterServiceCall();
    }

    @Override
    public void registerService(com.fluxtion.dataflow.runtime.service.Service<?> arg0) {
        beforeServiceCall(
                "public void com.fluxtion.dataflow.runtime.service.ServiceRegistryNode.registerService(com.fluxtion.dataflow.runtime.service.Service<?>)");
        ExportFunctionAuditEvent typedEvent = functionAudit;
        serviceRegistry.registerService(arg0);
        afterServiceCall();
    }
    //EXPORTED SERVICE FUNCTIONS - END

    //EVENT BUFFERING - START
    public void bufferEvent(Object event) {
        buffering = true;
        if (event
                instanceof com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineProfileEvent) {
            MachineProfileEvent typedEvent = (MachineProfileEvent) event;
            auditEvent(typedEvent);
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_26) {
                groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
            }
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_66) {
                mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
            }
            isDirty_handlerMachineProfileEvent = handlerMachineProfileEvent.onEvent(typedEvent);
            if (isDirty_handlerMachineProfileEvent) {
                mapRef2RefFlowFunction_4.inputUpdated(handlerMachineProfileEvent);
            }
        } else if (event
                instanceof com.fluxtion.dataflow.examples.temp_monitoring.inprocess.MachineReadingEvent) {
            MachineReadingEvent typedEvent = (MachineReadingEvent) event;
            auditEvent(typedEvent);
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_26) {
                groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
            }
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_66) {
                mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
            }
            isDirty_handlerMachineReadingEvent = handlerMachineReadingEvent.onEvent(typedEvent);
            if (isDirty_handlerMachineReadingEvent) {
                groupByTimedSlidingWindow_2.inputUpdated(handlerMachineReadingEvent);
                mapRef2RefFlowFunction_1.inputUpdated(handlerMachineReadingEvent);
            }
        } else if (event
                instanceof com.fluxtion.dataflow.examples.temp_monitoring.inprocess.SupportContactEvent) {
            SupportContactEvent typedEvent = (SupportContactEvent) event;
            auditEvent(typedEvent);
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_26) {
                groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
            }
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_66) {
                mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
            }
            isDirty_handlerSupportContactEvent = handlerSupportContactEvent.onEvent(typedEvent);
            if (isDirty_handlerSupportContactEvent) {
                mapRef2RefFlowFunction_8.inputUpdated(handlerSupportContactEvent);
            }
        } else if (event instanceof com.fluxtion.dataflow.runtime.output.SinkDeregister) {
            SinkDeregister typedEvent = (SinkDeregister) event;
            auditEvent(typedEvent);
            switch (typedEvent.filterString()) {
                //Event Class:[com.fluxtion.dataflow.runtime.output.SinkDeregister] filterString:[alarmPublisher]
                case ("alarmPublisher"):
                    handle_SinkDeregister_alarmPublisher_bufferDispatch(typedEvent);
                    afterEvent();
                    return;
            }
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_26) {
                groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
            }
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_66) {
                mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
            }
        } else if (event instanceof com.fluxtion.dataflow.runtime.output.SinkRegistration) {
            SinkRegistration typedEvent = (SinkRegistration) event;
            auditEvent(typedEvent);
            switch (typedEvent.filterString()) {
                //Event Class:[com.fluxtion.dataflow.runtime.output.SinkRegistration] filterString:[alarmPublisher]
                case ("alarmPublisher"):
                    handle_SinkRegistration_alarmPublisher_bufferDispatch(typedEvent);
                    afterEvent();
                    return;
            }
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_26) {
                groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
            }
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_66) {
                mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
            }
        } else if (event
                instanceof com.fluxtion.dataflow.runtime.time.ClockStrategy.ClockStrategyEvent) {
            ClockStrategyEvent typedEvent = (ClockStrategyEvent) event;
            auditEvent(typedEvent);
            isDirty_clock = true;
            clock.setClockStrategy(typedEvent);
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_26) {
                groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
            }
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.setClockStrategy(typedEvent);
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_66) {
                mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
            }
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.setClockStrategy(typedEvent);
        } else if (event instanceof java.lang.Object) {
            Object typedEvent = (Object) event;
            auditEvent(typedEvent);
            isDirty_fixedRateTrigger_26 = fixedRateTrigger_26.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_26) {
                groupByTimedSlidingWindow_2.timeTriggerFired(fixedRateTrigger_26);
            }
            isDirty_fixedRateTrigger_66 = fixedRateTrigger_66.hasExpired(typedEvent);
            if (isDirty_fixedRateTrigger_66) {
                mapRef2RefFlowFunction_17.publishTriggerOverrideNodeUpdated(fixedRateTrigger_66);
            }
        }
    }

    private void handle_SinkDeregister_alarmPublisher_bufferDispatch(SinkDeregister typedEvent) {
        alarmPublisher.unregisterSink(typedEvent);
    }

    private void handle_SinkRegistration_alarmPublisher_bufferDispatch(SinkRegistration typedEvent) {
        alarmPublisher.sinkRegistration(typedEvent);
    }

    public void triggerCalculation() {
        buffering = false;
        String typedEvent = "No event information - buffered dispatch";
        if (guardCheck_groupByTimedSlidingWindow_2()) {
            isDirty_groupByTimedSlidingWindow_2 = groupByTimedSlidingWindow_2.triggered();
            if (isDirty_groupByTimedSlidingWindow_2) {
                binaryMapToRefFlowFunction_15.input2Updated(groupByTimedSlidingWindow_2);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_1()) {
            isDirty_mapRef2RefFlowFunction_1 = mapRef2RefFlowFunction_1.map();
            if (isDirty_mapRef2RefFlowFunction_1) {
                binaryMapToRefFlowFunction_11.input2Updated(mapRef2RefFlowFunction_1);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_4()) {
            isDirty_mapRef2RefFlowFunction_4 = mapRef2RefFlowFunction_4.map();
            if (isDirty_mapRef2RefFlowFunction_4) {
                mapRef2RefFlowFunction_6.inputUpdated(mapRef2RefFlowFunction_4);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_6()) {
            isDirty_mapRef2RefFlowFunction_6 = mapRef2RefFlowFunction_6.map();
            if (isDirty_mapRef2RefFlowFunction_6) {
                binaryMapToRefFlowFunction_9.inputUpdated(mapRef2RefFlowFunction_6);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_8()) {
            isDirty_mapRef2RefFlowFunction_8 = mapRef2RefFlowFunction_8.map();
            if (isDirty_mapRef2RefFlowFunction_8) {
                binaryMapToRefFlowFunction_9.input2Updated(mapRef2RefFlowFunction_8);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_9()) {
            isDirty_binaryMapToRefFlowFunction_9 = binaryMapToRefFlowFunction_9.map();
            if (isDirty_binaryMapToRefFlowFunction_9) {
                binaryMapToRefFlowFunction_11.inputUpdated(binaryMapToRefFlowFunction_9);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_11()) {
            isDirty_binaryMapToRefFlowFunction_11 = binaryMapToRefFlowFunction_11.map();
            if (isDirty_binaryMapToRefFlowFunction_11) {
                mapRef2RefFlowFunction_13.inputUpdated(binaryMapToRefFlowFunction_11);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_13()) {
            isDirty_mapRef2RefFlowFunction_13 = mapRef2RefFlowFunction_13.map();
            if (isDirty_mapRef2RefFlowFunction_13) {
                binaryMapToRefFlowFunction_15.inputUpdated(mapRef2RefFlowFunction_13);
            }
        }
        if (guardCheck_binaryMapToRefFlowFunction_15()) {
            isDirty_binaryMapToRefFlowFunction_15 = binaryMapToRefFlowFunction_15.map();
            if (isDirty_binaryMapToRefFlowFunction_15) {
                mapRef2RefFlowFunction_17.inputUpdated(binaryMapToRefFlowFunction_15);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_17()) {
            isDirty_mapRef2RefFlowFunction_17 = mapRef2RefFlowFunction_17.map();
            if (isDirty_mapRef2RefFlowFunction_17) {
                mapRef2RefFlowFunction_19.inputUpdated(mapRef2RefFlowFunction_17);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_19()) {
            isDirty_mapRef2RefFlowFunction_19 = mapRef2RefFlowFunction_19.map();
            if (isDirty_mapRef2RefFlowFunction_19) {
                mapRef2RefFlowFunction_20.inputUpdated(mapRef2RefFlowFunction_19);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_20()) {
            isDirty_mapRef2RefFlowFunction_20 = mapRef2RefFlowFunction_20.map();
            if (isDirty_mapRef2RefFlowFunction_20) {
                mapRef2RefFlowFunction_22.inputUpdated(mapRef2RefFlowFunction_20);
            }
        }
        if (guardCheck_mapRef2RefFlowFunction_22()) {
            isDirty_mapRef2RefFlowFunction_22 = mapRef2RefFlowFunction_22.map();
            if (isDirty_mapRef2RefFlowFunction_22) {
                filterFlowFunction_23.inputUpdated(mapRef2RefFlowFunction_22);
            }
        }
        if (guardCheck_filterFlowFunction_23()) {
            isDirty_filterFlowFunction_23 = filterFlowFunction_23.filter();
            if (isDirty_filterFlowFunction_23) {
                pushFlowFunction_24.inputUpdated(filterFlowFunction_23);
            }
        }
        if (guardCheck_pushFlowFunction_24()) {
            isDirty_pushFlowFunction_24 = pushFlowFunction_24.push();
        }
        afterEvent();
    }
    //EVENT BUFFERING - END

    private void auditEvent(Object typedEvent) {
        clock.eventReceived(typedEvent);
        nodeNameLookup.eventReceived(typedEvent);
        serviceRegistry.eventReceived(typedEvent);
    }

    private void auditEvent(Event typedEvent) {
        clock.eventReceived(typedEvent);
        nodeNameLookup.eventReceived(typedEvent);
        serviceRegistry.eventReceived(typedEvent);
    }

    private void initialiseAuditor(Auditor auditor) {
        auditor.init();
        auditor.nodeRegistered(alarmDeltaFilter_21, "alarmDeltaFilter_21");
        auditor.nodeRegistered(callbackDispatcher, "callbackDispatcher");
        auditor.nodeRegistered(binaryMapToRefFlowFunction_9, "binaryMapToRefFlowFunction_9");
        auditor.nodeRegistered(binaryMapToRefFlowFunction_11, "binaryMapToRefFlowFunction_11");
        auditor.nodeRegistered(binaryMapToRefFlowFunction_15, "binaryMapToRefFlowFunction_15");
        auditor.nodeRegistered(filterFlowFunction_23, "filterFlowFunction_23");
        auditor.nodeRegistered(mapRef2RefFlowFunction_1, "mapRef2RefFlowFunction_1");
        auditor.nodeRegistered(mapRef2RefFlowFunction_4, "mapRef2RefFlowFunction_4");
        auditor.nodeRegistered(mapRef2RefFlowFunction_6, "mapRef2RefFlowFunction_6");
        auditor.nodeRegistered(mapRef2RefFlowFunction_8, "mapRef2RefFlowFunction_8");
        auditor.nodeRegistered(mapRef2RefFlowFunction_13, "mapRef2RefFlowFunction_13");
        auditor.nodeRegistered(mapRef2RefFlowFunction_17, "mapRef2RefFlowFunction_17");
        auditor.nodeRegistered(mapRef2RefFlowFunction_19, "mapRef2RefFlowFunction_19");
        auditor.nodeRegistered(mapRef2RefFlowFunction_20, "mapRef2RefFlowFunction_20");
        auditor.nodeRegistered(mapRef2RefFlowFunction_22, "mapRef2RefFlowFunction_22");
        auditor.nodeRegistered(pushFlowFunction_24, "pushFlowFunction_24");
        auditor.nodeRegistered(
                groupByFilterFlowFunctionWrapper_18, "groupByFilterFlowFunctionWrapper_18");
        auditor.nodeRegistered(groupByFlowFunctionWrapper_0, "groupByFlowFunctionWrapper_0");
        auditor.nodeRegistered(groupByFlowFunctionWrapper_3, "groupByFlowFunctionWrapper_3");
        auditor.nodeRegistered(groupByFlowFunctionWrapper_7, "groupByFlowFunctionWrapper_7");
        auditor.nodeRegistered(groupByMapFlowFunction_5, "groupByMapFlowFunction_5");
        auditor.nodeRegistered(groupByMapFlowFunction_12, "groupByMapFlowFunction_12");
        auditor.nodeRegistered(groupByMapFlowFunction_16, "groupByMapFlowFunction_16");
        auditor.nodeRegistered(groupByTimedSlidingWindow_2, "groupByTimedSlidingWindow_2");
        auditor.nodeRegistered(innerJoin_10, "innerJoin_10");
        auditor.nodeRegistered(innerJoin_14, "innerJoin_14");
        auditor.nodeRegistered(mapTuple_116, "mapTuple_116");
        auditor.nodeRegistered(mapTuple_119, "mapTuple_119");
        auditor.nodeRegistered(subscriptionManager, "subscriptionManager");
        auditor.nodeRegistered(handlerMachineProfileEvent, "handlerMachineProfileEvent");
        auditor.nodeRegistered(handlerMachineReadingEvent, "handlerMachineReadingEvent");
        auditor.nodeRegistered(handlerSupportContactEvent, "handlerSupportContactEvent");
        auditor.nodeRegistered(context, "context");
        auditor.nodeRegistered(alarmPublisher, "alarmPublisher");
        auditor.nodeRegistered(fixedRateTrigger_26, "fixedRateTrigger_26");
        auditor.nodeRegistered(fixedRateTrigger_66, "fixedRateTrigger_66");
    }

    private void beforeServiceCall(String functionDescription) {
        functionAudit.setFunctionDescription(functionDescription);
        auditEvent(functionAudit);
        if (buffering) {
            triggerCalculation();
        }
        processing = true;
    }

    private void afterServiceCall() {
        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    private void afterEvent() {
        alarmDeltaFilter_21.purgerNewAlarms();

        clock.processingComplete();
        nodeNameLookup.processingComplete();
        serviceRegistry.processingComplete();
        isDirty_binaryMapToRefFlowFunction_9 = false;
        isDirty_binaryMapToRefFlowFunction_11 = false;
        isDirty_binaryMapToRefFlowFunction_15 = false;
        isDirty_clock = false;
        isDirty_filterFlowFunction_23 = false;
        isDirty_fixedRateTrigger_26 = false;
        isDirty_fixedRateTrigger_66 = false;
        isDirty_groupByTimedSlidingWindow_2 = false;
        isDirty_handlerMachineProfileEvent = false;
        isDirty_handlerMachineReadingEvent = false;
        isDirty_handlerSupportContactEvent = false;
        isDirty_mapRef2RefFlowFunction_1 = false;
        isDirty_mapRef2RefFlowFunction_4 = false;
        isDirty_mapRef2RefFlowFunction_6 = false;
        isDirty_mapRef2RefFlowFunction_8 = false;
        isDirty_mapRef2RefFlowFunction_13 = false;
        isDirty_mapRef2RefFlowFunction_17 = false;
        isDirty_mapRef2RefFlowFunction_19 = false;
        isDirty_mapRef2RefFlowFunction_20 = false;
        isDirty_mapRef2RefFlowFunction_22 = false;
        isDirty_pushFlowFunction_24 = false;
    }

    @Override
    public void batchPause() {
        auditEvent(Lifecycle.LifecycleEvent.BatchPause);
        processing = true;

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public void batchEnd() {
        auditEvent(Lifecycle.LifecycleEvent.BatchEnd);
        processing = true;

        afterEvent();
        callbackDispatcher.dispatchQueuedCallbacks();
        processing = false;
    }

    @Override
    public boolean isDirty(Object node) {
        return dirtySupplier(node).getAsBoolean();
    }

    @Override
    public BooleanSupplier dirtySupplier(Object node) {
        if (dirtyFlagSupplierMap.isEmpty()) {
            dirtyFlagSupplierMap.put(
                    binaryMapToRefFlowFunction_11, () -> isDirty_binaryMapToRefFlowFunction_11);
            dirtyFlagSupplierMap.put(
                    binaryMapToRefFlowFunction_15, () -> isDirty_binaryMapToRefFlowFunction_15);
            dirtyFlagSupplierMap.put(
                    binaryMapToRefFlowFunction_9, () -> isDirty_binaryMapToRefFlowFunction_9);
            dirtyFlagSupplierMap.put(clock, () -> isDirty_clock);
            dirtyFlagSupplierMap.put(filterFlowFunction_23, () -> isDirty_filterFlowFunction_23);
            dirtyFlagSupplierMap.put(fixedRateTrigger_26, () -> isDirty_fixedRateTrigger_26);
            dirtyFlagSupplierMap.put(fixedRateTrigger_66, () -> isDirty_fixedRateTrigger_66);
            dirtyFlagSupplierMap.put(
                    groupByTimedSlidingWindow_2, () -> isDirty_groupByTimedSlidingWindow_2);
            dirtyFlagSupplierMap.put(
                    handlerMachineProfileEvent, () -> isDirty_handlerMachineProfileEvent);
            dirtyFlagSupplierMap.put(
                    handlerMachineReadingEvent, () -> isDirty_handlerMachineReadingEvent);
            dirtyFlagSupplierMap.put(
                    handlerSupportContactEvent, () -> isDirty_handlerSupportContactEvent);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_1, () -> isDirty_mapRef2RefFlowFunction_1);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_13, () -> isDirty_mapRef2RefFlowFunction_13);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_17, () -> isDirty_mapRef2RefFlowFunction_17);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_19, () -> isDirty_mapRef2RefFlowFunction_19);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_20, () -> isDirty_mapRef2RefFlowFunction_20);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_22, () -> isDirty_mapRef2RefFlowFunction_22);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_4, () -> isDirty_mapRef2RefFlowFunction_4);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_6, () -> isDirty_mapRef2RefFlowFunction_6);
            dirtyFlagSupplierMap.put(mapRef2RefFlowFunction_8, () -> isDirty_mapRef2RefFlowFunction_8);
            dirtyFlagSupplierMap.put(pushFlowFunction_24, () -> isDirty_pushFlowFunction_24);
        }
        return dirtyFlagSupplierMap.getOrDefault(node, DataFlow.ALWAYS_FALSE);
    }

    @Override
    public void setDirty(Object node, boolean dirtyFlag) {
        if (dirtyFlagUpdateMap.isEmpty()) {
            dirtyFlagUpdateMap.put(
                    binaryMapToRefFlowFunction_11, (b) -> isDirty_binaryMapToRefFlowFunction_11 = b);
            dirtyFlagUpdateMap.put(
                    binaryMapToRefFlowFunction_15, (b) -> isDirty_binaryMapToRefFlowFunction_15 = b);
            dirtyFlagUpdateMap.put(
                    binaryMapToRefFlowFunction_9, (b) -> isDirty_binaryMapToRefFlowFunction_9 = b);
            dirtyFlagUpdateMap.put(clock, (b) -> isDirty_clock = b);
            dirtyFlagUpdateMap.put(filterFlowFunction_23, (b) -> isDirty_filterFlowFunction_23 = b);
            dirtyFlagUpdateMap.put(fixedRateTrigger_26, (b) -> isDirty_fixedRateTrigger_26 = b);
            dirtyFlagUpdateMap.put(fixedRateTrigger_66, (b) -> isDirty_fixedRateTrigger_66 = b);
            dirtyFlagUpdateMap.put(
                    groupByTimedSlidingWindow_2, (b) -> isDirty_groupByTimedSlidingWindow_2 = b);
            dirtyFlagUpdateMap.put(
                    handlerMachineProfileEvent, (b) -> isDirty_handlerMachineProfileEvent = b);
            dirtyFlagUpdateMap.put(
                    handlerMachineReadingEvent, (b) -> isDirty_handlerMachineReadingEvent = b);
            dirtyFlagUpdateMap.put(
                    handlerSupportContactEvent, (b) -> isDirty_handlerSupportContactEvent = b);
            dirtyFlagUpdateMap.put(mapRef2RefFlowFunction_1, (b) -> isDirty_mapRef2RefFlowFunction_1 = b);
            dirtyFlagUpdateMap.put(
                    mapRef2RefFlowFunction_13, (b) -> isDirty_mapRef2RefFlowFunction_13 = b);
            dirtyFlagUpdateMap.put(
                    mapRef2RefFlowFunction_17, (b) -> isDirty_mapRef2RefFlowFunction_17 = b);
            dirtyFlagUpdateMap.put(
                    mapRef2RefFlowFunction_19, (b) -> isDirty_mapRef2RefFlowFunction_19 = b);
            dirtyFlagUpdateMap.put(
                    mapRef2RefFlowFunction_20, (b) -> isDirty_mapRef2RefFlowFunction_20 = b);
            dirtyFlagUpdateMap.put(
                    mapRef2RefFlowFunction_22, (b) -> isDirty_mapRef2RefFlowFunction_22 = b);
            dirtyFlagUpdateMap.put(mapRef2RefFlowFunction_4, (b) -> isDirty_mapRef2RefFlowFunction_4 = b);
            dirtyFlagUpdateMap.put(mapRef2RefFlowFunction_6, (b) -> isDirty_mapRef2RefFlowFunction_6 = b);
            dirtyFlagUpdateMap.put(mapRef2RefFlowFunction_8, (b) -> isDirty_mapRef2RefFlowFunction_8 = b);
            dirtyFlagUpdateMap.put(pushFlowFunction_24, (b) -> isDirty_pushFlowFunction_24 = b);
        }
        dirtyFlagUpdateMap.get(node).accept(dirtyFlag);
    }

    private boolean guardCheck_binaryMapToRefFlowFunction_9() {
        return isDirty_mapRef2RefFlowFunction_6 | isDirty_mapRef2RefFlowFunction_8;
    }

    private boolean guardCheck_binaryMapToRefFlowFunction_11() {
        return isDirty_binaryMapToRefFlowFunction_9 | isDirty_mapRef2RefFlowFunction_1;
    }

    private boolean guardCheck_binaryMapToRefFlowFunction_15() {
        return isDirty_groupByTimedSlidingWindow_2 | isDirty_mapRef2RefFlowFunction_13;
    }

    private boolean guardCheck_filterFlowFunction_23() {
        return isDirty_mapRef2RefFlowFunction_22;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_1() {
        return isDirty_handlerMachineReadingEvent;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_4() {
        return isDirty_handlerMachineProfileEvent;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_6() {
        return isDirty_mapRef2RefFlowFunction_4;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_8() {
        return isDirty_handlerSupportContactEvent;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_13() {
        return isDirty_binaryMapToRefFlowFunction_11;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_17() {
        return isDirty_binaryMapToRefFlowFunction_15 | isDirty_fixedRateTrigger_66;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_19() {
        return isDirty_mapRef2RefFlowFunction_17;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_20() {
        return isDirty_mapRef2RefFlowFunction_19;
    }

    private boolean guardCheck_mapRef2RefFlowFunction_22() {
        return isDirty_mapRef2RefFlowFunction_20;
    }

    private boolean guardCheck_pushFlowFunction_24() {
        return isDirty_filterFlowFunction_23;
    }

    private boolean guardCheck_groupByTimedSlidingWindow_2() {
        return isDirty_fixedRateTrigger_26 | isDirty_handlerMachineReadingEvent;
    }

    private boolean guardCheck_context() {
        return isDirty_clock;
    }

    private boolean guardCheck_alarmPublisher() {
        return isDirty_pushFlowFunction_24;
    }

    @Override
    public <T> T getNodeById(String id) throws NoSuchFieldException {
        return nodeNameLookup.getInstanceById(id);
    }

    @Override
    public <A extends Auditor> A getAuditorById(String id)
            throws NoSuchFieldException, IllegalAccessException {
        return (A) this.getClass().getField(id).get(this);
    }

    @Override
    public void addEventFeed(EventFeed eventProcessorFeed) {
        subscriptionManager.addEventProcessorFeed(eventProcessorFeed);
    }

    @Override
    public void removeEventFeed(EventFeed eventProcessorFeed) {
        subscriptionManager.removeEventProcessorFeed(eventProcessorFeed);
    }

    @Override
    public Processor newInstance() {
        return new Processor();
    }

    @Override
    public Processor newInstance(Map<Object, Object> contextMap) {
        return new Processor();
    }

    @Override
    public String getLastAuditLogRecord() {
        try {
            EventLogManager eventLogManager =
                    (EventLogManager) this.getClass().getField(EventLogManager.NODE_NAME).get(this);
            return eventLogManager.lastRecordAsString();
        } catch (Throwable e) {
            return "";
        }
    }

    public void unKnownEventHandler(Object object) {
        unKnownEventHandler.accept(object);
    }

    @Override
    public <T> void setUnKnownEventHandler(Consumer<T> consumer) {
        unKnownEventHandler = consumer;
    }

    @Override
    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }
}

