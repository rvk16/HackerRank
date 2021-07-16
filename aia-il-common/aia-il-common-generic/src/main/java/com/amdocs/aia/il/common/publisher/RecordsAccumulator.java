package com.amdocs.aia.il.common.publisher;

import com.amdocs.aia.il.common.stores.scylla.monitor.DbMetrics;
import com.datastax.oss.driver.api.core.CqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RecordsAccumulator implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordsAccumulator.class);
    public static final String FAILURE = "Failure";

    private final LinkedBlockingQueue<CounterTypeAndParams> counterTypeAndParamsQueue;
    private boolean isRunning;
    private boolean reportingCycleFlag;
    private final Lock lock;
    private final Map<CounterType, IAccumulatorCounter> counters;
    private Timer printTimer;
    private final CqlSession session;
    private final DbMetrics metrics;
    private long metricsSchedulerTimeInMS;


    public RecordsAccumulator(CqlSession session, DbMetrics metrics,long metricsSchedulerTimeInMS) {
        this.lock = new ReentrantLock();
        this.counterTypeAndParamsQueue = new LinkedBlockingQueue<>();
        this.counters = new ConcurrentSkipListMap<>();
        this.isRunning = true;
        this.reportingCycleFlag = false;
        this.session = session;
        this.metrics = metrics;
        this.metricsSchedulerTimeInMS = metricsSchedulerTimeInMS;
        initTimer();
    }

    public void run() {
        while (this.isRunning) {
            try {
                CounterTypeAndParams counterTypeAndParams = this.counterTypeAndParamsQueue.take();
                this.lock.lock();
                IAccumulatorCounter counter = this.counters.get(counterTypeAndParams.getCounterType());
                if(counter != null) {
                    counter.addCounter(counterTypeAndParams.getParams());
                }
            } catch (Exception e) {
                LOGGER.error(FAILURE, e);
            } finally {
                this.lock.unlock();
            }
        }
    }

    public void addCounter(CounterType counterType, Object... args) {
        try {
            if(!this.counterTypeAndParamsQueue.offer(new CounterTypeAndParams(counterType, args), 2, TimeUnit.SECONDS)){
                LOGGER.warn("failed to add counter. Operation timed out");
            }
        } catch (Exception e) {
            LOGGER.warn("Problem in Accumulator counter while adding to queue new message", e);
        }
    }

    public void registerCounter(IAccumulatorCounter statisticsCounter) {
        this.counters.put(statisticsCounter.getType(), statisticsCounter);
    }

    public void shutdown() {
        this.isRunning = false;
        //reporting the statistics before shutting down
        this.reportMetrics();
        if (this.printTimer != null) {
            this.printTimer.cancel();
        }
    }

    public void report() {
        try {
            reportingCycleFlag = true;
            while (reportingCycleFlag) {
                //wait until microbatch completes and all metrics are reported
                Thread.sleep(10);
            }
        } catch (Exception e) {
            LOGGER.error(FAILURE, e);
        }
    }

    public void reportMetrics() {
        try {
            LOGGER.info("Reporting metrics");
            this.lock.lock();
            this.counters.values().forEach(counter -> counter.report(metrics, session));
            this.counters.values().forEach(IAccumulatorCounter::clear);
        } catch (Exception e) {
            LOGGER.error(FAILURE, e);
        } finally {
            this.lock.unlock();
            reportingCycleFlag = false;
        }
    }

    public void triggerReportingCycle() {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Triggering metrics cycle");
        }
        if (reportingCycleFlag) {
            reportMetrics();
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Completed triggering metrics cycle");
        }
    }

    private void initTimer() {
        LOGGER.info("MetricsSchedulerTimeInMS : {} " ,metricsSchedulerTimeInMS);
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
        scheduledExecutor.scheduleAtFixedRate(this::report, 30000, metricsSchedulerTimeInMS, TimeUnit.MILLISECONDS);
    }
}