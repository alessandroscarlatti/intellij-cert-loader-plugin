/** *****************************************************************************
 * Copyright 2017 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************** */
package com.scarlatti.certloader.services.dyorgio.runAsRoot;

import com.scarlatti.certloader.services.dyorgio.runAsRoot.impl.LinuxRootProcessManager;
import com.scarlatti.certloader.services.dyorgio.runAsRoot.impl.MacRootProcessManager;
import com.scarlatti.certloader.services.dyorgio.runAsRoot.impl.WinRootProcessManager;
import com.scarlatti.certloader.services.dyorgio.process.CallableSerializable;
import com.scarlatti.certloader.services.dyorgio.process.OneRunOutProcess;
import com.scarlatti.certloader.services.dyorgio.process.RunnableSerializable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Run serializable <code>Callable</code>s and <code>Runnable</code>s in another
 * JVM with elevated privileges.<br>
 * Every <code>run()</code> or <code>call()</code> creates a new JVM and destroy
 * it.<br>
 * Normally this class can be a singleton if classpath and jvmOptions are always
 * equals, otherwise create a new instance for every cenario.<br>
 * <br>
 *
 * @author dyorgio
 * @see CallableSerializable
 * @see RunnableSerializable
 * @author dyorgio
 */
public class RootExecutor implements Serializable {

    /**
     * System property flag to identify an run-as-root code at runtime.
     */
    public static final String RUNNING_AS_ROOT = "$RunnningAsRoot";

    private static final RootProcessManager MANAGER;

    static {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            MANAGER = new MacRootProcessManager();
        } else if (OS.contains("win")) {
            MANAGER = new WinRootProcessManager();
        } else if (OS.contains("nux")) {
            MANAGER = new LinuxRootProcessManager();
        } else {
            throw new RuntimeException("Unsupported OS:" + OS);
        }
    }

    private final OneRunOutProcess outProcess;

    /**
     * Creates an instance with specific java options
     *
     * @param javaOptions JVM options (ex:"-xmx32m")
     */
    public RootExecutor(String... javaOptions) throws IOException {
        this.outProcess = new OneRunOutProcess(MANAGER, getDefaultTimeoutMs(), javaOptions);
    }

    /**
     * Creates an instance with specific java options
     *
     * @param javaOptions JVM options (ex:"-xmx32m")
     */
    public RootExecutor(int timeoutMs, String... javaOptions) throws IOException {
        this.outProcess = new OneRunOutProcess(MANAGER, timeoutMs, javaOptions);
    }

    /**
     * Runs runnable in a new JVM with elevated privileges.
     *
     * @param runnable A <code>RunnableSerializable</code> to run.
     * @throws Exception If cannot create a new JVM.
     * @throws UserCanceledException If user cancel or close prompt.
     * @throws NotAuthorizedException If user doesn't have root privileges.
     * @see RunnableSerializable
     */
    public void run(RunnableSerializable runnable) throws Exception, UserCanceledException, NotAuthorizedException {
        execute(runnable, false);
    }


    /**
     * Calls runnable in a new JVM with elevated privileges.
     *
     * @param <T> Result type.
     * @param callable A <code>CallableSerializable</code> to be called.
     * @return The result.
     * @throws Exception If cannot create a new JVM.
     * @throws UserCanceledException If user cancel or close prompt.
     * @throws NotAuthorizedException If user doesn't have root privileges.
     * @see CallableSerializable
     */
    public <T extends Serializable> T call(CallableSerializable<T> callable) throws Exception, UserCanceledException, NotAuthorizedException {
        return (T) execute(callable, true);
    }

    private Serializable execute(final Serializable command, final boolean hasResult) throws Exception, UserCanceledException, NotAuthorizedException {
        if (System.getProperty(RUNNING_AS_ROOT) != null) {
            if (hasResult) {
                Callable<? extends Serializable> callable = (Callable<? extends Serializable>) command;
                return callable.call();
            } else {
                ((Runnable) command).run();
                return null;
            }

        }

        OneRunOutProcess.OutProcessResult<Serializable> result = outProcess.call(new CallableSerializable<Serializable>() {
            @Override
            public Serializable call() throws Exception {
                System.setProperty(RUNNING_AS_ROOT, "true");
                if (hasResult) {
                    Callable<? extends Serializable> callable = (Callable<? extends Serializable>) command;
                    return callable.call();
                } else {
                    ((Runnable) command).run();
                    return null;
                }
            }
        });

        MANAGER.handleCode(result.getReturnCode());

        return result.getResult();
    }

    public int getDefaultTimeoutMs() {
        return 10000;
    }
}
