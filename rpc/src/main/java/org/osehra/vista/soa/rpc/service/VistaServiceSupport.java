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

package org.osehra.vista.soa.rpc.service;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class VistaServiceSupport implements VistaService {

    private AtomicBoolean active = new AtomicBoolean(false);
    private AtomicBoolean starting = new AtomicBoolean(false);
    private CountDownLatch startLock = new CountDownLatch(1);
    private AtomicBoolean stopping = new AtomicBoolean(false);
    private CountDownLatch stopLock = new CountDownLatch(0);

    public boolean start() {
        // NOTE on the boolean return value for start/stop
        // If start/stop is executed from multiple threads (which in general
        // would be a possibility for an application with a larger number
        // of services), the following code will only allow the first thread
        // to perform the start/stop procedure. We don't want the other threads
        // to return the service is fully started/stopped, because presumably
        // those thread may interact with the service in an undefined stated.
        // For that reason we make them wait. However, if the first thread 
        // fails to start/stop the service (by throwing a RuntimeException)
        // we don't want the other threads to either be blocked (awaiting 
        // on the latch) nor to return successfully, giving the false perception
        // that the start/stop procedure was successful. Throwing another
        // RuntimeException is not a good choice as we don't really have a cause
        // so returning a boolean indicating the success of the operation is
        // a better choice.
        if (!active.get()) {
            if (starting.compareAndSet(false, true)) {
                // NOTE on race conditions during start/stop.
                // Two threads may get to this point if preempted after 
                // the active.get() check and if the second thread executes
                // the compareAndSet after the first one finished the
                // startup procedure (and therefore the 'starting' flag is 
                // reset back to 'false'). The startLock count test is meant
                // to only let the first thread start the service (the second 
                // thread will have a false start). The 'starting' is reset 
                // to 'false' in either case.
                try {
                    if (startLock.getCount() > 0) {
                        startInternal();
                        active.set(true);
                        stopLock = new CountDownLatch(1);
                    }
                } finally {
                    starting.set(false);
                    startLock.countDown();
                }
            } else {
                try {
                    startLock.await();
                } catch (InterruptedException e) {  // ignore
                }
            }
        }
        return active.get();
    }

    public boolean stop() {
        if (active.get()) {
            if (stopping.compareAndSet(false, true)) {
                // see note in method start() above regarding race conditions during start/stop
                try {
                    if (stopLock.getCount() > 0) {
                        stopInternal();
                        active.set(false);
                        if (isRestartable()) {
                            startLock = new CountDownLatch(1);
                        }
                    }
                } finally {
                    stopping.set(false);
                    stopLock.countDown();
                }
            } else {
                try {
                    stopLock.await();
                } catch (InterruptedException e) {
                }
            }
        }
        return !active.get();
    }

    public boolean isActive() {
        return active.get();
    }

    protected abstract void startInternal();
    protected abstract void stopInternal();
    protected abstract boolean isRestartable();

}
