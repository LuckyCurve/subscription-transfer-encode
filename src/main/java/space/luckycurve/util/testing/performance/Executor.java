package space.luckycurve.util.testing.performance;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.helpers.MessageFormatter;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.*;

@NotThreadSafe
public class Executor {

    private final int timeConsumingNumber;

    private final PriorityBlockingQueue<Long> timeConsumingQueue;

    private final ExecutorService workExecutor;

    private final Request request;

    public Executor(Request request) {
        this.request = request;
        timeConsumingNumber = (request.getParallel() * request.getRequestPerThreadNumber()) / 2;
        timeConsumingQueue = new PriorityBlockingQueue<>(timeConsumingNumber, Comparator.reverseOrder());

        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setNameFormat(MessageFormatter.format("{}-%d", "press-executor").getMessage());
        workExecutor = new ThreadPoolExecutor(
                request.getParallel(), request.getParallel(),
                0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactoryBuilder.build()
        );
    }

    public Response execute() {
        Callable<Long> task = wrapTask(request.getTask());

        // execute warm up
        if (request.getWarmUpNumber() > 0) {
            doExecute(task, true);
        }

        // execute actual task and record
        doExecute(task);

        return new Response(new ArrayList<>(timeConsumingQueue));
    }

    private Callable<Long> wrapTask(Runnable runnable) {
        return () -> {
            long startTime = System.currentTimeMillis();

            runnable.run();

            long endTime = System.currentTimeMillis();
            return endTime - startTime;
        };
    }

    private void doExecute(Callable<Long> task) {
        doExecute(task, false);
    }

    private void doExecute(Callable<Long> task, Boolean isWarmUpTask) {
        int loopTime = calculateLoopTime(isWarmUpTask);

        ArrayList<Future<Long>> futures = new ArrayList<>(loopTime);
        for (int i = 0; i < loopTime; i++) {
            futures.add(workExecutor.submit(task));
        }

        futures.forEach(future -> {
            try {
                Long timeConsume = future.get();
                if (!isWarmUpTask) {
                    addConsumeTimeToQueue(timeConsume);
                }
            } catch (Exception e) {
                throw new RuntimeException(MessageFormatter.format("execute {} occur error", isWarmUpTask ? "warm up task" : "task").getMessage(), e);
            }
        });
    }

    private void addConsumeTimeToQueue(Long time) {
        timeConsumingQueue.put(time);
        if (timeConsumingQueue.size() > timeConsumingNumber) {
            timeConsumingQueue.poll();
        }
    }

    private int calculateLoopTime(Boolean isWarmUpTask) {
        if (isWarmUpTask) {
            return request.getParallel() * request.getWarmUpNumber();
        } else {
            return request.getParallel() * request.getRequestPerThreadNumber();
        }
    }
}
