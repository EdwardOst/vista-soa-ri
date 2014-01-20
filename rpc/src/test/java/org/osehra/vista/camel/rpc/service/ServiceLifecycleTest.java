/*
 * Copyright 2012-2014 The Open Source Electronic Health Record Alliance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osehra.vista.camel.rpc.service;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.osehra.vista.soa.rpc.service.VistaServiceSupport;

// TODO: add more tests for startup failure scenarios and service restart
public class ServiceLifecycleTest {
    private static final int THREADS = 3;
    private static final int WORKERS = 7;

    @Test
    public void testStart() throws Exception {
        concurrentToggleState(true);
    }

    @Test
    public void testStop() throws Exception {
        concurrentToggleState(false);
    }

    public void concurrentToggleState(boolean start) throws InterruptedException {
        AtomicBoolean pass = new AtomicBoolean(true);
        AtomicInteger count = new AtomicInteger(0);
        TestService service = new TestService(count);
        if (!start) {
            service.start();
            count.set(0);   // reset
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < WORKERS; i++) {
            Runnable worker = new StateToggle(service, start, pass);
            executor.execute(worker);
        }
        executor.shutdown();
        executor.awaitTermination(500, TimeUnit.MILLISECONDS);

        Assert.assertEquals(1, count.get());
        Assert.assertEquals(true, pass.get());
    }

    public static final class StateToggle implements Runnable {
        private final AtomicBoolean pass;
        private final TestService service;
        private final boolean start;

        public StateToggle(TestService service, boolean start, AtomicBoolean pass) {
            this.pass = pass;
            this.service = service;
            this.start = start;
        }

        public void run() {
            if (start) {
                startService();
            } else {
                stopService();
            }
        }
        
        private void startService() {
            service.start();
            pass.compareAndSet(true, service.isActive());
        }
        private void stopService() {
            service.stop();
            pass.compareAndSet(true, !service.isActive());
        }
    }

    public static final class TestService extends VistaServiceSupport {
        private final AtomicInteger executionCount;

        public TestService(AtomicInteger count) {
            this.executionCount = count;
        }

        @Override
        protected void startInternal() throws RuntimeException {
            toggleState();
        }
        @Override
        protected void stopInternal() throws RuntimeException {
            toggleState();
        }
        @Override
        protected boolean isRestartable() {
            return true;
        }

        private void toggleState() {
            try {
                // let threads catch up
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            executionCount.incrementAndGet();
        }
    }

}

