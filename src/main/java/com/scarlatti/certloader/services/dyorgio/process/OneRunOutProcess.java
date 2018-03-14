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
package com.scarlatti.certloader.services.dyorgio.process;

import com.scarlatti.certloader.services.dyorgio.process.entrypoint.OneRunRemoteMain;
import com.scarlatti.certloader.services.dyorgio.runAsRoot.UserCanceledException;
import com.scarlatti.certloader.ui.controls.TextAreaLogger;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.scarlatti.certloader.services.dyorgio.process.OutProcessUtils.RUNNING_AS_OUT_PROCESS;
import static com.scarlatti.certloader.services.dyorgio.process.OutProcessUtils.getCurrentClasspath;

/**
 * Run serializable <code>Callable</code>s and <code>Runnable</code>s in another
 * JVM.<br>
 * Every <code>run()</code> or <code>call()</code> creates a new JVM and destroy
 * it.<br>
 * Normally this class can be a singleton if classpath and jvmOptions are always
 * equals, otherwise create a new instance for every cenario.<br>
 * <br>
 * If you need to share states/data between executions (<code>run</code> and
 * <code>call</code>) use <code>OutProcessExecutorService</code> class instead.
 *
 * @author dyorgio
 * @see CallableSerializable
 * @see RunnableSerializable
 * @see OutProcessExecutorService
 */
public class OneRunOutProcess implements Serializable {

    private final transient ProcessBuilderFactory processBuilderFactory;
    private final transient String classpath;
    private final transient String[] javaOptions;
    private final transient int timeoutMs;

//    /**
//     * Creates an instance with specific java options
//     *
//     * @param javaOptions JVM options (ex:"-xmx32m")
//     */
//    public OneRunOutProcess(String... javaOptions) {
//        this(new DefaultProcessBuilderFactory(), null, javaOptions);
//    }

//    /**
//     * Creates an instance with specific classpath and java options
//     *
//     * @param classpath JVM classpath, if <code>null</code> will use current
//     * thread classpath.
//     * @param javaOptions JVM options (ex:"-xmx32m")
//     * @see dyorgio.runtime.out.process.OutProcessUtils#getCurrentClasspath()
//     */
//    public OneRunOutProcess(String classpath, String[] javaOptions) {
//        this(new DefaultProcessBuilderFactory(), classpath, javaOptions);
//    }

    /**
     * Creates an instance with specific processBuilderFactory and java options
     *
     * @param processBuilderFactory A factory to convert a
     * <code>List&lt;String&gt;</code> to <code>ProcessBuilder</code>.
     * @param javaOptions JVM options (ex:"-xmx32m")
     * @see ProcessBuilderFactory
     * @see ProcessBuilder
     */
    public OneRunOutProcess(ProcessBuilderFactory processBuilderFactory, int timeoutMs, String... javaOptions) {
        this(processBuilderFactory, timeoutMs, null, javaOptions);
    }

    /**
     * Creates an instance with specific processBuilderFactory, classpath and
     * java options
     *
     * @param processBuilderFactory A factory to convert a
     * <code>List&lt;String&gt;</code> to <code>ProcessBuilder</code>.
     * @param classpath JVM classpath, if <code>null</code> will use current
     * thread classpath.
     * @param javaOptions JVM options (ex:"-xmx32m")
     * @see ProcessBuilderFactory
     * @see ProcessBuilder
     * @see OutProcessUtils#getCurrentClasspath()
     * @throws NullPointerException If <code>processBuilderFactory</code> is
     * <code>null</code>.
     */
    public OneRunOutProcess(ProcessBuilderFactory processBuilderFactory, int timeoutMs, String classpath, String[] javaOptions) {
        Objects.requireNonNull(processBuilderFactory, "Process Builder Factory cannot be null.");
        this.processBuilderFactory = processBuilderFactory;
        this.classpath = classpath == null ? getCurrentClasspath() : classpath;
        this.javaOptions = javaOptions;
        this.timeoutMs = timeoutMs;
    }

