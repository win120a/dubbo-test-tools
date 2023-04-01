package org.apache.dubbo.errorcode.integration;

import org.apache.dubbo.errorcode.reporter.InspectionResult;
import org.apache.dubbo.errorcode.reporter.Reporter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class IntegrationTestReporter implements Reporter, Future<InspectionResult> {

    private boolean done = false;

    private BlockingQueue<InspectionResult> result = new LinkedBlockingQueue<>(1);

    @Override
    public void report(InspectionResult inspectionResult) {

        result.add(inspectionResult);
        done = true;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public InspectionResult get() throws InterruptedException {
        return result.take();
    }

    @Override
    public InspectionResult get(long timeout, TimeUnit unit) throws InterruptedException {
        return result.poll(timeout, unit);
    }
}
