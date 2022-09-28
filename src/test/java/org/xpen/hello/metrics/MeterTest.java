package org.xpen.hello.metrics;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;

/**
 * metrics演示
 */
public class MeterTest {
    
    private static final MetricRegistry metrics = new MetricRegistry();
    
    /**
     * 每秒数量加1000,每3秒统计一次
     */
    @Test
    public void testMeter() throws Exception {
        startReport();
        Meter requests = metrics.meter("requests");
        for (int i = 0; i < 100; i++) {
            requests.mark(1000);
            wait1Seconds();
        }
    }
    
    private static void startReport() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
        reporter.start(3, TimeUnit.SECONDS);
        
        JmxReporter jmxReporter = JmxReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        jmxReporter.start();
    }

    private static void wait1Seconds() {
        try {
            Thread.sleep(1*1000);
        }
        catch(InterruptedException e) {}
    }
}