    /**
     * Runs runnable in a new JVM.
     *
     * @param runnable A <code>RunnableSerializable</code> to run.
     * @return The process <code>int</code> return code.
     * @throws Exception If cannot create a new JVM.
     * @throws ExecutionException If a error occurred in execution.
     * @see RunnableSerializable
     * @serialData
     */
    public int run(RunnableSerializable runnable) throws Exception, ExecutionException {
        return call(new RunnableCallableWrapper(runnable)).getReturnCode();
    }

    /**
     * Calls callable in a new JVM.
     *
     * @param <T> Result type.
     * @param callable A <code>CallableSerializable</code> to be called.
     * @return An <code>OutProcessResult</code> object containing the result and
     * return code.
     * @throws Exception If cannot create a new JVM.
     * @throws ExecutionException If a error occurred in execution.
     * @see CallableSerializable
     * @see OutProcessResult
     * @serialData
     */
    public <T extends Serializable> OutProcessResult<T> call(CallableSerializable<T> callable) throws Exception, ExecutionException {

        // If is already out process
        if (System.getProperty(RUNNING_AS_OUT_PROCESS) != null) {
            // run here
            return new OutProcessResult(callable.call(), 0);
        }

        return getResult(callable, javaOptions, classpath, processBuilderFactory);
    }

    private <T extends Serializable> OutProcessResult<T> getResult(CallableSerializable<T> callable, String[] javaOptions, String classpath, ProcessBuilderFactory processBuilderFactory) throws Exception{

        try (SocketTransaction<CallableSerializable<T>, Serializable> tx = new SocketTransaction<>(callable, timeoutMs)) {

            // create out process command
            List<String> commandList = new ArrayList<>();
            commandList.addAll(Arrays.asList(javaOptions));
            commandList.add("-cp");
            commandList.add(StringUtils.wrap(classpath, '\''));  // powershell likes the classpath to be wrapped with single quotes
            commandList.add(OneRunRemoteMain.class.getName());
            commandList.add(String.valueOf(tx.getPort()));
            commandList.add(tx.getSecret());

            // adjust in processBuilderFactory and starts
            ProcessBuilder builder = processBuilderFactory.create(commandList);

            Process process = builder.start();

            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), System.err::println);

            new Thread(outputGobbler).start();
            new Thread(errorGobbler).start();

            process.getOutputStream().close();

            int returnCode = process.waitFor();

            System.out.println("got return code: " + returnCode);

            if (returnCode != 0) {
                throw new UserCanceledException("Unable to start installation process. " +
                    "User may not have allowed elevated privileges.  Return code was " + returnCode);
            }

            // otherwise, wait for a max of 10 seconds for the installation to complete
            Serializable response = tx.exchange();

            return new OutProcessResult(response, returnCode);

        } catch (SocketTransaction.TransactionTimeoutException e) {
            throw new ExecutionException("Callable timed out.", e);
        } catch (UserCanceledException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException("Error executing callable.", e);
        }
    }

    public static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumeInputLine;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumeInputLine) {
            this.inputStream = inputStream;
            this.consumeInputLine = consumeInputLine;
        }

        public void run() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = bufferedReader.readLine()) != null)
                    consumeInputLine.accept(line);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Represents the result of a out process call
     *
     * @param <V> Type of result
     * @see
     *
     */
    public static final class OutProcessResult<V extends Serializable> {

        private final V result;
        private final int returnCode;

        private OutProcessResult(final V result, final int returnCode) {
            this.result = result;
            this.returnCode = returnCode;
        }

        public V getResult() {
            return result;
        }

        public int getReturnCode() {
            return returnCode;
        }
    }

    private static final class RunnableCallableWrapper implements CallableSerializable<Serializable> {

        private final Runnable runnable;

        private RunnableCallableWrapper(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public Serializable call() throws Exception {
            runnable.run();
            return null;
        }
    }
}
